package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.config.PluginConfig;
import cn.whitrayhb.furbot.data.FetchJson;
import cn.whitrayhb.furbot.data.FetchPicture;
import cn.whitrayhb.furbot.data.JsonDecoder;
import cn.whitrayhb.furbot.util.Cooler;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.ConsoleCommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class FurPic extends JRawCommand {
    private static final MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final FurPic INSTANCE = new FurPic();
    public FurPic() {
        super(FurbotMain.INSTANCE,"fur-pic","来只");
        this.setDescription("#来一只兽兽~");
        this.setUsage("(/)来只 <兽兽名字/sid/uid>");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        if(!(sender instanceof ConsoleCommandSender)){
            if (Cooler.isLocked(Objects.requireNonNull(sender.getUser()).getId())) {
                sender.sendMessage("操作太快了，请稍后再试");
                return;
            }
            Cooler.lock(sender.getUser().getId(), PluginConfig.CoolDown.INSTANCE.getFurpicCD());
        }
        //从指令中获取名称
        String name;
        String picQueryURL;
        String type = null;
        HashMap<String,String> info;
        try{
            name = arg.get(0).contentToString();
        }catch (Exception ignored){
            sender.sendMessage("下次记得把兽兽名字告诉我哦~");
            return;
        }
        try {type = arg.get(1).contentToString();} catch (Exception ignored) {}
        
        if(name.matches("\\d+")){//如果参数为纯数字则直接作为sid查询
            picQueryURL = new StringBuilder()
                    .append("https://cloud.foxtail.cn/api/function/pictures?picture=")
                    .append(name)
                    .append("&model=1").toString();
        } else if (name.matches("[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+")) {
            picQueryURL = new StringBuilder()
                    .append("https://cloud.foxtail.cn/api/function/pictures?picture=")
                    .append(name)
                    .append("&model=0").toString();
        } else{//如果参数不为纯数字则作为名字查询
            //构建查询URL
            String picIDQueryURL = new StringBuilder()
                    .append("https://cloud.foxtail.cn/api/function/random?name=")
                    .append(name)
                    .append("&type=")
                    .append(type).toString();
            //拉取有图片ID的JSON
            String picQueryJson = FetchJson.fetchJson(picIDQueryURL);
            if(picQueryJson==null){
                sender.sendMessage("图片信息拉取失败……");
                return;
            }
            //解析JSON获得图片信息
            info = JsonDecoder.decodeQueryJson(picQueryJson);
            if (info == null) {
                sender.sendMessage("没有找到这只兽……");
                return;
            }
            //构建查询图片ID信息JSON的URL
            picQueryURL = "https://cloud.foxtail.cn/api/function/pictures?picture=" + info.get("picID") + "&model=";
        }
        //获取查询图片ID信息JSON
        String picJson = FetchJson.fetchJson(picQueryURL);
        if(picJson==null){
            sender.sendMessage("图片信息拉取失败……");
            return;
        }
        //获取图片信息
        HashMap<String,String> picInfo = JsonDecoder.decodePicJson(picJson);
        if(Objects.equals(picInfo.get("examine"), "3")){
            sender.sendMessage("没有找到这只兽……或许可以使用“投只兽”来投稿");
            return;
        }
        //图片链接
        String picURL = picInfo.get("url");
        //图片保存的位置
        String savePath = "./data/cn.whitrayhb.furbot/cache/furpic/";
        //拉取图片并获取保存位置+图片名
        String picPath = FetchPicture.fetchPicture(picURL,savePath);
        if(picPath==null){
            sender.sendMessage("下载图片失败……");
            return;
        }
        File file = new File(picPath);
        if(sender.getSubject()!=null) {
            ExternalResource resource = ExternalResource.create(file);
            Image image = sender.getSubject().uploadImage(resource);
            MessageChain message;
            if(image.getSize()!=0) {
                message = new MessageChainBuilder()
                        .append("---==每日兽图Bot==---\n")
                        .append("今天也是福瑞控呢\n")
                        .append("兽名:").append(picInfo.get("name")).append("\n")
                        .append("SID: ").append(picInfo.get("id")).append("\n")
                        //.append("种类："+picType)
                        .append(image)
                        .append("Code By WHB\n")
                        .append("API By 兽云祭").build();
                sender.sendMessage(message);
            }
            try {
                resource.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            sender.sendMessage("请不要在控制台中运行该命令");
            MessageChain message = new MessageChainBuilder()
                    .append("---==每日兽图Bot==---\n")
                    .append("今天也是福瑞控呢\n")
                    .append("兽名:").append(picInfo.get("name")).append("\n")
                    .append("SID: ").append(picInfo.get("id")).append("\n")
                    .append("咕Bot By WHB\n")
                    .append("API By 兽云祭").build();
            sender.sendMessage(message);
        }
    }
}
