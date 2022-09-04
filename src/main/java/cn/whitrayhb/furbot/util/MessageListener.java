package cn.whitrayhb.furbot.util;

import cn.whitrayhb.furbot.FurbotMain;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

public class MessageListener {
    private static MessageChain messages = null;
    public static MessageChain nextMessage(CommandSender sender){
        long rawUserId = sender.getUser().getId();
        if(sender.getSubject()!=null) {
            messages = null;
            EventChannel<Event> groupChannel = GlobalEventChannel.INSTANCE.filter(event -> event instanceof GroupMessageEvent && ((GroupMessageEvent) event).getSender().getId() == rawUserId);
            Listener<GroupMessageEvent> listener = groupChannel.subscribeAlways(GroupMessageEvent.class, m -> {
                messages = m.getMessage();
            });
            listener.start();
            int time = 30000;
            while (messages == null || messages.isEmpty()) {
                try {
                    Thread.sleep(1);
                    time = time - 1;
                } catch (Exception ignored) {
                }
                if (time <= 0) {
                    break;
                }
            }
            listener.complete();
            return messages;
        }else{
            messages = null;
            EventChannel<Event> friendChannel = GlobalEventChannel.INSTANCE.filter(event -> event instanceof FriendMessageEvent && ((FriendMessageEvent) event).getSender().getId() == rawUserId);
            Listener<GroupMessageEvent> listener = friendChannel.subscribeAlways(GroupMessageEvent.class, m -> {
                messages = m.getMessage();
            });
            listener.start();
            int time = 30000;
            while (messages == null || messages.isEmpty()) {
                try {
                    Thread.sleep(1);
                    time = time - 1;
                } catch (Exception ignored) {}
                if (time <= 0) {
                    break;
                }
            }
            listener.complete();
            return messages;
        }
    }
}
