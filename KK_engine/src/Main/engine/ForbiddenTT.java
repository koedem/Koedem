package Main.engine;

import Main.engineIO.Logging;

import java.util.Arrays;

/**
 *
 */
public class ForbiddenTT {

	private final int  size;
	private final long mask;
	private final long[] entries;

	private long entryCount = 0, collisionCount = 0;

	public ForbiddenTT(int sizeInMB) {
		size = (sizeInMB/ 8) * 1048576;
		mask = size / 8 - 1; // eight entries per bucket
		entries = new long[size];
	}

	public long isPresent(long hash) {
		int bucket = (int) (hash & mask);
		for (int i = 0; i < 8; i++) {
			if ((entries[8 * bucket + i] & (~mask)) == (hash & (~mask))) {
				return entries[8 * bucket + i] & mask;
			}
		}
		return 100;
	}

	public void put(long hash, int depthSoFar) {
		int bucket = (int) (hash & mask);
		for (int i = 0; i < 8; i++) {
			if ((entries[8 * bucket + i] & (~mask)) == (hash & (~mask))) { // TODO could it be entered at different depths?
				int a = 0; // this should be impossible once proper implementation happened
				return;
			}
		}
		for (int i = 0; i < 8; i++) {
			if (entries[8 * bucket + i] == 0) {
				entries[8 * bucket] = (hash & (~mask)) + depthSoFar;
				entryCount++;
				return;
			}
		}
		collisionCount++;
	}

	public void printCounts() {
		Logging.printLine("Unique positions: " + entryCount + ", collision losses: " + collisionCount);
		Logging.printLine("Fill: " + String.format("%.2f", (double) entryCount * 100 / (double) size) + "%; "
		                  + "loss percentage: " + String.format("%.2f", (double) collisionCount * 100 / (double) entryCount) + "%.");
	}

	public void reset() {
		Arrays.fill(entries, 0);
		entryCount = 0;
		collisionCount = 0;
	}
}
