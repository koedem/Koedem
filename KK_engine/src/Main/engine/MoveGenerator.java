package Main.engine;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Anon
 *
 */
public class MoveGenerator implements Serializable {

	/**
	 * The board for which we want to generate moves.
	 */
	private Board board;

	/**
	 * The maximum number of legal moves (+1) we expect a position to have. 218 + 1 = 219 for normal chess, rounded up to the next power of 2.
	 */
	public static final int MAX_MOVE_COUNT = 256;

	/**
	 * 2D array to put captures in. First index is the piece that gets captured. (1 = pawn, 2 = knight etc.)
	 *                 captures[i][0] stores the number of capture moves in that array. captures[0] should be empty
	 *                 except captures[0][0] == -1 if the position was illegal (king capture possible or similar)
	 */
	private int[][] captures = new int[6][64]; // hopefully at max 64 captures for a single captured piece type; captures[0] isn't used

	/**
	 * Array to put non-capture moves in. nonCaptures[0] stores the number of non capture moves.
	 */
	private int[] nonCaptures = new int[MAX_MOVE_COUNT];

	private byte file = 0; // this is non-local to only have one instance of it to prevent memory leaks
	private byte row = 0;
	private int startSquare = 0;
	private int capturedPiece, capturedPieceValue;

	public MoveGenerator(Board board) {
		this.board = board;
	}

	/**
	 * @param toMove : who to move it is
	 * @return array of ints, each containing a move
	 */
	public int[] collectMoves(boolean toMove, int[] allMoves, int[] movesSize) {
		for (int piece = 0; piece < 6; piece++) {
			movesSize[piece] = 0;
			captures[piece][0] = 0;
		}
		nonCaptures[0] = 0;
		startSquare = 0;
		file = 0;

		while (file < 8) {
			row = 0;
			while (row < 8) {
				if (toMove) {
					switch (board.square[file][row]) {
						case 1:
							pawnMove(true, movesSize);
							break;
						case 2:
							knightMove(true, movesSize);
							break;
						case 3:
							bishopMove(true, false, movesSize);
							break;
						case 4:
							rookMove(true, false, movesSize);
							break;
						case 5: // queen moves like rook + bishop
							rookMove(true, true, movesSize);
							bishopMove(true, true, movesSize);
							break;
						case 6:
							kingMove(true, movesSize);
							break;
					}
				} else {
					switch (board.square[file][row]) {
						case -1:
							pawnMove(false, movesSize);
							break;
						case -2:
							knightMove(false, movesSize);
							break;
						case -3:
							bishopMove(false, false, movesSize);
							break;
						case -4:
							rookMove(false, false, movesSize);
							break;
						case -5: // queen moves like rook + bishop
							rookMove(false, true, movesSize);
							bishopMove(false, true, movesSize);
							break;
						case -6:
							kingMove(false, movesSize);
							break;
					}
				}
				++startSquare;
				row++;
			}
			file++;
		}

		if (captures[0][0] == -1) {
			allMoves[0] = -1;
		} else {
			allMoves[0] = 0;
			for (int piece = captures.length - 1; piece > 0; piece--) {
				int destPos = allMoves[0] + 1; // plus one because allMoves[0] doesn't store a move (oversimplified)
				System.arraycopy(captures[piece], 1, allMoves, destPos, captures[piece][0]);
				allMoves[0] += captures[piece][0];
			}
			System.arraycopy(nonCaptures, 1, allMoves, allMoves[0] + 1, nonCaptures[0]);
			allMoves[0] += nonCaptures[0];
		}

		return allMoves;
	}
	
