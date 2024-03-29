package Main.MultiThreading;

import Main.engine.TTEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static Main.engineIO.UCI.MAX_DEPTH;

/**
 *
 */
public class SearchTT implements SearchTTInterface {

	/**
	 * Scheme: Table is split into cells of 8 longs, each cell is split into 4 entries, each entry consisting of a
	 * Zobrist hash long and an information long created in the SearchTT object.
	 * The hashing long gets XORed with the information to provide a race condition free access.
	 */
	protected long[] table;
	protected int bitmask;
	protected boolean lowBound;

	protected TTEntry oldEntry = new TTEntry();

	protected long ttHits = 0;
	protected long ttFill = 0;
	protected long ttOverwrites = 0;
	protected long ttImprovements = 0;

	protected int pseudoRandom = 0;

	/**
	 *
	 * @param sizeInByte power of 2.
	 */
	public SearchTT(long sizeInByte, boolean lowBound) {
		int size = (int) (sizeInByte / 8L);
		assert size >= 0 && Integer.highestOneBit(size) > Integer.highestOneBit(size - 1); // make sure it's a power of 2
		table = new long[size];
		bitmask = size / 8 - 1;
		this.lowBound = lowBound;
		//ByteBuffer.allocateDirect(size); // TODO wip
	}

	@Override
	public void clearTT() {
		Arrays.fill(table, 0);
		oldEntry = new TTEntry();

		ttHits = 0;
		ttFill = 0;
		ttOverwrites = 0;
		ttImprovements = 0;

		pseudoRandom = 0;
	}

	@Override
	public TTEntry get(long zobristHashOne, TTEntry entry, int depth) {
		long information;
		int position = (int) zobristHashOne & bitmask;
		for (int count = 0; count < 8; count += 2) {
			if ((table[(position << 3) + count] ^ (information = table[(position << 3) + count + 1])) == zobristHashOne) {
				entry.setAllInformation(information);
				ttHits++;
				return entry;
			}
		}
		return null;
	}

	@Override
	public void put(long zobristHash, @NotNull TTEntry entry) {
		int position = (int) zobristHash & bitmask;
		if (Math.abs(entry.getEval()) > 9000) {
			entry.setDepth(MAX_DEPTH);
		}
		long oldInformation;
		for (int count = 0; count < 8; count += 2) {
			if (table[(position << 3) + count] == 0) { // entry free, we can just put new entry there and return
				table[(position << 3) + count] = zobristHash ^ entry.getAllInformation();
				table[(position << 3) + count + 1] = entry.getAllInformation();
				ttFill++;
				return;
			} else if ((table[(position << 3) + count] ^ (oldInformation = table[(position << 3) + count + 1])) == zobristHash) {
				oldEntry.setAllInformation(oldInformation);
				//if (oldEntry.getDepth() <= entry.getDepth()) { // version 3 is disabling this
					//if (lowBound ? oldEntry.getEval() < entry.getEval() // version 2 is disabling this
					  //           : oldEntry.getEval() > entry.getEval()) {
						// if we're storing lower bounds we want the eval to go up, otherwise we want it to go down TODO if higher depth we might want to replace anyway
						table[(position << 3) + count] = zobristHash ^ entry.getAllInformation();
						table[(position << 3) + count + 1] = entry.getAllInformation();
						ttImprovements++;
					//}
				//}
				return;
			}
		}

		pseudoRandom = (pseudoRandom + 1) % 4; // for now always replace some random entry
		table[(position << 3) + pseudoRandom * 2] = zobristHash ^ entry.getAllInformation();
		table[(position << 3) + pseudoRandom * 2 + 1] = entry.getAllInformation();
		ttOverwrites++;

        /*if ((table[(position << 3) + 7] & 0xFFFFFFFFL) < (information & 0xFFFFFFFFL)) { // our entry is more valuable
            table[(position << 3) + 6] = depthHash ^ information;
            table[(position << 3) + 7] = information;
        }
        for (int entry = 2; entry >= 0; entry--) {
            if ((table[(position << 3) + 2 * entry + 1] & 0xFFFFFFFFL) < (information & 0xFFFFFFFFL)) {
                table[(position << 3) + entry * 2 + 2] = table[(position << 3) + entry * 2];
                table[(position << 3) + entry * 2 + 3] = table[(position << 3) + entry * 2 + 1];

                table[(position << 3) + entry * 2] = depthHash ^ information;
                table[(position << 3) + entry * 2 + 1] = information;
            } else {
                break;
            }
        }*/
	}

	@Override
	public void printStats() {
		System.out.println("Entries: " + table.length / 2 + ", Fill: " + ttFill + ", Hits: " + ttHits
		                   + ", Overwrites: " + ttOverwrites + ", Improvements: " + ttImprovements);
	}

	@Override
	public long getFill() {
		return (2000 * ttFill) / table.length;
	}
}
