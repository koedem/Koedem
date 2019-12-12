package Main.MultiThreading;

import Main.engine.Board;
import Main.engine.BoardInterface;
import Main.engine.MateTTEntry;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;

public abstract class AbstractMatefinderTT implements TranspositionTableInterface {

    /**
     * Scheme: Table is split into cells of 8 longs, each cell is split into 4 entries, each entry consisting of a
     * Zobrist hash long and an information long made of 32 Bit move, 16 Bit depth, 16 Bit eval. TODO more bits for hashing
     * The hashing long gets XORed with the information to provide a race condition free access.
     */
    private long[] table;
    private int bitmask;

    private MateTTEntry probingEntry = new MateTTEntry();

    /**
     *
     * @param sizeInByte power of 2.
     */
    public AbstractMatefinderTT(long sizeInByte) {
        assert Long.highestOneBit(sizeInByte) > Long.highestOneBit(sizeInByte - 1);
        int entries = (int) (sizeInByte / 8);
        table = new long[entries];
        bitmask = entries / 8 - 1;
    }

    @Override
    public MateTTEntry get(long zobristHash, MateTTEntry entry) {
        long information;
        int position = (int) zobristHash & bitmask;
        for (int count = 0; count < 8; count += 2) {
            if ((table[(position << 3) + count] ^ (information = table[(position << 3) + count + 1])) == zobristHash) {
                entry.setAllInformation(information);
                return entry;
            }
        }
        return null;
    }

    @Override
    public void put(long zobristHash, MateTTEntry newEntry) {
        int position = (int) zobristHash & bitmask;
        MateTTEntry oldEntry = get(zobristHash, probingEntry);
        if (oldEntry != null) { // we already have this position so we merge the two to get the new entry
            newEntry.merge(oldEntry);
        }
        long information = newEntry.getAllInformation();

        for (int count = 0; count < 8; count += 2) {
            if (table[(position << 3) + count] == 0 || (table[(position << 3) + count] ^ table[(position << 3) + count + 1]) == zobristHash) {
                table[(position << 3) + count] = zobristHash ^ information;
                table[(position << 3) + count + 1] = information;
                break;
            }
        }
    }

    public void printPV(BoardInterface board) {
        BoardInterface copy = board.cloneBoard();
        MateTTEntry entry = new MateTTEntry();
        while (ThreadOrganization.globalMateTT.get(copy.getZobristHash(), entry) != null && entry.getMove() != 0) {
            Logging.printLine(Transformation.numberToMove(entry.getMove()));
            copy.makeMove(entry.getMove());
        }
    }
}
