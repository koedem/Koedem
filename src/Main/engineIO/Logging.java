package Main.engineIO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Logging {

	private static String directoryName = "Koedem-Logs";
	private static File directory = new File(directoryName);
	private static String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()).concat("-" + ManagementFactory.getRuntimeMXBean().getName());
	private static File logFile = new File(directoryName + File.separator + "Logfile" + timeLog);
	private static BufferedWriter writer = null;
	
	public static void setup() {
		if (UCI.logging) {
			try {
				directory.mkdir();
				writer = new BufferedWriter(new FileWriter(logFile));
				System.out.println("Logfile at " + logFile.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void printLine(String line) {
        System.out.println(line);
		if (UCI.logging) {
			addToLogFile(line);
		}
	}
	
	public static void addToLogFile(String line) {
		if (UCI.logging) {
			try {
				writer.write(line + "\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close() {
		if (UCI.logging) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Logging() {
	}
}
