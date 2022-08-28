package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.config.PluginConfig;
import cn.whitrayhb.furbot.util.Proving;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;


public class APILogin extends JRawCommand {
    private static final MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final APILogin INSTANCE = new APILogin();
    public APILogin() {
        super(FurbotMain.INSTANCE,"api-login","登录");
        this.setDescription("#登录API");
        this.setUsage("(/)api-login");
        this.setPrefixOptional(false);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        if(sender.getSubject()==null){
            sender.sendMessage("请勿在控制台中执行该命令");
            return;
        }
        String account = PluginConfig.Account.INSTANCE.getAccount();
        String password = PluginConfig.Account.INSTANCE.getPassword();
        String proving = Proving.proving(sender);
    }
}
