package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.data.FetchJson;
import cn.whitrayhb.furbot.data.FetchPicture;
import cn.whitrayhb.furbot.data.JsonDecoder;
import net.mamoe.mirai.console.command.CommandSender;
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

public class RandomFurPic extends JRawCommand {
    private static MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final RandomFurPic INSTANCE = new RandomFurPic();
    public RandomFurPic() {
        super(FurbotMain.INSTANCE,"random-furpic","来张毛图","来张福瑞图");
        this.setDescription("来随机吸一只毛毛吧~");
        this.setUsage("来只毛");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        String randomPicJsonURL = "https://cloud.foxtail.cn/api/function/random?name=&type=";
        //拉取随机图片信息JSON
        String randomPicJson = FetchJson.fetchJson(randomPicJsonURL);
        //解码随机图片信息获取图片ID
        HashMap info = JsonDecoder.INSTANCE.decodeQueryJson(randomPicJson);
        if(info == null){
            logger.info("Json解析失败！");
        }
        //构建查询图片ID信息JSON的URL
        String picQueryURL = new StringBuilder().append("https://cloud.foxtail.cn/api/function/pictures?picture=")
                .append(info.get("picID")).append("&model=").toString();
        logger.info(picQueryURL);
        //获取查询图片ID信息JSON
        String picJson = FetchJson.fetchJson(picQueryURL);
        //解码图片ID查询信息JSON获取图片URL
        String picURL = JsonDecoder.INSTANCE.decodePicJson(picJson);
        //图片保存的位置
        String savePath = "./data/cn.whitrayhb.furbot/cache/furpic/";
        //拉取图片并返回保存的位置+文件名
        String picPath = FetchPicture.fetchPicture(picURL,savePath);
        //发送消息
        File file = new File(picPath);
        if(sender.getSubject()!=null) {
            ExternalResource resource = ExternalResource.create(file);
            Image image = sender.getSubject().uploadImage(resource);
            MessageChain message = new MessageChainBuilder()
                    .append("--=每日毛图Bot=--\n")
                    .append("今天也记得吸毛了呢\n")
                    .append("毛毛名字："+info.get("name")+"\n")
                    .append("毛毛ID："+info.get("id")+"\n")
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
