package Main.MultiThreading;

import Main.engineIO.Logging;

public abstract class AbstractMatefinderTT implements TranspositionTableInterface {

    /**
     * Scheme: Table is split into cells of 8 longs, each cell is split into 4 entries, each entry consisting of a
     * Zobrist hash long and an information long made of 32 Bit move, 16 Bit depth, 16 Bit eval. TODO more bits for hashing
     * The hashing long gets XORed with the information to provide a race condition free access.
     */
    private long[] table;
    private int bitmask;

    /**
     *
     * @param sizeInByte power of 2.
     */
    public AbstractMatefinderTT(int sizeInByte) {
        assert Long.highestOneBit(sizeInByte) > Long.highestOneBit(sizeInByte - 1);
        table = new long[sizeInByte / 8];
        bitmask = sizeInByte / 64 - 1;
    }

    @Override
    public long get(long zobristHashOne, long zobristHashTwo) {
        long information;
        int position = (int) zobristHashOne & bitmask;
        for (int count = 0; count < 8; count += 2) {
            if ((table[position << 3 + count] ^ (information = table[position << 3 + count + 1])) == zobristHashOne) {
                return information;
            }
        }
        return 0;
    }

    @Override
    public void put(long zobristHash, long information) {
        int position = (int) zobristHash & bitmask;
        long oldInformation;
        for (int count = 0; count < 8; count += 2) {
            if (table[position << 3 + count] == 0) {
                table[position << 3 + count] = zobristHash ^ information;
                table[position << 3 + count + 1] = information;
                return;
            } else if ((table[position << 3 + count] ^ (oldInformation = table[position << 3 + count + 1])) == zobristHash) {
                if ((oldInformation & 0xFFFF) == 0 && (((information & 0xFFFF) != 0) || (oldInformation & 0xFFFF0000L) < (information & 0xFFFF0000L))) {
                    table[position << 3 + count] = zobristHash ^ information;
                    table[position << 3 + count + 1] = information;
                } else {
                    Logging.printLine("Weird mate finder replacement behavior. Probably hash collision or race condition.");
                }
                return;
            }
        }

        if ((table[position << 3 + 7] & 0xFFFFFFFFL) < (information & 0xFFFFFFFFL)) { // our entry is more valuable
            table[position << 3 + 6] = zobristHash ^ information;
            table[position << 3 + 7] = information;
        }
        for (int entry = 2; entry >= 0; entry--) {
            if ((table[position << 3 + 2 * entry + 1] & 0xFFFFFFFFL) < (information & 0xFFFFFFFFL)) {
                table[position << 3 + entry * 2 + 2] = table[position << 3 + entry * 2];
                table[position << 3 + entry * 2 + 3] = table[position << 3 + entry * 2 + 1];

                table[position << 3 + entry * 2] = zobristHash ^ information;
                table[position << 3 + entry * 2 + 1] = information;
            } else {
                break;
            }
        }
    }
}
