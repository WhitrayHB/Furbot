package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.config.PluginConfig;
import cn.whitrayhb.furbot.data.JsonDecoder;
import cn.whitrayhb.furbot.data.PluginData;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;


public class APILogin extends JRawCommand {
    private static final MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final APILogin INSTANCE = new APILogin();

    public APILogin() {
        super(FurbotMain.INSTANCE, "api-login", "刷新登录");
        this.setDescription("#登录API");
        this.setUsage("/刷新登录");
        this.setPrefixOptional(false);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg) {
        FurbotMain.INSTANCE.reloadPluginConfig(PluginConfig.Account.INSTANCE);
        String account = PluginConfig.Account.INSTANCE.getAccount();
        String password = PluginConfig.Account.INSTANCE.getPassword();
        //String proving = Proving.proving(sender);
        String apiToken = PluginConfig.Account.INSTANCE.getApiToken();
        if (account.isEmpty() || password.isEmpty() || apiToken.isEmpty()) {
            sender.sendMessage("登录配置不完整啊笨蛋，检查检查config吧");
            return;
        }
        try {//net相关
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("account", PluginConfig.Account.INSTANCE.getAccount())
                    .addFormDataPart("password", PluginConfig.Account.INSTANCE.getPassword())
                    .addFormDataPart("token", PluginConfig.Account.INSTANCE.getApiToken())
                    .addFormDataPart("model", "1")
                    .build();
            Request request = new Request.Builder()
                    .url("https://cloud.foxtail.cn/api/account/login")
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();

            //返回值处理
            if(!response.isSuccessful()){
                sender.sendMessage("服务器响应咕咕咕了……或许是网络环境的问题？");
                return;
            }
            String returnJson = response.body().source().readString(StandardCharsets.UTF_8);
            HashMap<String,String> returnMap = JsonDecoder.decodeAccountActionReturn(returnJson);
            logger.info(returnMap.get("msg"));
            logger.info("返回码为："+returnMap.get("code"));
            List<String> listCookies = response.headers("Set-Cookie");
            logger.info(listCookies.toString());
            listCookies.forEach((c)->{
                if(c.startsWith("PHPSESSID")){
                    String[] arrSession = c.split(";");
                    PluginData.Cookie.INSTANCE.setPhpsessionid(arrSession[0].split("=")[1]);
                }
                if(c.startsWith("Token")){
                    String[] arrToken = c.split(";");
                    PluginData.Cookie.INSTANCE.setToken(arrToken[0].split("=")[1]);
                }
                if(c.startsWith("User")){
                    String[] arrToken = c.split(";");
                    PluginData.Cookie.INSTANCE.setUser(arrToken[0].split("=")[1]);
                }
            });
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
