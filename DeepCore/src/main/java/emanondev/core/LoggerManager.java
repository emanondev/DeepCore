package emanondev.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LoggerManager {

	private CorePlugin plugin;

	public LoggerManager(CorePlugin plugin) {
		this.plugin = plugin;
		this.data = plugin.getConfig("loggerConfig.yml");
		reload();
	}

	private final YMLConfig data;

	private final LinkedBlockingQueue<Request> requests = new LinkedBlockingQueue<>();

	public String getDefaultDateFormat() {
		return data.loadString("default.DateFormat", "[dd.MM.yyyy HH:mm:ss] ");
	}

	/**
	 * reload loggers formats and reopen the task
	 */
	public void reload() {
		reloadLoggers();
		if (task == null || task.isCancelled()) {
			task = new BukkitRunnable() {
				@Override
				public void run() {
					while (plugin.isEnabled() && !this.isCancelled()) {
						Request req;
						synchronized (requests) {
							req = requests.poll();
							if (req == null)
								try {
									requests.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
						}
						if (req != null)
							log(req);
					}
					while (!requests.isEmpty())
						log(requests.poll());
				}
			}.runTaskAsynchronously(plugin);
		}
	}

	protected void disable() {
		if (task != null && !task.isCancelled())
			task.cancel();
		synchronized (requests) {
			requests.notifyAll();
		}
	}

	private BukkitTask task = null;

	private void reloadLoggers() {
		loggers.values().forEach((logger) -> logger.reload());
	}

	private final WeakHashMap<String, Logger> loggers = new WeakHashMap<String, Logger>();

	private void registerLogger(Logger logger, String fileName) {
		loggers.put(new String(fileName), logger);
	}

	/**
	 * 
	 * @param fileName
	 *            where the logger is supposed to log
	 * @return logger from the cash else create the logger
	 */
	private Logger getLogger(String fileName) {
		if (fileName == null)
			throw new NullPointerException();
		if (fileName.equals("") || fileName.equals(".log"))
			throw new IllegalArgumentException();
		if (!fileName.endsWith(".log"))
			fileName = fileName + ".log";
		fileName = fileName.toLowerCase();
		Logger result = loggers.get(fileName);
		if (result == null) {
			result = new Logger(fileName);
			registerLogger(result, fileName);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void log(Request req) {
		if (req == null)
			return;
		try {
			Logger logger = getLogger(req.fileName);

			switch (req.type) {
			case LOG_ARRAY_OF_TEXT:
				logger.log(req.timestamp, (String[]) req.log);
				return;
			case LOG_LIST_OF_TEXT:
				logger.log(req.timestamp, (List<String>) req.log);
				return;
			case LOG_TEXT:
				logger.log(req.timestamp, (String) req.log);
				return;
			default:
				new IllegalArgumentException().printStackTrace();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void logText(String fileName, List<String> text) {
		synchronized (requests) {
			requests.add(new Request(fileName, text, RequestType.LOG_LIST_OF_TEXT));
			requests.notify();
		}
	}

	public void logText(String fileName, String[] text) {
		synchronized (requests) {
			requests.add(new Request(fileName, text, RequestType.LOG_ARRAY_OF_TEXT));
			requests.notify();
		}
	}

	public void logText(String fileName, String text) {
		synchronized (requests) {
			requests.add(new Request(fileName, text, RequestType.LOG_TEXT));
			requests.notify();
		}
	}

	private class Request {
		private final String fileName;
		private final Object log;
		private final RequestType type;
		private final long timestamp;

		public Request(String fileName, Object log, RequestType type) {
			this.fileName = fileName;
			this.log = log;
			this.type = type;
			this.timestamp = System.currentTimeMillis();
		}
	}

	private enum RequestType {
		LOG_TEXT, LOG_ARRAY_OF_TEXT, LOG_LIST_OF_TEXT
	}

	/**
	 * 
	 * An utility class to log on files Async <br>
	 * 
	 * @author emanon
	 *
	 */
	private class Logger {
		private final File file;
		private DateFormat dateFormat;
		private final String path;

		/**
		 * 
		 * @param fileName
		 *            the name of the file/path associated to the logger <br>
		 *            it may ends with ".log" else ".log" will be added<br>
		 *            to choose a file in a sub folder use "/" in the name<br>
		 *            example "myfolder/mysubfolder/mylogger.log"
		 */
		private Logger(String fileName) {

			if (plugin == null || fileName == null)
				throw new NullPointerException();
			if (fileName.equals("") || fileName.equals(".log"))
				throw new IllegalArgumentException();
			if (!fileName.endsWith(".log"))
				fileName = fileName + ".log";
			fileName = fileName.toLowerCase();
			file = new File(plugin.getDataFolder(), fileName);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) { // Create parent folders if they don't exist
					file.getParentFile().mkdirs();
				}
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			path = "loggers." + '"' + fileName + '"';
			this.reload();
		}

		/**
		 * reload the config for this logger
		 */
		private void reload() {
			dateFormat = new SimpleDateFormat(data.loadString(path + ".DateFormat", getDefaultDateFormat()),
					Locale.ITALY);
		}

		/**
		 * 
		 * @param message
		 *            <br>
		 *
		 *            adds this message to a new line of the file
		 */
		private void log(long timestamp, String message) {
			String date = dateFormat.format(new Date(timestamp));

			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				bw.append(date + message + "\n");
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * 
		 * @param messages
		 *            <br>
		 * 
		 *            adds this messages to the file
		 */
		private void log(long timestamp, List<String> messages) {
			if (messages == null)
				return;
			String date = dateFormat.format(new Date(timestamp));
			log(timestamp, String.join("\n" + date, messages));
		}

		/**
		 * 
		 * @param messages
		 *            <br>
		 * 
		 *            adds this messages to the file
		 */
		private void log(long timestamp, String... messages) {
			if (messages == null)
				return;
			String date = dateFormat.format(new Date(timestamp));
			log(timestamp, String.join("\n" + date, messages));
		}
	}

}