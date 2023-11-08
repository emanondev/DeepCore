package emanondev.core.actions;

import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class Action {

    private final String id;

    public Action(String id) {
        this.id = id.toLowerCase(Locale.ENGLISH);

    }

    @Deprecated
    public String getID() {
        return id;
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
    public abstract void execute(Player player, String text);

    public abstract String getInfo();

}
