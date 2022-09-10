package cn.whitrayhb.furbot.util;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.ConsoleCommandSender;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

public class MessageListener {
    private MessageChain messages = null;
    public MessageChain nextMessage(CommandSender sender,int time){
        long rawUserId;
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage("请勿在控制台中使用此命令");
            return null;
        }
        rawUserId = sender.getUser().getId();
        int time1 = time;
        if(sender.getSubject() instanceof Group) {
            messages = null;
            EventChannel<Event> groupChannel = GlobalEventChannel.INSTANCE.filter(event -> event instanceof GroupMessageEvent && ((GroupMessageEvent) event).getSender().getId() == rawUserId);
            Listener<GroupMessageEvent> listener = groupChannel.subscribeAlways(GroupMessageEvent.class, m -> {if(m.getSender().getId()==rawUserId) messages = m.getMessage();});
            listener.start();
            while (messages == null || messages.isEmpty()) {
                try {
                    Thread.sleep(1);
                    time1 = time1 - 1;
                } catch (Exception ignored) {}
                if (time1 <= 0) break;
            }
            listener.complete();
            return messages;
        }else{
            messages = null;
            EventChannel<Event> friendChannel = GlobalEventChannel.INSTANCE.filter(event -> event instanceof FriendMessageEvent && ((FriendMessageEvent) event).getSender().getId() == rawUserId);
            Listener<FriendMessageEvent> listener = friendChannel.subscribeAlways(FriendMessageEvent.class, m -> {if(m.getSender().getId()==rawUserId) messages = m.getMessage();});
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
