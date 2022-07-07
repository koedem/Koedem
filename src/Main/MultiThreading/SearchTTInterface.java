package Main.MultiThreading;

import Main.engine.TTEntry;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface SearchTTInterface {

	void clearTT();
	TTEntry get(long zobristHashOne, TTEntry entry, int depth);
	void put(long zobristHash, @NotNull TTEntry entry);
	void printStats();
	long getFill();
}
