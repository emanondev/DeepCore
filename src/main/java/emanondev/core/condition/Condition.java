package emanondev.core.condition;

import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class Condition {

    private final String id;

    public Condition(String id) {
        this.id = id.toLowerCase(Locale.ENGLISH);

    }

    public final String getId() {
        return id;
    }

    /**
     * @param text
     * @throws IllegalArgumentException if text is not valid
     */
    public abstract void validateInfo(String text);

    /**
     * @param text
     * @throws IllegalArgumentException if text is not valid
     */
    public abstract boolean isValid(Player player, String text);

    public abstract String getInfo();
}
