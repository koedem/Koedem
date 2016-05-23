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
		int advancement = 0;
		for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				if (board.square[i][j] != 0) {
					advancement += 2 * j - 7;
				}
			}
		}
		/*for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				if (board.square[i][j] == 1) {
					eval += 100;
				} else if (board.square[i][j] == 2) {
					eval += 300;
				} else if (board.square[i][j] == 3) {
					eval += 300;
				} else if (board.square[i][j] == 4) {
					eval += 500;
				} else if (board.square[i][j] == 5) {
					eval += 900;
				} else if (board.square[i][j] == 6) {
					eval += 10000;
				} else if (board.square[i][j] == -1) {
					eval -= 100;
				} else if (board.square[i][j] == -2) {
					eval -= 300;
				} else if (board.square[i][j] == -3) {
					eval -= 300;
				} else if (board.square[i][j] == -4) {
					eval -= 500;
				} else if (board.square[i][j] == -5) {
					eval -= 900;
				} else if (board.square[i][j] == -6) {
					eval -= 10000;
				}
			}
		}
		if (!toMove) {
			eval = (short) -eval;
		}
		eval += (Math.random() * 200) - 100;
		
		return eval;*/
		eval = board.getMaterialCount();
		eval += advancement;
		if (!toMove) {
			eval = (short) -eval;
		}
		//eval += (Math.random() * 100) - 50;
		Search.nodeCountPlusOne();
		return eval;
	}
	
	private Evaluation() {
	}
}
