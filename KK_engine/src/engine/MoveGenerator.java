package engine;

import java.util.ArrayList;

/**
 * 
 * @author Anon
 *
 */
public final class MoveGenerator {

	/**
	 * 
	 * @param board : the board on which we generate moves
	 * @param toMove : who to move it is
	 * @return array of ints, each containing a move
	 */
	public static ArrayList<Integer> collectMoves(Board board, boolean toMove) {
		ArrayList<Integer> captures = new ArrayList<Integer>(1); // save all Qs getting captured
		ArrayList<Integer> captureR = new ArrayList<Integer>(1); // save all Rs getting captured
		ArrayList<Integer> captureB = new ArrayList<Integer>(1);
		ArrayList<Integer> captureN = new ArrayList<Integer>(1);
		ArrayList<Integer> captureP = new ArrayList<Integer>(1);
		ArrayList<Integer> nonCaptures = new ArrayList<Integer>(10);
		for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				
				if (toMove) {
					if (board.square[i][j] == 1) {
						pawnMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						continue;
					} else if (board.square[i][j] == 2) {
						knightMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						continue;
					} else if (board.square[i][j] == 3) {
						bishopMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						continue;
					} else if (board.square[i][j] == 4) {
						rookMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						continue;
					} else if (board.square[i][j] == 5) { // queen moves like rook + bishop
						rookMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						bishopMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
					} else if (board.square[i][j] == 6) {
						kingMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
					}
				} else {
					if (board.square[i][j] == -1) {
						pawnMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						continue;
					} else if (board.square[i][j] == -2) {
						knightMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						continue;
					} else if (board.square[i][j] == -3) {
						bishopMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						continue;
					} else if (board.square[i][j] == -4) {
						rookMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						continue;
					} else if (board.square[i][j] == -5) { // queen moves like rook + bishop
						rookMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
						bishopMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
					} else if (board.square[i][j] == -6) {
						kingMove(i, j, board, toMove, captures, captureR, captureB, captureN, captureP, nonCaptures);
					}
				}
			}
		}
		captures.addAll(captureR);
		captures.addAll(captureB);
		captures.addAll(captureN);
		captures.addAll(captureP);
		captures.addAll(nonCaptures);
		return captures;
	}
	
	/**
	 * 
	 * @param board : The board on which captures get generated.
	 * @param toMove : Who to move it is.
	 * @return ArrayList of Integers, containing all captures.
	 */
	public static ArrayList<Integer> collectCaptures(Board board, boolean toMove) {
		ArrayList<Integer> captures = new ArrayList<Integer>(1); // save all Qs getting captured
		ArrayList<Integer> captureR = new ArrayList<Integer>(1); // save all Rs getting captured
		ArrayList<Integer> captureB = new ArrayList<Integer>(1);
		ArrayList<Integer> captureN = new ArrayList<Integer>(1);
		ArrayList<Integer> captureP = new ArrayList<Integer>(1);
		
		for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				
				if (toMove) {
					if (board.square[i][j] == 1) {
						pawnCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						continue;
					} else if (board.square[i][j] == 2) {
						knightCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						continue;
					} else if (board.square[i][j] == 3) {
						bishopCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						continue;
					} else if (board.square[i][j] == 4) {
						rookCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						continue;
					} else if (board.square[i][j] == 5) { // queen moves like rook + bishop
						rookCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						bishopCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == 6) {
						kingCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					}
				} else {
					if (board.square[i][j] == -1) {
						pawnCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						continue;
					} else if (board.square[i][j] == -2) {
						knightCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						continue;
					} else if (board.square[i][j] == -3) {
						bishopCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						continue;
					} else if (board.square[i][j] == -4) {
						rookCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						continue;
					} else if (board.square[i][j] == -5) { // queen moves like rook + bishop
						rookCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						bishopCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == -6) {
						kingCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					}
				}
			}
		}
		if (captures.size() > 0 && captures.get(0) == -1) {
			captures.clear();
			captures.add(-1);
			return captures;
		}
		captures.addAll(captureR);
		captures.addAll(captureB);
		captures.addAll(captureN);
		captures.addAll(captureP);
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
	 * @param nonCaptures2 
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 */
	private static void pawnMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP, ArrayList<Integer> nonCaptures) {
		if (toMove) {
			if (board.getSquare(file, row + 1) == 0) {
				nonCaptures.add((1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				if (row == 1 && board.getSquare(file, row + 2) == 0) {
					nonCaptures.add((1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 2)));
				}
			}
			if (file > 0 && board.getSquare(file - 1, row + 1) < 0) {
				if (board.getSquare(file - 1, row + 1) == -6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (board.getSquare(file - 1, row + 1) == -5) {
					captures.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if (board.getSquare(file - 1, row + 1) == -4) {
					captureR.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if (board.getSquare(file - 1, row + 1) == -3) {
					captureB.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if (board.getSquare(file - 1, row + 1) == -2) {
					captureN.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if (board.getSquare(file - 1, row + 1) == -1) {
					captureP.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				}
			}
			if (file < 7 && board.getSquare(file + 1, row + 1) < 0) {
				if (board.getSquare(file + 1, row + 1) == -6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (board.getSquare(file + 1, row + 1) == -5) {
					captures.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if (board.getSquare(file + 1, row + 1) == -4) {
					captureR.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if (board.getSquare(file + 1, row + 1) == -3) {
					captureB.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if (board.getSquare(file + 1, row + 1) == -2) {
					captureN.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if (board.getSquare(file + 1, row + 1) == -1) {
					captureP.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				}
			}
		} else if (!toMove) {
			if (board.getSquare(file, row - 1) == 0) {
				nonCaptures.add((1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				if (row == 6 && board.getSquare(file, row - 2) == 0) {
					nonCaptures.add((1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 2)));
				}
			}
			if (file > 0 && board.getSquare(file - 1, row - 1) > 0) {
				if (board.getSquare(file - 1, row - 1) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (board.getSquare(file - 1, row - 1) == 5) {
					captures.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if (board.getSquare(file - 1, row - 1) == 4) {
					captureR.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if (board.getSquare(file - 1, row - 1) == 3) {
					captureB.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if (board.getSquare(file - 1, row - 1) == 2) {
					captureN.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if (board.getSquare(file - 1, row - 1) == 1) {
					captureP.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				}
			}
			if (file < 7 && board.getSquare(file + 1, row - 1) > 0) {
				if (board.getSquare(file + 1, row - 1) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (board.getSquare(file + 1, row - 1) == 5) {
					captures.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if (board.getSquare(file + 1, row - 1) == 4) {
					captureR.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if (board.getSquare(file + 1, row - 1) == 3) {
					captureB.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if (board.getSquare(file + 1, row - 1) == 2) {
					captureN.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if (board.getSquare(file + 1, row - 1) == 1) {
					captureP.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				}
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
	 * @param nonCaptures2 
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 */
	private static void knightMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB, ArrayList<Integer> captureN, ArrayList<Integer> captureP, ArrayList<Integer> nonCaptures) {
		if (file > 0 && row > 1) {
			if ((toMove && board.square[file - 1][row - 2] < 0) 
					|| ((!toMove) && board.square[file - 1][row - 2] > 0)) {
				
				if (Math.abs(board.square[file - 1][row - 2]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file - 1][row - 2]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				}
			} else if ((toMove && board.square[file - 1][row - 2] == 0) 
					|| ((!toMove) && board.square[file - 1][row - 2] == 0)) {
				
				nonCaptures.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
			}
			
		}
		if (file > 0 && row < 6) {
			if ((toMove && board.square[file - 1][row + 2] < 0) 
					|| ((!toMove) && board.square[file - 1][row + 2] > 0)) {
				
				if (Math.abs(board.square[file - 1][row + 2]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file - 1][row + 2]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				}
			} else if ((toMove && board.square[file - 1][row + 2] == 0) 
					|| ((!toMove) && board.square[file - 1][row + 2] == 0)) {
				
				nonCaptures.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
			}
			
		}
		if (file > 1 && row > 0) {
			if ((toMove && board.square[file - 2][row - 1] < 0) 
					|| ((!toMove) && board.square[file - 2][row - 1] > 0)) {
				
				if (Math.abs(board.square[file - 2][row - 1]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file - 2][row - 1]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				}
			} else if ((toMove && board.square[file - 2][row - 1] == 0) 
					|| ((!toMove) && board.square[file - 2][row - 1] == 0)) {
				
				nonCaptures.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
			}
			
		}
		if (file > 1 && row < 7) {
			if ((toMove && board.square[file - 2][row + 1] < 0) 
					|| ((!toMove) && board.square[file - 2][row + 1] > 0)) {
				
				if (Math.abs(board.square[file - 2][row + 1]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file - 2][row + 1]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				}
			} else if ((toMove && board.square[file - 2][row + 1] == 0) 
					|| ((!toMove) && board.square[file - 2][row + 1] == 0)) {
				
				nonCaptures.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
			}
			
		}
		if (file < 6 && row > 0) {
			if ((toMove && board.square[file + 2][row - 1] < 0) 
					|| ((!toMove) && board.square[file + 2][row - 1] > 0)) {
				
				if (Math.abs(board.square[file + 2][row - 1]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file + 2][row - 1]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				}
			} else if ((toMove && board.square[file + 2][row - 1] == 0) 
					|| ((!toMove) && board.square[file + 2][row - 1] == 0)) {
				
				nonCaptures.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
			}
		}
		if (file < 6 && row < 7) {
			if ((toMove && board.square[file + 2][row + 1] < 0) 
					|| ((!toMove) && board.square[file + 2][row + 1] > 0)) {
				
				if (Math.abs(board.square[file + 2][row + 1]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file + 2][row + 1]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				}
			} else if ((toMove && board.square[file + 2][row + 1] == 0) 
					|| ((!toMove) && board.square[file + 2][row + 1] == 0)) {
				
				nonCaptures.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
			}
		}
		if (file < 7 && row > 1) {
			if ((toMove && board.square[file + 1][row - 2] < 0) 
					|| ((!toMove) && board.square[file + 1][row - 2] > 0)) {
				
				if (Math.abs(board.square[file + 1][row - 2]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file + 1][row - 2]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				}
			} else if ((toMove && board.square[file + 1][row - 2] == 0) 
					|| ((!toMove) && board.square[file + 1][row - 2] == 0)) {
				
				nonCaptures.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
			}
		}
		if (file < 7 && row < 6) {
			if ((toMove && board.square[file + 1][row + 2] < 0) 
					|| ((!toMove) && board.square[file + 1][row + 2] > 0)) {
				
				if (Math.abs(board.square[file + 1][row + 2]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file + 1][row + 2]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				}
			} else if ((toMove && board.square[file + 1][row + 2] == 0) 
					|| ((!toMove) && board.square[file + 1][row + 2] == 0)) {
				
				nonCaptures.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
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
	 * @param nonCaptures2 
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 */
	private static void rookMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP, ArrayList<Integer> nonCaptures) {
		byte thisPiece = (byte) Math.abs(board.getSquare(file, row));
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), row, board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file + i][row]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				} else if (Math.abs(board.square[file + i][row]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				} else if (Math.abs(board.square[file + i][row]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				} else if (Math.abs(board.square[file + i][row]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				} else if (Math.abs(board.square[file + i][row]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), row, board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file - i][row]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				} else if (Math.abs(board.square[file - i][row]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				} else if (Math.abs(board.square[file - i][row]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				} else if (Math.abs(board.square[file - i][row]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				} else if (Math.abs(board.square[file - i][row]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file][row + i]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file][row + i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				} else if (Math.abs(board.square[file][row + i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				} else if (Math.abs(board.square[file][row + i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				} else if (Math.abs(board.square[file][row + i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				} else if (Math.abs(board.square[file][row + i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file][row - i]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file][row - i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				} else if (Math.abs(board.square[file][row - i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				} else if (Math.abs(board.square[file][row - i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				} else if (Math.abs(board.square[file][row - i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				} else if (Math.abs(board.square[file][row - i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
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
	 * @param nonCaptures2 
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 */
	private static void bishopMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP, ArrayList<Integer> nonCaptures) {
		byte thisPiece = (byte) Math.abs(board.getSquare(file, row));
		for (byte i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row + i]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file + i][row + i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file + i][row + i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file + i][row + i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file + i][row + i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file + i][row + i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row - i]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file - i][row - i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file - i][row - i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file - i][row - i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file - i][row - i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file - i][row - i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row - i]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file + i][row - i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file + i][row - i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file + i][row - i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file + i][row - i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file + i][row - i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row + i]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file - i][row + i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file - i][row + i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file - i][row + i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file - i][row + i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file - i][row + i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
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
	 * @param nonCaptures2 
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 */
	private static void kingMove(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP, ArrayList<Integer> nonCaptures) {
		if (file > 0) {
			if ((toMove && board.getSquare(file - 1, row) < 0) || (!toMove && board.getSquare(file - 1, row) > 0)) {
				if (Math.abs(board.square[file - 1][row]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file - 1][row]) == 5) {
					captures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				} else if (Math.abs(board.square[file - 1][row]) == 4) {
					captureR.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				} else if (Math.abs(board.square[file - 1][row]) == 3) {
					captureB.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				} else if (Math.abs(board.square[file - 1][row]) == 2) {
					captureN.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				} else if (Math.abs(board.square[file - 1][row]) == 1) {
					captureP.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				}
			} else if ((toMove && board.getSquare(file - 1, row) == 0) 
					|| (!toMove && board.getSquare(file - 1, row) == 0)) {
				
				nonCaptures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
			}
			if (row > 0) {
				if ((toMove && board.getSquare(file - 1, row - 1) < 0) 
						|| (!toMove && board.getSquare(file - 1, row - 1) > 0)) {
					
					if (Math.abs(board.square[file - 1][row - 1]) == 6) {
						//captures.clear();
						captures.add(-1);
						captures.set(0, -1);
						return;
					} else if (Math.abs(board.square[file - 1][row - 1]) == 5) {
						captures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 4) {
						captureR.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 3) {
						captureB.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 2) {
						captureN.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 1) {
						captureP.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					}
				} else if ((toMove && board.getSquare(file - 1, row - 1) == 0) 
						|| (!toMove && board.getSquare(file - 1, row - 1) == 0)) {
					
					nonCaptures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				}
			}
			if (row < 7) {
				if ((toMove && board.getSquare(file - 1, row + 1) < 0) 
						|| (!toMove && board.getSquare(file - 1, row + 1) > 0)) {
					
					if (Math.abs(board.square[file - 1][row + 1]) == 6) {
						//captures.clear();
						captures.add(-1);
						captures.set(0, -1);
						return;
					} else if (Math.abs(board.square[file - 1][row + 1]) == 5) {
						captures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 4) {
						captureR.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 3) {
						captureB.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 2) {
						captureN.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 1) {
						captureP.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					}
				} else if ((toMove && board.getSquare(file - 1, row + 1) == 0) 
						|| (!toMove && board.getSquare(file - 1, row + 1) == 0)) {
					
					nonCaptures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				}
			}
		}
		
		if (file < 7) {
			if ((toMove && board.getSquare(file + 1, row) < 0) || (!toMove && board.getSquare(file + 1, row) > 0)) {
				if (Math.abs(board.square[file + 1][row]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file + 1][row]) == 5) {
					captures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				} else if (Math.abs(board.square[file + 1][row]) == 4) {
					captureR.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				} else if (Math.abs(board.square[file + 1][row]) == 3) {
					captureB.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				} else if (Math.abs(board.square[file + 1][row]) == 2) {
					captureN.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				} else if (Math.abs(board.square[file + 1][row]) == 1) {
					captureP.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				}
			} else if ((toMove && board.getSquare(file + 1, row) == 0) 
					|| (!toMove && board.getSquare(file + 1, row) == 0)) {
				
				nonCaptures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
			}
			if (row > 0) {
				if ((toMove && board.getSquare(file + 1, row - 1) < 0) 
						|| (!toMove && board.getSquare(file + 1, row - 1) > 0)) {
					
					if (Math.abs(board.square[file + 1][row - 1]) == 6) {
						//captures.clear();
						captures.add(-1);
						captures.set(0, -1);
						return;
					} else if (Math.abs(board.square[file + 1][row - 1]) == 5) {
						captures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 4) {
						captureR.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 3) {
						captureB.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 2) {
						captureN.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 1) {
						captureP.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					}
				} else if ((toMove && board.getSquare(file + 1, row - 1) == 0) 
						|| (!toMove && board.getSquare(file + 1, row - 1) == 0)) {
					
					nonCaptures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				}
			}
			if (row < 7) {
				if ((toMove && board.getSquare(file + 1, row + 1) < 0) 
						|| (!toMove && board.getSquare(file + 1, row + 1) > 0)) {
					
					if (Math.abs(board.square[file + 1][row + 1]) == 6) {
						//captures.clear();
						captures.add(-1);
						captures.set(0, -1);
						return;
					} else if (Math.abs(board.square[file + 1][row + 1]) == 5) {
						captures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 4) {
						captureR.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 3) {
						captureB.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 2) {
						captureN.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 1) {
						captureP.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					}
				} else if ((toMove && board.getSquare(file + 1, row + 1) == 0) 
						|| (!toMove && board.getSquare(file + 1, row + 1) == 0)) {
					
					nonCaptures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				}
			}
		}
		
		if (row > 0) {
			if ((toMove && board.getSquare(file, row - 1) < 0) || (!toMove && board.getSquare(file, row - 1) > 0)) {
				if (Math.abs(board.square[file][row - 1]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file][row - 1]) == 5) {
					captures.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file][row - 1]) == 4) {
					captureR.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file][row - 1]) == 3) {
					captureB.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file][row - 1]) == 2) {
					captureN.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file][row - 1]) == 1) {
					captureP.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				}
			} else if ((toMove && board.getSquare(file, row - 1) == 0) 
					|| (!toMove && board.getSquare(file, row - 1) == 0)) {
				
				nonCaptures.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
			}
		}
		if (row < 7) {
			if ((toMove && board.getSquare(file, row + 1) < 0) || (!toMove && board.getSquare(file, row + 1) > 0)) {
				if (Math.abs(board.square[file][row + 1]) == 6) {
					//captures.clear();
					captures.add(-1);
					captures.set(0, -1);
					return;
				} else if (Math.abs(board.square[file][row + 1]) == 5) {
					captures.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file][row + 1]) == 4) {
					captureR.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file][row + 1]) == 3) {
					captureB.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file][row + 1]) == 2) {
					captureN.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file][row + 1]) == 1) {
					captureP.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				}
			} else if ((toMove && board.getSquare(file, row + 1) == 0) 
					|| (!toMove && board.getSquare(file, row + 1) == 0)) {
				
				nonCaptures.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
			}
		}
		
		if (toMove && file == 4 && row == 0) {
			if ((board.getCastlingRights() & 0x18) == 0x18) {
				if (board.square[5][0] == 0 && board.square[6][0] == 0) {
					nonCaptures.add(6 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + row);
				}
			} else if (((board.getCastlingRights() & 0x30) == 0x30)) {
				if (board.square[3][0] == 0 && board.square[2][0] == 0 && board.square[1][0] == 0) {
					nonCaptures.add(6 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + row);
				}
			}
		} else if ((!toMove) && file == 4 && row == 7) {
			if ((board.getCastlingRights() & 0x3) == 0x3) {
				if (board.square[5][7] == 0 && board.square[6][7] == 0) {
					nonCaptures.add(6 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + row);
				}
			} else if (((board.getCastlingRights() & 0x6) == 0x6)) {
				if (board.square[3][7] == 0 && board.square[2][7] == 0 && board.square[1][7] == 0) {
					nonCaptures.add(6 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + row);
				}
			}
		}
	}
	
	private static void pawnCapture(byte file, byte row, Board board, boolean toMove, ArrayList<Integer> captures,
			ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP) {
		if (toMove) {
			if (file > 0 && board.getSquare(file - 1, row + 1) < 0) {
				if (board.getSquare(file - 1, row + 1) == -6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (board.getSquare(file - 1, row + 1) == -5) {
					captures.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if (board.getSquare(file - 1, row + 1) == -4) {
					captureR.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if (board.getSquare(file - 1, row + 1) == -3) {
					captureB.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if (board.getSquare(file - 1, row + 1) == -2) {
					captureN.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				} else if (board.getSquare(file - 1, row + 1) == -1) {
					captureP.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
				}
			}
			if (file < 7 && board.getSquare(file + 1, row + 1) < 0) {
				if (board.getSquare(file + 1, row + 1) == -6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (board.getSquare(file + 1, row + 1) == -5) {
					captures.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if (board.getSquare(file + 1, row + 1) == -4) {
					captureR.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if (board.getSquare(file + 1, row + 1) == -3) {
					captureB.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if (board.getSquare(file + 1, row + 1) == -2) {
					captureN.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				} else if (board.getSquare(file + 1, row + 1) == -1) {
					captureP.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
				}
			}
		} else if (!toMove) {
			if (file > 0 && board.getSquare(file - 1, row - 1) > 0) {
				if (board.getSquare(file - 1, row - 1) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (board.getSquare(file - 1, row - 1) == 5) {
					captures.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if (board.getSquare(file - 1, row - 1) == 4) {
					captureR.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if (board.getSquare(file - 1, row - 1) == 3) {
					captureB.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if (board.getSquare(file - 1, row - 1) == 2) {
					captureN.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				} else if (board.getSquare(file - 1, row - 1) == 1) {
					captureP.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
				}
			}
			if (file < 7 && board.getSquare(file + 1, row - 1) > 0) {
				if (board.getSquare(file + 1, row - 1) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (board.getSquare(file + 1, row - 1) == 5) {
					captures.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if (board.getSquare(file + 1, row - 1) == 4) {
					captureR.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if (board.getSquare(file + 1, row - 1) == 3) {
					captureB.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if (board.getSquare(file + 1, row - 1) == 2) {
					captureN.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				} else if (board.getSquare(file + 1, row - 1) == 1) {
					captureP.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
				}
			}
		}
		
	}

	/**
	 * Generate all Knight captures.
	 * 
	 * @param file : position of the knight on the board
	 * @param row : " "
	 * @param board : on which board we are
	 * @param toMove : who to move it is
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 * @param captureR 
	 */
	private static void knightCapture(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP) {
		if (file > 0 && row > 1) {
			if ((toMove && board.square[file - 1][row - 2] < 0) 
					|| ((!toMove) && board.square[file - 1][row - 2] > 0)) {
				
				if (Math.abs(board.square[file - 1][row - 2]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file - 1][row - 2]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
				}
			}
		}
		if (file > 0 && row < 6) {
			if ((toMove && board.square[file - 1][row + 2] < 0) 
					|| ((!toMove) && board.square[file - 1][row + 2] > 0)) {
				
				if (Math.abs(board.square[file - 1][row + 2]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file - 1][row + 2]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
				}
			}
		}
		if (file > 1 && row > 0) {
			if ((toMove && board.square[file - 2][row - 1] < 0) 
					|| ((!toMove) && board.square[file - 2][row - 1] > 0)) {
				
				if (Math.abs(board.square[file - 2][row - 1]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file - 2][row - 1]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
				}
			}
		}
		if (file > 1 && row < 7) {
			if ((toMove && board.square[file - 2][row + 1] < 0) 
					|| ((!toMove) && board.square[file - 2][row + 1] > 0)) {
				
				if (Math.abs(board.square[file - 2][row + 1]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file - 2][row + 1]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
				}
			}
		}
		if (file < 6 && row > 0) {
			if ((toMove && board.square[file + 2][row - 1] < 0) 
					|| ((!toMove) && board.square[file + 2][row - 1] > 0)) {
				
				if (Math.abs(board.square[file + 2][row - 1]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + 2][row - 1]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
				}
			}
		}
		if (file < 6 && row < 7) {
			if ((toMove && board.square[file + 2][row + 1] < 0) 
					|| ((!toMove) && board.square[file + 2][row + 1] > 0)) {
				
				if (Math.abs(board.square[file + 2][row + 1]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + 2][row + 1]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
				}
			}
		}
		if (file < 7 && row > 1) {
			if ((toMove && board.square[file + 1][row - 2] < 0) 
					|| ((!toMove) && board.square[file + 1][row - 2] > 0)) {
				
				if (Math.abs(board.square[file + 1][row - 2]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + 1][row - 2]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
				}
			}
		}
		if (file < 7 && row < 6) {
			if ((toMove && board.square[file + 1][row + 2] < 0) 
					|| ((!toMove) && board.square[file + 1][row + 2] > 0)) {
				
				if (Math.abs(board.square[file + 1][row + 2]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + 1][row + 2]) == 5) {
					captures.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 4) {
					captureR.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 3) {
					captureB.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 2) {
					captureN.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 1) {
					captureP.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
				}
			}
		}
	}
	
	/**
	 * Generate all legal rook captures.
	 * 
	 * @param file : position of the rook on the board
	 * @param row : " "
	 * @param board : on which board we are
	 * @param toMove : who to move it is
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 * @param captureR 
	 */
	private static void rookCapture(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP) {
		byte thisPiece = (byte) Math.abs(board.getSquare(file, row));
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), row, board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + i][row]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				} else if (Math.abs(board.square[file + i][row]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				} else if (Math.abs(board.square[file + i][row]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				} else if (Math.abs(board.square[file + i][row]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				} else if (Math.abs(board.square[file + i][row]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				}
				break;
			} else if (squareValue == 1) {
				continue;
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), row, board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file - i][row]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				} else if (Math.abs(board.square[file - i][row]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				} else if (Math.abs(board.square[file - i][row]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				} else if (Math.abs(board.square[file - i][row]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				} else if (Math.abs(board.square[file - i][row]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				}
				break;
			} else if (squareValue == 1) {
				continue;
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file][row + i]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file][row + i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				} else if (Math.abs(board.square[file][row + i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				} else if (Math.abs(board.square[file][row + i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				} else if (Math.abs(board.square[file][row + i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				} else if (Math.abs(board.square[file][row + i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				}
				break;
			} else if (squareValue == 1) {
				continue;
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file][row - i]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file][row - i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				} else if (Math.abs(board.square[file][row - i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				} else if (Math.abs(board.square[file][row - i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				} else if (Math.abs(board.square[file][row - i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				} else if (Math.abs(board.square[file][row - i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				}
				break;
			} else if (squareValue == 1) {
				continue;
			} else {
				break;
			}
		}
	}
	
	/**
	 * Generate all legal bishop captures.
	 * 
	 * @param file : position of the bishop on the board
	 * @param row : " "
	 * @param board : on which board we are
	 * @param toMove : who to move it is
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 * @param captureR 
	 */
	private static void bishopCapture(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP) {
		byte thisPiece = (byte) Math.abs(board.getSquare(file, row));
		for (byte i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row + i]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + i][row + i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file + i][row + i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file + i][row + i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file + i][row + i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file + i][row + i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				}
				break;
			} else if (squareValue == 1) {
				continue;
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row - i]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file - i][row - i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file - i][row - i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file - i][row - i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file - i][row - i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file - i][row - i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				}
				break;
			} else if (squareValue == 1) {
				continue;
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row - i]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + i][row - i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file + i][row - i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file + i][row - i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file + i][row - i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				} else if (Math.abs(board.square[file + i][row - i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				}
				break;
			} else if (squareValue == 1) {
				continue;
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row + i]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file - i][row + i]) == 5) {
					captures.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file - i][row + i]) == 4) {
					captureR.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file - i][row + i]) == 3) {
					captureB.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file - i][row + i]) == 2) {
					captureN.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				} else if (Math.abs(board.square[file - i][row + i]) == 1) {
					captureP.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				}
				break;
			} else if (squareValue == 1) {
				continue;
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
	 * @param captureP 
	 * @param captureN 
	 * @param captureB 
	 * @param captureR 
	 */
	private static void kingCapture(byte file, byte row, Board board, boolean toMove,
			ArrayList<Integer> captures, ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP) {
		if (file > 0) {
			if ((toMove && board.getSquare(file - 1, row) < 0) || (!toMove && board.getSquare(file - 1, row) > 0)) {
				if (Math.abs(board.square[file - 1][row]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file - 1][row]) == 5) {
					captures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				} else if (Math.abs(board.square[file - 1][row]) == 4) {
					captureR.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				} else if (Math.abs(board.square[file - 1][row]) == 3) {
					captureB.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				} else if (Math.abs(board.square[file - 1][row]) == 2) {
					captureN.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				} else if (Math.abs(board.square[file - 1][row]) == 1) {
					captureP.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
				}
			}
			if (row > 0) {
				if ((toMove && board.getSquare(file - 1, row - 1) < 0) 
						|| (!toMove && board.getSquare(file - 1, row - 1) > 0)) {
					
					if (Math.abs(board.square[file - 1][row - 1]) == 6) {
						captures.clear();
						captures.add(-1);
						return;
					} else if (Math.abs(board.square[file - 1][row - 1]) == 5) {
						captures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 4) {
						captureR.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 3) {
						captureB.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 2) {
						captureN.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 1) {
						captureP.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					}
				}
			}
			if (row < 7) {
				if ((toMove && board.getSquare(file - 1, row + 1) < 0) 
						|| (!toMove && board.getSquare(file - 1, row + 1) > 0)) {
					
					if (Math.abs(board.square[file - 1][row + 1]) == 6) {
						captures.clear();
						captures.add(-1);
						return;
					} else if (Math.abs(board.square[file - 1][row + 1]) == 5) {
						captures.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 4) {
						captureR.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 3) {
						captureB.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 2) {
						captureN.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 1) {
						captureP.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					}
				}
			}
		}
		
		if (file < 7) {
			if ((toMove && board.getSquare(file + 1, row) < 0) || (!toMove && board.getSquare(file + 1, row) > 0)) {
				if (Math.abs(board.square[file + 1][row]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + 1][row]) == 5) {
					captures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				} else if (Math.abs(board.square[file + 1][row]) == 4) {
					captureR.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				} else if (Math.abs(board.square[file + 1][row]) == 3) {
					captureB.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				} else if (Math.abs(board.square[file + 1][row]) == 2) {
					captureN.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				} else if (Math.abs(board.square[file + 1][row]) == 1) {
					captureP.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
				}
			}
			if (row > 0) {
				if ((toMove && board.getSquare(file + 1, row - 1) < 0) 
						|| (!toMove && board.getSquare(file + 1, row - 1) > 0)) {
					
					if (Math.abs(board.square[file + 1][row - 1]) == 6) {
						captures.clear();
						captures.add(-1);
						return;
					} else if (Math.abs(board.square[file + 1][row - 1]) == 5) {
						captures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 4) {
						captureR.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 3) {
						captureB.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 2) {
						captureN.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 1) {
						captureP.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					}
				}
			}
			if (row < 7) {
				if ((toMove && board.getSquare(file + 1, row + 1) < 0) 
						|| (!toMove && board.getSquare(file + 1, row + 1) > 0)) {
					
					if (Math.abs(board.square[file + 1][row + 1]) == 6) {
						captures.clear();
						captures.add(-1);
						return;
					} else if (Math.abs(board.square[file + 1][row + 1]) == 5) {
						captures.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 4) {
						captureR.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 3) {
						captureB.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 2) {
						captureN.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 1) {
						captureP.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					}
				}
			}
		}
		
		if (row > 0) {
			if ((toMove && board.getSquare(file, row - 1) < 0) || (!toMove && board.getSquare(file, row - 1) > 0)) {
				if (Math.abs(board.square[file][row - 1]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file][row - 1]) == 5) {
					captures.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file][row - 1]) == 4) {
					captureR.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file][row - 1]) == 3) {
					captureB.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file][row - 1]) == 2) {
					captureN.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				} else if (Math.abs(board.square[file][row - 1]) == 1) {
					captureP.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				}
			}
		}
		if (row < 7) {
			if ((toMove && board.getSquare(file, row + 1) < 0) || (!toMove && board.getSquare(file, row + 1) > 0)) {
				if (Math.abs(board.square[file][row + 1]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file][row + 1]) == 5) {
					captures.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file][row + 1]) == 4) {
					captureR.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file][row + 1]) == 3) {
					captureB.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file][row + 1]) == 2) {
					captureN.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				} else if (Math.abs(board.square[file][row + 1]) == 1) {
					captureP.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				}
			}
		}
		
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
