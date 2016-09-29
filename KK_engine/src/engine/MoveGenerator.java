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
	public static ArrayList<Integer> collectMoves(Board board, boolean toMove, int[] movesSize) {
		for (int piece = 0; piece < 6; piece++) {
			movesSize[piece] = 0;
		}
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] movesByPriority = (ArrayList<Integer>[]) new ArrayList[6];
		movesByPriority[0] = new ArrayList<Integer>(1); // save all Qs getting captured
		movesByPriority[1] = new ArrayList<Integer>(1); // save all Rs getting captured
		movesByPriority[2] = new ArrayList<Integer>(1); // Bs getting captured
		movesByPriority[3] = new ArrayList<Integer>(1); // Ns getting captured
		movesByPriority[4] = new ArrayList<Integer>(1); // Ps getting captured
		movesByPriority[5] = new ArrayList<Integer>(10); // non-captures
		for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				
				if (toMove) {
					if (board.square[i][j] == 1) {
						pawnMove(i, j, board, toMove, movesByPriority, movesSize);
						continue;
					} else if (board.square[i][j] == 2) {
						knightMove(i, j, board, toMove, movesByPriority, movesSize);
						continue;
					} else if (board.square[i][j] == 3) {
						bishopMove(i, j, board, toMove, false, movesByPriority, movesSize);
						continue;
					} else if (board.square[i][j] == 4) {
						rookMove(i, j, board, toMove, false, movesByPriority, movesSize);
						continue;
					} else if (board.square[i][j] == 5) { // queen moves like rook + bishop
						rookMove(i, j, board, toMove, true, movesByPriority, movesSize);
						bishopMove(i, j, board, toMove, true, movesByPriority, movesSize);
					} else if (board.square[i][j] == 6) {
						kingMove(i, j, board, toMove, movesByPriority, movesSize);
					}
				} else {
					if (board.square[i][j] == -1) {
						pawnMove(i, j, board, toMove, movesByPriority, movesSize);
						continue;
					} else if (board.square[i][j] == -2) {
						knightMove(i, j, board, toMove, movesByPriority, movesSize);
						continue;
					} else if (board.square[i][j] == -3) {
						bishopMove(i, j, board, toMove, false, movesByPriority, movesSize);
						continue;
					} else if (board.square[i][j] == -4) {
						rookMove(i, j, board, toMove, false, movesByPriority, movesSize);
						continue;
					} else if (board.square[i][j] == -5) { // queen moves like rook + bishop
						rookMove(i, j, board, toMove, true, movesByPriority, movesSize);
						bishopMove(i, j, board, toMove, true, movesByPriority, movesSize);
					} else if (board.square[i][j] == -6) {
						kingMove(i, j, board, toMove, movesByPriority, movesSize);
					}
				}
			}
		}
		movesByPriority[0].addAll(movesByPriority[1]);
		movesByPriority[0].addAll(movesByPriority[2]);
		movesByPriority[0].addAll(movesByPriority[3]);
		movesByPriority[0].addAll(movesByPriority[4]);
		movesByPriority[0].addAll(movesByPriority[5]);
		return movesByPriority[0];
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
	 * TODO: Write pawn promotion.
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
			ArrayList<Integer>[] movesByPriority, int[] movesSize) {
		if (toMove) {
			if (board.getSquare(file, row + 1) == 0) {
				movesByPriority[5].add((1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
				movesSize[0]++;
				if (row == 1 && board.getSquare(file, row + 2) == 0) {
					movesByPriority[5].add((1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 2)));
					movesSize[0]++;
				}
			}
			
			if (file > 0 && board.getSquare(file - 1, row + 1) < 0) {
				if (board.getSquare(file - 1, row + 1) == -6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[0]++;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - 1, row + 1))]
							.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					movesSize[0]++;
				}
			}
			
			if (file < 7 && board.getSquare(file + 1, row + 1) < 0) {
				if (board.getSquare(file + 1, row + 1) == -6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[0]++;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + 1, row + 1))]
							.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					movesSize[0]++;
				}
			}
			
			if ((file * 8 + row) - 7 == board.getEnPassant()) {
				if (row == 4) {
					movesByPriority[4].add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
					movesSize[0]++;
				}
			} else if ((file * 8 + row) + 9 == board.getEnPassant()) {
				if (row == 4) {
					movesByPriority[4].add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
					movesSize[0]++;
				}
			}
		} else if (!toMove) {
			if (board.getSquare(file, row - 1) == 0) {
				movesByPriority[5].add((1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
				movesSize[0]++;
				if (row == 6 && board.getSquare(file, row - 2) == 0) {
					movesByPriority[5].add((1 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 2)));
					movesSize[0]++;
				}
			}
			
			if (file > 0 && board.getSquare(file - 1, row - 1) > 0) {
				if (board.getSquare(file - 1, row - 1) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[0]++;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - 1, row - 1))]
							.add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					movesSize[0]++;
				}
			}
			
			if (file < 7 && board.getSquare(file + 1, row - 1) > 0) {
				if (board.getSquare(file + 1, row - 1) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[0]++;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + 1, row - 1))]
							.add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					movesSize[0]++;
				}
			}
			
			if ((file * 8 + row) + 7 == board.getEnPassant()) {
				if (row == 3) {
					movesByPriority[4].add((1 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
					movesSize[0]++;
				}
			} else if ((file * 8 + row) - 9 == board.getEnPassant()) {
				if (row == 3) {
					movesByPriority[4].add((1 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
					movesSize[0]++;
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
			ArrayList<Integer>[] movesByPriority, int[] movesSize) {
		if (file > 0 && row > 1) {
			if ((toMove && board.square[file - 1][row - 2] <= 0) 
					|| ((!toMove) && board.square[file - 1][row - 2] >= 0)) {
				
				if (Math.abs(board.square[file - 1][row - 2]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[1]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - 1, row - 2))]
							.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 2)));
					movesSize[1]++;
				}
			}
		}
		
		if (file > 0 && row < 6) {
			if ((toMove && board.square[file - 1][row + 2] <= 0) 
					|| ((!toMove) && board.square[file - 1][row + 2] >= 0)) {
				
				if (Math.abs(board.square[file - 1][row + 2]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[1]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - 1, row + 2))]
							.add((2 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 2)));
					movesSize[1]++;
				}
			}
		}
		
		if (file > 1 && row > 0) {
			if ((toMove && board.square[file - 2][row - 1] <= 0) 
					|| ((!toMove) && board.square[file - 2][row - 1] >= 0)) {
				
				if (Math.abs(board.square[file - 2][row - 1]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[1]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - 2, row - 1))]
							.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row - 1)));
					movesSize[1]++;
				}
			}
		}
		
		if (file > 1 && row < 7) {
			if ((toMove && board.square[file - 2][row + 1] <= 0) 
					|| ((!toMove) && board.square[file - 2][row + 1] >= 0)) {
				
				if (Math.abs(board.square[file - 2][row + 1]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[1]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - 2, row + 1))]
							.add((2 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + (row + 1)));
					movesSize[1]++;
				}
			}
		}
		
		if (file < 6 && row > 0) {
			if ((toMove && board.square[file + 2][row - 1] <= 0) 
					|| ((!toMove) && board.square[file + 2][row - 1] >= 0)) {
				
				if (Math.abs(board.square[file + 2][row - 1]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[1]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + 2, row - 1))]
							.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row - 1)));
					movesSize[1]++;
				}
			}
		}
		
		if (file < 6 && row < 7) {
			if ((toMove && board.square[file + 2][row + 1] <= 0) 
					|| ((!toMove) && board.square[file + 2][row + 1] >= 0)) {
				
				if (Math.abs(board.square[file + 2][row + 1]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[1]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + 2, row + 1))]
							.add((2 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + (row + 1)));
					movesSize[1]++;
				}
			}
		}
		
		if (file < 7 && row > 1) {
			if ((toMove && board.square[file + 1][row - 2] <= 0) 
					|| ((!toMove) && board.square[file + 1][row - 2] >= 0)) {
				
				if (Math.abs(board.square[file + 1][row - 2]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[1]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + 1, row - 2))]
							.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 2)));
					movesSize[1]++;
				}
			}
		}
		
		if (file < 7 && row < 6) {
			if ((toMove && board.square[file + 1][row + 2] <= 0) 
					|| ((!toMove) && board.square[file + 1][row + 2] >= 0)) {
				
				if (Math.abs(board.square[file + 1][row + 2]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[1]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + 1, row + 2))]
							.add((2 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 2)));
					movesSize[1]++;
				}
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
	private static void rookMove(byte file, byte row, Board board, boolean toMove, boolean queen,
			ArrayList<Integer>[] movesByPriority, int[] movesSize) {
		byte thisPiece = (byte) Math.abs(board.getSquare(file, row));
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), row, board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + i, row))]
							.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				movesByPriority[5].add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + row));
				if (queen) {
					movesSize[4]++;
				} else {
					movesSize[3]++;
				}
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), row, board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - i, row))]
							.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				movesByPriority[5].add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + row));
				if (queen) {
					movesSize[4]++;
				} else {
					movesSize[3]++;
				}
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file][row + i]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file, row + i))]
							.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				movesByPriority[5].add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row + i)));
				if (queen) {
					movesSize[4]++;
				} else {
					movesSize[3]++;
				}
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file][row - i]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file, row - i))]
							.add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				movesByPriority[5].add((thisPiece * 4096 + file * 512 + row * 64 + (file) * 8 + (row - i)));
				if (queen) {
					movesSize[4]++;
				} else {
					movesSize[3]++;
				}
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
	private static void bishopMove(byte file, byte row, Board board, boolean toMove, boolean queen,
			ArrayList<Integer>[] movesByPriority, int[] movesSize) {
		byte thisPiece = (byte) Math.abs(board.getSquare(file, row));
		for (byte i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row + i]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + i, row + i))]
							.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				movesByPriority[5].add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row + i)));
				if (queen) {
					movesSize[4]++;
				} else {
					movesSize[2]++;
				}
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row - i]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - i, row - i))]
							.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				movesByPriority[5].add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row - i)));
				if (queen) {
					movesSize[4]++;
				} else {
					movesSize[2]++;
				}
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row - i]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + i, row - i))]
							.add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				movesByPriority[5].add((thisPiece * 4096 + file * 512 + row * 64 + (file + i) * 8 + (row - i)));
				if (queen) {
					movesSize[4]++;
				} else {
					movesSize[2]++;
				}
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file - i][row + i]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - i, row + i))]
							.add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				movesByPriority[5].add((thisPiece * 4096 + file * 512 + row * 64 + (file - i) * 8 + (row + i)));
				if (queen) {
					movesSize[4]++;
				} else {
					movesSize[2]++;
				}
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
			ArrayList<Integer>[] movesByPriority, int[] movesSize) {
		if (file > 0) {
			if ((toMove && board.getSquare(file - 1, row) <= 0) || (!toMove && board.getSquare(file - 1, row) >= 0)) {
				if (Math.abs(board.square[file - 1][row]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[5]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file - 1, row))]
							.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + row));
					movesSize[5]++;
				}
			}
			
			if (row > 0) {
				if ((toMove && board.getSquare(file - 1, row - 1) <= 0) 
						|| (!toMove && board.getSquare(file - 1, row - 1) >= 0)) {
					
					if (Math.abs(board.square[file - 1][row - 1]) == 6) {
						movesByPriority[0].add(-1);
						movesByPriority[0].set(0, -1);
						movesSize[5]++;
						return;
					} else {
						movesByPriority[5 - Math.abs(board.getSquare(file - 1, row - 1))]
								.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row - 1)));
						movesSize[5]++;
					}
				}
			}
			
			if (row < 7) {
				if ((toMove && board.getSquare(file - 1, row + 1) <= 0) 
						|| (!toMove && board.getSquare(file - 1, row + 1) >= 0)) {
					
					if (Math.abs(board.square[file - 1][row + 1]) == 6) {
						movesByPriority[0].add(-1);
						movesByPriority[0].set(0, -1);
						movesSize[5]++;
						return;
					} else {
						movesByPriority[5 - Math.abs(board.getSquare(file - 1, row + 1))]
								.add((6 * 4096 + file * 512 + row * 64 + (file - 1) * 8 + (row + 1)));
						movesSize[5]++;
					}
				}
			}
		}
		
		if (file < 7) {
			if ((toMove && board.getSquare(file + 1, row) <= 0) || (!toMove && board.getSquare(file + 1, row) >= 0)) {
				if (Math.abs(board.square[file + 1][row]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[5]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file + 1, row))]
							.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + row));
					movesSize[5]++;
				}
			}
			
			if (row > 0) {
				if ((toMove && board.getSquare(file + 1, row - 1) <= 0) 
						|| (!toMove && board.getSquare(file + 1, row - 1) >= 0)) {
					
					if (Math.abs(board.square[file + 1][row - 1]) == 6) {
						movesByPriority[0].add(-1);
						movesByPriority[0].set(0, -1);
						movesSize[5]++;
						return;
					} else {
						movesByPriority[5 - Math.abs(board.getSquare(file + 1, row - 1))]
								.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row - 1)));
						movesSize[5]++;
					}
				}
			}
			
			if (row < 7) {
				if ((toMove && board.getSquare(file + 1, row + 1) <= 0) 
						|| (!toMove && board.getSquare(file + 1, row + 1) >= 0)) {
					
					if (Math.abs(board.square[file + 1][row + 1]) == 6) {
						movesByPriority[0].add(-1);
						movesByPriority[0].set(0, -1);
						movesSize[5]++;
						return;
					} else {
						movesByPriority[5 - Math.abs(board.getSquare(file + 1, row + 1))]
								.add((6 * 4096 + file * 512 + row * 64 + (file + 1) * 8 + (row + 1)));
						movesSize[5]++;
					}
				}
			}
		}
		
		if (row > 0) {
			if ((toMove && board.getSquare(file, row - 1) <= 0) || (!toMove && board.getSquare(file, row - 1) >= 0)) {
				if (Math.abs(board.square[file][row - 1]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[5]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file, row - 1))]
							.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row - 1)));
					movesSize[5]++;
				}
			}
		}
		
		if (row < 7) {
			if ((toMove && board.getSquare(file, row + 1) <= 0) || (!toMove && board.getSquare(file, row + 1) >= 0)) {
				if (Math.abs(board.square[file][row + 1]) == 6) {
					movesByPriority[0].add(-1);
					movesByPriority[0].set(0, -1);
					movesSize[5]++;
					return;
				} else {
					movesByPriority[5 - Math.abs(board.getSquare(file, row + 1))]
							.add((6 * 4096 + file * 512 + row * 64 + (file) * 8 + (row + 1)));
					movesSize[5]++;
				}
			}
		}
		
		if (toMove && file == 4 && row == 0) {
			if ((board.getCastlingRights() & 0x18) == 0x18) {
				if (board.square[5][0] == 0 && board.square[6][0] == 0) {
					board.square[5][0] = 6;
					ArrayList<Integer> testLegality = collectCaptures(board, !toMove);
					if (testLegality.size() == 0 || testLegality.get(0) != -1) {
						movesByPriority[5].add(6 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + row);
						movesSize[5]++;
					}
					board.square[5][0] = 0;
				}
			}
			
			if (((board.getCastlingRights() & 0x30) == 0x30)) {
				if (board.square[3][0] == 0 && board.square[2][0] == 0 && board.square[1][0] == 0) {
					board.square[3][0] = 6;
					ArrayList<Integer> testLegality = collectCaptures(board, !toMove);
					if (testLegality.size() == 0 || testLegality.get(0) != -1) {
						movesByPriority[5].add(6 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + row);
						movesSize[5]++;
					}
					board.square[3][0] = 0;
				}
			}
		} else if ((!toMove) && file == 4 && row == 7) {
			if ((board.getCastlingRights() & 0x3) == 0x3) {
				if (board.square[5][7] == 0 && board.square[6][7] == 0) {
					board.square[5][7] = -6;
					ArrayList<Integer> testLegality = collectCaptures(board, !toMove);
					if (testLegality.size() == 0 || testLegality.get(0) != -1) {
						movesByPriority[5].add(6 * 4096 + file * 512 + row * 64 + (file + 2) * 8 + row);
						movesSize[5]++;
					}
					board.square[5][7] = 0;
				}
			}
			
			if (((board.getCastlingRights() & 0x6) == 0x6)) {
				if (board.square[3][7] == 0 && board.square[2][7] == 0 && board.square[1][7] == 0) {
					board.square[3][7] = -6;
					ArrayList<Integer> testLegality = collectCaptures(board, !toMove);
					if (testLegality.size() == 0 || testLegality.get(0) != -1) {
						movesByPriority[5].add(6 * 4096 + file * 512 + row * 64 + (file - 2) * 8 + row);
						movesSize[5]++;
					}
					board.square[3][7] = 0;
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
	
	public static int[] activityEval(Board board, boolean toMove) {
		int[] movesSize = new int[6];
		collectMoves(board, toMove, movesSize);
		return movesSize;
	}
	
	public static ArrayList<Integer> collectAllPNMoves(Board board, boolean toMove) {
		int movesSize[] = new int[6];
		ArrayList<Integer> moves = collectMoves(board, toMove, movesSize);
		if (moves.get(0) == -1) {
			return moves;
		}
		
		for (int index = 0; index < moves.size(); index++) {
			int move = moves.get(index);
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (MateFinder.inCheck(board)) {
				moves.remove(index);
				index--;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece);
			board.addCastlingRights(castlingRights);
		}
		return moves;
	}
	
	public static ArrayList<Integer> collectPNSearchMoves(Board board, boolean toMove) {
		int movesSize[] = new int[6];
		ArrayList<Integer> moves = collectMoves(board, toMove, movesSize);
		if (moves.get(0) == -1) {
			return moves;
		}
		ArrayList<Integer> checks = new ArrayList<Integer>();
		for (int index = 0; index < moves.size(); index++) {
			int move = moves.get(index);
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (MateFinder.inCheck(board)) {
				moves.remove(index);
				index--;
			} else {
				board.changeToMove();
				if (MateFinder.inCheck(board)) {
					checks.add(move);
					moves.remove(index);
					index--;
				}
				board.changeToMove();
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece);
			board.addCastlingRights(castlingRights);
		}
		checks.addAll(moves);
		return checks;
	}
	
	public static ArrayList<Integer> collectCheckMoves(Board board, boolean toMove) {
		int movesSize[] = new int[6];
		ArrayList<Integer> moves = collectMoves(board, toMove, movesSize);
		ArrayList<Integer> checks = new ArrayList<Integer>();
		if (moves.get(0) == -1) {
			return checks;
		}
		for (int index = 0; index < moves.size(); index++) {
			int move = moves.get(index);
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (MateFinder.inCheck(board)) {
				moves.remove(index);
				index--;
			} else {
				board.changeToMove();
				if (MateFinder.inCheck(board)) {
					checks.add(move);
					moves.remove(index);
					index--;
				}
				board.changeToMove();
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece);
			board.addCastlingRights(castlingRights);
		}
		return checks;
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
