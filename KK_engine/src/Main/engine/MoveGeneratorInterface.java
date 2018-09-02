package Main.engine;

import java.io.Serializable;

/**
 *
 */
public interface MoveGeneratorInterface extends Serializable {

	void resetMoveGenerator();

	int[] activityEval(boolean toMove, int[] storage, int[] whiteSize);

	int[] collectMoves(boolean toMove, int[] allMoves, int[] moveSize);
}
