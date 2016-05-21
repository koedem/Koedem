package engine;

import java.util.ArrayList;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public final class MoveGenerator {

	/**
	 * 
	 * @param board : the board on which we generate moves
	 * @param toMove : who to move it is
	 * @return array of ints, each containing a move
	 */
	public static ArrayList<Short> collectMoves(Board board, boolean toMove) {
		ArrayList<Short> captures = new ArrayList<Short>(1);
		ArrayList<Short> nonCaptures = new ArrayList<Short>(10);
		for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				
				if (toMove) {
					if (board.square[i][j] == 1) {
						pawnMove(i, j, board, toMove, captures, nonCaptures);
						continue;
					} else if (board.square[i][j] == 2) {
						knightMove(i, j, board, toMove, captures, nonCaptures);
						continue;
					} else if (board.square[i][j] == 3) {
						bishopMove(i, j, board, toMove, captures, nonCaptures);
						continue;
					} else if (board.square[i][j] == 4) {
						rookMove(i, j, board, toMove, captures, nonCaptures);
						continue;
					} else if (board.square[i][j] == 5) { // queen moves like rook + bishop
						rookMove(i, j, board, toMove, captures, nonCaptures);
						bishopMove(i, j, board, toMove, captures, nonCaptures);
					} else if (board.square[i][j] == 6) {
						kingMove(i, j, board, toMove, captures, nonCaptures);
					}
				} else {
					if (board.square[i][j] == -1) {
						pawnMove(i, j, board, toMove, captures, nonCaptures);
						continue;
					} else if (board.square[i][j] == -2) {
						knightMove(i, j, board, toMove, captures, nonCaptures);
						continue;
					} else if (board.square[i][j] == -3) {
						bishopMove(i, j, board, toMove, captures, nonCaptures);
						continue;
					} else if (board.square[i][j] == -4) {
						rookMove(i, j, board, toMove, captures, nonCaptures);
						continue;
					} else if (board.square[i][j] == -5) { // queen moves like rook + bishop
						rookMove(i, j, board, toMove, captures, nonCaptures);
						bishopMove(i, j, board, toMove, captures, nonCaptures);
					} else if (board.square[i][j] == -6) {
						kingMove(i, j, board, toMove, captures, nonCaptures);
					}
				}
			}
		}
		captures.addAll(nonCaptures);
		return captures;
	}
	
	/**
	 * Generate pawn moves by checking if they can move and/or capture.
	 * TODO: Write en passant and pawn promotion.
	 * 
	 * @param file : position of the pawn on the board
	 * @param row : " "
	 * @param board : on which board we are
	 * @param toMove : who to move it is
	 */
	private static void pawnMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Short> captures, ArrayList<Short> nonCaptures) {
		if (toMove) {
			if (board.getSquare(file, row + 1) == 0) {
				nonCaptures.add((short) (1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				if (row == 1 && board.getSquare(file, row + 2) == 0) {
					nonCaptures.add((short) (1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 2)));
				}
			}
			if (file > 0 && board.getSquare(file - 1, row + 1) < 0) {
				captures.add((short) (1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
			}
			if (file < 7 && board.getSquare(file + 1, row + 1) < 0) {
				captures.add((short) (1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
			}
		} else if (!toMove) {
			if (board.getSquare(file, row - 1) == 0) {
				nonCaptures.add((short) (1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				if (row == 6 && board.getSquare(file, row - 2) == 0) {
					nonCaptures.add((short) (1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 2)));
				}
			}
			if (file > 0 && board.getSquare(file - 1, row - 1) > 0) {
				captures.add((short) (1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
			}
			if (file < 7 && board.getSquare(file + 1, row - 1) > 0) {
				captures.add((short) (1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
			}
		}
		
	}

	/**
	 * Generate all Knight-moves by checking which moves don't "capture" our own pieces
	 * and whether we still are within the bounds of the board.
	 * 
	 * @param file : position of the knight on the board
	 * @param row : " "
	 * @param board : on which board we are
	 * @param toMove : who to move it is
	 */
	private static void knightMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Short> captures, ArrayList<Short> nonCaptures) {
		if (file > 0 && row > 1) {
			if ((toMove && board.square[file - 1][row - 2] < 0) 
					|| ((!toMove) && board.square[file - 1][row - 2] > 0)) {
				
				captures.add((short) (2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
			} else if ((toMove && board.square[file - 1][row - 2] == 0) 
					|| ((!toMove) && board.square[file - 1][row - 2] == 0)) {
				
				nonCaptures.add((short) (2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
			}
			
		}
		if (file > 0 && row < 6) {
			if ((toMove && board.square[file - 1][row + 2] < 0) 
					|| ((!toMove) && board.square[file - 1][row + 2] > 0)) {
				
				captures.add((short) (2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
			} else if ((toMove && board.square[file - 1][row + 2] == 0) 
					|| ((!toMove) && board.square[file - 1][row + 2] == 0)) {
				
				nonCaptures.add((short) (2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
			}
			
		}
		if (file > 1 && row > 0) {
			if ((toMove && board.square[file - 2][row - 1] < 0) 
					|| ((!toMove) && board.square[file - 2][row - 1] > 0)) {
				
				captures.add((short) (2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
			} else if ((toMove && board.square[file - 2][row - 1] == 0) 
					|| ((!toMove) && board.square[file - 2][row - 1] == 0)) {
				
				nonCaptures.add((short) (2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
			}
			
		}
		if (file > 1 && row < 7) {
			if ((toMove && board.square[file - 2][row + 1] < 0) 
					|| ((!toMove) && board.square[file - 2][row + 1] > 0)) {
				
				captures.add((short) (2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
			} else if ((toMove && board.square[file - 2][row + 1] == 0) 
					|| ((!toMove) && board.square[file - 2][row + 1] == 0)) {
				
				nonCaptures.add((short) (2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
			}
			
		}
		if (file < 6 && row > 0) {
			if ((toMove && board.square[file + 2][row - 1] < 0) 
					|| ((!toMove) && board.square[file + 2][row - 1] > 0)) {
				
				captures.add((short) (2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
			} else if ((toMove && board.square[file + 2][row - 1] == 0) 
					|| ((!toMove) && board.square[file + 2][row - 1] == 0)) {
				
				nonCaptures.add((short) (2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
			}
		}
		if (file < 6 && row < 7) {
			if ((toMove && board.square[file + 2][row + 1] < 0) 
					|| ((!toMove) && board.square[file + 2][row + 1] > 0)) {
				
				captures.add((short) (2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
			} else if ((toMove && board.square[file + 2][row + 1] == 0) 
					|| ((!toMove) && board.square[file + 2][row + 1] == 0)) {
				
				nonCaptures.add((short) (2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
			}
		}
		if (file < 7 && row > 1) {
			if ((toMove && board.square[file + 1][row - 2] < 0) 
					|| ((!toMove) && board.square[file + 1][row - 2] > 0)) {
				
				captures.add((short) (2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
			} else if ((toMove && board.square[file + 1][row - 2] == 0) 
					|| ((!toMove) && board.square[file + 1][row - 2] == 0)) {
				
				nonCaptures.add((short) (2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
			}
		}
		if (file < 7 && row < 6) {
			if ((toMove && board.square[file + 1][row + 2] < 0) 
					|| ((!toMove) && board.square[file + 1][row + 2] > 0)) {
				
				captures.add((short) (2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
			} else if ((toMove && board.square[file + 1][row + 2] == 0) 
					|| ((!toMove) && board.square[file + 1][row + 2] == 0)) {
				
				nonCaptures.add((short) (2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
			}
		}
	}
	
	/**
	 * Generate all legal rook moves by checking how far we can move
	 * so that we still are on the board and don't move through pieces.
	 * 
	 * @param file : position of the rook on the board
	 * @param row : " "
	 * @param board : on which board we are
	 * @param toMove : who to move it is
	 */
	private static void rookMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Short> captures, ArrayList<Short> nonCaptures) {
		byte thisPiece = (byte) Math.abs(board.getSquare(file, row));
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), row, board, toMove);
			if (squareValue == 0) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), row, board, toMove);
			if (squareValue == 0) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
			} else {
				break;
			}
		}
	}
	
	/**
	 * Generate all legal bishop moves by checking how far we can move
	 * so that we are still on the board and don't move through pieces.
	 * 
	 * @param file : position of the bishop on the board
	 * @param row : " "
	 * @param board : on which board we are
	 * @param toMove : who to move it is
	 */
	private static void bishopMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Short> captures, ArrayList<Short> nonCaptures) {
		byte thisPiece = (byte) Math.abs(board.getSquare(file, row));
		for (byte i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				break;
			} else if (squareValue == 1) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				captures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((short) (thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
			} else {
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param file : position of the king on the board
	 * @param row : " "
	 * @param board : on which board we are
	 * @param toMove : who to move it is
	 */
	private static void kingMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Short> captures, ArrayList<Short> nonCaptures) {
		if (file > 0) {
			if ((toMove && board.getSquare(file - 1, row) < 0) || (!toMove && board.getSquare(file - 1, row) > 0)) {
				captures.add((short) (6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
			} else if ((toMove && board.getSquare(file - 1, row) == 0) 
					|| (!toMove && board.getSquare(file - 1, row) == 0)) {
				
				nonCaptures.add((short) (6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
			}
			if (row > 0) {
				if ((toMove && board.getSquare(file - 1, row - 1) < 0) 
						|| (!toMove && board.getSquare(file - 1, row - 1) > 0)) {
					
					captures.add((short) (6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if ((toMove && board.getSquare(file - 1, row - 1) == 0) 
						|| (!toMove && board.getSquare(file - 1, row - 1) == 0)) {
					
					nonCaptures.add((short) (6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				}
			}
			if (row < 7) {
				if ((toMove && board.getSquare(file - 1, row + 1) < 0) 
						|| (!toMove && board.getSquare(file - 1, row + 1) > 0)) {
					
					captures.add((short) (6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if ((toMove && board.getSquare(file - 1, row + 1) == 0) 
						|| (!toMove && board.getSquare(file - 1, row + 1) == 0)) {
					
					nonCaptures.add((short) (6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				}
			}
		}
		
		if (file < 7) {
			if ((toMove && board.getSquare(file + 1, row) < 0) || (!toMove && board.getSquare(file + 1, row) > 0)) {
				captures.add((short) (6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
			} else if ((toMove && board.getSquare(file + 1, row) == 0) 
					|| (!toMove && board.getSquare(file + 1, row) == 0)) {
				
				nonCaptures.add((short) (6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
			}
			if (row > 0) {
				if ((toMove && board.getSquare(file + 1, row - 1) < 0) 
						|| (!toMove && board.getSquare(file + 1, row - 1) > 0)) {
					
					captures.add((short) (6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if ((toMove && board.getSquare(file + 1, row - 1) == 0) 
						|| (!toMove && board.getSquare(file + 1, row - 1) == 0)) {
					
					nonCaptures.add((short) (6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				}
			}
			if (row < 7) {
				if ((toMove && board.getSquare(file + 1, row + 1) < 0) 
						|| (!toMove && board.getSquare(file + 1, row + 1) > 0)) {
					
					captures.add((short) (6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if ((toMove && board.getSquare(file + 1, row + 1) == 0) 
						|| (!toMove && board.getSquare(file + 1, row + 1) == 0)) {
					
					nonCaptures.add((short) (6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				}
			}
		}
		
		if (row > 0) {
			if ((toMove && board.getSquare(file, row - 1) < 0) || (!toMove && board.getSquare(file, row - 1) > 0)) {
				captures.add((short) (6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
			} else if ((toMove && board.getSquare(file, row - 1) == 0) 
					|| (!toMove && board.getSquare(file, row - 1) == 0)) {
				
				nonCaptures.add((short) (6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
			}
		}
		if (row < 7) {
			if ((toMove && board.getSquare(file, row + 1) < 0) || (!toMove && board.getSquare(file, row + 1) > 0)) {
				captures.add((short) (6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
			} else if ((toMove && board.getSquare(file, row + 1) == 0) 
					|| (!toMove && board.getSquare(file, row + 1) == 0)) {
				
				nonCaptures.add((short) (6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
			}
		}
		
		//if (toMove && file == 4 && row == 0) {
			// TODO: castling
			
		//}
	}
	
	/**
	 * 
	 * @param file : position of the to be checked square is
	 * @param row : " "
	 * @param board : on which board the to be checked square it is
	 * @param toMove : who to move it is
	 * @return whether the square is free or not
	 */
	private static byte isFreeSquare(byte file, byte row, Board board, boolean toMove) {
		if (file < 0 || file > 7 || row < 0 || row > 7) {
			return -1;
		}
		byte squareValue = board.getSquare(file, row);
		if (toMove) {
			if (squareValue == 0) {
				return 1;
			} else if (squareValue < 0) {
				return 0;
			}
		} else {
			if (squareValue == 0) {
				return 1;
			} else if (squareValue > 0) {
				return 0;
			}
		}
		return -1;
	}
	
	private MoveGenerator() {
	}
}
