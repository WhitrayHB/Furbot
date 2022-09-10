package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class APIInfo extends JRawCommand {
    public static final APIInfo INSTANCE = new APIInfo();
    private APIInfo() {
        super(FurbotMain.INSTANCE,"api-info","接口信息");
        this.setDescription("接口信息");
        this.setUsage("(/)接口信息  #获取接口信息");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        FurbotMain.INSTANCE.refreshAPIInfo();
        HashMap<String,String> apiInfo = FurbotMain.INSTANCE.apiinfo;
        MessageChain message = new MessageChainBuilder()
                .append("---==API信息==---\n")
                .append("API:兽云祭\n")
                .append("图片请求数：").append(apiInfo.get("queryNum")).append("\n")
                .append("总图片数为：").append(apiInfo.get("picNum")).append("\n")
                .append("公开图片数为：").append(apiInfo.get("publicPicNum")).append("\n")
                .append("审核中图片数为：").append(apiInfo.get("examinePicNum")).append("\n")
                .append("API连续运行时长为：").append(apiInfo.get("time")).append("天\n")
                .append("Code By WhitrayHB")
                .build();
        sender.sendMessage(message);
    }
}
