package emanondev.core.condition;

import emanondev.core.CoreMain;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ConditionHandler {

    private static final HashMap<String, Condition> conditionTypes = new HashMap<>();


    public static boolean evaluateCondition(@NotNull Player player, @NotNull String rawCond) {
        String type = rawCond.split(" ")[0];
        boolean reversed = type.startsWith("!");
        try {
            return reversed != conditionTypes.get((reversed ? type.substring(1) : type).toLowerCase(Locale.ENGLISH)).isValid(player, rawCond.substring(type.length() + 1));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean evaluateConditions(@NotNull Player player, @NotNull List<String> conditions) {
        for (String rawCond : conditions) {
            if (!evaluateCondition(player, rawCond))
                return false;
        }
        return true;
    }

    public static void registerCondition(@NotNull Condition condition) {
        if (conditionTypes.containsKey(condition.getId()))
            throw new IllegalArgumentException();
        conditionTypes.put(condition.getId(), condition);
        CoreMain.get().logInfo("Registered Condition "+condition.getId()+" sintax: &e[!]"+condition.getInfo());
    }

    public static void unregisterCondition(@NotNull String conditionId) {
        conditionId = conditionId.toLowerCase(Locale.ENGLISH);
        if (!conditionTypes.containsKey(conditionId))
            throw new IllegalArgumentException();
        conditionTypes.remove(conditionId);
        CoreMain.get().logInfo("Unregistered Condition Type &e" + conditionId);
    }

    public static void clearConditions() {
        conditionTypes.clear();
        CoreMain.get().logInfo("Cleared Condition Types");
    }

    /**
     * @throws RuntimeException if not valid
     */
    public static void validateCondition(@NotNull String rawCond) {
        String type = rawCond.split(" ")[0];
        boolean reversed = type.startsWith("!");
        conditionTypes.get((reversed ? type.substring(1) : type).toLowerCase(Locale.ENGLISH)).validateInfo(rawCond.substring(type.length() + 1));
    }

    public static List<String> getTypes() {
        return new ArrayList<>(conditionTypes.keySet());
    }

    public static Condition getCondition(String type) {
        return conditionTypes.get(type.toLowerCase(Locale.ENGLISH));
    }

}
