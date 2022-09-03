package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.data.JsonDecoder;
import cn.whitrayhb.furbot.data.PluginData;
import cn.whitrayhb.furbot.data.PostData;
import cn.whitrayhb.furbot.util.MessageListener;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;


public class PostFur extends JRawCommand {
    private static MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final PostFur INSTANCE = new PostFur();
    private PostFur() {
        super(FurbotMain.INSTANCE,"post-fur","投只兽");
        this.setDescription("#投只兽：上传图片");
        this.setUsage("(/)投只兽");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        if(sender.getSubject()==null){
            sender.sendMessage("请不要在控制台中执行此命令");
            return;
        }
        if(PluginData.Cookie.INSTANCE.getToken().isEmpty()){
            sender.sendMessage("请使用 /刷新登录 登录API");
            return;
        }
        String name;
        String type;
        String suggest;
        String power = "1";
        Image image;
        //名字
        sender.sendMessage("要投稿的这只兽的名字是？");
        MessageChain nameMessage = MessageListener.nextMessage(sender);
        if(nameMessage==null){sender.sendMessage("是还没准备好吗？那稍后准备好以后再重新投稿吧");return;}
        if(nameMessage.stream().filter(m->m instanceof PlainText).findFirst().orElse(null)==null){
            sender.sendMessage("名字不正确，请重新投稿");
            return;
        }
        name = nameMessage.stream().filter(m->m instanceof PlainText).findFirst().orElse(null).contentToString();
        //类型
        sender.sendMessage("要投稿图片的类型为？");
        MessageChain typeMessage = MessageListener.nextMessage(sender);
        if(typeMessage==null){sender.sendMessage("是还没准备好吗？那稍后准备好以后再重新投稿吧");return;}
        if(typeMessage.stream().filter(m->m instanceof PlainText).findFirst().orElse(null)==null){sender.sendMessage("种类不正确，请重新投稿");return;}
        String rawType = typeMessage.stream().filter(m->m instanceof PlainText).findFirst().orElse(null).contentToString();
        switch (rawType){
            case"设定":
            case"0":
                type = "0";
                break;
            case"毛图":
            case"1":
                type = "1";
                break;
            case"插画":
            case"2":
                type = "2";
                break;
            default:
                sender.sendMessage("种类不正确，请重新投稿");
                return;
        }
        //留言
        sender.sendMessage("投稿图片的留言为？（输入无则留空）");
        MessageChain suggestMessage = MessageListener.nextMessage(sender);
        if(suggestMessage==null){sender.sendMessage("是还没准备好吗？那稍后准备好以后再重新投稿吧");return;}
        if(suggestMessage.stream().filter(m->m instanceof PlainText).findFirst().orElse(null)==null){sender.sendMessage("留言不正确，请重新投稿");return;}
        suggest = suggestMessage.stream().filter(m->m instanceof PlainText).findFirst().orElse(null).contentToString();
        //图片
        sender.sendMessage("请发送将要投稿的图片");
        MessageChain imageMessage = MessageListener.nextMessage(sender);
        if(imageMessage==null){sender.sendMessage("是还没准备好吗？那稍后准备好以后再重新投稿吧");return;}
        if(imageMessage.stream().filter(m->m instanceof Image).findFirst().orElse(null)==null){sender.sendMessage("图片不正确，请重新投稿");return;}
        image = (Image) imageMessage.stream().filter(m->m instanceof Image).findFirst().orElse(null);
        //投稿备份+确认
        UUID uuid = UUID.randomUUID();
        String imagePath = PostData.postBackup(uuid,name,type,suggest,image,sender);
        File imageFile = new File(imagePath);
        image = ExternalResource.uploadAsImage(imageFile,sender.getSubject());
        MessageChain ensureMessage = new MessageChainBuilder()
                .append("投稿兽名为："+name+"\n")
                .append("投稿类型为："+rawType+"\n")
                .append("投稿留言为："+suggest+"\n")
                .append("投稿图片为：\n")
                .append(image).append("\n")
                .append("投稿人为："+sender.getUser().getId()+"\n")
                .append("!!!请输入 确认投稿 以投稿!!!\n")
                .append("咕Bot By WHB\n")
                .append("API By 兽云祭").build();
        sender.sendMessage(ensureMessage);
        if(!MessageListener.nextMessage(sender).stream().filter(m->m instanceof PlainText).findFirst().orElse(null).contentToString().equals("确认投稿")) {
            sender.sendMessage("投稿已取消");
            return;
        }else{
            PostData.postEnsure(uuid);
        }
        //信息处理+POST
        if(suggest.equals("无")) suggest = "";
        try{
            //String imageType = imageFile.getName().split(".")[imageFile.getName().split(".").length-1];
            ImageType imageType = image.getImageType();
            String imageTypeString;
            switch (imageType){
                case JPG:
                    imageTypeString="jpg";
                    break;
                case PNG:
                    imageTypeString="png";
                    break;
                case BMP:
                    imageTypeString="bmp";
                    break;
                case GIF:
                    imageTypeString="gif";
                    break;
                case APNG:
                    imageTypeString="apng";
                    break;
                default:
                    sender.sendMessage("图片不正确，请重新投稿！");
                    return;
            }
            MediaType mediaType = MediaType.parse("image/"+imageTypeString);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            RequestBody imageBody = MultipartBody.create(imageFile,mediaType);
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("name", name)
                    .addFormDataPart("file",imageFile.getName(),imageBody)
                    .addFormDataPart("type",type)
                    .addFormDataPart("suggest", suggest)
                    .addFormDataPart("power", "1")
                    .build();
            String cookie = "Token=" + PluginData.Cookie.INSTANCE.getToken() + ";" +
                    "User=" + PluginData.Cookie.INSTANCE.getUser() + ";" +
                    "PHPSESSID=" + PluginData.Cookie.INSTANCE.getPhpsessionid();
            Request request = new Request.Builder()
                    .url("https://cloud.foxtail.cn/api/function/upload")
                    .addHeader("cookie",cookie)
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();
            String returnJson = response.body().source().readString(StandardCharsets.UTF_8);
            logger.info(returnJson);
            HashMap<String,String> returnMap = JsonDecoder.decodeAccountActionReturn(returnJson);
            sender.sendMessage(new MessageChainBuilder()
                    .append(returnMap.get("msg")).append("\n返回码为：")
                    .append(returnMap.get("code")).build());
            sender.sendMessage("投稿已投递！本地图片投稿ID为："+uuid);
        }catch (Exception e){
            logger.error(e);
        }

    }
}
