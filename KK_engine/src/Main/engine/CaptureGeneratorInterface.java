package Main.engine;

import java.io.Serializable;

/**
 *
 */
public interface CaptureGeneratorInterface extends Serializable {

	void resetCaptureGenerator();

	int[] collectShootingMoves(boolean whoToMove, int[] shootingMoves);

	int[] collectCaptures(boolean toMove, int[] allCaptures);
}
