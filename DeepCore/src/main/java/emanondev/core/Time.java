package emanondev.core;

import org.bukkit.command.CommandSender;

public enum Time {
	WEEK(604800),
	DAY(86400),
	HOUR(3600),
	MINUTE(60),
	SECOND(1);
	public final long seconds;
	private Time(long seconds) {
		this.seconds = seconds;
	}
	
	public String getSingleName(CommandSender sender) {
		return CoreMain.get().getLanguageConfig(sender).loadString("time.single."+this.name().toLowerCase(), this.name().toLowerCase());
	}
	public String getMultipleName(CommandSender sender) {
		return CoreMain.get().getLanguageConfig(sender).loadString("time.multi."+this.name().toLowerCase(), this.name().toLowerCase()+"s");
	}
}