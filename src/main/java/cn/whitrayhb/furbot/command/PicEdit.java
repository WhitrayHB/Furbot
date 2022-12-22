package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.data.*;
import cn.whitrayhb.furbot.util.Cooler;
import cn.whitrayhb.furbot.util.MessageListener;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.ConsoleCommandSender;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static cn.whitrayhb.furbot.command.PostFur.getPicType;

public class PicEdit extends CommandBase {
    public static final PicEdit INSTANCE = new PicEdit();
    private PicEdit() {
        super("edit-picture",new String[]{"改兽图"});
        this.setDescription("#改兽图：图片操作");
        this.setUsage("(/)改兽图  #图片操作");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage("请不要在控制台中运行此命令");
        }else if (sender.getSubject()!=null&& Cooler.isLocked(Objects.requireNonNull(sender.getUser()).getId())) {
            sender.sendMessage("操作太快了，请稍后再试");
            return;
        }
        try {
            MessageChain actionTypeMessage = ask(sender, "请选择图片操作类型：更改/删除", 15000);
            String actionTypeString = actionTypeMessage.stream().filter(m -> m instanceof PlainText).findFirst().orElse(null).contentToString();
            MessageChain IdMessage = ask(sender, "要更改的图片id是？", 15000);
            String id = IdMessage.stream().filter(m -> m instanceof PlainText).findFirst().orElse(null).contentToString();
            if (actionTypeString.equals("删除")) {
                doDelete(sender, id);
            } else if (actionTypeString.equals("更改")) {
                doEdit(sender, id);
            } else {
                sender.sendMessage("操作类型错误");
            }
        }catch (TimeoutException e){
            sender.sendMessage("是还没准备好吗？那稍后准备好再来吧");
        }
    }
    public void doDelete(@NotNull CommandSender sender, String id){
        int model;
        if(id.matches("[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+")){
            model = 0;
        }else if (id.matches("\\d+")) {
            model = 1;
        }else{
            sender.sendMessage("图片id错误");
            return;
        }
        String proving = proving(sender);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String cookie = "Token=" + PluginData.Cookie.INSTANCE.getToken() + ";" +
                        "User=" + PluginData.Cookie.INSTANCE.getUser() + ";" +
                        "PHPSESSID=" + PluginData.Cookie.INSTANCE.getPhpsessionid();
        Request request = new Request.Builder()
                .url("https://cloud.foxtail.cn/api/function/delete?picture="+id+"&proving="+proving+"&model="+model)
                .addHeader("Cookie",cookie)
                .method("GET",null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String returnJson = response.body().source().readString(StandardCharsets.UTF_8);
            HashMap<String,String> returnMap = JsonDecoder.decodeAccountActionReturn(returnJson);
            sender.sendMessage(new MessageChainBuilder()
                    .append(returnMap.get("msg")).append("\n返回码为：")
                    .append(returnMap.get("code")).build());
        }catch(Exception e){
            logger.error(e);
        }
    }
    public void doEdit(@NotNull CommandSender sender, String id){
        try {
            int model;
            if(id.matches("[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+")){
                model = 0;
            }else if (id.matches("\\d+")) {
                model = 1;
            }else{
                sender.sendMessage("图片id错误");
                return;
            }
            //图片信息展示
            showInfo(sender, id);
            String detailedAction = askForString(sender, "请选择图片更改部分：名称/内容（换图）/类型/留言", 30000);
            RequestBody action = null;
            Image image;
            switch (detailedAction) {
                case "名称":
                    String name = askForString(sender, "要更改为的名字是？",30000);
                    action = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("type", "0")
                            .addFormDataPart("name",name)
                            .build();
                    break;
                case "内容":
                case "换图":
                    MessageChain imageMessage = ask(sender,"要更改的图片为？",60000);
                    UUID uuid = UUID.randomUUID();
                    image = (Image) imageMessage.stream().filter(m->m instanceof Image).findFirst().orElse(null);
                    String imagePath = PostData.postBackup(uuid,"edit","edit","edit",image,sender);
                    File imageFile = new File(imagePath);
                    assert image != null;
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
                        case UNKNOWN:
                            imageTypeString=getPicType(new FileInputStream(imageFile));
                            break;
                        default:
                            sender.sendMessage("图片格式不支持，请重新投稿！");
                            return;
                    }
                    MediaType mediaType = MediaType.parse("image/"+imageTypeString);
                    RequestBody imageBody = MultipartBody.create(imageFile,mediaType);
                    action = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("type", "1")
                            .addFormDataPart("file",imageFile.getName(),imageBody)
                            .build();
                    break;
                case "类型":
                    String formRaw = askForString(sender, "要更改为的类型是（设定/插画/毛图)？",30000);
                    String form = "2";
                    switch (formRaw){
                        case "设定":
                            form = "0";
                            break;
                        case "插画":
                            form = "1";
                            break;
                        case "毛图":
                            form = "2";
                            break;
                    }
                    action = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("type", "2")
                            .addFormDataPart("form",form)
                            .build();
                    break;
                case "留言":
                    String suggest = askForString(sender, "要更改为的名字是？",30000);
                    action = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("type", "3")
                            .addFormDataPart("suggest",suggest)
                            .build();
                    break;
            }
            if(!askForString(sender,"请输入“确认更改”以确认更改",15000).equals("确认更改")){
                sender.sendMessage("更改已取消");
                return;
            }
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String cookie = "Token=" + PluginData.Cookie.INSTANCE.getToken() + ";" +
                    "User=" + PluginData.Cookie.INSTANCE.getUser() + ";" +
                    "PHPSESSID=" + PluginData.Cookie.INSTANCE.getPhpsessionid();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("picture",id)
                    .addFormDataPart("model", String.valueOf(model))
                    .addPart(action).build();
            Request request = new Request.Builder()
                    .url("https://cloud.foxtail.cn/api/function/modify")
                    .addHeader("Cookie",cookie)
                    .method("POST",body)
                    .build();
            Response response = client.newCall(request).execute();
            String returnJson = response.body().source().readString(StandardCharsets.UTF_8);
            HashMap<String,String> returnMap = JsonDecoder.decodeAccountActionReturn(returnJson);
            sender.sendMessage(new MessageChainBuilder()
                    .append(returnMap.get("msg")).append("\n返回码为：")
                    .append(returnMap.get("code")).build());
        } catch (TimeoutException e){
            sender.sendMessage("是还没准备好吗？那稍后准备好再来吧");
        } catch (FileNotFoundException | NullPointerException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
            sender.sendMessage("网络异常，请稍后再试");
        }
    }

    public void showInfo(@NotNull CommandSender sender,String id){
        String picQueryURL;
        if(id.matches("\\d+")){//如果参数为纯数字则直接作为sid查询
            picQueryURL = "https://cloud.foxtail.cn/api/function/pictures?picture=" + id + "&model=1";
        } else if (id.matches("[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+-[A-Za-z\\d]+")) {
            picQueryURL = "https://cloud.foxtail.cn/api/function/pictures?picture=" + id + "&model=0";
        }else{
            sender.sendMessage("图片id错误");
            return;
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
            if (image.getSize() != 0) {
                message = new MessageChainBuilder()
                        .append("将要修改的图片信息：\n")
                        .append("名字:").append(picInfo.get("name")).append("\n")
                        .append("SID: ").append(picInfo.get("id")).append("\n")
                        .append("留言：").append(picInfo.get("suggest")).append("\n")
                        .append(image).build();
                sender.sendMessage(message);
            }
            try {
                resource.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static String proving(CommandSender sender) {
        String provingPicPath = "./data/cn.whitrayhb.furbot/cache/proving/";
        String provingPicPathWithName = FetchPicture.fetchPictureWithCookie("https://cloud.foxtail.cn/api/check", provingPicPath);
        ExternalResource provingPic = ExternalResource.create(new File(provingPicPathWithName));
        Image image = sender.getSubject().uploadImage(provingPic);
        sender.sendMessage("请输入此图中的图片验证码\n");
        sender.sendMessage(image);
        MessageListener messageListener = new MessageListener();
        try {
            return messageListener.nextMessage(sender,30000).stream().filter(m->m instanceof PlainText).findFirst().orElse(null).contentToString();
        }catch (TimeoutException e){
            return null;
        }
    }
}
