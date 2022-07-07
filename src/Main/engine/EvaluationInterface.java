package Main.engine;

import java.io.Serializable;

/**
 *
 */
public interface EvaluationInterface extends Serializable {

	void resetEvaluation();

	int evaluation(boolean toMove, int lowBound);

	int fullPST();
}
