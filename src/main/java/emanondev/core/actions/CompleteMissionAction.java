package emanondev.core.actions;

import emanondev.deepquests.Quests;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Task;
import org.bukkit.entity.Player;

public class CompleteMissionAction extends Action {

    public static final String ID = "completemission";

    public CompleteMissionAction() {
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

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        QuestManager manager = Quests.get().getQuestManager(args[0]);
        manager.getUserManager().getUser(player).completeMission(manager.getMission(Integer.parseInt(args[1])));

        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UtilsString.fix(text, player, true, "%player%", player.getName()));
    }


    @Override
    public String getInfo() {
        return "completemission <manager> <missionid>";
    }
}