package emanondev.core.actions;

import emanondev.core.CoreMain;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ActionHandler {

    private static final HashMap<String, Action> actions = new HashMap<>();

    public static void handleAction(Player player, String type, String action) {
        actions.get(type).execute(player, action);
    }

    public static void registerAction(Action action) {
        if (action == null)
            throw new NullPointerException();
        if (actions.containsKey(action.getId()))
            throw new IllegalArgumentException();
        actions.put(action.getId().toLowerCase(Locale.ENGLISH), action);
        CoreMain.get().logInfo("Registered Action &e" + action.getInfo());
    }

    public static void clearActions() {
        actions.clear();
    }

    public static void validateAction(String rawAction) {
        String type = rawAction.split(" ")[0].toLowerCase(Locale.ENGLISH);
        if (!actions.containsKey(type))
            throw new IllegalArgumentException();
        String info = rawAction.length() > type.length() ? rawAction.substring(type.length() + 1) : null;
        actions.get(type).validateInfo(info);
    }

    @Deprecated
    public static void validateActionType(String type) {
        if (!actions.containsKey(type.toLowerCase(Locale.ENGLISH)))
            throw new IllegalArgumentException();
    }

    @Deprecated
    public static void validateActionInfo(String type, String text) {
        actions.get(type.toLowerCase(Locale.ENGLISH)).validateInfo(text);
    }

    public static List<String> getTypes() {
        return new ArrayList<>(actions.keySet());
    }

    public static Action getAction(String type) {
        return actions.get(type.toLowerCase(Locale.ENGLISH));
    }

}
