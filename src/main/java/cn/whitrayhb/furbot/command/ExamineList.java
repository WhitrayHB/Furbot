package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.util.Cooler;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

/*Todo*/
public class ExamineList extends CommandBase {

    public static final ExamineList INSTANCE = new ExamineList();
    private ExamineList() {
        super("examine-list", new String[]{"审核列表"});
        this.setDescription("审核列表");
        this.setUsage("/审核列表  #获取审核列表");
        this.setPrefixOptional(false);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
    }
}