package Main.engineIO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Logging {

	private static boolean logging = true;
	private static String directoryName = "Koedem-Logs";
	private static File directory = new File(directoryName);
	private static String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()).concat("-" + ManagementFactory.getRuntimeMXBean().getName());
	private static File logFile = new File(directoryName + File.separator + timeLog);
	private static BufferedWriter writer = null;
	
	public static void setup() {
		if (logging) {
			/*try {
				System.out.println("Logfile at " + logFile.getCanonicalPath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}*/
			try {
				directory.mkdir();
				writer = new BufferedWriter(new FileWriter(logFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void printLine(String line) {
        System.out.println(line);
		if (logging) {
			addToLogFile(line);
		}
	}
	
	public static void addToLogFile(String line) {
		if (logging) {
			try {
				writer.write(line + "\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isLogging() {
		return logging;
	}

	public static void setLogging(boolean logging) {
		Logging.logging = logging;
	}
	
	public static void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Logging() {
	}
}
