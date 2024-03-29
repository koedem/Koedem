package Main.MultiThreading;

import Main.engine.TTEntry;
import Main.engineIO.Logging;
import org.jetbrains.annotations.NotNull;

public class SearchExactTT extends SearchTT {


    /**
     * @param sizeInByte power of 2.
     * @param lowBound
     */
    public SearchExactTT(long sizeInByte, boolean lowBound) {
        super(sizeInByte, lowBound);
    }


    @Override
    public TTEntry get(long zobristHashOne, TTEntry entry, int depth) {
        long depthHash = zobristHashOne + depth; // we include depth in the key to get exact depth matches when probing
        return super.get(depthHash, entry, depth); // use modified hash to look up normally
    }

    @Override
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

                if (oldEntry.getDepth() != entry.getDepth()) { // this should not happen since depth is stored in the hash key; unless some hash collision
                    Logging.printLine("Probably collision, storing position with wrong depth"); // TODO what do we do here?
                }
                assert  (lowBound ? oldEntry.getEval() < entry.getEval() : oldEntry.getEval() > entry.getEval())
                        || (oldEntry.getEval() == 0 && entry.getEval() == 0); // technically not correct but this would be in case of a stalemate

                table[(position << 3) + count] = depthHash ^ entry.getAllInformation();
                table[(position << 3) + count + 1] = entry.getAllInformation();
                ttImprovements++;
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
}
