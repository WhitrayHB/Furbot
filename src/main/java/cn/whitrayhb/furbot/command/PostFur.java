package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

public class PostFur extends JRawCommand {
    private static MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static final PostFur INSTANCE = new PostFur();
    private PostFur() {
        super(FurbotMain.INSTANCE,"post-fur","投只兽");
        this.setDescription("#投只兽：上传图片");
        this.setUsage("(/)post-fur");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        if(sender.getSubject()==null){
            sender.sendMessage("请不要在控制台中执行此命令");
        }
        sender.sendMessage("鸽子正在努力开发此功能……");
    }
}
