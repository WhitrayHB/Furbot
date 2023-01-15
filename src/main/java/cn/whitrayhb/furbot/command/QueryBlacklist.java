package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.config.PluginConfig;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class QueryBlacklist extends CommandBase {
    public static final QueryBlacklist INSTANCE = new QueryBlacklist();
    private QueryBlacklist() {
        super("query-blacklist",new String[]{"查云黑","梦梦云黑","云黑"});
        this.setDescription("查询云黑");
        this.setUsage("(/)查云黑  #查询一个QQ号在趣绮梦云黑中的云黑状态");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        if(PluginConfig.CloudBlacklist.INSTANCE.getApiKey().equals("")) sender.sendMessage("请在配置文件中填写趣绮梦云黑的APIKey");
        String id;
        String argument = null;
        At at = null;
        try{
            argument = arg.stream().filter(m -> m instanceof PlainText).findFirst().orElse(null).contentToString();
            at = (At) arg.stream().filter(m -> m instanceof At).findFirst().orElse(null);
        }catch (Exception ignored){}
        if(at != null){
            id = String.valueOf(at.getTarget());
        }else if(argument!=null && argument.matches("\\d+")){
            id = arg.stream().filter(m -> m instanceof PlainText).findFirst().orElse(null).contentToString();
        }else{
            try {id = askForString(sender,"想查询的QQ号或者群号是？",20000);} catch (TimeoutException ignored) {return;}
        }
        String url = "https://yunhei.qimeng.fun/OpenAPI.php?id="+id+"&key="+PluginConfig.CloudBlacklist.INSTANCE.getApiKey();
        String returnJson = null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = null;
            response = client.newCall(request).execute();
            returnJson = response.body().source().readString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject json = new JSONObject(returnJson);
        JSONObject info = json.getJSONArray("info").getJSONObject(0);
        if(!Objects.equals(info.getString("yh"), "true")){
            sender.sendMessage("该账号/群不在云黑中");
        }else{
            sender.sendMessage("该账号在云黑中，上黑原因为："+info.get("note"));
        }
    }
}
