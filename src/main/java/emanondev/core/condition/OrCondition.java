package emanondev.core.condition;

import emanondev.core.actions.ActionHandler;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class OrCondition extends Condition{
    public OrCondition() {
        super("or");
    }

    //sequence %S[#]%<action1>[%S[#]%<action2>...]
    @Override
    public void validateInfo(String text) {
        if (!text.startsWith("%O"))
            throw new IllegalArgumentException();
        int indexEnd = text.indexOf("%", 2);
        String separator = text.substring(0, indexEnd + 1);
        if (!Pattern.compile("%O[0-9]*%").matcher(separator).matches())
            throw new IllegalArgumentException();
        String[] args = text.substring(separator.length()).split(separator);
        for (String arg : args)
            ActionHandler.validateAction(arg);
    }

    @Override
    public boolean isValid(Player player, String text) {
        if (!text.startsWith("%O"))
            throw new IllegalArgumentException();
        int indexEnd = text.indexOf("%", 2);
        String separator = text.substring(0, indexEnd + 1);
        if (!Pattern.compile("%O[0-9]*%").matcher(separator).matches())
            throw new IllegalArgumentException();
        String[] args = text.substring(separator.length()).split(separator);
        for (String arg : args)
            if (ConditionHandler.evaluateCondition(player,arg))
                return true;
        return false;
    }

    @Override
    public String getInfo() {
        return "or %O[#]%<condition1>[%O[#]%<condition2>...]";
    }
}
