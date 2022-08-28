package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.data.FetchJson;
import cn.whitrayhb.furbot.data.JsonDecoder;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryFur extends JRawCommand {
    private static MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final QueryFur INSTANCE = new QueryFur();
    private QueryFur() {
        super(FurbotMain.INSTANCE,"query-fur","查只兽","查只","查兽图");
        this.setDescription("查一只兽兽");
        this.setUsage("(/)查只兽");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        String name;
        String queryURL = null;
        try{
            name = arg.get(0).contentToString();
        }catch (Exception ignored){
            sender.sendMessage("下次记得把id告诉我哦~");
            return;
        }
        //如果参数为纯数字则直接作为sid查询
        if(name.matches("\\d+")){
            queryURL = new StringBuilder()
                    .append("https://cloud.foxtail.cn/api/function/pullpic?picture=")
                    .append(name)
                    .append("&model=1").toString();
        } else if (name.matches("[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+")) {
            queryURL = new StringBuilder()
                    .append("https://cloud.foxtail.cn/api/function/pullpic?picture=")
                    .append(name)
                    .append("&model=0").toString();
        } else {//如果参数不为纯数字则作为名字查询
            queryURL = new StringBuilder()
                    .append("https://cloud.foxtail.cn/api/function/random?name=")
                    .append(name)
                    .append("&type=").toString();
        }
        if(queryURL.endsWith("&type=")){
            String json = FetchJson.fetchJson(queryURL);
            HashMap<String, String> map = JsonDecoder.decodeQueryJson(json);
            queryURL = new StringBuilder()
                    .append("https://cloud.foxtail.cn/api/function/pullpic?picture=")
                    .append(map.get("picID"))
                    .append("&model=0").toString();
        }
        String jinfo = FetchJson.fetchJson(queryURL);
        ArrayList<HashMap<String, String>> arrInfo = JsonDecoder.decodeListPicQueryJson(jinfo);
        HashMap<String, String> info = arrInfo.get(0);
        info.putIfAbsent("suggest", "");
        MessageChain message = new MessageChainBuilder()
                .append("----==兽图信息==----"+"\n")
                .append("名字："+info.get("name")+"\n")
                .append("sid："+info.get("id")+"\n")
                .append("uid："+info.get("picID")+"\n")
                .append("格式："+info.get("format")+"\n")
                //.append("时间："+info.get("time")+"\n")
                .append("留言："+info.get("suggest")+"\n")
                .append("Code By WHB\n")
                .append("API By 兽云祭").build();
        sender.sendMessage(message);
    }
}
