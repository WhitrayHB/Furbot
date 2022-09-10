package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.data.FetchJson;
import cn.whitrayhb.furbot.data.JsonDecoder;
import cn.whitrayhb.furbot.util.Cooler;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.ConsoleCommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class QueryFur extends JRawCommand {
    public static final QueryFur INSTANCE = new QueryFur();
    private QueryFur() {
        super(FurbotMain.INSTANCE,"query-fur","查只兽","查兽图","查投稿");
        this.setDescription("查一只兽兽");
        this.setUsage("(/)查只兽  #查询图片详细信息");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        if(!(sender instanceof ConsoleCommandSender)){
            if (Cooler.isLocked(Objects.requireNonNull(sender.getUser()).getId())) {
                sender.sendMessage("操作太快了，请稍后再试");
                return;
            }
            Cooler.lock(sender.getUser().getId(), 120);
        }
        String name;
        String queryURL;
        try{
            name = arg.get(0).contentToString();
        }catch (Exception ignored){
            sender.sendMessage("下次记得把id告诉我哦~");
            return;
        }
        //如果参数为纯数字则直接作为sid查询
        if(name.matches("\\d+")){
            queryURL = "https://cloud.foxtail.cn/api/function/pullpic?picture=" + name + "&model=1";
        } else if (name.matches("[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+")) {
            queryURL = "https://cloud.foxtail.cn/api/function/pullpic?picture=" + name + "&model=0";
        } else {//如果参数不为纯数字则作为名字查询
            queryURL = "https://cloud.foxtail.cn/api/function/random?name=" + name + "&type=";
        }
        if(queryURL.endsWith("&type=")){
            String json = FetchJson.fetchJson(queryURL);
            HashMap<String, String> map = JsonDecoder.decodeQueryJson(json);
            queryURL = "https://cloud.foxtail.cn/api/function/pullpic?picture=" + map.get("picID") + "&model=0";
        }
        String jinfo = FetchJson.fetchJson(queryURL);
        ArrayList<HashMap<String, String>> arrInfo = JsonDecoder.decodeListPicQueryJson(jinfo);
        HashMap<String, String> msg = arrInfo.get(1);
        HashMap<String, String> code = arrInfo.get(2);
        if(Objects.equals(code.get("code"), "20801")){
            sender.sendMessage(msg.get("msg"));
            return;
        }
        HashMap<String, String> info = arrInfo.get(0);
        info.putIfAbsent("suggest", "无");
        if(Objects.equals(info.get("suggest"), "")){
            info.put("suggest","无");
        }
        String examineStatus = null;
        switch(info.get("examine")){
            case"0":
                examineStatus = "审核中";
                break;
            case"1":
                examineStatus = "已通过";
                break;
            case"2":
                examineStatus = "被拒绝";
                break;
            case"3":
                examineStatus = "不存在";
                break;
        }
        //.append("时间："+info.get("time")+"\n")
        MessageChain message = new MessageChainBuilder()
                .append("----==兽图信息==----" + "\n")
                .append("名字：").append(info.get("name")).append("\n")
                .append("sid：").append(info.get("id")).append("\n")
                .append("uid：").append(info.get("picID")).append("\n")
                .append("格式：").append(info.get("format")).append("\n")
                .append("留言：").append(info.get("suggest")).append("\n")
                .append("审核状态：").append(examineStatus).append("\n")
                .append("Code By WHB\n")
                .append("API By 兽云祭").build();
        sender.sendMessage(message);
    }
}
