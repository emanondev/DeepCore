package emanondev.core.actions;

import emanondev.core.CoreMain;
import emanondev.core.message.DMessage;
import org.bukkit.entity.Player;

public class MessageAction extends Action {

    public MessageAction() {
        super("message");
    }

    @Override
    public void validateInfo(String text) {

    }

    @Override
    public void execute(Player player, String text) {
        new DMessage(CoreMain.get(), player).append(text).send();
    }

    @Override
    public String getInfo() {
        return "message <message>";
    }
}
