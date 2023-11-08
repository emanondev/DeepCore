package emanondev.core.actions;

import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class SequenceAction extends Action{
    public SequenceAction() {
        super("sequence");
    }

    //sequence %S[#]%<action1>[%S[#]%<action2>...]
    @Override
    public void validateInfo(String text) {
        if (!text.startsWith("%S"))
            throw new IllegalArgumentException();
        int indexEnd = text.indexOf("%",2);
        String separator = text.substring(0,indexEnd+1);
        if (!Pattern.compile("%S[0-9]*%").matcher(separator).matches())
            throw new IllegalArgumentException();
        String[] args = text.substring(separator.length()).split(separator);
        for (String arg:args)
            ActionHandler.validateAction(arg);
    }

    @Override
    public void execute(Player player, String text) {
        if (!text.startsWith("%S"))
            throw new IllegalArgumentException();
        int indexEnd = text.indexOf("%",2);
        String separator = text.substring(0,indexEnd+1);
        if (!Pattern.compile("%S[0-9]*%").matcher(separator).matches())
            throw new IllegalArgumentException();
        String[] args = text.substring(separator.length()).split(separator);
        for (String arg:args)
            ActionHandler.validateAction(arg);
    }

    @Override
    public String getInfo() {
        return "sequence %S[#]%<action1>[%S[#]%<action2>...]";
    }
}
