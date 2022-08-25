package cn.whitrayhb.furbot;

import cn.whitrayhb.furbot.command.FurPic;
import cn.whitrayhb.furbot.command.RandomFurPic;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

public final class FurbotMain extends JavaPlugin {
    public static final FurbotMain INSTANCE = new FurbotMain();
    private FurbotMain() {
        super(new JvmPluginDescriptionBuilder("cn.whitrayhb.furbot", "0.1.0")
                .info("EG")
                .name("FurBot插件")
                .author("WhitrayHB")
                .build());
    }

    @Override
    public void onEnable() {
        CommandManager.INSTANCE.registerCommand(FurPic.INSTANCE,true);
        CommandManager.INSTANCE.registerCommand(RandomFurPic.INSTANCE,true);
        getLogger().info("FurBot插件已加载！");
    }
}
