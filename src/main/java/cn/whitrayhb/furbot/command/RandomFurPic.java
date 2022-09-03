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
    private static final MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final RandomFurPic INSTANCE = new RandomFurPic();
    public RandomFurPic() {
        super(FurbotMain.INSTANCE,"random-furpic","来张毛图","来只福瑞","来只兽","随机兽图");
        this.setDescription("#随机来一只兽兽~");
        this.setUsage("(/)来只兽");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        String model = null;
        try{
            model = arg.get(0).contentToString();
            switch (model){
                case"设定":
                    model = "0";
                case"0":
                    sender.sendMessage("正在查询设定图");
                    break;

                case"毛图":
                    model = "1";
                case"1":
                    sender.sendMessage("正在查询毛图");
                    break;

                case"插画":
                    model = "2";
                case"2":
                    sender.sendMessage("正在查询插画");
                    break;

                default:
                    sender.sendMessage("查询种类不正确……0为设定，1为毛图，2为插画");
                    return;
            }
        }catch(Exception ignored){}
        String randomPicJsonURL = "https://cloud.foxtail.cn/api/function/random?name=&type="+model;
        //拉取随机图片信息JSON（random）
        String randomPicJson = FetchJson.fetchJson(randomPicJsonURL);
        if(randomPicJson==null){
            sender.sendMessage("获取图片信息JSON失败");
            return;
        }
        //解码随机图片信息获取图片ID
        HashMap<String, String> info = JsonDecoder.decodeQueryJson(randomPicJson);
        if(info == null){
            logger.info("Json解析失败！");
        }
        //构建查询图片ID信息JSON的URL（picture）
        String picQueryURL = new StringBuilder()
                .append("https://cloud.foxtail.cn/api/function/pictures?picture=")
                .append(info.get("picID"))
                .append("&model=").toString();
        //获取查询图片ID信息JSON
        String picJson = FetchJson.fetchJson(picQueryURL);
        //获取图片信息
        HashMap<String,String> picInfo = JsonDecoder.decodePicJson(picJson);
        //图片链接
        String picURL = picInfo.get("url");
        //图片保存的位置
        String savePath = "./data/cn.whitrayhb.furbot/cache/furpic/";
        //拉取图片并返回保存的位置+文件名
        String picPath = FetchPicture.fetchPicture(picURL,savePath);
        if(picPath==null){
            sender.sendMessage("图片下载失败……");
            return;
        }
        //发送消息
        File file = new File(picPath);
        if(sender.getSubject()!=null) {
            ExternalResource resource = ExternalResource.create(file);
            Image image = sender.getSubject().uploadImage(resource);
            MessageChain message;
            if(image.getSize()!=0) {
                message = new MessageChainBuilder()
                        .append("---==每日兽图Bot==---\n")
                        .append("今天也是福瑞控呢\n")
                        .append("兽名:" + info.get("name") + "\n")
                        .append("SID: " + info.get("id") + "\n")
                        .append(image)
                        .append("Code By WHB\n")
                        .append("API By 兽云祭").build();
            }else{
                message = new MessageChainBuilder()
                        .append("---==每日兽图Bot==---\n")
                        .append("今天也是福瑞控呢\n")
                        .append("兽名:" + info.get("name") + "\n")
                        .append("SID: " + info.get("id") + "\n")
                        .append("*这只兽在路上走丢了……*")
                        .append("Code By WHB\n")
                        .append("API By 兽云祭").build();
            }
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
