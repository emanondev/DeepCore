package emanondev.core.condition;

import emanondev.core.CoreMain;
import emanondev.core.UtilsString;
import org.bukkit.entity.Player;

public class NoCooldownCondition extends Condition {
    public NoCooldownCondition() {
        super("nocooldown");
    }

    @Override
    public void validateInfo(String text) {
        if (!UtilsString.isValidID(text))
            throw new IllegalStateException();
    }

    @Override
    public boolean isValid(Player player, String text) {
        return CoreMain.get().getCooldownAPI(true)
                .hasCooldown(player,text)
                ||CoreMain.get().getCooldownAPI(false)
                .hasCooldown(player,text);
    }

    @Override
    public String getInfo() {
        return "nocooldown <cooldown id>";
    }
}
