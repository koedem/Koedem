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

	public boolean isPresent(long hash) {
		int bucket = (int) (hash & mask);
		if (entries[8 * bucket] == hash || entries[8 * bucket + 1] == hash || entries[8 * bucket + 2] == hash || entries[8 * bucket + 3] == hash ||
		    entries[8 * bucket + 4] == hash || entries[8 * bucket + 5] == hash || entries[8 * bucket + 6] == hash || entries[8 * bucket + 7] == hash) {
			return true;
		} else {
			return false;
		}
	}

	public void put(long hash) {
		int bucket = (int) (hash & mask);
		if (entries[8 * bucket] == 0) {
			entries[8 * bucket] = hash;
			entryCount++;
		} else if (entries[8 * bucket + 1] == 0) {
			entries[8 * bucket + 1] = hash;
			entryCount++;
		} else if (entries[8 * bucket + 2] == 0) {
			entries[8 * bucket + 2] = hash;
			entryCount++;
		} else if (entries[8 * bucket + 3] == 0) {
			entries[8 * bucket + 3] = hash;
			entryCount++;
		} else if (entries[8 * bucket + 4] == 0) {
			entries[8 * bucket + 4] = hash;
			entryCount++;
		} else if (entries[8 * bucket + 5] == 0) {
			entries[8 * bucket + 5] = hash;
			entryCount++;
		} else if (entries[8 * bucket + 6] == 0) {
			entries[8 * bucket + 6] = hash;
			entryCount++;
		} else if (entries[8 * bucket + 7] == 0) {
			entries[8 * bucket + 7] = hash;
			entryCount++;
		} else {
			collisionCount++;
		}
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
