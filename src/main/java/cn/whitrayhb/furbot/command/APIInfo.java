package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class APIInfo extends JRawCommand {
    public static final APIInfo INSTANCE = new APIInfo();
    private APIInfo() {
        super(FurbotMain.INSTANCE,"api-info","接口信息");
        this.setDescription("接口信息");
        this.setUsage("(/)api-info");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        HashMap APIInfo = FurbotMain.INSTANCE.apiinfo;
        MessageChain message = new MessageChainBuilder().append("---==API信息==---\n")
                .append("API:兽云祭\n")
                .append("图片请求数："+APIInfo.get("queryNum")+"\n")
                .append("总图片数为："+APIInfo.get("picNum")+"\n")
                .append("公开图片数为："+APIInfo.get("publicPicNum")+"\n")
                .append("审核中图片数为："+APIInfo.get("examinePicNum")+"\n")
                .append("Code By WHB\n")
                .append("API By 兽云祭")
                .build();
        sender.sendMessage(message);
    }
}
