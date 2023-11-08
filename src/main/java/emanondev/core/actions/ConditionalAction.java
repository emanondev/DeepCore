package emanondev.core.actions;

import emanondev.core.condition.ConditionHandler;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class ConditionalAction extends Action {
    public ConditionalAction() {
        super("condition");
    }

    //condition %C[#]%<condition>%C[#]%<action>%C[#]%<alternativeaction>
    @Override
    public void validateInfo(String text) {
        if (!text.startsWith("%C"))
            throw new IllegalArgumentException();
        int indexEnd = text.indexOf("%", 2);
        String separator = text.substring(0, indexEnd + 1);
        if (!Pattern.compile("%C[0-9]*%").matcher(separator).matches())
            throw new IllegalArgumentException();
        String[] args = text.substring(separator.length()).split(separator);
        if (args.length > 3 || args.length < 2)
            throw new IllegalArgumentException();
        ConditionHandler.validateCondition(args[0]);
        ActionHandler.validateAction(args[1]);
        if (args.length > 2)
            ActionHandler.validateAction(args[2]);
    }

    @Override
    public void execute(Player player, String text) {
        if (!text.startsWith("%C"))
            throw new IllegalArgumentException();
        int indexEnd = text.indexOf("%", 2);
        String separator = text.substring(0, indexEnd + 1);
        if (!Pattern.compile("%C[0-9]*%").matcher(separator).matches())
            throw new IllegalArgumentException();
        String[] args = text.substring(separator.length()).split(separator);
        if (args.length > 3 || args.length < 2)
            throw new IllegalArgumentException();
        if (ConditionHandler.evaluateCondition(player, args[0]))
            ActionHandler.validateAction(args[1]);
        else if (args.length > 2)
            ActionHandler.validateAction(args[2]);
    }

    @Override
    public String getInfo() {
        return "condition %C[#]%<condition>%C[#]%<action>%C[#]%<alternativeaction>";
    }
}
