package emanondev.core.condition;

import org.bukkit.entity.Player;

public class WorldCondition extends Condition {

    public WorldCondition() {
        super("completedmission");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty() || text.contains(" "))
            throw new IllegalArgumentException();
    }

    //completedmission <managerid> <missionid>
    @Override
    public boolean isValid(Player player, String value) {
        String world = player.getWorld().getName();
        for (String name : value.split(","))
            if (name.equals(world))
                return true;
        return false;
    }

    @Override
    public String getInfo() {
        return "world <world>[,<world2>...]";
    }
}
