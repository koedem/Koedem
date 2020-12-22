package Main.MultiThreading;

import Main.engine.TTEntry;
import Main.engineIO.Logging;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SearchTT {

    /**
     * Scheme: Table is split into cells of 8 longs, each cell is split into 4 entries, each entry consisting of a
     * Zobrist hash long and an information long created in the SearchTT object.
     * The hashing long gets XORed with the information to provide a race condition free access.
     */
    private long[] table;
    private int bitmask;
    private boolean lowBound;

    private TTEntry oldEntry = new TTEntry();

    private long ttHits = 0;
    private long ttFill = 0;
    private long ttOverwrites = 0;
    private long ttImprovements = 0;

    private int pseudoRandom = 0;

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

    public void clearTT() {
        Arrays.fill(table, 0);
        oldEntry = new TTEntry();

        ttHits = 0;
        ttFill = 0;
        ttOverwrites = 0;
        ttImprovements = 0;

        pseudoRandom = 0;
    }

    public TTEntry get(long zobristHashOne, TTEntry entry, int depth) {
        long depthHash = zobristHashOne + depth;
        long information;
        int position = (int) depthHash & bitmask;
        for (int count = 0; count < 8; count += 2) {
            if ((table[(position << 3) + count] ^ (information = table[(position << 3) + count + 1])) == depthHash) {
                entry.setAllInformation(information);
                ttHits++;
                return entry;
            }
        }
        return null;
    }

    public void put(long zobristHash, @NotNull TTEntry entry) {
        long depthHash = zobristHash + entry.getDepth();
        int position = (int) depthHash & bitmask;
        long oldInformation;
        for (int count = 0; count < 8; count += 2) {
            if (table[(position << 3) + count] == 0) {
                table[(position << 3) + count] = depthHash ^ entry.getAllInformation();
                table[(position << 3) + count + 1] = entry.getAllInformation();
                ttFill++;
                return;
            } else if ((table[(position << 3) + count] ^ (oldInformation = table[(position << 3) + count + 1])) == depthHash) {
                oldEntry.setAllInformation(oldInformation);
                if (oldEntry.getDepth() <= entry.getDepth()) {
                    if (oldEntry.getDepth() < entry.getDepth()) {
                        Logging.printLine("Probably collision, storing position with wrong depth"); // TODO what do we do here?
                    }
                    if (lowBound ? oldEntry.getEval() < entry.getEval()
                                                                           : oldEntry.getEval() > entry.getEval()) {
                        // if we're storing lower bounds we want the eval to go up, otherwise we want it to go down
                        table[(position << 3) + count] = depthHash ^ entry.getAllInformation();
                        table[(position << 3) + count + 1] = entry.getAllInformation();
                        ttImprovements++;
                    }
                }
                return;
            }
        }

        pseudoRandom = (pseudoRandom + 1) % 4; // for now always replace some random entry
        table[(position << 3) + pseudoRandom * 2] = depthHash ^ entry.getAllInformation();
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

    public void printStats() {
        System.out.println("Entries: " + table.length / 2 + ", Fill: " + ttFill + ", Hits: " + ttHits
                           + ", Overwrites: " + ttOverwrites + ", Improvements: " + ttImprovements);
    }

    public long getFill() {
        return (2000 * ttFill) / table.length;
    }
}