	/**
	 * 
	 * @param board : The board on which captures get generated.
	 * @param toMove : Who to move it is.
	 * @return ArrayList of Integers, containing all captures.
	 */
	public static ArrayList<Integer> collectCaptures(Board board, boolean toMove) {
		ArrayList<Integer> captures = new ArrayList<>(1); // save all Qs getting captured
		ArrayList<Integer> captureR = new ArrayList<>(1); // save all Rs getting captured
		ArrayList<Integer> captureB = new ArrayList<>(1);
		ArrayList<Integer> captureN = new ArrayList<>(1);
		ArrayList<Integer> captureP = new ArrayList<>(1);
		
		for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				
				if (toMove) {
					if (board.square[i][j] == 1) {
						pawnCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == 2) {
						knightCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == 3) {
						bishopCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == 4) {
						rookCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == 5) { // queen moves like rook + bishop
						rookCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
						bishopCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == 6) {
						kingCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					}
				} else {
					if (board.square[i][j] == -1) {
						pawnCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == -2) {
						knightCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == -3) {
						bishopCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
					} else if (board.square[i][j] == -4) {
						rookCapture(i, j, board, toMove, captures, captureR, captureB, captureN, captureP);
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
	 *
	 * @param toMove who to move it is
	 * @param movesSize array that holds the number of legal moves that each piece type has. Does include king moves into check.
	 */
	private void pawnMove(boolean toMove, int[] movesSize) {
		if (toMove) {
			if (row < 6 && row > 0) { // no promotion
				if (board.getSquare(file, row + 1) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 1);
					movesSize[0]++;
					if (row == 1 && board.getSquare(file, row + 2) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 2);
						movesSize[0]++;
					}
				}

				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row + 1)) < 0) {
					if (capturedPiece == -6) {
						captures[0][0] = -1;
						movesSize[0]++;
					} else {
						capturedPieceValue = -capturedPiece; // capturedPiece < 0 so -capturedPiece > 0 therefore legal array index
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 7);
						movesSize[0]++;
					}
				}
				
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row + 1)) < 0) {
					if (capturedPiece == -6) {
						captures[0][0] = -1;
						movesSize[0]++;
					} else {
						capturedPieceValue = -capturedPiece;
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 9);
						movesSize[0]++;
					}
				}

				if (row == 4) {
					if (startSquare - 7 == board.getEnPassant()) {
						captures[1][++captures[1][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 7);
						movesSize[0]++;
					} else if ((file * 8 + row) + 9 == board.getEnPassant()) {
						captures[1][++captures[1][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 9);
						movesSize[0]++;
					}
				}
			} else if (row == 6) { // promotion
				if (board.getSquare(file, row + 1) == 0) {
					for (int piece = 5; piece > 1; piece--) { // TODO outsource variable?!
						captures[piece][++captures[piece][0]] = (1 << 15) + (startSquare << 9) + ((startSquare + 1) << 3) + piece; // priority according to material "gain"
						movesSize[0]++;
					}
				}
				
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row + 1)) < 0) {
					if (capturedPiece == -6) {
						captures[0][0] = -1;
						movesSize[0]++;
					} else {
						for (int piece = 5; piece > 1; piece--) {
							captures[5][++captures[5][0]] = (1 << 15) + (startSquare << 9) + ((startSquare - 7) << 3) + piece; // capturing while promoting always best priority
							movesSize[0]++;
						}
					}
				}
				
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row + 1)) < 0) {
					if (capturedPiece == -6) {
						captures[0][0] = -1;
						movesSize[0]++;
					} else {
						for (int piece = 5; piece > 1; piece--) {
							captures[5][++captures[5][0]] = (1 << 15) + (startSquare << 9) + ((startSquare + 9) << 3) + piece;
							movesSize[0]++;
						}
					}
				}
			} else {
				assert false;
			}
		} else {
			if (row > 1 && row < 7) { // no promotion
				if (board.getSquare(file, row - 1) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 1);
					movesSize[0]++;
					if (row == 6 && board.getSquare(file, row - 2) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 2);
						movesSize[0]++;
					}
				}
				
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row - 1)) > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
						movesSize[0]++;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 9); // capturedPiece > 0
						movesSize[0]++;
					}
				}
				
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row - 1)) > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
						movesSize[0]++;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 7);
						movesSize[0]++;
					}
				}

				if (row == 3) {
					if ((file * 8 + row) + 7 == board.getEnPassant()) {
						captures[1][++captures[1][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 7);
						movesSize[0]++;
					} else if ((file * 8 + row) - 9 == board.getEnPassant()) {
						captures[1][++captures[1][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 9);
						movesSize[0]++;
					}
				}
			} else if (row == 1) { // promotion
				if (board.getSquare(file, row - 1) == 0) {
					for (int piece = 5; piece > 1; piece--) {
						captures[piece][++captures[piece][0]] = (1 << 15) + (startSquare << 9) + ((startSquare - 1) << 3) + piece; // priority according to material gain
						movesSize[0]++;
					}
				}
				
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row - 1)) > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
						movesSize[0]++;
					} else {
						for (int piece = 5; piece > 1; piece--) {
							captures[5][++captures[5][0]] = (1 << 15) + (startSquare << 9) + ((startSquare - 9) << 3) + piece; // always best priority for capture-promotion
							movesSize[0]++;
						}
					}
				}
				
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row - 1)) > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
						movesSize[0]++;
					} else {
						for (int piece = 5; piece > 1; piece--) {
							captures[5][++captures[5][0]] = (1 << 15) + (startSquare << 9) + ((startSquare + 7) << 3) + piece;
							movesSize[0]++;
						}
					}
				}
			} else {
				assert false;
			}
		}
	}

	/**
	 * Generate all Knight-moves by checking which moves don't "capture" our own pieces
	 * and whether we still are within the bounds of the board.
	 *
	 * @param toMove who to move it is
	 * @param movesSize array that holds the number of legal moves that each piece type has. Does include king moves into check.
	 */
	private void knightMove(boolean toMove, int[] movesSize) {
		if (toMove) {
			if (file > 0 && row > 1) {
				if ((capturedPiece = board.square[file - 1][row - 2]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 10);
					movesSize[1]++;
				} else if (capturedPiece < 0) {
					if ((capturedPieceValue = -capturedPiece) == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 10);
					}
					movesSize[1]++;
				}
			}

			if (file > 0 && row < 6) {
				if ((capturedPiece = board.square[file - 1][row + 2]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 6);
					movesSize[1]++;
				} else if (capturedPiece < 0) {
					if ((capturedPieceValue = -capturedPiece) == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 6);
					}
					movesSize[1]++;
				}
			}

			if (file > 1 && row > 0) {
				if ((capturedPiece = board.square[file - 2][row - 1]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 17);
					movesSize[1]++;
				} else if (capturedPiece < 0) {
					if ((capturedPieceValue = -capturedPiece) == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 17);
					}
					movesSize[1]++;
				}
			}

			if (file > 1 && row < 7) {
				if ((capturedPiece = board.square[file - 2][row + 1]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 15);
					movesSize[1]++;
				} else if (capturedPiece < 0) {
					if ((capturedPieceValue = -capturedPiece) == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 15);
					}
					movesSize[1]++;
				}
			}

			if (file < 6 && row > 0) {
				if ((capturedPiece = board.square[file + 2][row - 1]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 15);
					movesSize[1]++;
				} else if (capturedPiece < 0) {
					if ((capturedPieceValue = -capturedPiece) == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 15);
					}
					movesSize[1]++;
				}
			}

			if (file < 6 && row < 7) {
				if ((capturedPiece = board.square[file + 2][row + 1]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 17);
					movesSize[1]++;
				} else if (capturedPiece < 0) {
					if ((capturedPieceValue = -capturedPiece) == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 17);
					}
					movesSize[1]++;
				}
			}

			if (file < 7 && row > 1) {
				if ((capturedPiece = board.square[file + 1][row - 2]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 6);
					movesSize[1]++;
				} else if (capturedPiece < 0) {
					if ((capturedPieceValue = -capturedPiece) == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 6);
					}
					movesSize[1]++;
				}
			}

			if (file < 7 && row < 6) {
				if ((capturedPiece = board.square[file + 1][row + 2]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 10);
					movesSize[1]++;
				} else if (capturedPiece < 0) {
					if ((capturedPieceValue = -capturedPiece) == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 10);
					}
					movesSize[1]++;
				}
			}
		} else {
			if (file > 0 && row > 1) {
				if ((capturedPiece = board.square[file - 1][row - 2]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 10);
					movesSize[1]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 10); // capturedPiece = |capturedPiece|
					}
					movesSize[1]++;
				}
			}

			if (file > 0 && row < 6) {
				if ((capturedPiece = board.square[file - 1][row + 2]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 6);
					movesSize[1]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 6);
					}
					movesSize[1]++;
				}
			}

			if (file > 1 && row > 0) {
				if ((capturedPiece = board.square[file - 2][row - 1]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 17);
					movesSize[1]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 17);
					}
					movesSize[1]++;
				}
			}

			if (file > 1 && row < 7) {
				if ((capturedPiece = board.square[file - 2][row + 1]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 15);
					movesSize[1]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 15);
					}
					movesSize[1]++;
				}
			}

			if (file < 6 && row > 0) {
				if ((capturedPiece = board.square[file + 2][row - 1]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 15);
					movesSize[1]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 15);
					}
					movesSize[1]++;
				}
			}

			if (file < 6 && row < 7) {
				if ((capturedPiece = board.square[file + 2][row + 1]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 17);
					movesSize[1]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 17);
					}
					movesSize[1]++;
				}
			}

			if (file < 7 && row > 1) {
				if ((capturedPiece = board.square[file + 1][row - 2]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 6);
					movesSize[1]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 6);
					}
					movesSize[1]++;
				}
			}

			if (file < 7 && row < 6) {
				if ((capturedPiece = board.square[file + 1][row + 2]) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 10);
					movesSize[1]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 10);
					}
					movesSize[1]++;
				}
			}
		}
	}
	
	/**
	 * Generate all legal rook moves by checking how far we can move
	 * so that we still are on the board and don't move through pieces.
	 *
	 * @param toMove : who to move it is
	 */
	private void rookMove(boolean toMove, boolean queen, int[] movesSize) {
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), row, board, toMove); // TODO maybe avoid using this method for perf reasons
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.square[file + i][row])) == 6) {
					captures[0][0] = -1;
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				} else {
					captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + startSquare + (i << 3);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare + (i << 3);
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
				if ((capturedPieceValue = Math.abs(board.square[file - i][row])) == 6) {
					captures[0][0] = -1;
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				} else {
					captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + startSquare - (i << 3);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare - (i << 3);
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
				if ((capturedPieceValue = Math.abs(board.square[file][row + i])) == 6) {
					captures[0][0] = -1;
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				} else {
					captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + i);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + i);
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
				if ((capturedPieceValue = Math.abs(board.square[file][row - i])) == 6) {
					captures[0][0] = -1;
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				} else {
					captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - i);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[3]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - i);
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
	 * @param toMove : who to move it is
	 */
	private void bishopMove(boolean toMove, boolean queen, int[] movesSize) {
		for (byte i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.square[file + i][row + i])) == 6) {
					captures[0][0] = -1;
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				} else {
					captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + (i << 3) + i);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + (i << 3) + i);
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
				if ((capturedPieceValue = Math.abs(board.square[file - i][row - i])) == 6) {
					captures[0][0] = -1;
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				} else {
					captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - (i << 3) - i);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - (i << 3) - i);
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
				if ((capturedPieceValue = Math.abs(board.square[file + i][row - i])) == 6) {
					captures[0][0] = -1;
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				} else {
					captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + (i << 3) - i);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + (i << 3) - i);
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
				if ((capturedPieceValue = Math.abs(board.square[file - i][row + i])) == 6) {
					captures[0][0] = -1;
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				} else {
					captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - (i << 3) + i);
					if (queen) {
						movesSize[4]++;
					} else {
						movesSize[2]++;
					}
				}
				break;
			} else if (squareValue == 1) {
				nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - (i << 3) + i);
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
	 * @param toMove : who to move it is
	 */
	private void kingMove(boolean toMove, int[] movesSize) {
		if (toMove) {
			if (file > 0) {
				if ((capturedPiece = board.getSquare(file - 1, row)) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 8);
					movesSize[5]++;
				} else if (capturedPiece < 0) {
					if (capturedPiece == -6) {
						captures[0][0] = -1;
					} else {
						capturedPieceValue = -capturedPiece;
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 8);
					}
					movesSize[5]++;
				}

				if (row > 0) {
					if ((capturedPiece = board.getSquare(file - 1, row - 1)) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 9);
						movesSize[5]++;
					} else if (capturedPiece < 0) {
						if (capturedPiece == -6) {
							captures[0][0] = -1;
						} else {
							capturedPieceValue = -capturedPiece;
							captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 9);
						}
						movesSize[5]++;
					}
				}

				if (row < 7) {
					if ((capturedPiece = board.getSquare(file - 1, row + 1)) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 7);
						movesSize[5]++;
					} else if (capturedPiece < 0) {
						if (capturedPiece == -6) {
							captures[0][0] = -1;
						} else {
							capturedPieceValue = -capturedPiece;
							captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 7);
						}
						movesSize[5]++;
					}
				}
			}

			if (file < 7) {
				if ((capturedPiece = board.getSquare(file + 1, row)) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 8);
					movesSize[5]++;
				} else if (capturedPiece < 0) {
					if (capturedPiece == -6) {
						captures[0][0] = -1;
					} else {
						capturedPieceValue = -capturedPiece;
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 8);
					}
					movesSize[5]++;
				}

				if (row > 0) {
					if ((capturedPiece = board.getSquare(file + 1, row - 1)) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 7);
						movesSize[5]++;
					} else if (capturedPiece < 0) {
						if (capturedPiece == -6) {
							captures[0][0] = -1;
						} else {
							capturedPieceValue = -capturedPiece;
							captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 7);
						}
						movesSize[5]++;
					}
				}

				if (row < 7) {
					if ((capturedPiece = board.getSquare(file + 1, row + 1)) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 9);
						movesSize[5]++;
					} else if (capturedPiece < 0) {
						if (capturedPiece == -6) {
							captures[0][0] = -1;
						} else {
							capturedPieceValue = -capturedPiece;
							captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 9);
						}
						movesSize[5]++;
					}
				}
			}

			if (row > 0) {
				if ((capturedPiece = board.getSquare(file, row - 1)) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 1);
					movesSize[5]++;
				} else if (capturedPiece < 0) {
					if (capturedPiece == -6) {
						captures[0][0] = -1;
					} else {
						capturedPieceValue = -capturedPiece;
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 1);
					}
					movesSize[5]++;
				}
			}

			if (row < 7) {
				if ((capturedPiece = board.getSquare(file, row + 1)) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 1);
					movesSize[5]++;
				} else if (capturedPiece < 0) {
					if (capturedPiece == -6) {
						captures[0][0] = -1;
					} else {
						capturedPieceValue = -capturedPiece;
						captures[capturedPieceValue][++captures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 1);
					}
					movesSize[5]++;
				}
			}

			if (file == 4 && row == 0) {
				if ((board.getCastlingRights() & 0x18) == 0x18) {
					if (board.square[5][0] == 0 && board.square[6][0] == 0) {
						board.square[5][0] = 6;
						ArrayList<Integer> testLegality = collectCaptures(board, false);
						// TODO make this prettier; right now we don't check [6][0] for legality because we'll find out next move in case it wasn't
						if (testLegality.size() == 0 || testLegality.get(0) != -1) {
							nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare + (2 << 3);
							movesSize[5]++;
						}
						board.square[5][0] = 0;
					}
				}

				if (((board.getCastlingRights() & 0x30) == 0x30)) {
					if (board.square[3][0] == 0 && board.square[2][0] == 0 && board.square[1][0] == 0) {
						board.square[3][0] = 6;
						ArrayList<Integer> testLegality = collectCaptures(board, false);
						if (testLegality.size() == 0 || testLegality.get(0) != -1) {
							nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare - (2 << 3);
							movesSize[5]++;
						}
						board.square[3][0] = 0;
					}
				}
			}
		} else {
			if (file > 0) {
				if ((capturedPiece = board.getSquare(file - 1, row)) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 8);
					movesSize[5]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 8);
					}
					movesSize[5]++;
				}

				if (row > 0) {
					if ((capturedPiece = board.getSquare(file - 1, row - 1)) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 9);
						movesSize[5]++;
					} else if (capturedPiece > 0) {
						if (capturedPiece == 6) {
							captures[0][0] = -1;
						} else {
							captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 9);
						}
						movesSize[5]++;
					}
				}

				if (row < 7) {
					if ((capturedPiece = board.getSquare(file - 1, row + 1)) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 7);
						movesSize[5]++;
					} else if (capturedPiece > 0) {
						if (capturedPiece == 6) {
							captures[0][0] = -1;
						} else {
							captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 7);
						}
						movesSize[5]++;
					}
				}
			}

			if (file < 7) {
				if ((capturedPiece = board.getSquare(file + 1, row)) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 8);
					movesSize[5]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 8);
					}
					movesSize[5]++;
				}

				if (row > 0) {
					if ((capturedPiece = board.getSquare(file + 1, row - 1)) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 7);
						movesSize[5]++;
					} else if (capturedPiece > 0) {
						if (capturedPiece == 6) {
							captures[0][0] = -1;
						} else {
							captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 7);
						}
						movesSize[5]++;
					}
				}

				if (row < 7) {
					if ((capturedPiece = board.getSquare(file + 1, row + 1)) == 0) {
						nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 9);
						movesSize[5]++;
					} else if (capturedPiece > 0) {
						if (capturedPiece == 6) {
							captures[0][0] = -1;
						} else {
							captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 9);
						}
						movesSize[5]++;
					}
				}
			}

			if (row > 0) {
				if ((capturedPiece = board.getSquare(file, row - 1)) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare - 1);
					movesSize[5]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 1);
					}
					movesSize[5]++;
				}
			}

			if (row < 7) {
				if ((capturedPiece = board.getSquare(file, row + 1)) == 0) {
					nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + (startSquare + 1);
					movesSize[5]++;
				} else if (capturedPiece > 0) {
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						captures[capturedPiece][++captures[capturedPiece][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 1);
					}
					movesSize[5]++;
				}
			}

			if (file == 4 && row == 7) {
				if ((board.getCastlingRights() & 0x3) == 0x3) {
					if (board.square[5][7] == 0 && board.square[6][7] == 0) {
						board.square[5][7] = -6;
						ArrayList<Integer> testLegality = collectCaptures(board, true);
						if (testLegality.size() == 0 || testLegality.get(0) != -1) {
							nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare + (2 << 3);
							movesSize[5]++;
						}
						board.square[5][7] = 0;
					}
				}

				if (((board.getCastlingRights() & 0x6) == 0x6)) {
					if (board.square[3][7] == 0 && board.square[2][7] == 0 && board.square[1][7] == 0) {
						board.square[3][7] = -6;
						ArrayList<Integer> testLegality = collectCaptures(board, true);
						if (testLegality.size() == 0 || testLegality.get(0) != -1) {
							nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare - (2 << 3);
							movesSize[5]++;
						}
						board.square[3][7] = 0;
					}
				}
			}
		}
	}
	
	private static void pawnCapture(byte file, byte row, Board board, boolean toMove, ArrayList<Integer> captures,
			ArrayList<Integer> captureR, ArrayList<Integer> captureB,
			ArrayList<Integer> captureN, ArrayList<Integer> captureP) {
		if (toMove) {
			if (row > 0 && row < 6) { // no promotion
				if (file > 0 && board.getSquare(file - 1, row + 1) < 0) {
					if (board.getSquare(file - 1, row + 1) == -6) {
						captures.clear();
						captures.add(-1);
						return;
					} else if (board.getSquare(file - 1, row + 1) == -5) {
						captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					} else if (board.getSquare(file - 1, row + 1) == -4) {
						captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					} else if (board.getSquare(file - 1, row + 1) == -3) {
						captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					} else if (board.getSquare(file - 1, row + 1) == -2) {
						captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					} else if (board.getSquare(file - 1, row + 1) == -1) {
						captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					}
				}
				
				if (file < 7 && board.getSquare(file + 1, row + 1) < 0) {
					if (board.getSquare(file + 1, row + 1) == -6) {
						captures.clear();
						captures.add(-1);
					} else if (board.getSquare(file + 1, row + 1) == -5) {
						captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					} else if (board.getSquare(file + 1, row + 1) == -4) {
						captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					} else if (board.getSquare(file + 1, row + 1) == -3) {
						captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					} else if (board.getSquare(file + 1, row + 1) == -2) {
						captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					} else if (board.getSquare(file + 1, row + 1) == -1) {
						captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					}
				}
			} else if (row == 6) {
				if (file > 0 && board.getSquare(file - 1, row + 1) < 0) {
					for (int piece = 5; piece > 1; piece--) {
						if (board.getSquare(file - 1, row + 1) == -6) {
							captures.clear();
							captures.add(-1);
							return;
						} else if (board.getSquare(file - 1, row + 1) == -5) {
							captures.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row + 1) << 3) + piece);
						} else if (board.getSquare(file - 1, row + 1) == -4) {
							captureR.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row + 1) << 3) + piece);
						} else if (board.getSquare(file - 1, row + 1) == -3) {
							captureB.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row + 1) << 3) + piece);
						} else if (board.getSquare(file - 1, row + 1) == -2) {
							captureN.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row + 1) << 3) + piece);
						} else if (board.getSquare(file - 1, row + 1) == -1) {
							captureP.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row + 1) << 3) + piece);
						}
					}
				}
				
				if (file < 7 && board.getSquare(file + 1, row + 1) < 0) {
					for (int piece = 5; piece > 1; piece--) {
						if (board.getSquare(file + 1, row + 1) == -6) {
							captures.clear();
							captures.add(-1);
							return;
						} else if (board.getSquare(file + 1, row + 1) == -5) {
							captures.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row + 1) << 3) + piece);
						} else if (board.getSquare(file + 1, row + 1) == -4) {
							captureR.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row + 1) << 3) + piece);
						} else if (board.getSquare(file + 1, row + 1) == -3) {
							captureB.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row + 1) << 3) + piece);
						} else if (board.getSquare(file + 1, row + 1) == -2) {
							captureN.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row + 1) << 3) + piece);
						} else if (board.getSquare(file + 1, row + 1) == -1) {
							captureP.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row + 1) << 3) + piece);
						}
					}
				}
			} else {
				assert false;
			}
		} else if (!toMove) {
			if (row > 1 && row < 7) { // no promotions
				if (file > 0 && board.getSquare(file - 1, row - 1) > 0) {
					if (board.getSquare(file - 1, row - 1) == 6) {
						captures.clear();
						captures.add(-1);
						return;
					} else if (board.getSquare(file - 1, row - 1) == 5) {
						captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					} else if (board.getSquare(file - 1, row - 1) == 4) {
						captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					} else if (board.getSquare(file - 1, row - 1) == 3) {
						captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					} else if (board.getSquare(file - 1, row - 1) == 2) {
						captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					} else if (board.getSquare(file - 1, row - 1) == 1) {
						captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					}
				}
				if (file < 7 && board.getSquare(file + 1, row - 1) > 0) {
					if (board.getSquare(file + 1, row - 1) == 6) {
						captures.clear();
						captures.add(-1);
					} else if (board.getSquare(file + 1, row - 1) == 5) {
						captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					} else if (board.getSquare(file + 1, row - 1) == 4) {
						captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					} else if (board.getSquare(file + 1, row - 1) == 3) {
						captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					} else if (board.getSquare(file + 1, row - 1) == 2) {
						captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					} else if (board.getSquare(file + 1, row - 1) == 1) {
						captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					}
				}
			} else if (row == 1) {
				if (file > 0 && board.getSquare(file - 1, row - 1) > 0) {
					for (int piece = 5; piece > 1; piece--) {
						if (board.getSquare(file - 1, row - 1) == 6) {
							captures.clear();
							captures.add(-1);
							return;
						} else if (board.getSquare(file - 1, row - 1) == 5) {
							captures.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row - 1) << 3) + piece);
						} else if (board.getSquare(file - 1, row - 1) == 4) {
							captureR.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row - 1) << 3) + piece);
						} else if (board.getSquare(file - 1, row - 1) == 3) {
							captureB.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row - 1) << 3) + piece);
						} else if (board.getSquare(file - 1, row - 1) == 2) {
							captureN.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row - 1) << 3) + piece);
						} else if (board.getSquare(file - 1, row - 1) == 1) {
							captureP.add((1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row - 1) << 3) + piece);
						}
					}
				}
				if (file < 7 && board.getSquare(file + 1, row - 1) > 0) {
					for (int piece = 5; piece > 1; piece--) {
						if (board.getSquare(file + 1, row - 1) == 6) {
							captures.clear();
							captures.add(-1);
							return;
						} else if (board.getSquare(file + 1, row - 1) == 5) {
							captures.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row - 1) << 3) + piece);
						} else if (board.getSquare(file + 1, row - 1) == 4) {
							captureR.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row - 1) << 3) + piece);
						} else if (board.getSquare(file + 1, row - 1) == 3) {
							captureB.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row - 1) << 3) + piece);
						} else if (board.getSquare(file + 1, row - 1) == 2) {
							captureN.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row - 1) << 3) + piece);
						} else if (board.getSquare(file + 1, row - 1) == 1) {
							captureP.add((1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row - 1) << 3) + piece);
						}
					}
				}
			} else {
				assert false;
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 2));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 2));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 2));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 2));
				} else if (Math.abs(board.square[file - 1][row - 2]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 2));
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 2));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 2));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 2));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 2));
				} else if (Math.abs(board.square[file - 1][row + 2]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 2));
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row - 1));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row - 1));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row - 1));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row - 1));
				} else if (Math.abs(board.square[file - 2][row - 1]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row - 1));
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row + 1));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row + 1));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row + 1));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row + 1));
				} else if (Math.abs(board.square[file - 2][row + 1]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row + 1));
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row - 1));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row - 1));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row - 1));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row - 1));
				} else if (Math.abs(board.square[file + 2][row - 1]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row - 1));
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row + 1));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row + 1));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row + 1));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row + 1));
				} else if (Math.abs(board.square[file + 2][row + 1]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row + 1));
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 2));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 2));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 2));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 2));
				} else if (Math.abs(board.square[file + 1][row - 2]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 2));
				}
			}
		}
		if (file < 7 && row < 6) {
			if ((toMove && board.square[file + 1][row + 2] < 0) 
					|| ((!toMove) && board.square[file + 1][row + 2] > 0)) {
				
				if (Math.abs(board.square[file + 1][row + 2]) == 6) {
					captures.clear();
					captures.add(-1);
				} else if (Math.abs(board.square[file + 1][row + 2]) == 5) {
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 2));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 2));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 2));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 2));
				} else if (Math.abs(board.square[file + 1][row + 2]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 2));
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
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), row, board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + i][row]) == 5) {
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + row);
				} else if (Math.abs(board.square[file + i][row]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + row);
				} else if (Math.abs(board.square[file + i][row]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + row);
				} else if (Math.abs(board.square[file + i][row]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + row);
				} else if (Math.abs(board.square[file + i][row]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + row);
				}
				break;
			} else if (squareValue == 1) {
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + row);
				} else if (Math.abs(board.square[file - i][row]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + row);
				} else if (Math.abs(board.square[file - i][row]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + row);
				} else if (Math.abs(board.square[file - i][row]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + row);
				} else if (Math.abs(board.square[file - i][row]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + row);
				}
				break;
			} else if (squareValue == 1) {
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + i));
				} else if (Math.abs(board.square[file][row + i]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + i));
				} else if (Math.abs(board.square[file][row + i]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + i));
				} else if (Math.abs(board.square[file][row + i]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + i));
				} else if (Math.abs(board.square[file][row + i]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + i));
				}
				break;
			} else if (squareValue == 1) {
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - i));
				} else if (Math.abs(board.square[file][row - i]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - i));
				} else if (Math.abs(board.square[file][row - i]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - i));
				} else if (Math.abs(board.square[file][row - i]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - i));
				} else if (Math.abs(board.square[file][row - i]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - i));
				}
				break;
			} else if (squareValue == 1) {
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
		for (byte i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if (Math.abs(board.square[file + i][row + i]) == 6) {
					captures.clear();
					captures.add(-1);
					return;
				} else if (Math.abs(board.square[file + i][row + i]) == 5) {
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row + i));
				} else if (Math.abs(board.square[file + i][row + i]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row + i));
				} else if (Math.abs(board.square[file + i][row + i]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row + i));
				} else if (Math.abs(board.square[file + i][row + i]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row + i));
				} else if (Math.abs(board.square[file + i][row + i]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row + i));
				}
				break;
			} else if (squareValue == 1) {
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row - i));
				} else if (Math.abs(board.square[file - i][row - i]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row - i));
				} else if (Math.abs(board.square[file - i][row - i]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row - i));
				} else if (Math.abs(board.square[file - i][row - i]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row - i));
				} else if (Math.abs(board.square[file - i][row - i]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row - i));
				}
				break;
			} else if (squareValue == 1) {
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row - i));
				} else if (Math.abs(board.square[file + i][row - i]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row - i));
				} else if (Math.abs(board.square[file + i][row - i]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row - i));
				} else if (Math.abs(board.square[file + i][row - i]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row - i));
				} else if (Math.abs(board.square[file + i][row - i]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row - i));
				}
				break;
			} else if (squareValue == 1) {
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row + i));
				} else if (Math.abs(board.square[file - i][row + i]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row + i));
				} else if (Math.abs(board.square[file - i][row + i]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row + i));
				} else if (Math.abs(board.square[file - i][row + i]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row + i));
				} else if (Math.abs(board.square[file - i][row + i]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row + i));
				}
				break;
			} else if (squareValue == 1) {
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + row);
				} else if (Math.abs(board.square[file - 1][row]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + row);
				} else if (Math.abs(board.square[file - 1][row]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + row);
				} else if (Math.abs(board.square[file - 1][row]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + row);
				} else if (Math.abs(board.square[file - 1][row]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + row);
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
						captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 4) {
						captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 3) {
						captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 2) {
						captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
					} else if (Math.abs(board.square[file - 1][row - 1]) == 1) {
						captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1));
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
						captures.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 4) {
						captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 3) {
						captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 2) {
						captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
					} else if (Math.abs(board.square[file - 1][row + 1]) == 1) {
						captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1));
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + row);
				} else if (Math.abs(board.square[file + 1][row]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + row);
				} else if (Math.abs(board.square[file + 1][row]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + row);
				} else if (Math.abs(board.square[file + 1][row]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + row);
				} else if (Math.abs(board.square[file + 1][row]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + row);
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
						captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 4) {
						captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 3) {
						captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 2) {
						captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
					} else if (Math.abs(board.square[file + 1][row - 1]) == 1) {
						captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1));
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
						captures.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 4) {
						captureR.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 3) {
						captureB.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 2) {
						captureN.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
					} else if (Math.abs(board.square[file + 1][row + 1]) == 1) {
						captureP.add((1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1));
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
					captures.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - 1));
				} else if (Math.abs(board.square[file][row - 1]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - 1));
				} else if (Math.abs(board.square[file][row - 1]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - 1));
				} else if (Math.abs(board.square[file][row - 1]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - 1));
				} else if (Math.abs(board.square[file][row - 1]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - 1));
				}
			}
		}
		if (row < 7) {
			if ((toMove && board.getSquare(file, row + 1) < 0) || (!toMove && board.getSquare(file, row + 1) > 0)) {
				if (Math.abs(board.square[file][row + 1]) == 6) {
					captures.clear();
					captures.add(-1);
				} else if (Math.abs(board.square[file][row + 1]) == 5) {
					captures.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + 1));
				} else if (Math.abs(board.square[file][row + 1]) == 4) {
					captureR.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + 1));
				} else if (Math.abs(board.square[file][row + 1]) == 3) {
					captureB.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + 1));
				} else if (Math.abs(board.square[file][row + 1]) == 2) {
					captureN.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + 1));
				} else if (Math.abs(board.square[file][row + 1]) == 1) {
					captureP.add((1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + 1));
				}
			}
		}
		
	}

	/**
	 *
	 * @param storage Give this an unused int-Array of size 256
	 * @param toMove For which side we want to calculate the activity.
	 * @return int[] containing the number of legal moves the pieces from 0 (pawn) to 5 (king) have.
	 */
	int[] activityEval(int[] storage, boolean toMove) {
		int[] movesSize = new int[6];
		collectMoves(toMove, storage, movesSize);
		return movesSize;
	}
	
	int[] collectAllPNMoves(int[] storage, Board board, boolean toMove) {
		int[] movesSize = new int[6];
		storage = collectMoves(toMove, storage, movesSize);
		if (storage[0] == -1) {
			return storage;
		}
		
		for (int index = 1; index <= storage[0]; index++) { // storage[0] = actual size of array excluding that entry itself
			int move = storage[index];
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (MateFinder.inCheck(board)) {
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
											// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
		}
		return storage;
	}
	
	int[] collectPNSearchMoves(int[] storage, int[] checks, Board board, boolean toMove) {
		int[] movesSize = new int[6];
		storage = collectMoves(toMove, storage, movesSize);
		if (storage[0] == -1) {
			return storage;
		}
		checks[0] = 0;
		for (int index = 1; index <= storage[storage[0]]; index++) { // storage[0] = actual size of array excluding that entry itself
			int move = storage[index];
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (MateFinder.inCheck(board)) {
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
											// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			} else {
				board.changeToMove();
				if (MateFinder.inCheck(board)) {
					checks[++checks[0]] = move;
					if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
												// in case index is the last element we simply reduce the size by one to delete it
						storage[index] = storage[storage[0]];
					}
					storage[0]--;
					index--;
				}
				board.changeToMove();
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
		}
		System.arraycopy(storage, 1, checks, checks[0] + 1, storage[0]); // add non-checks to the checks; all we did here was move ordering the checks to the front
		checks[0] += storage[0];
		return checks;
	}
	
	int[] collectCheckMoves(int[] storage, int[] checks, Board board, boolean toMove) {
		int[] movesSize = new int[6];
		storage = collectMoves(toMove, storage, movesSize);
		checks[0] = 0;
		if (storage[0] == -1) {
			return checks;
		}
		for (int index = 1; index <= storage[0]; index++) { // storage[0] = actual size of array excluding that entry itself
			int move = storage[index];
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (MateFinder.inCheck(board)) {
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
											// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			} else {
				board.changeToMove();
				if (MateFinder.inCheck(board)) {
					checks[++checks[0]] = move;
					if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
												// in case index is the last element we simply reduce the size by one to delete it
						storage[index] = storage[storage[0]];
					}
					storage[0]--;
					index--;
				}
				board.changeToMove();
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
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
	 * @return 1 if the square is empty, 0 if the square is occupied by an enemy piece,
	 *  -1 if the square is either not on the board or occupied by a friendly piece
	 */
	private static byte isFreeSquare(byte file, byte row, Board board, boolean toMove) {
		byte isFree;
		if (file < 0 || file > 7 || row < 0 || row > 7) {
			isFree = -1;
		} else {
			byte squareValue = board.getSquare(file, row);
			if (toMove) {
				if (squareValue == 0) {
					isFree = 1;
				} else if (squareValue < 0) {
					isFree = 0;
				} else {
					isFree = -1;
				}
			} else {
				if (squareValue == 0) {
					isFree = 1;
				} else if (squareValue > 0) {
					isFree = 0;
				} else {
					isFree = -1;
				}
			}
		}
		return isFree;
	}
}
