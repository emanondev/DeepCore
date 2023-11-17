package emanondev.core.condition;

import emanondev.deepquests.Quests;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Task;
import org.bukkit.entity.Player;

public class CompletedTaskCondition extends Condition {

    public static final String ID = "completedtask";


    public CompletedTaskCondition() {
        super(ID);
    }

    @Override
    public void validateInfo(String text) {
        String[] args = text.split(" ");
        QuestManager manager = Quests.get().getQuestManager(args[0]);
        Task task = manager.getTask(Integer.parseInt(args[1]));
        if (task == null)
            throw new IllegalArgumentException();
    }

    //completedmission <managerid> <missionid>
    @Override
    public boolean isValid(Player player, String value) {
        String[] args = value.split(" ");
        QuestManager manager = Quests.get().getQuestManager(args[0]);
        return manager.getUserManager().getUser(player).getTaskData(manager.getTask(Integer.parseInt(args[1]))).isCompleted();
    }

    @Override
    public String getInfo() {
        return "completedtask <managerid> <taskid>";
    }
}
