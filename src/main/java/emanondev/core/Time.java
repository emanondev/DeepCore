package emanondev.core;

import org.bukkit.command.CommandSender;

import java.util.Locale;

public enum Time {
    WEEK(604800),
    DAY(86400),
    HOUR(3600),
    MINUTE(60),
    SECOND(1);
    public final long seconds;

    Time(long seconds) {
        this.seconds = seconds;
    }

    public String getSingleName(CommandSender sender) {
        return CoreMain.get().getLanguageConfig(sender).loadString("time.single." + this.name().toLowerCase(Locale.ENGLISH), this.name().toLowerCase(Locale.ENGLISH));
    }

    public String getMultipleName(CommandSender sender) {
        return CoreMain.get().getLanguageConfig(sender).loadString("time.multi." + this.name().toLowerCase(Locale.ENGLISH), this.name().toLowerCase(Locale.ENGLISH) + "s");
    }
}