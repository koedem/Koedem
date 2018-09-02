package Main.engine;

/**
 *
 */
public class CaptureGenerator implements CaptureGeneratorInterface {

	/**
	 * 2D array to put captures in. First index is the piece that gets captured. (1 = pawn, 2 = knight etc.)
	 *                 captures[i][0] stores the number of capture moves in that array. captures[0] should be empty
	 *                 except captures[0][0] == -1 if the position was illegal (king capture possible or similar)
	 */
	private final int[][] qSearchCaptures = new int[6][64]; // hopefully at max 64 captures for a single captured piece type; captures[0] isn't used

	BoardInterface board;

	public CaptureGenerator(BoardInterface board) {
		this.board = board;
	}

	@Override
	public void resetCaptureGenerator() {

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
			int squareValue = board.isFreeSquare(file + i, row, toMove);
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
			int squareValue = board.isFreeSquare(file - i, row, toMove);
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
			int squareValue = board.isFreeSquare(file, row + i, toMove);
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
			int squareValue = board.isFreeSquare(file, row - i, toMove);
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
			int squareValue = board.isFreeSquare(file + i, row + i, toMove);
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
			int squareValue = board.isFreeSquare(file - i, row - i, toMove);
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
			int squareValue = board.isFreeSquare(file + i, row - i, toMove);
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
			int squareValue = board.isFreeSquare(file - i, row + i, toMove);
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
}
