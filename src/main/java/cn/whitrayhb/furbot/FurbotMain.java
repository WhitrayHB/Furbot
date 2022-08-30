package cn.whitrayhb.furbot;

import cn.whitrayhb.furbot.command.*;
import cn.whitrayhb.furbot.config.PluginConfig;
import cn.whitrayhb.furbot.data.FetchJson;
import cn.whitrayhb.furbot.data.JsonDecoder;
import cn.whitrayhb.furbot.data.PluginData;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.data.PluginDataStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

import javax.sound.sampled.Line;
import java.util.HashMap;

public final class FurbotMain extends JavaPlugin {
    public static final FurbotMain INSTANCE = new FurbotMain();
    public String infoJson = null;
    public HashMap apiinfo = null;
    private FurbotMain() {
        super(new JvmPluginDescriptionBuilder("cn.whitrayhb.furbot", "0.1.0")
                .info("Furbot")
                .name("FurBot插件")
                .author("WhitrayHB")
                .build());
    }

    @Override
    public void onEnable() {
        CommandManager.INSTANCE.registerCommand(FurPic.INSTANCE,true);
        CommandManager.INSTANCE.registerCommand(RandomFurPic.INSTANCE,true);
        CommandManager.INSTANCE.registerCommand(APIInfo.INSTANCE,true);
        CommandManager.INSTANCE.registerCommand(PostFur.INSTANCE,true);
        CommandManager.INSTANCE.registerCommand(QueryFur.INSTANCE,true);
        CommandManager.INSTANCE.registerCommand(APILogin.INSTANCE,true);
        refreshAPIInfo();
        configInitialize();

        getLogger().info("FurBot插件已加载！");
    }
    public void refreshAPIInfo(){
        infoJson = FetchJson.fetchJson("https://cloud.foxtail.cn/api/information/feedback");
        apiinfo = JsonDecoder.decodeAPIInfoJson(infoJson);
    }
    public void configInitialize(){
        reloadPluginConfig(PluginConfig.Account.INSTANCE);
        reloadPluginData(PluginData.Cookie.INSTANCE);
        //if(PluginConfig.Account.INSTANCE.getAccount().isEmpty())PluginConfig.Account.INSTANCE.setAccount("#请在此处填写账号");
        //if(PluginConfig.Account.INSTANCE.getPassword().isEmpty())PluginConfig.Account.INSTANCE.setPassword("#请在此处填写密码");
        //if(PluginConfig.Account.INSTANCE.getApiToken().isEmpty())PluginConfig.Account.INSTANCE.setApiToken("#请在此处填写唯一Token");
    }
}
