package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.config.PluginConfig;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class QueryBlacklist extends CommandBase {
    public static final QueryBlacklist INSTANCE = new QueryBlacklist();
    private QueryBlacklist() {
        super("query-blacklist",new String[]{"查云黑","梦梦云黑","云黑"});
        this.setDescription("查询云黑");
        this.setUsage("(/)查云黑  #查询一个QQ号在趣绮梦云黑中的云黑状态");
        this.setPrefixOptional(true);
    }
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain arg){
        if(PluginConfig.CloudBlacklist.INSTANCE.getApiKey().equals("")) sender.sendMessage("请在配置文件中填写趣绮梦云黑的APIKey");
        String id = null;
        String argument = arg.stream().filter(m -> m instanceof PlainText).findFirst().orElse(null).contentToString();
        if(argument.matches("\\d+")){
            id = argument;
        }else{
            try {id = askForString(sender,"想查询的QQ号或者群号是？",10000);} catch (TimeoutException ignored) {}
        }
        String url = "https://yunhei.qimeng.fun/OpenAPI.php?id="+id+"&key="+PluginConfig.CloudBlacklist.INSTANCE.getApiKey();
        JSONObject json = new JSONObject(fetchJson(url));
        JSONObject info = json.getJSONArray("info").getJSONObject(0);
        if(!Objects.equals(info.getString("yh"), "true")){
            sender.sendMessage("该账号/群不在云黑中");
        }else{
            sender.sendMessage("该账号在云黑中，上黑原因为"+info.get("note"));
        }
    }
}
