package Main.MultiThreading;

import Main.engine.BoardInterface;
import Main.engine.MateTTEntry;

public interface TranspositionTableInterface {

    /**
     * Hash table get operation with up to 128 bit hash key depending on implementation.
     * @param zobristHash first part of the hash key.
     * @return the information associated with the given hash key, 0 if not available.
     */
    MateTTEntry get(long zobristHash, MateTTEntry entry);

    void put(long zobristHash, MateTTEntry newEntry);

    void printPV(BoardInterface board);
}
