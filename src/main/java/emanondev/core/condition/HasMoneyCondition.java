package emanondev.core.condition;

import emanondev.core.CoreMain;
import emanondev.core.VaultEconomyHandler;
import org.bukkit.entity.Player;

public class HasMoneyCondition extends Condition{
    public HasMoneyCondition() {
        super("hasmoney");
    }

    @Override
    public void validateInfo(String text) {
        if (Double.parseDouble(text)>0)
            throw new IllegalStateException();
    }

    @Override
    public boolean isValid(Player player, String text) {
        return new VaultEconomyHandler().hasMoney(player,Double.parseDouble(text));
    }

    @Override
    public String getInfo() {
        return "hasmoney <amount>";
    }
}
