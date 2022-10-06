package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBase extends JRawCommand {
    public static final MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public CommandBase(String primaryName,String[] secondaryNames){
        super(FurbotMain.INSTANCE,primaryName,secondaryNames);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
    }
    public void addTail(MessageChain messages){
    }
}
