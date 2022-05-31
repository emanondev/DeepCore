package emanondev.core.util;

import java.util.List;

public interface FileLogger extends CorePluginLinked {

    public default void logOnFile(String fileName, String text) {
        getPlugin().getLoggerManager().logText(fileName, text);
    }

    public default void logOnFile(String fileName, List<String> text) {
        getPlugin().getLoggerManager().logText(fileName, text);
    }

    public default void logOnFile(String fileName, String[] text) {
        getPlugin().getLoggerManager().logText(fileName, text);
    }
}
