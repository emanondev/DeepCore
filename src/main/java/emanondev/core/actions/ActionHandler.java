package emanondev.core.actions;

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
        if (actions.containsKey(action.getID()))
            throw new IllegalArgumentException();
        actions.put(action.getID().toLowerCase(Locale.ENGLISH), action);
    }

    public static void clearActions() {
        actions.clear();
    }

    public static void validateActionType(String type) {
        if (!actions.containsKey(type.toLowerCase(Locale.ENGLISH)))
            throw new IllegalArgumentException();
    }

    public static void validateActionInfo(String type, String text) {
        actions.get(type.toLowerCase(Locale.ENGLISH)).validateInfo(text);
    }

    public static List<String> getTypes() {
        return new ArrayList<>(actions.keySet());
    }

    public static Action getAction(String type) {
        return actions.get(type.toLowerCase(Locale.ENGLISH));
    }

    public static void executeAction(String wholeLine) {

    }

}
