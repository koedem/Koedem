package Main.engine;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 */
public interface SearchInterface extends Serializable {

	void resetSearch();

	void incrementAbortedNodes();

	void incrementNodes();

	void setNodes(long nodes);

	void setAbortedNodes(long abortedNodes);

	void setQNodes(long qNodes);

	long getNodes();

	int[] rootMax(boolean toMove, int depth, long time, long maxTime);

	int[] negaMax(boolean toMove, int depth, int depthLeft, int alpha, int beta, long finishUntil);

	int[] openWindowDepthOneSearch(boolean toMove, int depth, int alphaBound, int betaBound);

	int nullWindowSearch(boolean toMove, int depth, int depthLeft, int scoreToBeat, long finishUntil);

	long getAbortedNodes();

	long getQNodes();

	ArrayList<Integer> qSearch(boolean toMove, int alpha, int beta, int depthSoFar);

	int memoryEfficientQSearch(boolean toMove, int alpha, int beta, int depthSoFar);

	long getExactNodes();
}
