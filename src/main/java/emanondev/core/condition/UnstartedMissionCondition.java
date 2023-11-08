package emanondev.core.condition;

import emanondev.deepquests.Quests;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import org.bukkit.entity.Player;

public class UnstartedMissionCondition extends Condition {


    public UnstartedMissionCondition() {
        super("unstartedmission");
    }

    @Override
    public void validateInfo(String text) {
        String[] args = text.split(" ");
        QuestManager manager = Quests.get().getQuestManager(args[0]);
        Mission mission = manager.getMission(Integer.parseInt(args[1]));
        if (mission==null)
            throw new IllegalArgumentException();
    }

    //unstartedmission <managerid> <missionid>
    @Override
    public boolean isValid(Player player, String value){
        String[] args = value.split(" ");
        QuestManager manager = Quests.get().getQuestManager(args[0]);
        return switch (manager.getUserManager().getUser(player).getDisplayState(manager.getMission(Integer.parseInt(args[1])))){
            case LOCKED, UNSTARTED ->  true;
            case ONPROGRESS , COOLDOWN , COMPLETED, FAILED-> false;
        };
    }

    @Override
    public String getInfo() {
        return "unstartedmission <managerid> <missionid>";
    }
}
