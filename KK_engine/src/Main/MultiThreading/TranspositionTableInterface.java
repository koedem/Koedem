package Main.MultiThreading;

public interface TranspositionTableInterface {

    /**
     * Hash table get operation with up to 128 bit hash key depending on implementation.
     * @param zobristHashOne first part of the hash key.
     * @param zobristHashTwo second part of the hash key, usually also contains the entries information, depending on
     *                       the implementation this will not be used for hashing.
     * @return the information associated with the given hash key, 0 if not available.
     */
    long get(long zobristHashOne, long zobristHashTwo);

    void put(long zobristHash, long information);
}
