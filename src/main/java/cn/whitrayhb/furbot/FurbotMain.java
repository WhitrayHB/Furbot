package cn.whitrayhb.furbot;

import cn.whitrayhb.furbot.command.*;
import cn.whitrayhb.furbot.data.FetchJson;
import cn.whitrayhb.furbot.data.JsonDecoder;
import net.mamoe.mirai.console.command.CommandManager;
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
        refreshAPIInfo();
        getLogger().info("FurBot插件已加载！");
    }
    public void refreshAPIInfo(){
        infoJson = FetchJson.fetchJson("https://cloud.foxtail.cn/api/information/feedback");
        apiinfo = JsonDecoder.decodeAPIInfoJson(infoJson);
    }
}
