package emanondev.core.actions;

import emanondev.deepquests.Quests;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Task;
import org.bukkit.entity.Player;

public class ProgressTaskAction extends Action {

    public static final String ID = "progresstask";

    public ProgressTaskAction() {
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
        manager.getUserManager().getUser(player).progressTask(manager.getTask(Integer.parseInt(args[1])),
                args.length >= 3 ? Integer.parseInt(args[2]) : 1, player, true);

        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UtilsString.fix(text, player, true, "%player%", player.getName()));
    }


    @Override
    public String getInfo() {
        return "progresstask <manager> <taskid> [amount=1]";
    }
}