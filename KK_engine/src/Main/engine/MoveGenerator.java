package Main.engine;

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

	/**
	 * 2D array to put captures in. First index is the piece that gets captured. (1 = pawn, 2 = knight etc.)
	 *                 captures[i][0] stores the number of capture moves in that array. captures[0] should be empty
	 *                 except captures[0][0] == -1 if the position was illegal (king capture possible or similar)
	 */
	private final int[][] qSearchCaptures = new int[6][64]; // hopefully at max 64 captures for a single captured piece type; captures[0] isn't used


	public MoveGenerator(BoardInterface board) {
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
			System.arraycopy(nonCaptures, 1, allMoves, allMoves[0] + 1, nonCaptures[0]);
			allMoves[0] += nonCaptures[0];
		}

		return allMoves;
	}
	
	/**
	 *
	 * @param toMove : Who to move it is.
	 * @return ArrayList of Integers, containing all captures.
	 */
	public int[] collectCaptures(boolean toMove, int[] allCaptures) {
	    for (int piece = 0; piece < 6; piece++) {
	        qSearchCaptures[piece][0] = 0;
        }
		
		for (byte file = 0; file < 8; file++) { // don't use class variable file here to avoid "race conditions"
			for (byte row = 0; row < 8; row++) {
				if (toMove) {
                    switch (board.getSquare(file, row)) {
                        case 1:
                            pawnCapture(true, row, file);
                            break;
                        case 2:
                            knightCapture(true, row, file);
                            break;
                        case 3:
                            bishopCapture(true, row, file);
                            break;
                        case 4:
                            rookCapture(true, row, file);
                            break;
                        case 5:  // queen moves like rook + bishop
                            rookCapture(true, row, file);
                            bishopCapture(true, row, file);
                            break;
                        case 6:
                            kingCapture(true, row, file);
                            break;
                    }
				} else {
                    switch (board.getSquare(file, row)) {
                        case -1:
                            pawnCapture(false, row, file);
                            break;
                        case -2:
                            knightCapture(false, row, file);
                            break;
                        case -3:
                            bishopCapture(false, row, file);
                            break;
                        case -4:
                            rookCapture(false, row, file);
                            break;
                        case -5:  // queen moves like rook + bishop
                            rookCapture(false, row, file);
                            bishopCapture(false, row, file);
                            break;
                        case -6:
                            kingCapture(false, row, file);
                            break;
                    }
				}
			}
		}
		if (qSearchCaptures[0][0] == -1) {
			allCaptures[0] = -1;
		} else {
		    allCaptures[0] = 0;
		    for (int piece = qSearchCaptures.length - 1; piece > 0; piece--) {
		        int destPos = allCaptures[0] + 1; // plus one because allCaptures[0] doesn't store moves
                System.arraycopy(qSearchCaptures[piece], 1, allCaptures, destPos, qSearchCaptures[piece][0]);
                allCaptures[0] += qSearchCaptures[piece][0];
            }
        }

		return allCaptures;
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
			byte squareValue = isFreeSquare((byte) (file + i), row, board, toMove); // TODO maybe avoid using this method for perf reasons
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
			byte squareValue = isFreeSquare((byte) (file - i), row, board, toMove);
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
			byte squareValue = isFreeSquare(file, (byte) (row + i), board, toMove);
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
			byte squareValue = isFreeSquare(file, (byte) (row - i), board, toMove);
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
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row + i), board, toMove);
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
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row - i), board, toMove);
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
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row - i), board, toMove);
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
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row + i), board, toMove);
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
				if ((board.getCastlingRights() & 0x18) == 0x18) {
					if (board.getSquare(5, 0) == 0 && board.getSquare(6, 0) == 0) {
						board.setSquare(5, 0, (byte) 6);
						int castlingLegality[] = collectCaptures(false, castlingTestCaptures);
						// TODO make this prettier; right now we don't check [6][0] for legality because we'll find out next move in case it wasn't
						if (castlingLegality[0] != -1) {
							nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare + (2 << 3);
							movesSize[5]++;
						}
						board.setSquare(5, 0, (byte) 0);
					}
				}

				if (((board.getCastlingRights() & 0x30) == 0x30)) {
					if (board.getSquare(3, 0) == 0 && board.getSquare(2, 0) == 0 && board.getSquare(1, 0) == 0) {
						board.setSquare(3, 0, (byte) 6);
						int castlingLegality[] = collectCaptures(false, castlingTestCaptures);
						if (castlingLegality[0] != -1) {
							nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare - (2 << 3);
							movesSize[5]++;
						}
						board.setSquare(3, 0, (byte) 0);
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
					if (board.getSquare(5, 7) == 0 && board.getSquare(6, 7) == 0) {
						board.setSquare(5, 7, (byte) -6);
						int[] castlingLegality = collectCaptures(true, castlingTestCaptures);
						if (castlingLegality[0] != -1) {
							nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare + (2 << 3);
							movesSize[5]++;
						}
						board.setSquare(5, 7, (byte) 0);
					}
				}

				if (((board.getCastlingRights() & 0x6) == 0x6)) {
					if (board.getSquare(3, 7) == 0 && board.getSquare(2, 7) == 0 && board.getSquare(1, 7) == 0) {
						board.setSquare(3, 7, (byte) -6);
						int[] castlingLegality = collectCaptures(true, castlingTestCaptures);
						if (castlingLegality[0] != -1) {
							nonCaptures[++nonCaptures[0]] = (1 << 12) + (startSquare << 6) + startSquare - (2 << 3);
							movesSize[5]++;
						}
						board.setSquare(3, 7, (byte) 0);
					}
				}
			}
		}
	}
	
	private void pawnCapture(boolean toMove, byte row, byte file) {
        int capturedPiece, capturedPieceValue;
		if (toMove) {
			if (row > 0 && row < 6) { // no promotion
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row + 1)) < 0) {
					if (capturedPiece == -6) {
						qSearchCaptures[0][0] = -1;
						return;
					} else {
					    capturedPieceValue = -capturedPiece; // capturedPiece < 0 so -capturedPiece > 0 therefor legal array index
					    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]] = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1);
                    }
				}
				
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row + 1)) < 0) {
					if (capturedPiece == -6) {
						qSearchCaptures[0][0] = -1;
						return;
					} else {
					    capturedPieceValue = -capturedPiece;
						qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]] = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1);
					}
				}
			} else if (row == 6) {
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row + 1)) < 0) {
				    if (capturedPiece == -6) {
				        qSearchCaptures[0][0] = -1;
				        return;
                    } else {
				        capturedPieceValue = -capturedPiece;
                        for (int piece = 5; piece > 1; piece--) {
                            qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                    = (1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row + 1) << 3) + piece;
                        }
                    }
				}
				
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row + 1)) < 0) {
				    if (capturedPiece == -6) {
				        qSearchCaptures[0][0] = -1;
				        return;
                    } else {
				        capturedPieceValue = -capturedPiece;
				        for (int piece = 5; piece > 1; piece--) {
						    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                    = (1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row + 1) << 3) + piece;
						}
					}
				}
			} else {
				assert false;
			}
		} else {
			if (row > 1 && row < 7) { // no promotions
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row - 1)) > 0) {
					if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    } else {
					    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1);
					}
				}
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row - 1)) > 0) {
					if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    } else {
					    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1);
					}
				}
			} else if (row == 1) {
				if (file > 0 && (capturedPiece = board.getSquare(file - 1, row - 1)) > 0) {
				    if (capturedPiece == 6) {
				        qSearchCaptures[0][0] = -1;
				        return;
                    } else {
                        for (int piece = 5; piece > 1; piece--) {
                            qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                                    = (1 << 15) + (file << 12) + (row << 9) + ((file - 1) << 6) + ((row - 1) << 3) + piece;
                        }
                    }
				}
				if (file < 7 && (capturedPiece = board.getSquare(file + 1, row - 1)) > 0) {
				    if (capturedPiece == 6) {
				        qSearchCaptures[0][0] = -1;
				        return;
                    } else {
						for (int piece = 5; piece > 1; piece--) {
						    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                                    = (1 << 15) + (file << 12) + (row << 9) + ((file + 1) << 6) + ((row - 1) << 3) + piece;
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
	 * @param toMove : who to move it is
	 */
	private void knightCapture(boolean toMove, byte row, byte file) {
        int capturedPiece, capturedPieceValue;
	    if (toMove) {
            if (file > 0 && row > 1) {
                if ((capturedPiece = board.getSquare(file - 1, row - 2)) < 0) {
                    if (capturedPiece == -6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    capturedPieceValue = -capturedPiece;
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 2);
                }
            }
            if (file > 0 && row < 6) {
                if ((capturedPiece = board.getSquare(file - 1, row + 2)) < 0) {
                    if (capturedPiece == -6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    capturedPieceValue = -capturedPiece;
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 2);
                }
            }
            if (file > 1 && row > 0) {
                if ((capturedPiece = board.getSquare(file - 2, row - 1)) < 0) {
                    if (capturedPiece == -6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    capturedPieceValue = -capturedPiece;
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row - 1);
                }
            }
            if (file > 1 && row < 7) {
                if ((capturedPiece = board.getSquare(file - 2, row + 1)) < 0) {
                    if (capturedPiece == -6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    capturedPieceValue = -capturedPiece;
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row + 1);
                }
            }
            if (file < 6 && row > 0) {
                if ((capturedPiece = board.getSquare(file + 2, row - 1)) < 0) {
                    if (capturedPiece == -6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    capturedPieceValue = -capturedPiece;
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row - 1);
                }
            }
            if (file < 6 && row < 7) {
                if ((capturedPiece = board.getSquare(file + 2, row + 1)) < 0) {
                    if (capturedPiece == -6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    capturedPieceValue = -capturedPiece;
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row + 1);

                }
            }
            if (file < 7 && row > 1) {
                if ((capturedPiece = board.getSquare(file + 1, row - 2)) < 0) {
                    if (capturedPiece == -6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    capturedPieceValue = -capturedPiece;
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 2);

                }
            }
            if (file < 7 && row < 6) {
                if ((capturedPiece = board.getSquare(file + 1, row + 2)) < 0) {
                    if (capturedPiece == -6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    capturedPieceValue = -capturedPiece;
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 2);
                }
            }
        } else {
            if (file > 0 && row > 1) {
                if ((capturedPiece = board.getSquare(file - 1, row - 2)) > 0) {
                    if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 2);
                }
            }
            if (file > 0 && row < 6) {
                if ((capturedPiece = board.getSquare(file - 1, row + 2)) > 0) {
                    if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 2);
                }
            }
            if (file > 1 && row > 0) {
                if ((capturedPiece = board.getSquare(file - 2, row - 1)) > 0) {
                    if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row - 1);
                }
            }
            if (file > 1 && row < 7) {
                if ((capturedPiece = board.getSquare(file - 2, row + 1)) > 0) {
                    if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 2) << 3) + (row + 1);
                }
            }
            if (file < 6 && row > 0) {
                if ((capturedPiece = board.getSquare(file + 2, row - 1)) > 0) {
                    if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row - 1);
                }
            }
            if (file < 6 && row < 7) {
                if ((capturedPiece = board.getSquare(file + 2, row + 1)) > 0) {
                    if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 2) << 3) + (row + 1);
                }
            }
            if (file < 7 && row > 1) {
                if ((capturedPiece = board.getSquare(file + 1, row - 2)) > 0) {
                    if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 2);
                }
            }
            if (file < 7 && row < 6) {
                if ((capturedPiece = board.getSquare(file + 1, row + 2)) > 0) {
                    if (capturedPiece == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPiece][++qSearchCaptures[capturedPiece][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 2);
                }
            }
        }
	}
	
	/**
	 * Generate all legal rook captures.
	 *
	 * @param toMove : who to move it is
	 */
	private void rookCapture(boolean toMove, byte row, byte file) {
        int capturedPiece, capturedPieceValue;
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), row, board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file + i, row))) == 6) {
					qSearchCaptures[0][0] = -1;
					return;
				}
				qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                        = (1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + row;
				break;
			} else if (squareValue == 1) {
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), row, board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file - i, row))) == 6) {
                    qSearchCaptures[0][0] = -1;
                    return;
                }
                qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                        = (1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + row;
				break;
			} else if (squareValue == 1) {
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file, row + i))) == 6) {
					qSearchCaptures[0][0] = -1;
					return;
				}
				qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                        = (1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + i);
				break;
			} else if (squareValue == 1) {
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare(file, (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file, row - i))) == 6) {
					qSearchCaptures[0][0] = -1;
					return;
				}
				qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                        = (1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - i);
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
	 * @param toMove : who to move it is
	 */
	private void bishopCapture(boolean toMove, byte row, byte file) {
        int capturedPiece, capturedPieceValue;
		for (byte i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file + i, row + i))) == 6) {
					qSearchCaptures[0][0] = -1;
					return;
				}
				qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                        = (1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row + i);
				break;
			} else if (squareValue == 1) {
			} else {
				break;
			}
		}

		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file - i, row - i))) == 6) {
					qSearchCaptures[0][0] = -1;
					return;
				}
				qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                        = (1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row - i);
				break;
			} else if (squareValue == 1) {
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file + i), (byte) (row - i), board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file + i, row - i))) == 6) {
					qSearchCaptures[0][0] = -1;
					return;
				}
				qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                        = (1 << 12) + (file << 9) + (row << 6) + ((file + i) << 3) + (row - i);
				break;
			} else if (squareValue == 1) {
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 8; i++) {
			byte squareValue = isFreeSquare((byte) (file - i), (byte) (row + i), board, toMove);
			if (squareValue == 0) {
				if ((capturedPieceValue = Math.abs(board.getSquare(file - i, row + i))) == 6) {
					qSearchCaptures[0][0] = -1;
					return;
				}
				qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                        = (1 << 12) + (file << 9) + (row << 6) + ((file - i) << 3) + (row + i);
				break;
			} else if (squareValue == 1) {
			} else {
				break;
			}
		}
	}
	
	/**
	 *
	 * @param toMove : who to move it is
	 */
	private void kingCapture(boolean toMove, byte row, byte file) {
        int capturedPiece, capturedPieceValue;
	    if (toMove) {
            if (file > 0) {
                if (board.getSquare(file - 1, row) < 0) {
                    if ((capturedPieceValue = -(board.getSquare(file - 1, row))) == 6) { // opponent pieces are < 0 so we negate them
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + row;
                }
                if (row > 0) {
                    if (board.getSquare(file - 1, row - 1) < 0) {
                        if ((capturedPieceValue = -(board.getSquare(file - 1, row - 1))) == 6) {
                            qSearchCaptures[0][0] = -1;
                            return;
                        }
                        qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1);
                    }
                }
                if (row < 7) {
                    if (board.getSquare(file - 1, row + 1) < 0) {
                        if ((capturedPieceValue = -(board.getSquare(file - 1, row + 1))) == 6) {
                            qSearchCaptures[0][0] = -1;
                            return;
                        }
                        qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1);
                    }
                }
            }

            if (file < 7) {
                if (board.getSquare(file + 1, row) < 0) {
                    if ((capturedPieceValue = -(board.getSquare(file + 1, row))) == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + row;
                }
                if (row > 0) {
                    if (board.getSquare(file + 1, row - 1) < 0) {
                        if ((capturedPieceValue = -(board.getSquare(file + 1, row - 1))) == 6) {
                            qSearchCaptures[0][0] = -1;
                            return;
                        }
                        qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1);
                    }
                }
                if (row < 7) {
                    if (board.getSquare(file + 1, row + 1) < 0) {
                        if ((capturedPieceValue = -(board.getSquare(file + 1, row + 1))) == 6) {
                            qSearchCaptures[0][0] = -1;
                            return;
                        }
                        qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1);
                    }
                }
            }

            if (row > 0) {
                if (board.getSquare(file, row - 1) < 0) {
                    if ((capturedPieceValue = -(board.getSquare(file, row - 1))) == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - 1);
                }
            }
            if (row < 7) {
                if (board.getSquare(file, row + 1) < 0) {
                    if ((capturedPieceValue = -(board.getSquare(file, row + 1))) == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + 1);
                }
            }
        } else {
            if (file > 0) {
                if (board.getSquare(file - 1, row) > 0) {
                    if ((capturedPieceValue = (board.getSquare(file - 1, row))) == 6) { // enemy pieces are > 0 so no need to change anything
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + row;
                }
                if (row > 0) {
                    if (board.getSquare(file - 1, row - 1) > 0) {
                        if ((capturedPieceValue = (board.getSquare(file - 1, row - 1))) == 6) {
                            qSearchCaptures[0][0] = -1;
                            return;
                        }
                        qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row - 1);
                    }
                }
                if (row < 7) {
                    if (board.getSquare(file - 1, row + 1) > 0) {
                        if ((capturedPieceValue = (board.getSquare(file - 1, row + 1))) == 6) {
                            qSearchCaptures[0][0] = -1;
                            return;
                        }
                        qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file - 1) << 3) + (row + 1);
                    }
                }
            }

            if (file < 7) {
                if (board.getSquare(file + 1, row) > 0) {
                    if ((capturedPieceValue = (board.getSquare(file + 1, row))) == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + row;
                }
                if (row > 0) {
                    if (board.getSquare(file + 1, row - 1) > 0) {
                        if ((capturedPieceValue = (board.getSquare(file + 1, row - 1))) == 6) {
                            qSearchCaptures[0][0] = -1;
                            return;
                        }
                        qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row - 1);
                    }
                }
                if (row < 7) {
                    if (board.getSquare(file + 1, row + 1) > 0) {
                        if ((capturedPieceValue = (board.getSquare(file + 1, row + 1))) == 6) {
                            qSearchCaptures[0][0] = -1;
                            return;
                        }
                        qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                                = (1 << 12) + (file << 9) + (row << 6) + ((file + 1) << 3) + (row + 1);
                    }
                }
            }

            if (row > 0) {
                if (board.getSquare(file, row - 1) > 0) {
                    if ((capturedPieceValue = (board.getSquare(file, row - 1))) == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row - 1);
                }
            }
            if (row < 7) {
                if (board.getSquare(file, row + 1) > 0) {
                    if ((capturedPieceValue = (board.getSquare(file, row + 1))) == 6) {
                        qSearchCaptures[0][0] = -1;
                        return;
                    }
                    qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]]
                            = (1 << 12) + (file << 9) + (row << 6) + (file << 3) + (row + 1);
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
	public int[] activityEval(boolean toMove, int[] storage, int[]movesSize) {
		collectMoves(toMove, storage, movesSize);
		return movesSize;
	}
	
	public int[] collectAllPNMoves(int[] storage, BoardInterface board, boolean toMove) {
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
	
	public int[] collectPNSearchMoves(int[] storage, int[] checks, BoardInterface board, boolean toMove) {
		int[] movesSize = new int[6];
		storage = collectMoves(toMove, storage, movesSize);
		if (storage[0] == -1) {
			return storage;
		}
		checks[0] = 0;
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
		System.arraycopy(storage, 1, checks, checks[0] + 1, storage[0]); // add non-checks to the checks; all we did here was move ordering the checks to the front
		checks[0] += storage[0];
		return checks;
	}
	
	public int[] collectCheckMoves(int[] storage, int[] checks, BoardInterface board, boolean toMove) {
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
	private static byte isFreeSquare(byte file, byte row, BoardInterface board, boolean toMove) {
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

	/**
	 * This method does nothing right now. If we ever have add state to the MoveGenerator we need to implement that state being resetted here.
	 */
	public void resetMoveGenerator() {

	}
}
