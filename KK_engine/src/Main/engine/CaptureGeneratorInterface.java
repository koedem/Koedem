package Main.engine;

import java.io.Serializable;

/**
 *
 */
public interface CaptureGeneratorInterface extends Serializable {

	void resetCaptureGenerator();

	int[] collectCaptures(boolean toMove, int[] allCaptures);
}
