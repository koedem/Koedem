package Main.engine;

import Main.Utility.Constants;
import Main.engineIO.UCI;

import java.util.Random;

/**
 * 
 * @author Anon
 *
 */
public class MoveGenerator implements MoveGeneratorInterface {

	/**
	 * The board for which we want to generate moves.
	 */
	private BoardInterface board;

	/**
	 * The maximum number of legal moves (+1) we expect a position to have. 218 + 1 = 219 for normal chess, rounded up to the next power of 2.
	 */
	public static final int MAX_MOVE_COUNT = 256;

	/**
	 * 2D array to put captures in. First index is the piece that gets captured. (1 = pawn, 2 = knight etc.)
	 *                 captures[i][0] stores the number of capture moves in that array. captures[0] should be empty
	 *                 except captures[0][0] == -1 if the position was illegal (king capture possible or similar)
	 */
	private final int[][] captures = new int[6][64]; // hopefully at max 64 captures for a single captured piece type; captures[0] isn't used

    /**
     * Array to put non-capture moves in. nonCaptures[0] stores the number of non capture moves.
     */
    private final int[] nonCaptures = new int[MAX_MOVE_COUNT];

    /**
     * Array to put captures to test for castling legality in.
     */
    private final int[] castlingTestCaptures = new int[MAX_MOVE_COUNT];

	private static Random random = new Random();
	private static boolean randomize = true;

	public MoveGenerator(BoardInterface board) {
		this.board = board;
	}

	/**
	 * @param toMove : who to move it is
	 * @return array of ints, each containing a move
	 */
	public int[] collectMoves(boolean toMove, int[] allMoves, int[] movesSize) {
		if (UCI.CHECKS_ARE_FORCING && board.getAttackBoard().inCheck(toMove)) { // TODO: both versions, especially evade checks, are slow, should return one move at a time
			return evadeChecks(toMove, allMoves, movesSize);                	// instead of an entire array that might be mostly wasted
		} else {
			return generateAllMoves(toMove, allMoves, movesSize);
		}
	}

	private int[] generateAllMoves(boolean toMove, int[] allMoves, int[] movesSize) {
		for (int piece = 0; piece < 6; piece++) {
			movesSize[piece] = 0;
			captures[piece][0] = 0;
		}
		nonCaptures[0] = 0;

		byte startSquare = 0;

		for (byte file = 0; file < 8; file++) {
			for (byte row = 0; row < 8; row++) {
				if (toMove) {
					switch (board.getSquare(file, row)) {
						case 1:
							pawnMove(true, row, file, startSquare, movesSize);
							break;
						case 2:
							knightMove(true, row, file, startSquare, movesSize);
							break;
						case 3:
							bishopMove(true, row, file, startSquare, false, movesSize);
							break;
						case 4:
							rookMove(true, row, file, startSquare, false, movesSize);
							break;
						case 5: // queen moves like rook + bishop
							rookMove(true, row, file, startSquare, true, movesSize);
							bishopMove(true, row, file, startSquare, true, movesSize);
							break;
						case 6:
							kingMove(true, row, file, startSquare, movesSize);
							break;
					}
				} else {
					switch (board.getSquare(file, row)) {
						case -1:
							pawnMove(false, row, file, startSquare, movesSize);
							break;
						case -2:
							knightMove(false, row, file, startSquare, movesSize);
							break;
						case -3:
							bishopMove(false, row, file, startSquare, false, movesSize);
							break;
						case -4:
							rookMove(false, row, file, startSquare, false, movesSize);
							break;
						case -5: // queen moves like rook + bishop
							rookMove(false, row, file, startSquare, true, movesSize);
							bishopMove(false, row, file, startSquare, true, movesSize);
							break;
						case -6:
							kingMove(false, row, file, startSquare, movesSize);
							break;
					}
				}
				++startSquare;
			}
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
			if (randomize) {
				shuffle(nonCaptures, nonCaptures[0]);
			}
			System.arraycopy(nonCaptures, 1, allMoves, allMoves[0] + 1, nonCaptures[0]);
			allMoves[0] += nonCaptures[0];
		}

		return allMoves;
	}
	
