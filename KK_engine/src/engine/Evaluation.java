package engine;

import engineIO.Logging;
import engineIO.UCI;
import test.Assertions;

/**
 * 
 * @author Anon
 *
 */
public final class Evaluation {

	/**
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @param lowBound Score that the player is guaranteed. If material eval is far below this, cutoff.
	 * @return evaluation based on material
	 */
	public static int evaluation(Board board, boolean toMove, int lowBound) {

		assert Assertions.advancement(board);
		assert Assertions.materialCount(board);
		//assert Evaluation.correctBitBoard(board);
        if (!Evaluation.correctBitBoard(board)) {
            Logging.printLine("BitBoard-Error.");
            System.exit(1);
        }
		int eval = board.getMaterialCount();
		eval += pieceSquareTables(board);

		if (!toMove) {
			eval = (short) -eval;
		}
		board.nodes++;
		return eval;
	}
	
	private static int pieceSquareTables(Board board) {
		long kingSquare = board.bitboard.getBitBoard(1, 6, 0);
		if ((kingSquare & 0x0000001818000000L) != 0) {
			return 0;
		} else
		if ((kingSquare & 0x00003B24243B0000L) != 0) {
			return 1;
		} else
		if ((kingSquare & 0x007E424242427E00L) != 0) {
			return 2;
		} else {
			return 3;
		}
	}
	
	private Evaluation() {
	}

	private static boolean correctBitBoard(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getSquare(i, j) != board.bitboard.getSquare(i * 8 + j)) {
                    Logging.printLine("BitBoard wrong." + i + " " + j);
                    board.printBoard();
                    board.bitboard.printBitBoard();
                    return false;
                }
            }
        }
		return true;
	}
}