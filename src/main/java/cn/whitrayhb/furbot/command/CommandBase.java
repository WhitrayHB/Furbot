package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.util.MessageListener;
import io.ktor.client.features.Sender;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeoutException;

public abstract class CommandBase extends JRawCommand {
    public static final MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public CommandBase(String primaryName,String[] secondaryNames){
        super(FurbotMain.INSTANCE,primaryName,secondaryNames);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
    }
    public MessageChain ask(CommandSender sender, String question, int time) throws TimeoutException {
        sender.sendMessage(question);
        MessageListener messageListener = new MessageListener();
        return messageListener.nextMessage(sender,time);
    }
    public String askForString(CommandSender sender, String question, int time) throws TimeoutException{
        return ask(sender,question,time).stream().filter(m -> m instanceof PlainText).findFirst().orElse(null).contentToString();
    }
}
