package Main.MultiThreading;

import java.io.*;

/**
 *
 */
public class CorrespondenceOrganisation {

	private static CorrespondenceOrganisation instance = new CorrespondenceOrganisation();

	private static boolean                    ccOn          = false;
	private static String                     directoryName = "CC-Organisation";
	private static File                       directory     = new File(directoryName);
	private static String                     lockName      = "lockFile";
	private static File                       lockFile      = new File(directoryName + File.separator + lockName);
	private static FileWriter                 writer        = null;
	private static FileOutputStream           reader        = null;
	private static java.nio.channels.FileLock lock          = null;

	public static CorrespondenceOrganisation getInstance() {
		return instance;
	}

	public void setup() {
		if (ccOn) {
			try {
				directory.mkdir();
				writer = new FileWriter(lockFile);
				writer.append("a");
				reader = new FileOutputStream(lockFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void getLock() {
		try {
			lock = reader.getChannel().lock();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void returnLock() {
		try {
			lock.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutDown() {
		if (ccOn) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private CorrespondenceOrganisation() {}
}