	/**
	 * Generate pawn moves by checking if they can move and/or capture.
	 *
	 * @param toMove who to move it is
	 * @param movesSize array that holds the number of legal moves that each piece type has. Does include king moves into check.
	 */
	private void pawnMove(boolean toMove, byte row, byte file, byte startSquare, int[] movesSize) {
	    int capturedPiece, capturedPieceValue;
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
						captures[1][++captures[1][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 7); // Not counting e.p. for activity eval doesn't make a
																											// big difference and simplifies AttackBoard activity eval
					} else if ((file * 8 + row) + 9 == board.getEnPassant()) {
						captures[1][++captures[1][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 9); // Not counting e.p. for activity eval doesn't make a
																											// big difference and simplifies AttackBoard activity eval
					}
				}
			} else if (row == 6) { // promotion
				if (board.getSquare(file, row + 1) == 0) {
					movesSize[0]++; // only count once to simplify AttackBoard based activity eval
					for (int piece = 5; piece > 1; piece--) { // TODO outsource variable?!
						captures[piece][++captures[piece][0]] = (1 << 15) + (startSquare << 9) + ((startSquare + 1) << 3) + piece; // priority according to material "gain"
					}
				}
				
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row + 1)) < 0) {
					movesSize[0]++; // only count once to simplify AttackBoard based activity eval
					if (capturedPiece == -6) {
						captures[0][0] = -1;
					} else {
						for (int piece = 5; piece > 1; piece--) {
							captures[5][++captures[5][0]] = (1 << 15) + (startSquare << 9) + ((startSquare - 7) << 3) + piece; // capturing while promoting always best priority
						}
					}
				}
				
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row + 1)) < 0) {
					movesSize[0]++; // only count once to simplify AttackBoard based activity eval
					if (capturedPiece == -6) {
						captures[0][0] = -1;
					} else {
						for (int piece = 5; piece > 1; piece--) {
							captures[5][++captures[5][0]] = (1 << 15) + (startSquare << 9) + ((startSquare + 9) << 3) + piece;
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
						captures[1][++captures[1][0]] = (1 << 12) + (startSquare << 6) + (startSquare + 7); // Not counting e.p. for activity eval doesn't make a
																											// big difference and simplifies AttackBoard activity eval
					} else if ((file * 8 + row) - 9 == board.getEnPassant()) {
						captures[1][++captures[1][0]] = (1 << 12) + (startSquare << 6) + (startSquare - 9); // Not counting e.p. for activity eval doesn't make a
																											// big difference and simplifies AttackBoard activity eval
					}
				}
			} else if (row == 1) { // promotion
				if (board.getSquare(file, row - 1) == 0) {
					movesSize[0]++; // only count once to simplify AttackBoard based activity eval
					for (int piece = 5; piece > 1; piece--) {
						captures[piece][++captures[piece][0]] = (1 << 15) + (startSquare << 9) + ((startSquare - 1) << 3) + piece; // priority according to material gain
					}
				}
				
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row - 1)) > 0) {
					movesSize[0]++; // only count once to simplify AttackBoard based activity eval
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						for (int piece = 5; piece > 1; piece--) {
							captures[5][++captures[5][0]] = (1 << 15) + (startSquare << 9) + ((startSquare - 9) << 3) + piece; // always best priority for capture-promotion
						}
					}
				}
				
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row - 1)) > 0) {
					movesSize[0]++; // only count once to simplify AttackBoard based activity eval
					if (capturedPiece == 6) {
						captures[0][0] = -1;
					} else {
						for (int piece = 5; piece > 1; piece--) {
							captures[5][++captures[5][0]] = (1 << 15) + (startSquare << 9) + ((startSquare + 7) << 3) + piece;
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
	private void knightMove(boolean toMove, byte row, byte file, byte startSquare, int[] movesSize) {
        int capturedPiece, capturedPieceValue;
		if (toMove) {
			if (file > 0 && row > 1) {
				if ((capturedPiece = board.getSquare(file - 1, row - 2)) == 0) {
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
				if ((capturedPiece = board.getSquare(file - 1, row + 2)) == 0) {
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
				if ((capturedPiece = board.getSquare(file - 2, row - 1)) == 0) {
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
				if ((capturedPiece = board.getSquare(file - 2, row + 1)) == 0) {
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
				if ((capturedPiece = board.getSquare(file + 2, row - 1)) == 0) {
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
				if ((capturedPiece = board.getSquare(file + 2, row + 1)) == 0) {
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
				if ((capturedPiece = board.getSquare(file + 1, row - 2)) == 0) {
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
				if ((capturedPiece = board.getSquare(file + 1, row + 2)) == 0) {
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
				if ((capturedPiece = board.getSquare(file - 1, row - 2)) == 0) {
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
				if ((capturedPiece = board.getSquare(file - 1, row + 2)) == 0) {
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
				if ((capturedPiece = board.getSquare(file - 2, row - 1)) == 0) {
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
				if ((capturedPiece = board.getSquare(file - 2, row + 1)) == 0) {
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
				if ((capturedPiece = board.getSquare(file + 2, row - 1)) == 0) {
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
				if ((capturedPiece = board.getSquare(file + 2, row + 1)) == 0) {
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
				if ((capturedPiece = board.getSquare(file + 1, row - 2)) == 0) {
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
				if ((capturedPiece = board.getSquare(file + 1, row + 2)) == 0) {
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
	private void rookMove(boolean toMove, byte row, byte file, byte startSquare, boolean queen, int[] movesSize) {
        int capturedPiece, capturedPieceValue;
		for (int i = 1; i < 8; i++) {
			int squareValue = board.isFreeSquare(file + i, row, toMove); // TODO maybe avoid using this method for perf reasons
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file + i, row))) == 6) {
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
			int squareValue = board.isFreeSquare(file - i, row, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file - i, row))) == 6) {
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
			int squareValue = board.isFreeSquare(file, row + i, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file, row + i))) == 6) {
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
			int squareValue = board.isFreeSquare(file, row - i, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file, row - i))) == 6) {
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
	private void bishopMove(boolean toMove, byte row, byte file, byte startSquare, boolean queen, int[] movesSize) {
        int capturedPiece, capturedPieceValue;
		for (byte i = 1; i < 8; i++) {
			int squareValue = board.isFreeSquare(file + i, row + i, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file + i, row + i))) == 6) {
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
			int squareValue = board.isFreeSquare(file - i, row - i, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file - i, row - i))) == 6) {
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
			int squareValue = board.isFreeSquare(file + i, row - i, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file + i, row - i))) == 6) {
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
			int squareValue = board.isFreeSquare(file - i, row + i, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file - i, row + i))) == 6) {
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
	private void kingMove(boolean toMove, byte row, byte file, byte startSquare, int[] movesSize) {
        int capturedPiece, capturedPieceValue;
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
				if ((board.getCastlingRights() & 0x30) == 0x30) { // Q side castling
					for (int index = 0; index < 10; index++) {
						if (((0x1010101L << 8) & board.getAttackBoard().getAttackBoards()[0][4][index]) == (0x1010101L << 8)) {
																// i.e. nothing between rook and king, castling possible
							if (((0x101L << 24) & board.getAttackBoard().getAllPieces()[1]) == 0) { // i.e. black can't capture on e1 or d1 -> castling actually legal
								nonCaptures[++nonCaptures[0]] = (1 << 12) + (32 << 6) + 16; // note, still only pseudo legal; merely to match movegenerator
							}
						}
					}
				}
				if ((board.getCastlingRights() & 0x18) == 0x18) { // K side castling
					for (int index = 0; index < 10; index++) {
						if (((0x10101L << 32) & board.getAttackBoard().getAttackBoards()[0][4][index]) == (0x10101L << 32)) {
																// i.e. nothing between rook and king, castling possible
							if (((0x101L << 32) & board.getAttackBoard().getAllPieces()[1]) == 0) { // i.e. black can't capture on e1 or f1 -> castling actually legal
								nonCaptures[++nonCaptures[0]] = (1 << 12) + (32 << 6) + 48;
							}
						}
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
				if ((board.getCastlingRights() & 0x6) == 0x6) { // Q side castling
					for (int index = 0; index < 10; index++) {
						if (((0x1010101L << 15) & board.getAttackBoard().getAttackBoards()[1][4][index]) == (0x1010101L << 15)) {
																				// i.e. nothing between rook and king, castling possible
							if (((0x101L << 31) & board.getAttackBoard().getAllPieces()[0]) == 0) { // i.e. white can't capture on e8 or d8 -> castling actually legal
								nonCaptures[++nonCaptures[0]] = (1 << 12) + (39 << 6) + 23; // note, still only pseudo legal; merely to match movegenerator
							}
						}
					}
				}
				if ((board.getCastlingRights() & 0x3) == 0x3) { // K side castling
					for (int index = 0; index < 10; index++) {
						if (((0x10101L << 39) & board.getAttackBoard().getAttackBoards()[1][4][index]) == (0x10101L << 39)) {
																				// i.e. nothing between rook and king, castling possible
							if (((0x101L << 39) & board.getAttackBoard().getAllPieces()[0]) == 0) { // i.e. white can't capture on e8 or f8 -> castling actually legal
								nonCaptures[++nonCaptures[0]] = (1 << 12) + (39 << 6) + 55;
							}
						}
					}
				}
			}
		}
	}

	private int[] evadeChecks(boolean toMove, int[] moves, int[] movesSize) {
		generateAllMoves(toMove, moves, movesSize);
		for (int i = 1; i <= moves[0]; i++) {
			board.makeMove(moves[i]);
			boolean stillInCheck = board.getAttackBoard().inCheck(toMove);
			board.unmakeMove(moves[i]);
			if (stillInCheck) {
				moves[i] = moves[moves[0]--]; // overwrite non evading move
				i--;
			}
		}
		return moves;
	}

	/**
	 * Temporary method that detects checkmate, this should be implemented properly at some point, and probably belongs in AttackBoard.
	 * @param toMove
	 * @param moves
	 * @param movesSize
	 * @return
	 */
	public boolean isCheckmate(boolean toMove, int[] moves, int[] movesSize) {
		boolean canEvade = false;
		int toMoveInt = toMove ? 0 : 1;
		int kingSquare = Long.numberOfTrailingZeros(board.getBitboard().getPieceTypeBoard(toMoveInt, Constants.KING));
		board.getBitboard().remove(kingSquare, true);
		long attacked = board.getAttackBoard().getAllPieces()[toMove ? 1 : 0]; // which squares does the opponent attack?
		long nonAttacked = ~attacked & board.getAttackBoard().getPieceTypes()[toMoveInt][6]; // which unattacked squares can our king reach?
		long available = nonAttacked & ~board.getBitboard().getAllPieces(toMoveInt); // we can't capture own pieces
		board.getBitboard().add(Constants.KING, toMoveInt, kingSquare);
		canEvade = available != 0;
		if (!canEvade) {
			canEvade = evadeChecks(toMove, moves, movesSize)[0] != 0;
		}
		return !canEvade; // if we can evade it's not checkmate
	}

	/**
	 *
	 * @param storage Give this an unused int-Array of size 256
	 * @param toMove For which side we want to calculate the activity.
	 * @return int[] containing the number of legal moves the pieces from 0 (pawn) to 5 (king) have.
	 */
	public int[] activityEval(boolean toMove, int[] storage, int[]movesSize) {
		collectMoves(toMove, storage, movesSize);
		return movesSize;
	}

	private	static void shuffle(int[] array, int n) {
		// Loop over array.
		for (int i = 1; i < n + 1; i++) { // TODO document this, we start at 1
			// Get a random index of the array past the current index.
			// ... The argument is an exclusive bound.
			//     It will not go past the array's end.
			int randomValue = i + random.nextInt(n + 1 - i);
			// Swap the random element with the present element.
			int randomElement = array[randomValue];
			array[randomValue] = array[i];
			array[i] = randomElement;
		}
	}

	/**
	 * This method does nothing right now. If we ever have add state to the MoveGenerator we need to implement that state being resetted here.
	 */
	public void resetMoveGenerator() {

	}
}
