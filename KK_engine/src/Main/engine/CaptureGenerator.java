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

	public int[] collectShootingMoves(boolean whoToMove, int[] shootingMoves) {
		shootingMoves[0] = 0;

		long shotPiecesBitSet = board.getAttackBoard().getAllPieces()[whoToMove ? 0 : 1] & board.getBitboard().getAllPieces(whoToMove ? 1 : 0);
		while (shotPiecesBitSet != 0) {
			int endSquare = Long.numberOfTrailingZeros(shotPiecesBitSet);
			shootingMoves[++shootingMoves[0]] = (1 << 12) + (endSquare << 6) + (endSquare);
			shotPiecesBitSet &= ~(1L << endSquare);
		}
		return shootingMoves;
	}

	/**
	 * This code works with the AttackBoard to quickly determine capture options.
	 * @param whoToMove who to move it is. true = white, false = black.
	 * @param allCaptures integer array to store captures in the usual move format in.
	 * @return integer array containing all legal captures for toMove in the current position.
	 */
	public int[] collectCaptures(boolean whoToMove, int[] allCaptures) { // TODO sort captures according to material gain
		int toMove = whoToMove ? 0 : 1;
		BitBoardInterface bitboard = board.getBitboard();
		if ((bitboard.getBitBoard(whoToMove ? 1 : 0, 6, 0) & board.getAttackBoard().getAllPieces()[toMove]) != 0) { // i.e. we can capture a king
			allCaptures[0] = -1;
			return allCaptures;
		} else {
			allCaptures[0] = 0;
			//collectShootingMoves(whoToMove, allCaptures);
			if ((board.getAttackBoard().getAllPieces()[toMove] & bitboard.getAllPieces(whoToMove ? 1 : 0)) == 0) { // i.e. there's no legal captures
				return allCaptures;
			}
			for (int i = 0; i < qSearchCaptures.length; i++) {
				qSearchCaptures[i][0] = 0;
			}
			long       legalCaptures;
			int        startSquare, endSquare = -1;
			int capturedPieceValue = -1;
			long[][][] attackBoards           = board.getAttackBoard().getAttackBoards();
			if ((board.getAttackBoard().getPieceTypes()[toMove][1] & bitboard.getAllPieces(whoToMove ? 1 : 0)) != 0) { // i.e. there are some pawn captures
				for (int index = 0; index < 8; index++) {
					if ((legalCaptures = attackBoards[toMove][1][index] & bitboard.getAllPieces(whoToMove ? 1 : 0)) != 0) {
						startSquare = Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, 1, index));
						if (whoToMove && (startSquare & 7) != 6 || !whoToMove && (startSquare & 7) != 1) { // no promotion
							while (legalCaptures != 0) {
								endSquare = Long.numberOfTrailingZeros(legalCaptures);
								capturedPieceValue = whoToMove ? -board.getSquare(endSquare / 8, endSquare % 8) : board.getSquare(endSquare / 8, endSquare % 8);
								qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (endSquare);
								legalCaptures &= ~(1L << endSquare);
							}
						} else {
							while (legalCaptures != 0) {
								for (int promotion = 2; promotion <= 5; promotion++) {
									qSearchCaptures[5][++qSearchCaptures[5][0]] =
											(1 << 15) + (startSquare << 9) + ((endSquare = Long.numberOfTrailingZeros(legalCaptures)) << 3) + promotion;
								}
								legalCaptures &= ~(1L << endSquare);
							}
						}
					}
					if (board.getEnPassant() != 0 && (whoToMove && (board.getEnPassant() & 7) == 5 || !whoToMove && (board.getEnPassant() & 7) == 2)) {
						if ((legalCaptures = (1L << board.getEnPassant()) & attackBoards[toMove][1][index]) != 0) {
							qSearchCaptures[1][++qSearchCaptures[1][0]] = (1 << 12) + ((Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, 1, index))) << 6)
							                                              + (endSquare = Long.numberOfTrailingZeros(legalCaptures));
						}
					}
				}
			}

			for (int piece = 2; piece < attackBoards[toMove].length; piece++) {
				if ((board.getAttackBoard().getPieceTypes()[toMove][piece] & bitboard.getAllPieces(whoToMove ? 1 : 0)) != 0) { // i.e. there are some captures with that piece
					for (int index = 0; index < 2; index++) {
						startSquare = Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, piece, index));
						legalCaptures = attackBoards[toMove][piece][index] & (bitboard.getAllPieces(whoToMove ? 1 : 0));
						while (legalCaptures != 0) {
							endSquare = Long.numberOfTrailingZeros(legalCaptures);
							capturedPieceValue = whoToMove ? -board.getSquare(endSquare / 8, endSquare % 8) : board.getSquare(endSquare / 8, endSquare % 8);
							qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (endSquare);
							legalCaptures &= ~(1L << endSquare);
						}
					}
					if ((board.getAttackBoard().getPieceTypes()[toMove + 4][piece] & bitboard.getAllPieces(whoToMove ? 1 : 0)) != 0) {
																																	// i.e. there are captures with promoted pieces
						for (int index = 2; index < attackBoards[toMove][piece].length; index++) {
							startSquare = Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, piece, index));
							legalCaptures = attackBoards[toMove][piece][index] & (bitboard.getAllPieces(whoToMove ? 1 : 0));
							while (legalCaptures != 0) {
								endSquare = Long.numberOfTrailingZeros(legalCaptures);
								capturedPieceValue = whoToMove ? -board.getSquare(endSquare / 8, endSquare % 8) : board.getSquare(endSquare / 8, endSquare % 8);
								qSearchCaptures[capturedPieceValue][++qSearchCaptures[capturedPieceValue][0]] = (1 << 12) + (startSquare << 6) + (endSquare);
								legalCaptures &= ~(1L << endSquare);
							}
						}
					}
				}
			}
		}
		for (int piece = qSearchCaptures.length - 1; piece > 0; piece--) {
			int destPos = allCaptures[0] + 1; // plus one because allCaptures[0] doesn't store moves
			System.arraycopy(qSearchCaptures[piece], 1, allCaptures, destPos, qSearchCaptures[piece][0]);
			allCaptures[0] += qSearchCaptures[piece][0];
		}

		return allCaptures;
	}

	/**
	 *
	 * @param toMove : Who to move it is.
	 * @return ArrayList of Integers, containing all captures.
	 */
	public int[] oldCollectCaptures(boolean toMove, int[] allCaptures) {
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
