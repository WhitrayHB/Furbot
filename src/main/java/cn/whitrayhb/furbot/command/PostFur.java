package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.data.PluginData;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        String[] arrToken = PluginData.Cookie.INSTANCE.getToken().split(";");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        Date expire = null;
        try {expire = sdf.parse(arrToken[2]);} catch (ParseException e) {logger.error("时间转换失败");}
        if(new Date().compareTo(expire)>=0){
            sender.sendMessage("登录过期，请使用/api-login重新登录");
            return;
        }

    }
}
