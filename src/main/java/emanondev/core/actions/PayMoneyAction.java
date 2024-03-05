package emanondev.core.actions;

import emanondev.core.VaultEconomyHandler;
import org.bukkit.entity.Player;

public class PayMoneyAction extends Action {
    public PayMoneyAction() {
        super("paymoney");
    }

    @Override
    public void validateInfo(String text) {
        if (Double.parseDouble(text) > 0)
            throw new IllegalStateException();
    }

    @Override
    public void execute(Player player, String text) {
        new VaultEconomyHandler().removeMoney(player, Double.parseDouble(text));
    }

    @Override
    public String getInfo() {
        return "paymoney <amount>";
    }
}
