package test;

import engine.Board;

/**
 * This class provides methods for assertion testing.
 * @author Tom Marvolo
 *
 */
public final class Assertions {

	/**
	 * This method calculates the advancement of the pieces on given board and compares the values with the
	 * incrementally updated ones from board.
	 * @param board  
	 * @return True if all incrementally updated advancement values are correct, false otherwise.
	 */
	public static boolean advancement(Board board) {
		int[] advancement = new int[7];
		for (int file = 0; file < 8; file++) {
			for (int row = 0; row < 8; row++) {
				advancement[Math.abs(board.getSquare(file, row))] += 2 * row - 7;
			}
		}
		boolean correctness = true;
		for (int piece = 1; piece < 7; piece++) {
			if (advancement[piece] != board.getPieceAdvancement(piece)) {
				correctness = false;
			}
		}
		return correctness;
	}
	
	/**
	 * This method calculates the current material balance on given board and compares that value with the 
	 * incrementally updated one from board.
	 * @param board 
	 * @return True if the material incrementally updated material balance is correct, false otherwise.
	 */
	public static boolean materialCount(Board board) {
		int material = 0;
		for (int file = 0; file < 8; file++) {
			for (int row = 0; row < 8; row++) {
				int square = board.getSquare(file, row);
				if (square >= 0) {
					material += Board.PIECEVALUE[square];
				} else {
					material -= Board.PIECEVALUE[-square];
				}
			}
		}
		return material == board.getMaterialCount();
	}
	
	private Assertions() {
	}
}
