
public class MoveGenerator {

	private static int[] move = new int[100];
	
	public static int[] collectMoves(Board board, boolean toMove) {
		move[99] = 0;
		for (int i = 1; i < 9; i++) {
			for (int j = 1; j < 9; j++) {
				
				if (toMove) {
					if (board.square[i][j] == 2) {
						knightMove(i, j, board, toMove);
						continue;
					}
					else if (board.square[i][j] == 4) {
						rookMove(i, j, board, toMove);
						continue;
					}
				}
			}
		}
		
		return move;
	}
	
	public static void knightMove(int file, int row, Board board, boolean toMove) {
		if (file < 7 && row < 8) {
			if ((toMove && board.square[file + 2][row + 1] <= 0) || (!toMove) && board.square[file + 2][row + 1] >= 0) {
				move[move[99]] = 2 * 10000 + file * 1000 + row * 100 + (file + 2) * 10 + (row + 1);
				move[99]++;
			}
		} if (file < 8 && row < 7) {
			if ((toMove && board.square[file + 1][row + 2] <= 0) || (!toMove) && board.square[file + 1][row + 2] >= 0) {
				move[move[99]] = 2 * 10000 + file * 1000 + row * 100 + (file + 1) * 10 + (row + 2);
				move[99]++;
			}
		}
	}
	
	public static void rookMove(int file, int row, Board board, boolean toMove) {
		for (int i = 1; i < 8; i++) {
			if (isFreeSquare(file, (row + i), board, toMove)) {
				move[move[99]] = 4 * 10000 + file * 1000 + row * 100 + (file) * 10 + (row + i);
				move[99]++;
			} else{
				break;
			}
		}
		for (int i = 1; i < 8; i++) {
			if (isFreeSquare(file, (row - i), board, toMove)) {
				move[move[99]] = 4 * 10000 + file * 1000 + row * 100 + (file) * 10 + (row - i);
				move[99]++;
			} else{
				break;
			}
		}
	}
	
	public static boolean isFreeSquare(int file, int row, Board board, boolean toMove) {
		if (file < 1 || file > 8 || row < 1 || row > 8) {
			return false;
		}
		if (toMove) {
			if (board.getSquare(file, row) <= 0) {
				return true;
			}
		} else {
			if (board.getSquare(file, row) >= 0) {
				return true;
			}
		}
		return false;
	}
}
