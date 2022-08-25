package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.data.FetchJson;
import cn.whitrayhb.furbot.data.FetchPicture;
import cn.whitrayhb.furbot.data.JsonDecoder;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
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

public class FurPic extends JRawCommand {
    private static MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final FurPic INSTANCE = new FurPic();
    public FurPic() {
        super(FurbotMain.INSTANCE,"furpic","来只");
        this.setDescription("来吸一只毛毛吧~");
        this.setUsage("来只 <毛毛名字>");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        //从指令中获取名称
        String name = arg.get(0).contentToString();
        //构建查询URL
        String picIDQueryURL = new StringBuilder()
                .append("https://cloud.foxtail.cn/api/function/random?name=")
                .append(name)
                .append("&type=").toString();
        //拉取有图片ID的JSON
        String picQueryJson = FetchJson.fetchJson(picIDQueryURL);
        //解析JSON获得图片信息
        HashMap info = JsonDecoder.INSTANCE.decodeQueryJson(picQueryJson);
        if(info==null){
            sender.sendMessage("没有找到这只毛毛……");
            return;
        }
        //构建查询图片ID信息JSON的URL
        String picQueryURL = new StringBuilder()
                .append("https://cloud.foxtail.cn/api/function/pictures?picture=")
                .append(info.get("picID"))
                .append("&model=").toString();
        //获取查询图片ID信息JSON
        String picJson = FetchJson.fetchJson(picQueryURL);
        //获取图片链接
        String picURL = JsonDecoder.INSTANCE.decodePicJson(picJson);
        //图片保存的位置
        String savePath = "./data/cn.whitrayhb.furbot/cache/furpic/";
        //拉取图片并获取保存位置+图片名
        String picPath = FetchPicture.fetchPicture(picURL,savePath);
        File file = new File(picPath);
        if(sender.getSubject()!=null) {
            ExternalResource resource = ExternalResource.create(file);
            Image image = sender.getSubject().uploadImage(resource);
            MessageChain message = new MessageChainBuilder()
                    .append("---==每日毛图Bot==---\n")
                    .append("今天也记得吸毛了呢\n")
                    .append("毛毛名字:"+info.get("name")+"\n")
                    .append("毛毛ID: "+info.get("id")+"\n")
                    .append(image)
                    .append("咕Bot By WHB").build();
            sender.sendMessage(message);
            try {
                resource.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            sender.sendMessage("请不要在控制台中运行该命令");
        }
    }
}
