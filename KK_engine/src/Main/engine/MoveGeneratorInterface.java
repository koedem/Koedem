package Main.engine;

import java.io.Serializable;

/**
 *
 */
public interface MoveGeneratorInterface extends Serializable {

	void resetMoveGenerator();

	int[] activityEval(boolean toMove, int[] storage, int[] whiteSize);

	int[] collectCaptures(boolean toMove, int[] allCaptures);

	int[] collectMoves(boolean toMove, int[] allMoves, int[] moveSize);

	int[] collectAllPNMoves(int[] storage, BoardInterface board, boolean toMove);

	int[] collectCheckMoves(int[] storage, int[] checks, BoardInterface board, boolean toMove);

	int[] collectPNSearchMoves(int[] storage, int[] checks, BoardInterface board, boolean toMove);
}
