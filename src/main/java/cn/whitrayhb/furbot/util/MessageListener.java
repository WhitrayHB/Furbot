package cn.whitrayhb.furbot.util;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

public class MessageListener {
    private static MessageChain messages = null;
    public static MessageChain nextMessage(CommandSender sender,int time){
        long rawUserId;
        try{
            rawUserId = sender.getUser().getId();
        }catch (Exception e){
            sender.sendMessage("请勿在控制台中使用此命令");
            return null;
        }
        int time1 = time;
        if(sender.getSubject() instanceof Group) {
            messages = null;
            EventChannel<Event> groupChannel = GlobalEventChannel.INSTANCE.filter(event -> event instanceof GroupMessageEvent && ((GroupMessageEvent) event).getSender().getId() == rawUserId);
            Listener<GroupMessageEvent> listener = groupChannel.subscribeAlways(GroupMessageEvent.class, m -> messages = m.getMessage());
            listener.start();
            while (messages == null || messages.isEmpty()) {
                try {
                    Thread.sleep(1);
                    time1 = time1 - 1;
                } catch (Exception ignored) {
                }
                if (time1 <= 0) {
                    break;
                }
            }
            listener.complete();
            return messages;
        }else{
            messages = null;
            EventChannel<Event> friendChannel = GlobalEventChannel.INSTANCE.filter(event -> event instanceof FriendMessageEvent && ((FriendMessageEvent) event).getSender().getId() == rawUserId);
            Listener<FriendMessageEvent> listener = friendChannel.subscribeAlways(FriendMessageEvent.class, m -> messages = m.getMessage());
            listener.start();
            while (messages == null || messages.isEmpty()) {
                try {
                    Thread.sleep(1);
                    time1 = time1 - 1;
                } catch (Exception ignored) {}
                if (time1 <= 0) {
                    break;
                }
            }
            listener.complete();
            return messages;
        }
    }
}
