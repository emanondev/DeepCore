package emanondev.core;

import emanondev.core.condition.CompletedMissionCondition;
import emanondev.core.condition.ConditionHandler;
import emanondev.core.condition.OngoingMissionCondition;
import emanondev.core.condition.UnstartedMissionCondition;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginListener implements Listener {

    @EventHandler
    private void event(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("DeepQuests5")) {
            CoreMain.get().logInfo("Registering conditions for DeepQuests5");
            ConditionHandler.registerCondition(new CompletedMissionCondition());
            ConditionHandler.registerCondition(new UnstartedMissionCondition());
            ConditionHandler.registerCondition(new OngoingMissionCondition());
            CoreMain.get().logInfo("Registered conditions for DeepQuests5");
        }
    }
    @EventHandler
    private void event(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("DeepQuests5")) {
            CoreMain.get().logInfo("Unregistering conditions for DeepQuests5");
            ConditionHandler.unregisterCondition(CompletedMissionCondition.ID);
            ConditionHandler.unregisterCondition(UnstartedMissionCondition.ID);
            ConditionHandler.unregisterCondition(OngoingMissionCondition.ID);
            CoreMain.get().logInfo("Unregistered conditions for DeepQuests5");
        }
    }
}
