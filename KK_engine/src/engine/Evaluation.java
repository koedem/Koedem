package engine;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public final class Evaluation {

	/**
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @return evaluation based on material
	 */
	public static int evaluation(Board board, boolean toMove) {
		int eval = 0;
		for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				if (board.square[i][j] == 1) {
					eval += 1000;
				} else if (board.square[i][j] == 2) {
					eval += 3000;
				} else if (board.square[i][j] == 3) {
					eval += 3000;
				} else if (board.square[i][j] == 4) {
					eval += 5000;
				} else if (board.square[i][j] == 5) {
					eval += 9000;
				} else if (board.square[i][j] == 6) {
					eval += 100000;
				} else if (board.square[i][j] == -1) {
					eval -= 1000;
				} else if (board.square[i][j] == -2) {
					eval -= 3000;
				} else if (board.square[i][j] == -3) {
					eval -= 3000;
				} else if (board.square[i][j] == -4) {
					eval -= 5000;
				} else if (board.square[i][j] == -5) {
					eval -= 9000;
				} else if (board.square[i][j] == -6) {
					eval -= 100000;
				}
			}
		}
		if (!toMove) {
			eval = -eval;
		}
		eval += (Math.random() * 2000) - 1000;
		
		return eval;
	}
	
	private Evaluation() {
	}
}
