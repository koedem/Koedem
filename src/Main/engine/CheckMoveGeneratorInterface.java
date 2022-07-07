package Main.engine;

import java.io.Serializable;

/**
 *
 */
public interface CheckMoveGeneratorInterface extends Serializable {

	void resetCheckMoveGenerator();

	int[] collectAllPNMoves(int[] storage, boolean toMove);

	int[] collectCheckMoves(int[] storage, int[] checks, boolean toMove);

	int[] collectPNSearchMoves(int[] storage, int[] checks, boolean toMove);
}
