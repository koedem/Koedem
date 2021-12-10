package Main.engine;

import Main.engineIO.Logging;
import Main.engineIO.UCI;

import java.util.Arrays;

/**
 *
 */
public class PerftTT_8 {

	private final int  size;
	private final long mask;
	private final int    FREQUENCY_CAP = 8;
	private final long[] entries;
	private final long[] limitHitsPerDepth = new long[10];

	private long entryCount = 0, dupeCount = 0, collisionCount = 0;

	public PerftTT_8(int sizeInMB) {
		size = (sizeInMB/ 8) * 1048576;
		mask = size / 8 - 1; // eight entries per bucket
		entries = new long[size];
	}

	public void putEmpty(long hash) {
		int bucket = (int) (hash & mask);
		if (entries[8 * bucket] == (hash & (~mask)) + 1 || entries[8 * bucket + 1] == (hash & (~mask)) + 1
		    || entries[8 * bucket + 2] == (hash & (~mask)) + 1 || entries[8 * bucket + 3] == (hash & (~mask)) + 1
		    || entries[8 * bucket + 4] == (hash & (~mask)) + 1 || entries[8 * bucket + 5] == (hash & (~mask)) + 1
		    || entries[8 * bucket + 6] == (hash & (~mask)) + 1 || entries[8 * bucket + 7] == (hash & (~mask)) + 1) {
			dupeCount++;
		} else if (entries[8 * bucket] == 0) {
			entries[8 * bucket] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 1] == 0) {
			entries[8 * bucket + 1] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 2] == 0) {
			entries[8 * bucket + 2] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 3] == 0) {
			entries[8 * bucket + 3] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 4] == 0) {
			entries[8 * bucket + 4] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 5] == 0) {
			entries[8 * bucket + 5] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 6] == 0) {
			entries[8 * bucket + 6] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 7] == 0) {
			entries[8 * bucket + 7] = (hash & (~mask)) + 1;
			entryCount++;
		} else {
			collisionCount++;
		}
	}

	public void incrementPosition(long hash, boolean ruined) {
		int bucket = (int) (hash & mask);
		for (int i = 0; i < 8; i++) {
			if ((entries[8 * bucket + i] & (~mask)) == (hash & (~mask))) {
				long count;
				if ((count = entries[8 * bucket + i] & mask) < mask) { // once we reach the mask, i.e. equals, we stop incrementing
					entries[8 * bucket + i] = (entries[8 * bucket + i] & (~mask)) + count + (ruined ? 2 : 1);
				}
				break;
			}
		}
	}

	public boolean isUnique(long hash) {
		int bucket = (int) (hash & mask);
		for (int i = 0; i < 8; i++) {
			if ((entries[8 * bucket + i] & (~mask)) == (hash & (~mask))) {
				if ((entries[8 * bucket + i] & mask) == 2) { // two means once created then once reached from higher depth
					return true;
				}
				break;
			}
		}
		return false;
	}

	public long incrementToLimit(long hash, int limit, int depth) {
		int bucket = (int) (hash & mask);
		for (int i = 0; i < 8; i++) {
			if ((entries[8 * bucket + i] & (~mask)) == (hash & (~mask))) {
				long count;
				if ((count = entries[8 * bucket + i] & mask) < limit) { // once we reach the limit, i.e. equals, we stop incrementing
					entries[8 * bucket + i] = (entries[8 * bucket + i] & (~mask)) + count + 1;
				} else {
					limitHitsPerDepth[depth]++;
				}
				dupeCount++;
				return count;
			}
		}

		if (entries[8 * bucket] == 0) {
			entries[8 * bucket] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 1] == 0) {
			entries[8 * bucket + 1] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 2] == 0) {
			entries[8 * bucket + 2] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 3] == 0) {
			entries[8 * bucket + 3] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 4] == 0) {
			entries[8 * bucket + 4] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 5] == 0) {
			entries[8 * bucket + 5] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 6] == 0) {
			entries[8 * bucket + 6] = (hash & (~mask)) + 1;
			entryCount++;
		} else if (entries[8 * bucket + 7] == 0) {
			entries[8 * bucket + 7] = (hash & (~mask)) + 1;
			entryCount++;
		} else {
			collisionCount++;
		}
		return 0;
	}

	public void printCounts() {
		Logging.printLine("Unique positions: " + entryCount + ", collision losses: " + collisionCount + ", remainder (duplicates): " + dupeCount
		                  + ", sum: " + (entryCount + collisionCount + dupeCount));
		Logging.printLine("Fill: " + String.format("%.2f", (double) entryCount * 100 / (double) size) + "%; "
		                  + "duplication factor: " + String.format("%.2f", (double) dupeCount / (double) entryCount) + "; "
		                  + "loss percentage: " + String.format("%.2f", (double) collisionCount * 100 / (double) entryCount) + "%.");

		for (int depth = 0; depth < 10; depth++) {
			if (limitHitsPerDepth[depth] > 0) {
				Logging.printLine("Depth " + depth + " hits: " + limitHitsPerDepth[depth]);
			}
		}
	}

	public void printFrequencies() {
		long[] frequencies = new long[FREQUENCY_CAP + 1];
		for (int i = 0; i < entries.length; i++) {
			int frequency = (int) (entries[i] & mask);
			if (frequency < FREQUENCY_CAP) {
				frequencies[frequency]++;
			} else {
				frequencies[FREQUENCY_CAP]++;
			}
		}
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > 0) {
				Logging.printLine("Frequency " + i + ": " + frequencies[i]);
			}
		}
	}

	public void reset() {
		Arrays.fill(entries, 0);
		Arrays.fill(limitHitsPerDepth, 0);
		entryCount = 0;
		dupeCount = 0;
		collisionCount = 0;
	}
}
