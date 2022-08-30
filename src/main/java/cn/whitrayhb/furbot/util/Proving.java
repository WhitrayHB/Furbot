package cn.whitrayhb.furbot.util;

import cn.whitrayhb.furbot.FurbotMain;
import cn.whitrayhb.furbot.data.FetchPicture;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 图片验证码模块暂时停止开发
 * 转而使用唯一Token
 */
@Deprecated
public class Proving {

    public static String proving(CommandSender sender) {
        AtomicReference<String> proving = null;
        String provingPicPath = "./data/cn.whitrayhb.furbot/cache/proving/";
        String provingPicPathWithName = FetchPicture.fetchPicture("https://cloud.foxtail.cn/api/check", provingPicPath);
        ExternalResource provingPic = ExternalResource.create(new File(provingPicPathWithName));
        Image image = sender.getSubject().uploadImage(provingPic);
        sender.sendMessage("请输入下图中的图片验证码\n"+image);
        EventChannel<Event> eventChannel = GlobalEventChannel.INSTANCE.parentScope(FurbotMain.INSTANCE);
        if (sender instanceof Friend) {
            eventChannel.subscribe(FriendMessageEvent.class, f -> {
                if(true){

                }
                return null;
            });
        }
        if (sender instanceof Member) {
            do {
                eventChannel.subscribeOnce(GroupMessageEvent.class, g -> {
                    if (sender.getUser() == g.getSender() || g.getMessage().toString().matches("[a-zA-Z/d]+")) {
                        proving.set(g.toString());
                    } else if (g.toString() == "exit") {
                    } else {
                        sender.sendMessage("验证码格式不正确，输入exit退出");
                    }
                });
            }while(true);
        }
        return proving.get();
    }
}
