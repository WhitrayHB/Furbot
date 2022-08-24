package cn.whitrayhb.furbot.command;

import cn.whitrayhb.furbot.FurbotMain;
import net.mamoe.mirai.console.command.java.JCompositeCommand;

public class FurPic extends JCompositeCommand {
    public static final FurPic INSTANCE = new FurPic();
    public FurPic() {
        super(FurbotMain.INSTANCE,"furpic","来只");
        this.setDescription("来吸一只毛毛吧~");
        this.setPrefixOptional(true);
    }
    @SubCommand("")
    public void randomFur(){

    }
}
