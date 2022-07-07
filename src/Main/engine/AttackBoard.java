package Main.engine;

import Main.engineIO.Logging;

import java.io.Serializable;
import java.util.Arrays;

public class AttackBoard implements Serializable {

    private BitBoardInterface bitboard;
    private BoardInterface board;

	// The last initialization line contains overflows because the corresponding squares (e.g. i3) don't exist.
	@SuppressWarnings("NumericOverflow")
	private static final long[] KING_BOARD =
		{0x30203L >> 8, 0x70507L >> 8, 0xE0A0EL >> 8, 0x1C141CL >> 8, 0x382838L >> 8, 0x705070L >> 8, 0xE0A0E0L >> 8, 0xC040C0L >> 8,
		 0x30203L, 0x70507L, 0xE0A0EL, 0x1C141CL, 0x382838L, 0x705070L, 0xE0A0E0L, 0xC040C0L,
		 0x30203L << 8, 0x70507L << 8, 0xE0A0EL << 8, 0x1C141CL << 8, 0x382838L << 8, 0x705070L << 8, 0xE0A0E0L << 8, 0xC040C0L << 8,
		 0x30203L << 16, 0x70507L << 16, 0xE0A0EL << 16, 0x1C141CL << 16, 0x382838L << 16, 0x705070L << 16, 0xE0A0E0L << 16, 0xC040C0L << 16,
		 0x30203L << 24, 0x70507L << 24, 0xE0A0EL << 24, 0x1C141CL << 24, 0x382838L << 24, 0x705070L << 24, 0xE0A0E0L << 24, 0xC040C0L << 24,
		 0x30203L << 32, 0x70507L << 32, 0xE0A0EL << 32, 0x1C141CL << 32, 0x382838L << 32, 0x705070L << 32, 0xE0A0E0L << 32, 0xC040C0L << 32,
		 0x30203L << 40, 0x70507L << 40, 0xE0A0EL << 40, 0x1C141CL << 40, 0x382838L << 40, 0x705070L << 40, 0xE0A0E0L << 40, 0xC040C0L << 40,
		 0x30203L << 48, 0x70507L << 48, 0xE0A0EL << 48, 0x1C141CL << 48, 0x382838L << 48, 0x705070L << 48, 0xE0A0E0L << 48, 0xC040C0L << 48
		};

	// The last initialization line contains overflows because the corresponding squares (e.g. i3) don't exist.
	@SuppressWarnings("NumericOverflow")
	private static final long[] KNIGHT_BOARD =
		{   0x204000402L >> 16, 0x508000805L >> 16, 0xA1100110AL >> 16, 0x1422002214L >> 16, 0x2844004428L >> 16, 0x5088008850L >> 16, 0xA0100010A0L >> 16, 0x4020002040L >> 16,
		    0x204000402L >> 8, 0x508000805L >> 8, 0xA1100110AL >> 8, 0x1422002214L >> 8, 0x2844004428L >> 8, 0x5088008850L >> 8, 0xA0100010A0L >> 8, 0x4020002040L >> 8,
			0x204000402L, 0x508000805L, 0xA1100110AL, 0x1422002214L, 0x2844004428L, 0x5088008850L, 0xA0100010A0L, 0x4020002040L,
			0x204000402L << 8, 0x508000805L << 8, 0xA1100110AL << 8, 0x1422002214L << 8, 0x2844004428L << 8, 0x5088008850L << 8, 0xA0100010A0L << 8, 0x4020002040L << 8,
			0x204000402L << 16, 0x508000805L << 16, 0xA1100110AL << 16, 0x1422002214L << 16, 0x2844004428L << 16, 0x5088008850L << 16, 0xA0100010A0L << 16, 0x4020002040L << 16,
			0x204000402L << 24, 0x508000805L << 24, 0xA1100110AL << 24, 0x1422002214L << 24, 0x2844004428L << 24, 0x5088008850L << 24, 0xA0100010A0L << 24, 0x4020002040L << 24,
			0x204000402L << 32, 0x508000805L << 32, 0xA1100110AL << 32, 0x1422002214L << 32, 0x2844004428L << 32, 0x5088008850L << 32, 0xA0100010A0L << 32, 0x4020002040L << 32,
			0x204000402L << 40, 0x508000805L << 40, 0xA1100110AL << 40, 0x1422002214L << 40, 0x2844004428L << 40, 0x5088008850L << 40, 0xA0100010A0L << 40, 0x4020002040L << 40,
	};

	/**
	 *
	 */
	// The last initialization line contains overflows because the corresponding squares (e.g. i3) don't exist.
	@SuppressWarnings("NumericOverflow")
	private static final long[] WHITE_PAWN_CAPTURES =
		{   0, 0x40004L >> 8, 0x80008L >> 8, 0x100010L >> 8, 0x200020L >> 8, 0x400040L >> 8, 0x800080L >> 8, 0,
		    0, 0x40004L, 0x80008L, 0x100010L, 0x200020L, 0x400040L, 0x800080L, 0,
		    0, 0x40004L << 8, 0x80008L << 8, 0x100010L << 8, 0x200020L << 8, 0x400040L << 8, 0x800080L << 8, 0,
		    0, 0x40004L << 16, 0x80008L << 16, 0x100010L << 16, 0x200020L << 16, 0x400040L << 16, 0x800080L << 16, 0,
		    0, 0x40004L << 24, 0x80008L << 24, 0x100010L << 24, 0x200020L << 24, 0x400040L << 24, 0x800080L << 24, 0,
		    0, 0x40004L << 32, 0x80008L << 32, 0x100010L << 32, 0x200020L << 32, 0x400040L << 32, 0x800080L << 32, 0,
		    0, 0x40004L << 40, 0x80008L << 40, 0x100010L << 40, 0x200020L << 40, 0x400040L << 40, 0x800080L << 40, 0,
		    0, 0x40004L << 48, 0x80008L << 48, 0x100010L << 48, 0x200020L << 48, 0x400040L << 48, 0x800080L << 48, 0,
	};

	/**
	 * White pawn captures shifted to the right by two since Black pawns capture the other way.
	 */
	// The last initialization line contains overflows because the corresponding squares (e.g. i3) don't exist.
	@SuppressWarnings("NumericOverflow")
	private static final long[] BLACK_PAWN_CAPTURES =
			{   0, 0x40004L >> 10, 0x80008L >> 10, 0x100010L >> 10, 0x200020L >> 10, 0x400040L >> 10, 0x800080L >> 10, 0,
			    0, 0x40004L >> 2, 0x80008L >> 2, 0x100010L >> 2, 0x200020L >> 2, 0x400040L >> 2, 0x800080L >> 2, 0,
			    0, 0x40004L << 6, 0x80008L << 6, 0x100010L << 6, 0x200020L << 6, 0x400040L << 6, 0x800080L << 6, 0,
			    0, 0x40004L << 14, 0x80008L << 14, 0x100010L << 14, 0x200020L << 14, 0x400040L << 14, 0x800080L << 14, 0,
			    0, 0x40004L << 22, 0x80008L << 22, 0x100010L << 22, 0x200020L << 22, 0x400040L << 22, 0x800080L << 22, 0,
			    0, 0x40004L << 30, 0x80008L << 30, 0x100010L << 30, 0x200020L << 30, 0x400040L << 30, 0x800080L << 30, 0,
			    0, 0x40004L << 38, 0x80008L << 38, 0x100010L << 38, 0x200020L << 38, 0x400040L << 38, 0x800080L << 38, 0,
			    0, 0x40004L << 46, 0x80008L << 46, 0x100010L << 46, 0x200020L << 46, 0x400040L << 46, 0x800080L << 46, 0,
			    };

	private static final long[] NEGATED_ROOK_ROWS = {
		~0x0101010101010101L, ~0x0202020202020202L, ~0x0404040404040404L, ~0x0808080808080808L,
		~0x1010101010101010L, ~0x2020202020202020L, ~0x4040404040404040L, ~0x8080808080808080L
	};

	private static final long[] NEGATED_ROOK_FILES = {
		~(0xFFL), ~(0xFFL << 8), ~(0xFFL << 16), ~(0xFFL << 24), ~(0xFFL << 32), ~(0xFFL << 40), ~(0xFFL << 48), ~(0xFFL << 56)
	};

	/**
	 * Index i holds diagonal for i = 7 + file - row.
	 */
	private static final long[] NEGATED_UP_DIAGONALS = {
		~(0x80L), ~(0x8040L), ~(0x804020L), ~(0x80402010L), ~(0x8040201008L), ~(0x804020100804L), ~(0x80402010080402L), ~(0x8040201008040201L), ~(0x4020100804020100L),
		~(0x2010080402010000L), ~(0x1008040201000000L), ~(0x0804020100000000L), ~(0x0402010000000000L), ~(0x0201000000000000L), ~(0x0100000000000000L)
	};

	/**
	 * Index i holds diagonal for i = file + row.
	 */
	private static final long[] NEGATED_DOWN_DIAGONALS = {
		~(0x1L), ~(0x102L), ~(0x10204L), ~(0x1020408L), ~(0x102040810L), ~(0x10204081020L), ~(0x1020408102040L), ~(0x102040810204080L), ~(0x204081020408000L),
		~(0x408102040800000L), ~(0x810204080000000L), ~(0x1020408000000000L), ~(0x2040800000000000L), ~(0x4080000000000000L), ~(0x8000000000000000L),
	};

	public long[][][] getAttackBoards() {
		return attackBoards;
	}

	/**
	 * Contains the squares controlled by individual pieces. [0] for White pieces, [1] for Black pieces.
	 * [][0] for pawn moves, [][1] for pawn captures, [][2] for knight moves/captures etc.
	 * At most 10 pieces per piece type and side. (first two for the starting pieces, rest for promoted pieces)
	 */
	private long[][][] attackBoards = new long[2][7][10];

	public long[][] getPieceTypes() {
		return pieceTypes;
	}

	/**
	 * Contains the squares controlled by the piece types. [0][1] for White pawns, [0][2] for White knights, [1][1] for Black pawns etc.
	 * [2][] and [3][] contain the squares controlled by non-promotion pieces, [4][] and [5][] the ones for promoted pieces
	 */
	private long[][] pieceTypes = { { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 } };

    /**
     * The pseudo attack tables for the White ([0]) and Black ([1]) king squares. E.g. [0][3] contains the attack board
     * if the White King was a Bishop etc. Queens are not needed as they already are in the rook and bishop boards.
     */
	private long[][] kingPieceBoards = { { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 } };

    /**
     * The attack table for all sliders (i.e. bishops, rooks and queens) of a side combined.
     */
	private long[] sliders = { 0, 0 };

    /**
     * The attack table for all non pawns of a side combined. [0] for White, [1] for Black non pawns.
     */
	private long[] nonPawns = { 0, 0 };

	public long[] getAllPieces() {
		return allPieces;
	}

	/**
     * The attack table for all pieces of a side combined. [0] for White, [1] for Black pieces.
     */
	private long[] allPieces = { 0, 0 };

	/**
	 * pieceAttackCount[x][y] contains the number of squares that all pieces of colour x and piece type y control.
	 * Colour 0 for White, 1 for Black.
	 * Piece type 0 for pawn moves, 1 for pawn captures, 2 for knight, 3 for bishop, 4 for rook, 5 for queen, 6 for king.
	 */
	private byte[][] pieceAttackCount = new byte[2][7];

    AttackBoard(BoardInterface board, BitBoardInterface bitboard) {
	    for (long[][] attackBoard : attackBoards) {
		    for (long[] pieceBoards : attackBoard) {
			    // initialize empty board
			    Arrays.fill(pieceBoards, 0);
		    }
	    }
		this.board = board;
		this.bitboard = bitboard;
	}

    /**
     * TODO attackCount
     * @param colour 0 for white, 1 for black
     * @param pieceType 1-6 for pawn - king
     * @param pieceIndex Array-index in the bitboards
     * @param startSquare 8 * file + row
     * @param endSquare 8 * file + row
     * @return Whether the move was successful
     */
	public boolean move(int colour, int pieceType, int pieceIndex, int startSquare, int endSquare, boolean capture, boolean unCapture) {
		// TODO program flow, move -> AB-move -> BB-move ?
		if (!unCapture) {
			unblockSquare(startSquare);
		}
		if (pieceType == 1) {
			pawnMove(colour, pieceIndex, endSquare);
		} else if (pieceType == 2) {
			knightMove(colour, pieceIndex, endSquare);
		} else if (pieceType == 3) {
			bishopMove(colour, pieceIndex, startSquare, endSquare, capture);
		} else if (pieceType == 4) {
			rookMove(colour, pieceIndex, startSquare, endSquare, capture);
		} else if (pieceType == 5) {
			queenMove(colour, pieceIndex, startSquare, endSquare, capture);
		} else if (pieceType == 6) {
			kingMove(colour, pieceIndex, endSquare);
		} else {
			assert false;
		}
		if (!capture) {
			blockSquare(endSquare);
		}
		sliders[0] = pieceTypes[0][0] | pieceTypes[0][3] | pieceTypes[0][4] | pieceTypes[0][5];
		allPieces[0] = pieceTypes[0][1] | pieceTypes[0][2] | pieceTypes[0][3]
		                    | pieceTypes[0][4] | pieceTypes[0][5] | pieceTypes[0][6];
        sliders[1] = pieceTypes[1][0] | pieceTypes[1][3] | pieceTypes[1][4] | pieceTypes[1][5];
        allPieces[1] = pieceTypes[1][1] | pieceTypes[1][2] | pieceTypes[1][3]
                | pieceTypes[1][4] | pieceTypes[1][5] | pieceTypes[1][6];
		return true;
	}

	private void unblockSquare(int square) {
		long squareBoard = 1L << square;
		for (int sliderColour = 0; sliderColour <= 1; sliderColour++) {
			if ((squareBoard & sliders[sliderColour]) != 0) {
				if ((sliderColour == 0 && (square & 7) == 2 || sliderColour == 1 && (square & 7) == 5) // a pawn is only a slider if it's on its origin square
				    && (squareBoard & pieceTypes[sliderColour][0]) != 0) {
					pawnAdding(sliderColour, squareBoard);
				}
				if ((squareBoard & pieceTypes[sliderColour][3]) != 0) {
					bishopAdding(sliderColour, square, squareBoard);
				}
				if ((squareBoard & pieceTypes[sliderColour][4]) != 0) {
					rookAdding(sliderColour, square, squareBoard);
				}
				if ((squareBoard & pieceTypes[sliderColour][5]) != 0) {
					queenAdding(sliderColour, square, squareBoard);
				}
			}
			if ((squareBoard & kingPieceBoards[sliderColour][3]) != 0) {
				kingBishopAdding(sliderColour, square, squareBoard, square >> 3, square & 7);
			}
			if ((squareBoard & kingPieceBoards[sliderColour][4]) != 0) {
				kingRookAdding(sliderColour, square, squareBoard, square >> 3, square & 7);
			}
		}
	}

	private void blockSquare(int square) {
		long squareBoard = 1L << square;
		for (int sliderColour = 0; sliderColour <= 1; sliderColour++) {
			if ((squareBoard & sliders[sliderColour]) != 0) {
				if ((squareBoard & pieceTypes[sliderColour][0]) != 0) {
					pawnRemoving(sliderColour, squareBoard);
				}
				if ((squareBoard & pieceTypes[sliderColour][3]) != 0) {
					bishopRemoving(sliderColour, square, squareBoard);
				}
				if ((squareBoard & pieceTypes[sliderColour][4]) != 0) {
					rookRemoving(sliderColour, square, squareBoard);
				}
				if ((squareBoard & pieceTypes[sliderColour][5]) != 0) {
					queenRemoving(sliderColour, square, squareBoard);
				}
			}
			if ((squareBoard & kingPieceBoards[sliderColour][3]) != 0) {
				kingBishopRemoving(sliderColour, square, squareBoard, square >> 3, square & 7);
			}
			if ((squareBoard & kingPieceBoards[sliderColour][4]) != 0) {
				kingRookRemoving(sliderColour, square, squareBoard, square >> 3, square & 7);
			}
		}
	}
	
	private void kingMove(int colour, int pieceIndex, int endSquare) {
		attackBoards[colour][6][pieceIndex] = KING_BOARD[endSquare];
		pieceTypes[colour][6] = KING_BOARD[endSquare];
	}

	private void kingAddition(int colour, int pieceIndex, int square) {
		attackBoards[colour][6][pieceIndex] = KING_BOARD[square];
		pieceTypes[colour][6] = KING_BOARD[square];
	}
	
	private void knightMove(int colour, int pieceIndex, int endSquare) {
		attackBoards[colour][2][pieceIndex] = KNIGHT_BOARD[endSquare];

		if (pieceIndex <= 1) {
			pieceTypes[colour + 2][2] = attackBoards[colour][2][0] | attackBoards[colour][2][1];
		} else {
			pieceTypes[colour + 4][2] = attackBoards[colour][2][2] | attackBoards[colour][2][3]
					| attackBoards[colour][2][4] | attackBoards[colour][2][5] | attackBoards[colour][2][6]
					| attackBoards[colour][2][7] | attackBoards[colour][2][8] | attackBoards[colour][2][9];
		}

		pieceTypes[colour][2] = pieceTypes[colour + 2][2] | pieceTypes[colour + 4][2];
	}

	private void knightAddition(int colour, int pieceIndex, int square) {
		attackBoards[colour][2][pieceIndex] = KNIGHT_BOARD[square];

		if (pieceIndex <= 1) {
			pieceTypes[colour + 2][2] = attackBoards[colour][2][0] | attackBoards[colour][2][1];
		} else {
			pieceTypes[colour + 4][2] = attackBoards[colour][2][2] | attackBoards[colour][2][3]
			                            | attackBoards[colour][2][4] | attackBoards[colour][2][5] | attackBoards[colour][2][6]
			                            | attackBoards[colour][2][7] | attackBoards[colour][2][8] | attackBoards[colour][2][9];
		}

		pieceTypes[colour][2] = pieceTypes[colour + 2][2] | pieceTypes[colour + 4][2];
	}

	private void rookMove(int colour, int pieceIndex, int startSquare, int endSquare, boolean capture) {
		long board = attackBoards[colour][4][pieceIndex];
		int endFile = endSquare >> 3;
		int endRow = (endSquare & 7);
		if (((startSquare ^ endSquare) & 7) == 0) { // i.e. we move along a row
			board &= NEGATED_ROOK_FILES[startSquare >> 3]; // we don't control the old file anymore
			board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, board)); // build the new file
            if (capture) {
                if (startSquare < endSquare) {
                    board = buildRookRowUp(endFile, endRow, board);
                } else {
                    board = buildRookRowDown(endFile, endRow, board);
                }
            }
		} else if (((startSquare ^ endSquare) >> 3) == 0) { // i.e. we move along a file
			board &= NEGATED_ROOK_ROWS[startSquare & 7]; // we don't control the old row anymore
			board = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row
            if (capture) {
                if (startSquare < endSquare) {
                    board = buildRookFileUp(endFile, endRow, board);
                } else {
                    board = buildRookFileDown(endFile, endRow, board);
                }
            }
		} else {
			assert false;
		}
		board |= 1L << startSquare; // we control the start square now
		board &= ~(1L << endSquare); // we don't control the endsquare anymore

		attackBoards[colour][4][pieceIndex] = board;

		if (pieceIndex <= 1) {
			pieceTypes[colour + 2][4] = attackBoards[colour][4][0] | attackBoards[colour][4][1];
		} else {
			pieceTypes[colour + 4][4] = attackBoards[colour][4][2] | attackBoards[colour][4][3]
			                            | attackBoards[colour][4][4] | attackBoards[colour][4][5] | attackBoards[colour][4][6]
			                            | attackBoards[colour][4][7] | attackBoards[colour][4][8] | attackBoards[colour][4][9];
		}

		pieceTypes[colour][4] = pieceTypes[colour + 2][4] | pieceTypes[colour + 4][4];
	}

	private void rookAddition(int colour, int pieceIndex, int square) {
		int endFile = square >> 3;
		int endRow = (square & 7);

		long board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, 0)); // build the new file
		attackBoards[colour][4][pieceIndex] = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row

		if (pieceIndex <= 1) {
			pieceTypes[colour + 2][4] = attackBoards[colour][4][0] | attackBoards[colour][4][1];
		} else {
			pieceTypes[colour + 4][4] = attackBoards[colour][4][2] | attackBoards[colour][4][3]
			                            | attackBoards[colour][4][4] | attackBoards[colour][4][5] | attackBoards[colour][4][6]
			                            | attackBoards[colour][4][7] | attackBoards[colour][4][8] | attackBoards[colour][4][9];
		}

		pieceTypes[colour][4] = pieceTypes[colour + 2][4] | pieceTypes[colour + 4][4];
	}

	private void bishopMove(int colour, int pieceIndex, int startSquare, int endSquare, boolean capture) {
		long board = attackBoards[colour][3][pieceIndex];
		int endFile = endSquare >> 3;
		int endRow = (endSquare & 7);
		if (((startSquare - endSquare) % 9) == 0) { // i.e. we move up a diagonal; NOTE, a8-h1 also enters this branch however it doesn't matter
			board &= NEGATED_DOWN_DIAGONALS[(startSquare >> 3) + (startSquare & 7)]; // we don't control the old down-diagonal anymore
			board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, board)); // build the new down diagonal
            if (capture) {
                if (startSquare < endSquare) {
                    board = buildUpDiagonalUp(endFile, endRow, board);
                } else {
                    board = buildUpDiagonalDown(endFile, endRow, board);
                }
            }
		} else if (((startSquare - endSquare) % 7) == 0) { // i.e. we move down a diagonal
			board &= NEGATED_UP_DIAGONALS[7 + (startSquare >> 3) - (startSquare & 7)]; // we don't control the old up-diagonal anymore
			board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal
            if (capture) {
                if (startSquare < endSquare) {
                    board = buildDownDiagonalUp(endFile, endRow, board);
                } else {
                    board = buildDownDiagonalDown(endFile, endRow, board);
                }
            }
		} else {
			assert false;
		}
		board |= 1L << startSquare; // we control the start square now
		board &= ~(1L << endSquare); // we don't control the endsquare anymore

		attackBoards[colour][3][pieceIndex] = board;

		if (pieceIndex <= 1) {
			pieceTypes[colour + 2][3] = attackBoards[colour][3][0] | attackBoards[colour][3][1];
		} else {
			pieceTypes[colour + 4][3] = attackBoards[colour][3][2] | attackBoards[colour][3][3]
			                            | attackBoards[colour][3][4] | attackBoards[colour][3][5] | attackBoards[colour][3][6]
			                            | attackBoards[colour][3][7] | attackBoards[colour][3][8] | attackBoards[colour][3][9];
		}

		pieceTypes[colour][3] = pieceTypes[colour + 2][3] | pieceTypes[colour + 4][3];
	}

	private void bishopAddition(int colour, int pieceIndex, int square) {
		int endFile = square >> 3;
		int endRow = (square & 7);

		long board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, 0)); // build the new down diagonal
		attackBoards[colour][3][pieceIndex] = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal

		if (pieceIndex <= 1) {
			pieceTypes[colour + 2][3] = attackBoards[colour][3][0] | attackBoards[colour][3][1];
		} else {
			pieceTypes[colour + 4][3] = attackBoards[colour][3][2] | attackBoards[colour][3][3]
			                            | attackBoards[colour][3][4] | attackBoards[colour][3][5] | attackBoards[colour][3][6]
			                            | attackBoards[colour][3][7] | attackBoards[colour][3][8] | attackBoards[colour][3][9];
		}

		pieceTypes[colour][3] = pieceTypes[colour + 2][3] | pieceTypes[colour + 4][3];
	}
	
	private void queenMove(int colour, int pieceIndex, int startSquare, int endSquare, boolean capture) {
		long board = attackBoards[colour][5][pieceIndex];
		int endFile = endSquare >> 3;
		int endRow = (endSquare & 7);
		if (((startSquare - endSquare) % 9) == 0) { // i.e. we move up a diagonal; NOTE, a8-h1 also enters this branch however it doesn't matter
			board &= NEGATED_DOWN_DIAGONALS[(startSquare >> 3) + (startSquare & 7)]; // we don't control the old down-diagonal anymore
			board &= NEGATED_ROOK_FILES[startSquare >> 3]; // we don't control the old file anymore
			board &= NEGATED_ROOK_ROWS[startSquare & 7]; // we don't control the old row anymore

			board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, board)); // build the new down diagonal
			board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, board)); // build the new file
			board = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row
            if (capture) {
                if (startSquare < endSquare) {
                    board = buildUpDiagonalUp(endFile, endRow, board);
                } else {
                    board = buildUpDiagonalDown(endFile, endRow, board);
                }
            }
		} else if (((startSquare ^ endSquare) & 7) == 0) { // i.e. we move along a row
            board &= NEGATED_UP_DIAGONALS[7 + (startSquare >> 3) - (startSquare & 7)]; // we don't control the old up-diagonal anymore
            board &= NEGATED_DOWN_DIAGONALS[(startSquare >> 3) + (startSquare & 7)]; // we don't control the old down-diagonal anymore
            board &= NEGATED_ROOK_FILES[startSquare >> 3]; // we don't control the old file anymore

            board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal
            board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, board)); // build the new down diagonal
            board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, board)); // build the new file
            if (capture) {
                if (startSquare < endSquare) {
                    board = buildRookRowUp(endFile, endRow, board);
                } else {
                    board = buildRookRowDown(endFile, endRow, board);
                }
            }
        } else if (((startSquare ^ endSquare) >> 3) == 0) { // i.e. we move along a file
            board &= NEGATED_UP_DIAGONALS[7 + (startSquare >> 3) - (startSquare & 7)]; // we don't control the old up-diagonal anymore
            board &= NEGATED_DOWN_DIAGONALS[(startSquare >> 3) + (startSquare & 7)]; // we don't control the old down-diagonal anymore
            board &= NEGATED_ROOK_ROWS[startSquare & 7]; // we don't control the old row anymore

            board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal
            board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, board)); // build the new down diagonal
            board = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row
            if (capture) {
                if (startSquare < endSquare) {
                    board = buildRookFileUp(endFile, endRow, board);
                } else {
                    board = buildRookFileDown(endFile, endRow, board);
                }
            }
        } else if (((startSquare - endSquare) % 7) == 0) { // i.e. we move down a diagonal
			board &= NEGATED_UP_DIAGONALS[7 + (startSquare >> 3) - (startSquare & 7)]; // we don't control the old up-diagonal anymore
			board &= NEGATED_ROOK_FILES[startSquare >> 3]; // we don't control the old row anymore
			board &= NEGATED_ROOK_ROWS[startSquare & 7]; // we don't control the old file anymore

			board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal
			board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, board)); // build the new file
			board = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row
            if (capture) {
                if (startSquare < endSquare) {
                    board = buildDownDiagonalUp(endFile, endRow, board);
                } else {
                    board = buildDownDiagonalDown(endFile, endRow, board);
                }
            }
		} else {
			assert false;
		}
		board |= 1L << startSquare; // we control the start square now
		board &= ~(1L << endSquare); // we don't control the endsquare anymore

		attackBoards[colour][5][pieceIndex] = board;

		if (pieceIndex < 1) {
			pieceTypes[colour + 2][5] = attackBoards[colour][5][0];
		} else {
			pieceTypes[colour + 4][5] = attackBoards[colour][5][1] | attackBoards[colour][5][2] | attackBoards[colour][5][3]
			                            | attackBoards[colour][5][4] | attackBoards[colour][5][5] | attackBoards[colour][5][6]
			                            | attackBoards[colour][5][7] | attackBoards[colour][5][8];
		}

		pieceTypes[colour][5] = pieceTypes[colour + 2][5] | pieceTypes[colour + 4][5];
	}

	private void queenAddition(int colour, int pieceIndex, int square) {
		int endFile = square >> 3;
		int endRow = (square & 7);

		long board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, 0)); // build the new up diagonal
		board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, board)); // build the new down diagonal
		board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, board)); // build the new file
		board = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row

		attackBoards[colour][5][pieceIndex] = board;

		if (pieceIndex < 1) {
			pieceTypes[colour + 2][5] = attackBoards[colour][5][0];
		} else {
			pieceTypes[colour + 4][5] = attackBoards[colour][5][1] | attackBoards[colour][5][2] | attackBoards[colour][5][3]
			                            | attackBoards[colour][5][4] | attackBoards[colour][5][5] | attackBoards[colour][5][6]
			                            | attackBoards[colour][5][7] | attackBoards[colour][5][8];
		}

		pieceTypes[colour][5] = pieceTypes[colour + 2][5] | pieceTypes[colour + 4][5];
	}

	private void pawnMove(int colour, int pieceIndex, int endSquare) {
		if ((endSquare - colour) % 8 != 3 || (pieceTypes[colour][0] & bitboard.getBitBoard(colour, 1, pieceIndex)) == 0) {
			pieceTypes[colour][0] &= ~attackBoards[colour][0][pieceIndex]; // we can't move on the old squares anymore
		} // else : it was a double pawn and the pawn behind can still move there with a double step
		pawnSquareAddition(colour, pieceIndex, endSquare);
		pieceTypes[colour][0] |= attackBoards[colour][0][pieceIndex];

		if (colour == 0) {
			attackBoards[colour][1][pieceIndex] = WHITE_PAWN_CAPTURES[endSquare];
		} else {
			attackBoards[colour][1][pieceIndex] = BLACK_PAWN_CAPTURES[endSquare];
		}
		pieceTypes[colour][1] = attackBoards[colour][1][0] | attackBoards[colour][1][1] | attackBoards[colour][1][2] | attackBoards[colour][1][3]
			                  | attackBoards[colour][1][4] | attackBoards[colour][1][5] | attackBoards[colour][1][6] | attackBoards[colour][1][7];
	}

	private void pawnAddition(int colour, int pieceIndex, int square) {
		pawnSquareAddition(colour, pieceIndex, square);
		pieceTypes[colour][0] |= attackBoards[colour][0][pieceIndex];

		if (colour == 0) {
			attackBoards[colour][1][pieceIndex] = WHITE_PAWN_CAPTURES[square];
		} else {
			attackBoards[colour][1][pieceIndex] = BLACK_PAWN_CAPTURES[square];
		}
		pieceTypes[colour][1] = attackBoards[colour][1][0] | attackBoards[colour][1][1] | attackBoards[colour][1][2] | attackBoards[colour][1][3]
		                        | attackBoards[colour][1][4] | attackBoards[colour][1][5] | attackBoards[colour][1][6] | attackBoards[colour][1][7];
	}

	private void pawnSquareAddition(int colour, int pieceIndex, int square) {
		if (colour == 0) {
			attackBoards[colour][0][pieceIndex] = 1L << (square + 1);
			if ((square & 7) == 1 && board.getSquare(square >> 3, (square + 1) & 7) == 0) {
				attackBoards[colour][0][pieceIndex] |= 1L << (square + 2);
			}
		} else {
			attackBoards[colour][0][pieceIndex] = 1L << (square - 1);
			if ((square & 7) == 6 && board.getSquare(square >> 3, (square - 1) & 7) == 0) {
				attackBoards[colour][0][pieceIndex] |= 1L << (square - 2);
			}
		}
	}

	private void pawnAdding(int colour, long squareBoard) {
		for (int index = 0; index < attackBoards[colour][0].length; index++) {
			if ((squareBoard & attackBoards[colour][0][index]) != 0) {
				attackBoards[colour][0][index] |= colour == 0 ? squareBoard << 1 : squareBoard >> 1; // we can now step one square further}
				pieceTypes[colour][0] |= attackBoards[colour][0][index];
				break;
			}
		}
	}

	private void pawnRemoving(int colour, long squareBoard) {
		for (int index = 0; index < attackBoards[colour][0].length; index++) {
			if ((squareBoard & attackBoards[colour][0][index]) != 0) {
				attackBoards[colour][0][index] &= colour == 0 ? ~(squareBoard << 1) : ~(squareBoard >> 1); // we can't step two anymore
				pieceTypes[colour][0] = attackBoards[colour][0][0] | attackBoards[colour][0][1] | attackBoards[colour][0][2] | attackBoards[colour][0][3] |
				                        attackBoards[colour][0][4] | attackBoards[colour][0][5] | attackBoards[colour][0][6] | attackBoards[colour][0][7];
				break;
			}
		}
	}

	private void bishopAdding(int colour, int startSquare, long startBoard) {
		int file = startSquare >> 3;
		int row = startSquare & 7;
		if ((pieceTypes[colour + 2][3] & startBoard) != 0) {
			for (int index = 0; index < 2; index++) {
				bishopAdder(colour, startSquare, startBoard, file, row, index);
			}
			pieceTypes[colour + 2][3] = attackBoards[colour][3][0] | attackBoards[colour][3][1];
		}
		if ((pieceTypes[colour + 4][3] & startBoard) != 0) {
			for (int index = 2; index < 10; index++) {
				bishopAdder(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 4][3] = attackBoards[colour][3][2] | attackBoards[colour][3][3]
                    | attackBoards[colour][3][4] | attackBoards[colour][3][5] | attackBoards[colour][3][6]
                    | attackBoards[colour][3][7] | attackBoards[colour][3][8] | attackBoards[colour][3][9];
		}
		pieceTypes[colour][3] = pieceTypes[colour + 2][3] | pieceTypes[colour + 4][3];
	}

	private void bishopAdder(int colour, int startSquare, long startBoard, int file, int row, int index) {
		if ((attackBoards[colour][3][index] & startBoard) != 0) {
			int bishopPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 3, index));
			if (bishopPosition < startSquare) { // i.e. we move up from the startSquare
				if ((bishopPosition - startSquare) % 7 == 0) {
					attackBoards[colour][3][index] = buildDownDiagonalUp(file, row, attackBoards[colour][3][index]);
				} else if ((bishopPosition - startSquare) % 9 == 0) {
					attackBoards[colour][3][index] = buildUpDiagonalUp(file, row, attackBoards[colour][3][index]);
				} else {
					assert false;
				}
			} else {
				if ((bishopPosition - startSquare) % 7 == 0) {
					attackBoards[colour][3][index] = buildDownDiagonalDown(file, row, attackBoards[colour][3][index]);
				} else if ((bishopPosition - startSquare) % 9 == 0) {
					attackBoards[colour][3][index] = buildUpDiagonalDown(file, row, attackBoards[colour][3][index]);
				} else {
					assert false;
				}
			}
		}
	}

	private void bishopRemoving(int colour, int startSquare, long startBoard) {
		int file = startSquare >> 3;
		int row = startSquare & 7;
		if ((pieceTypes[colour + 2][3] & startBoard) != 0) {
			for (int index = 0; index < 2; index++) {
				bishopRemover(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 2][3] = attackBoards[colour][3][0] | attackBoards[colour][3][1];
		}
		if ((pieceTypes[colour + 4][3] & startBoard) != 0) {
			for (int index = 2; index < 10; index++) {
				bishopRemover(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 4][3] = attackBoards[colour][3][2] | attackBoards[colour][3][3]
                    | attackBoards[colour][3][4] | attackBoards[colour][3][5] | attackBoards[colour][3][6]
                    | attackBoards[colour][3][7] | attackBoards[colour][3][8] | attackBoards[colour][3][9];
		}
        pieceTypes[colour][3] = pieceTypes[colour + 2][3] | pieceTypes[colour + 4][3];
	}

	private void bishopRemover(int colour, int startSquare, long startBoard, int file, int row, int index) {
		if ((attackBoards[colour][3][index] & startBoard) != 0) {
			int bishopPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 3, index));
			if (bishopPosition < startSquare) { // i.e. we move up from the startSquare
				if ((bishopPosition - startSquare) % 7 == 0) {
					attackBoards[colour][3][index] &= ~buildDownDiagonalUp(file, row, 0); // create diagonals on empty board and invert them to remove them
				} else if ((bishopPosition - startSquare) % 9 == 0) {
					attackBoards[colour][3][index] &= ~buildUpDiagonalUp(file, row, 0);
				} else {
					assert false;
				}
			} else {
				if ((bishopPosition - startSquare) % 7 == 0) {
					attackBoards[colour][3][index] &= ~buildDownDiagonalDown(file, row, 0);
				} else if ((bishopPosition - startSquare) % 9 == 0) {
					attackBoards[colour][3][index] &= ~buildUpDiagonalDown(file, row, 0);
				} else {
					assert false;
				}
			}
		}
	}

	private void rookAdding(int colour, int startSquare, long startBoard) {
		int file = startSquare >> 3;
		int row = startSquare & 7;
		if ((pieceTypes[colour + 2][4] & startBoard) != 0) {
			for (int index = 0; index < 2; index++) {
				rookAdder(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 2][4] = attackBoards[colour][4][0] | attackBoards[colour][4][1];
		}
		if ((pieceTypes[colour + 4][4] & startBoard) != 0) {
			for (int index = 2; index < 10; index++) {
				rookAdder(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 4][4] = attackBoards[colour][4][2] | attackBoards[colour][4][3]
                    | attackBoards[colour][4][4] | attackBoards[colour][4][5] | attackBoards[colour][4][6]
                    | attackBoards[colour][4][7] | attackBoards[colour][4][8] | attackBoards[colour][4][9];
		}
        pieceTypes[colour][4] = pieceTypes[colour + 2][4] | pieceTypes[colour + 4][4];
	}

	private void rookAdder(int colour, int startSquare, long startBoard, int file, int row, int index) {
		if ((attackBoards[colour][4][index] & startBoard) != 0) {
			int rookPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 4, index));
			if (rookPosition < startSquare) { // i.e. we move up from the startSquare
				if (((rookPosition ^ startSquare) & 7) == 0) { // we move along a file
					attackBoards[colour][4][index] = buildRookRowUp(file, row, attackBoards[colour][4][index]);
				} else if (((rookPosition ^ startSquare) >> 3) == 0) { // we move along a row
					attackBoards[colour][4][index] = buildRookFileUp(file, row, attackBoards[colour][4][index]);
				} else {
					assert false;
				}
			} else {
				if (((rookPosition ^ startSquare) & 7) == 0) { // we move along a file
					attackBoards[colour][4][index] = buildRookRowDown(file, row, attackBoards[colour][4][index]);
				} else if (((rookPosition ^ startSquare) >> 3) == 0) { // we move along a row
					attackBoards[colour][4][index] = buildRookFileDown(file, row, attackBoards[colour][4][index]);
				} else {
					assert false;
				}
			}
		}
	}

	private void rookRemoving(int colour, int startSquare, long startBoard) {
		int file = startSquare >> 3;
		int row = startSquare & 7;
		if ((pieceTypes[colour + 2][4] & startBoard) != 0) {
			for (int index = 0; index < 2; index++) {
				rookRemover(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 2][4] = attackBoards[colour][4][0] | attackBoards[colour][4][1];
		}
		if ((pieceTypes[colour + 4][4] & startBoard) != 0) {
			for (int index = 2; index < 10; index++) {
				rookRemover(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 4][4] = attackBoards[colour][4][2] | attackBoards[colour][4][3]
                    | attackBoards[colour][4][4] | attackBoards[colour][4][5] | attackBoards[colour][4][6]
                    | attackBoards[colour][4][7] | attackBoards[colour][4][8] | attackBoards[colour][4][9];
		}
        pieceTypes[colour][4] = pieceTypes[colour + 2][4] | pieceTypes[colour + 4][4];
	}

	private void rookRemover(int colour, int startSquare, long startBoard, int file, int row, int index) {
		if ((attackBoards[colour][4][index] & startBoard) != 0) {
			int rookPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 4, index));
			if (rookPosition < startSquare) { // i.e. we move up from the startSquare
				if (((rookPosition ^ startSquare) & 7) == 0) { // we move along a file
					attackBoards[colour][4][index] &= ~buildRookRowUp(file, row, 0); // create file on empty board and invert them to remove them
				} else if (((rookPosition ^ startSquare) >> 3) == 0) { // we move along a row
					attackBoards[colour][4][index] &= ~buildRookFileUp(file, row, 0);
				} else {
					assert false;
				}
			} else {
				if (((rookPosition ^ startSquare) & 7) == 0) { // we move along a file
					attackBoards[colour][4][index] &= ~buildRookRowDown(file, row, 0);
				} else if (((rookPosition ^ startSquare) >> 3) == 0) { // we move along a row
					attackBoards[colour][4][index] &= ~buildRookFileDown(file, row, 0);
				} else {
					assert false;
				}
			}
		}
	}

	private void queenAdding(int colour, int startSquare, long startBoard) {
		int file = startSquare >> 3;
		int row = startSquare & 7;
		if ((pieceTypes[colour + 2][5] & startBoard) != 0) {
			for (int index = 0; index < 1; index++) {
				queenAdder(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 2][5] = attackBoards[colour][5][0];
		}
		if ((pieceTypes[colour + 4][5] & startBoard) != 0) {
			for (int index = 1; index < 9; index++) {
				queenAdder(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 4][5] = attackBoards[colour][5][1] | attackBoards[colour][5][2] | attackBoards[colour][5][3]
                    | attackBoards[colour][5][4] | attackBoards[colour][5][5] | attackBoards[colour][5][6]
                    | attackBoards[colour][5][7] | attackBoards[colour][5][8];
		}
        pieceTypes[colour][5] = pieceTypes[colour + 2][5] | pieceTypes[colour + 4][5];
	}

	private void queenAdder(int colour, int startSquare, long startBoard, int file, int row, int index) {
		if ((attackBoards[colour][5][index] & startBoard) != 0) {
			int queenPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 5, index));
			if (queenPosition < startSquare) { // i.e. we move up from the startSquare
				if ((queenPosition - startSquare) % 9 == 0) {
					attackBoards[colour][5][index] = buildUpDiagonalUp(file, row, attackBoards[colour][5][index]);
				} else if (((queenPosition ^ startSquare) & 7) == 0) { // we move along a row
					attackBoards[colour][5][index] = buildRookRowUp(file, row, attackBoards[colour][5][index]);
				} else if (((queenPosition ^ startSquare) >> 3) == 0) { // we move along a file
					attackBoards[colour][5][index] = buildRookFileUp(file, row, attackBoards[colour][5][index]);
				} else if ((queenPosition - startSquare) % 7 == 0) {
                    attackBoards[colour][5][index] = buildDownDiagonalUp(file, row, attackBoards[colour][5][index]);
                } else {
					assert false;
				}
			} else {
				if ((queenPosition - startSquare) % 9 == 0) {
					attackBoards[colour][5][index] = buildUpDiagonalDown(file, row, attackBoards[colour][5][index]);
				} else if (((queenPosition ^ startSquare) & 7) == 0) { // we move along a file
					attackBoards[colour][5][index] = buildRookRowDown(file, row, attackBoards[colour][5][index]);
				} else if (((queenPosition ^ startSquare) >> 3) == 0) { // we move along a row
					attackBoards[colour][5][index] = buildRookFileDown(file, row, attackBoards[colour][5][index]);
				} else if ((queenPosition - startSquare) % 7 == 0) {
                    attackBoards[colour][5][index] = buildDownDiagonalDown(file, row, attackBoards[colour][5][index]);
                } else {
					assert false;
				}
			}
		}
	}

	private void queenRemoving(int colour, int startSquare, long startBoard) {
		int file = startSquare >> 3;
		int row = startSquare & 7;
		if ((pieceTypes[colour + 2][5] & startBoard) != 0) {
			for (int index = 0; index < 1; index++) {
				queenRemover(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 2][5] = attackBoards[colour][5][0];
		}
		if ((pieceTypes[colour + 4][5] & startBoard) != 0) {
			for (int index = 1; index < 9; index++) {
				queenRemover(colour, startSquare, startBoard, file, row, index);
			}
            pieceTypes[colour + 4][5] = attackBoards[colour][5][1] | attackBoards[colour][5][2] | attackBoards[colour][5][3]
                    | attackBoards[colour][5][4] | attackBoards[colour][5][5] | attackBoards[colour][5][6]
                    | attackBoards[colour][5][7] | attackBoards[colour][5][8];
		}
        pieceTypes[colour][5] = pieceTypes[colour + 2][5] | pieceTypes[colour + 4][5];
	}

	private void queenRemover(int colour, int startSquare, long startBoard, int file, int row, int index) {
		if ((attackBoards[colour][5][index] & startBoard) != 0) {
			int queenPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 5, index));
			if (queenPosition < startSquare) { // i.e. we move up from the startSquare
				if ((queenPosition - startSquare) % 9 == 0) { // create diagonal on empty board and invert them to remove them
					attackBoards[colour][5][index] &= ~buildUpDiagonalUp(file, row, 0);
				} else if (((queenPosition ^ startSquare) & 7) == 0) { // we move along a row
					attackBoards[colour][5][index] &= ~buildRookRowUp(file, row, 0);
				} else if (((queenPosition ^ startSquare) >> 3) == 0) { // we move along a file
					attackBoards[colour][5][index] &= ~buildRookFileUp(file, row, 0);
				} else if ((queenPosition - startSquare) % 7 == 0) {
                    attackBoards[colour][5][index] &= ~buildDownDiagonalUp(file, row, 0);
                } else {
					assert false;
				}
			} else {
				if ((queenPosition - startSquare) % 9 == 0) {
					attackBoards[colour][5][index] &= ~buildUpDiagonalDown(file, row, 0);
				} else if (((queenPosition ^ startSquare) & 7) == 0) { // we move along a file
					attackBoards[colour][5][index] &= ~buildRookRowDown(file, row, 0);
				} else if (((queenPosition ^ startSquare) >> 3) == 0) { // we move along a row
					attackBoards[colour][5][index] &= ~buildRookFileDown(file, row, 0);
				} else if ((queenPosition - startSquare) % 7 == 0) {
                    attackBoards[colour][5][index] &= ~buildDownDiagonalDown(file, row, 0);
                } else {
					assert false;
				}
			}
		}
	}

	private void kingBishopAdding(int colour, int startSquare, long startBoard, int file, int row) { // TODO sliders array update
		if ((kingPieceBoards[colour][3] & startBoard) != 0) {
			int kingPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 6, 0)); // there is only one king per side
			if (kingPosition < startSquare) { // i.e. we move up from the startSquare
				if ((kingPosition - startSquare) % 7 == 0) {
					kingPieceBoards[colour][3] = buildDownDiagonalUp(file, row, kingPieceBoards[colour][3]);
				} else if ((kingPosition - startSquare) % 9 == 0) {
					kingPieceBoards[colour][3] = buildUpDiagonalUp(file, row, kingPieceBoards[colour][3]);
				} else {
					assert false;
				}
			} else {
				if ((kingPosition - startSquare) % 7 == 0) {
					kingPieceBoards[colour][3] = buildDownDiagonalDown(file, row, kingPieceBoards[colour][3]);
				} else if ((kingPosition - startSquare) % 9 == 0) {
					kingPieceBoards[colour][3] = buildUpDiagonalDown(file, row, kingPieceBoards[colour][3]);
				} else {
					assert false;
				}
			}
		}
	}

	private void kingBishopRemoving(int colour, int startSquare, long startBoard, int file, int row) {
		if ((kingPieceBoards[colour][3] & startBoard) != 0) {
			int kingPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 6, 0)); // there is only one king per side
			if (kingPosition < startSquare) { // i.e. we move up from the startSquare
				if ((kingPosition - startSquare) % 7 == 0) {
					kingPieceBoards[colour][3] &= ~buildDownDiagonalUp(file, row, 0); // create diagonal on empty board and invert them to remove them
				} else if ((kingPosition - startSquare) % 9 == 0) {
					kingPieceBoards[colour][3] &= ~buildUpDiagonalUp(file, row, 0);
				} else {
					assert false;
				}
			} else {
				if ((kingPosition - startSquare) % 7 == 0) {
					kingPieceBoards[colour][3] &= ~buildDownDiagonalDown(file, row, 0);
				} else if ((kingPosition - startSquare) % 9 == 0) {
					kingPieceBoards[colour][3] &= ~buildUpDiagonalDown(file, row, 0);
				} else {
					assert false;
				}
			}
		}
	}

	private void kingRookAdding(int colour, int startSquare, long startBoard, int file, int row) { // TODO recheck if comments and code on whether we add a file or row are correct
		if ((kingPieceBoards[colour][4] & startBoard) != 0) {
			int kingPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 6, 0)); // there is only one king per side
			if (kingPosition < startSquare) { // i.e. we move up from the startSquare
				if (((kingPosition ^ startSquare) & 7) == 0) { // we move along a row
					kingPieceBoards[colour][4] = buildRookFileUp(file, row, kingPieceBoards[colour][4]);
				} else if (((kingPosition ^ startSquare) >> 3) == 0) { // we move along a file
					kingPieceBoards[colour][4] = buildRookRowUp(file, row, kingPieceBoards[colour][4]);
				} else {
					assert false;
				}
			} else {
				if (((kingPosition ^ startSquare) & 7) == 0) { // we move along a row
					kingPieceBoards[colour][4] = buildRookFileDown(file, row, kingPieceBoards[colour][4]);
				} else if (((kingPosition ^ startSquare) >> 3) == 0) { // we move along a file
					kingPieceBoards[colour][4] = buildRookRowDown(file, row, kingPieceBoards[colour][4]);
				} else {
					assert false;
				}
			}
		}
	}

	private void kingRookRemoving(int colour, int startSquare, long startBoard, int file, int row) {
		if ((kingPieceBoards[colour][4] & startBoard) != 0) {
			int kingPosition = Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, 6, 0)); // there is only one king per side
			if (kingPosition < startSquare) { // i.e. we move up from the startSquare
				if (((kingPosition ^ startSquare) & 7) == 0) { // we move along a row
					kingPieceBoards[colour][4] &= ~buildRookFileUp(file, row, 0); // create diagonal on empty board and invert them to remove them
				} else if (((kingPosition ^ startSquare) >> 3) == 0) { // we move along a file
					kingPieceBoards[colour][4] &= ~buildRookRowUp(file, row, 0);
				} else {
					assert false;
				}
			} else {
				if (((kingPosition ^ startSquare) & 7) == 0) { // we move along a row
					kingPieceBoards[colour][4] &= ~buildRookFileDown(file, row, 0);
				} else if (((kingPosition ^ startSquare) >> 3) == 0) { // we move along a file
					kingPieceBoards[colour][4] &= ~buildRookRowDown(file, row, 0);
				} else {
					assert false;
				}
			}
		}
	}

	/**
	 * Build up a rook file and store the reachable squares in the Bitboard board.
	 * Example: for given square d4 this method will check the squares d5, d6, d7 and d8 and if reachable set the
	 * corresponding bits in the given board and return it. The given square itself will not be checked.
	 * @param endFile File of the square to be checked. Between 0 and 7.
	 * @param endRow Row of the square to be checked. Between 0 and 7.
	 * @param board the Bitboard to put the reachable squares in. The LSB corresponds to a1, the second-LSB a2 and so on.
	 * @return the original Bitboard board with the reachable square-bits set to 1.
	 */
	private long buildRookFileUp(int endFile, int endRow, long board) {
		for (int row = endRow + 1; row < 8; row++) { // move up through the file
			board |= 1L << (endFile * 8 + row);
			if (this.board.getSquare(endFile, row) != 0) {
				break;
			}
		}
		return board;
	}


	/**
	 * Build down a rook file and store the reachable squares in the Bitboard board.
	 * Example: for given square d4 this method will check the squares d3, d2 and d1 and if reachable set the
	 * corresponding bits in the given board and return it. The given square itself will not be checked.
	 * @param endFile File of the square to be checked. Between 0 and 7.
	 * @param endRow Row of the square to be checked. Between 0 and 7.
	 * @param board the Bitboard to put the reachable squares in. The LSB corresponds to a1, the second-LSB a2 and so on.
	 * @return the original Bitboard board with the reachable square-bits set to 1.
	 */
	private long buildRookFileDown(int endFile, int endRow, long board) {
		for (int row = endRow - 1; row >= 0; row--) { // move down through the file
            board |= 1L << (endFile * 8 + row);
            if (this.board.getSquare(endFile, row) != 0) {
                break;
            }
        }
        return board;
	}

	private long buildRookRowUp(int endFile, int endRow, long board) {
		for (int file = endFile + 1; file < 8; file++) { // move to the right
			board |= 1L << (file * 8 + endRow);
			if (this.board.getSquare(file, endRow) != 0) {
				break;
			}
		}
		return board;
	}

	private long buildRookRowDown(int endFile, int endRow, long board) {
		for (int file = endFile - 1; file >= 0; file--) { // move to the left
            board |= 1L << (file * 8 + endRow);
            if (this.board.getSquare(file, endRow) != 0) {
                break;
            }
        }
        return board;
	}

	private long buildUpDiagonalUp(int endFile, int endRow, long board) {
		for (int row = endRow + 1, file = endFile + 1; row < 8 && file < 8; row++, file++) { // move up the diagonal
			board |= 1L << (file * 8 + row);
			if (this.board.getSquare(file, row) != 0) {
				break;
			}
		}
		return board;
	}

	private long buildUpDiagonalDown(int endFile, int endRow, long board) {
		for (int row = endRow - 1, file = endFile - 1; row >= 0 && file >= 0; row--, file--) { // move down the diagonal
			board |= 1L << (file * 8 + row);
			if (this.board.getSquare(file, row) != 0) {
				break;
			}
		}
		return board;
	}

	private long buildDownDiagonalUp(int endFile, int endRow, long board) {
        for (int row = endRow - 1, file = endFile + 1; row >= 0 && file < 8; row--, file++) { // move down the diagonal
            board |= 1L << (file * 8 + row);
            if (this.board.getSquare(file, row) != 0) {
                break;
            }
        }
		return board;
	}

	private long buildDownDiagonalDown(int endFile, int endRow, long board) {
        for (int row = endRow + 1, file = endFile - 1; row < 8 && file >= 0; row++, file--) { // move up the diagonal
            board |= 1L << (file * 8 + row);
            if (this.board.getSquare(file, row) != 0) {
                break;
            }
        }
		return board;
	}

	/**
	 * TODO attackCount, tbd: pawnAddition, knightAddition bishopAddition, rookAddition, queenAddition, kingAddition, blockSquare
	 * @param colour
	 * @param pieceTyp
	 * @param index
	 * @param square
	 */
	void add(int colour, int pieceTyp, int index, int square) {
		switch (pieceTyp) {
			case 1: pawnAddition(colour, index, square);
				break;
			case 2: knightAddition(colour, index, square);
				break;
			case 3: bishopAddition(colour, index, square);
				break;
			case 4: rookAddition(colour, index, square);
				break;
			case 5: queenAddition(colour, index, square);
				break;
			case 6: kingAddition(colour, index, square);
				break;
		}
		blockSquare(square);
		sliders[0] = pieceTypes[0][0] | pieceTypes[0][3] | pieceTypes[0][4] | pieceTypes[0][5];
		allPieces[0] = pieceTypes[0][1] | pieceTypes[0][2] | pieceTypes[0][3]
		               | pieceTypes[0][4] | pieceTypes[0][5] | pieceTypes[0][6];
		sliders[1] = pieceTypes[1][0] | pieceTypes[1][3] | pieceTypes[1][4] | pieceTypes[1][5];
		allPieces[1] = pieceTypes[1][1] | pieceTypes[1][2] | pieceTypes[1][3]
		               | pieceTypes[1][4] | pieceTypes[1][5] | pieceTypes[1][6];
	}

	/**
	 * TODO attackCount
	 * @param colour
	 * @param pieceTyp
	 * @param index
	 * @param unblockSquare
	 */
	void remove(int colour, int pieceTyp, int index, boolean unblockSquare) {
		attackBoards[colour][pieceTyp][index] = 0;
		switch (pieceTyp) {
			case 1:
			    pieceTypes[colour][0] &= ~(attackBoards[colour][0][index]);
			    attackBoards[colour][0][index] = 0; // remove captured AND moves AB
                pieceTypes[colour][1] = attackBoards[colour][1][0] | attackBoards[colour][1][1] | attackBoards[colour][1][2] | attackBoards[colour][1][3]
                        | attackBoards[colour][1][4] | attackBoards[colour][1][5] | attackBoards[colour][1][6] | attackBoards[colour][1][7];
                break;
			case 2:
			    attackBoards[colour][2][index] = 0;
			    if (index < 2) {
			        pieceTypes[colour + 2][2] = attackBoards[colour][2][0] | attackBoards[colour][2][1];
                } else {
			        pieceTypes[colour + 4][2] = attackBoards[colour][2][2] | attackBoards[colour][2][3] | attackBoards[colour][2][4] | attackBoards[colour][2][5]
                            | attackBoards[colour][2][6] | attackBoards[colour][2][7] | attackBoards[colour][2][8] | attackBoards[colour][2][9];
                }
                pieceTypes[colour][2] = pieceTypes[colour + 2][2] | pieceTypes[colour + 4][2];
                break;
			case 3:
			    attackBoards[colour][3][index] = 0;
                if (index < 2) {
                    pieceTypes[colour + 2][3] = attackBoards[colour][3][0] | attackBoards[colour][3][1];
                } else {
                    pieceTypes[colour + 4][3] = attackBoards[colour][3][2] | attackBoards[colour][3][3] | attackBoards[colour][3][4] | attackBoards[colour][3][5]
                            | attackBoards[colour][3][6] | attackBoards[colour][3][7] | attackBoards[colour][3][8] | attackBoards[colour][3][9];
                }
                pieceTypes[colour][3] = pieceTypes[colour + 2][3] | pieceTypes[colour + 4][3];
                break;
			case 4:
			    attackBoards[colour][4][index] = 0;
                if (index < 2) {
                    pieceTypes[colour + 2][4] = attackBoards[colour][4][0] | attackBoards[colour][4][1];
                } else {
                    pieceTypes[colour + 4][4] = attackBoards[colour][4][2] | attackBoards[colour][4][3] | attackBoards[colour][4][4] | attackBoards[colour][4][5]
                            | attackBoards[colour][4][6] | attackBoards[colour][4][7] | attackBoards[colour][4][8] | attackBoards[colour][4][9];
                }
                pieceTypes[colour][4] = pieceTypes[colour + 2][4] | pieceTypes[colour + 4][4];
                break;
			case 5:
			    attackBoards[colour][5][index] = 0;
                if (index < 1) {
                    pieceTypes[colour + 2][5] = 0;
                } else {
                    pieceTypes[colour + 4][5] = attackBoards[colour][5][1] | attackBoards[colour][5][2] | attackBoards[colour][5][3] | attackBoards[colour][5][4]
                            | attackBoards[colour][5][5] | attackBoards[colour][5][6] | attackBoards[colour][5][7] | attackBoards[colour][5][8];
                }
                pieceTypes[colour][5] = pieceTypes[colour + 2][5] | pieceTypes[colour + 4][5];
                break;
			case 6:
                // Logging.printLine("We just removed a king from the board. That should not happen!");
                pieceTypes[colour][6] = attackBoards[colour][6][index] = 0;
                break;
		}
		if (unblockSquare) {
			this.unblockSquare(Long.numberOfTrailingZeros(bitboard.getBitBoard(colour, pieceTyp, index)));
		}
		sliders[0] = pieceTypes[0][0] | pieceTypes[0][3] | pieceTypes[0][4] | pieceTypes[0][5];
		allPieces[0] = pieceTypes[0][1] | pieceTypes[0][2] | pieceTypes[0][3]
		               | pieceTypes[0][4] | pieceTypes[0][5] | pieceTypes[0][6];
		sliders[1] = pieceTypes[1][0] | pieceTypes[1][3] | pieceTypes[1][4] | pieceTypes[1][5];
		allPieces[1] = pieceTypes[1][1] | pieceTypes[1][2] | pieceTypes[1][3]
		               | pieceTypes[1][4] | pieceTypes[1][5] | pieceTypes[1][6];
	}

	public int[] moveGenerator(int[] storage, boolean whoToMove) {
        int toMove = whoToMove ? 0 : 1;
        long legalMoves;
        int startSquare, endSquare = -1;
        for (int index = 0; index < attackBoards[toMove][0].length; index++) { // do pawns separately
        	if ((legalMoves = attackBoards[toMove][0][index] & ~(bitboard.getAllPieces(0) | bitboard.getAllPieces(1))) != 0) {
        		startSquare = Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, 1, index));
        		if (whoToMove && (startSquare & 7) != 6 || !whoToMove && (startSquare & 7) != 1) { // no promotion
			        while (legalMoves != 0) {
				        storage[++storage[0]] = (1 << 12) + (startSquare << 6) + (endSquare = Long.numberOfTrailingZeros(legalMoves));
				        legalMoves &= ~(1L << endSquare);
			        }
		        } else {
        			for (int promotion = 2; promotion <= 5; promotion++) {
				        storage[++storage[0]] = (1 << 15) + (startSquare << 9) + (Long.numberOfTrailingZeros(legalMoves) << 3) + promotion;
			        }
		        }
	        }
	        if ((legalMoves = attackBoards[toMove][1][index] & bitboard.getAllPieces(whoToMove ? 1 : 0)) != 0) {
		        startSquare = Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, 1, index));
		        if (whoToMove && (startSquare & 7) != 6 || !whoToMove && (startSquare & 7) != 1) { // no promotion
			        while (legalMoves != 0) {
				        storage[++storage[0]] = (1 << 12) + (startSquare << 6) + (endSquare = Long.numberOfTrailingZeros(legalMoves));
				        legalMoves &= ~(1L << endSquare);
			        }
		        } else {
			        while (legalMoves != 0) {
			            for (int promotion = 2; promotion <= 5; promotion++) {
				            storage[++storage[0]] = (1 << 15) + (startSquare << 9) + ((endSquare = Long.numberOfTrailingZeros(legalMoves)) << 3) + promotion;
			            }
			            legalMoves &= ~(1L << endSquare);
			        }
		        }
	        }
	        if (board.getEnPassant() != 0 && (whoToMove && (board.getEnPassant() & 7) == 5 || !whoToMove && (board.getEnPassant() & 7) == 2)) {
	        	if ((legalMoves = (1L << board.getEnPassant()) & attackBoards[toMove][1][index]) != 0) {
			        storage[++storage[0]] = (1 << 12) + ((Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, 1, index))) << 6) +
			                                             (endSquare = Long.numberOfTrailingZeros(legalMoves));
		        }
	        }
        }

        for (int piece = 2; piece < attackBoards[toMove].length; piece++) {
            for (int index = 0; index < attackBoards[toMove][piece].length; index++) {
                startSquare = Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, piece, index));
                legalMoves = attackBoards[toMove][piece][index] & ~(bitboard.getAllPieces(whoToMove ? 0 : 1));
	            while (legalMoves != 0) {
		            storage[++storage[0]] = (1 << 12) + (startSquare << 6) + (endSquare = Long.numberOfTrailingZeros(legalMoves));
		            legalMoves &= ~(1L << endSquare);
	            }
            }
        }

        // castling code
		if (board.getCastlingRights() != 0) {
			if (whoToMove) {
				if ((board.getCastlingRights() & 0x30) == 0x30) { // Q side castling
					for (int index = 0; index < 10; index++) {
						if (((0x1010101L << 8) & attackBoards[0][4][index]) == (0x1010101L << 8)) { // i.e. nothing between rook and king, castling possible
							if (((0x101L << 24) & allPieces[1]) == 0) { // i.e. black can't capture on e1 or d1 -> castling actually legal
								storage[++storage[0]] = (1 << 12) + (32 << 6) + 16; // note, still only pseudo legal; merely to match movegenerator
							}
						}
					}
				}
				if ((board.getCastlingRights() & 0x18) == 0x18) { // K side castling
					for (int index = 0; index < 10; index++) {
						if (((0x10101L << 32) & attackBoards[0][4][index]) == (0x10101L << 32)) { // i.e. nothing between rook and king, castling possible
							if (((0x101L << 32) & allPieces[1]) == 0) { // i.e. black can't capture on e1 or f1 -> castling actually legal
								storage[++storage[0]] = (1 << 12) + (32 << 6) + 48;
							}
						}
					}
				}
			} else {
				if ((board.getCastlingRights() & 0x6) == 0x6) { // Q side castling
					for (int index = 0; index < 10; index++) {
						if (((0x1010101L << 15) & attackBoards[1][4][index]) == (0x1010101L << 15)) { // i.e. nothing between rook and king, castling possible
							if (((0x101L << 31) & allPieces[0]) == 0) { // i.e. white can't capture on e8 or d8 -> castling actually legal
								storage[++storage[0]] = (1 << 12) + (39 << 6) + 23; // note, still only pseudo legal; merely to match movegenerator
							}
						}
					}
				}
				if ((board.getCastlingRights() & 0x3) == 0x3) { // K side castling
					for (int index = 0; index < 10; index++) {
						if (((0x10101L << 39) & attackBoards[1][4][index]) == (0x10101L << 39)) { // i.e. nothing between rook and king, castling possible
							if (((0x101L << 39) & allPieces[0]) == 0) { // i.e. white can't capture on e8 or f8 -> castling actually legal
								storage[++storage[0]] = (1 << 12) + (39 << 6) + 55;
							}
						}
					}
				}
			}
		}
        return storage;
    }

    void resetAttackBoard() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 7; j++) {
				for (int k = 0; k < 10; k++) {
					attackBoards[i][j][k] = 0;
				}
				pieceTypes[i][j] = 0;
				pieceAttackCount[i][j] = 0;
			}
			for (int j = 0; j < 5; j++) {
				kingPieceBoards[i][j] = 0;
			}
			sliders[i] = 0;
			nonPawns[i] = 0;
			allPieces[i] = 0;
		}
    }

    void generateAttackCount() {
	    for (byte[] bytes : pieceAttackCount) {
		    Arrays.fill(bytes, (byte) 0);
	    }
		for (int i = 0; i < attackBoards.length; i++) {
			for (int j = 2; j < attackBoards[i].length; j++) {
				for (int k = 0; k < 2; k++) { // most of the time we only have two pieces per piece type
					pieceAttackCount[i][j] += Long.bitCount(attackBoards[i][j][k] & ~bitboard.getAllPieces(i));
				}
				if (pieceTypes[i + 4][j] != 0) { // this only is relevant when we have promoted pieces
					for (int k = 2; k < attackBoards[i][j].length; k++) {
						pieceAttackCount[i][j] += Long.bitCount(attackBoards[i][j][k] & ~bitboard.getAllPieces(i));
					}
				}
			}
			// legal pawn moves
			pieceAttackCount[i][0] += Long.bitCount(pieceTypes[i][0] & ~bitboard.getAllPieces(0) & ~bitboard.getAllPieces(1));
			// squares to which pawns of the same colour can move, can't overlap

			for (int k = 0 ; k < 8; k++) { // legal pawn captures
				pieceAttackCount[i][1] += Long.bitCount(attackBoards[i][1][k] & bitboard.getAllPieces((i - 1) * (i - 1))); // i - 1 squared = the other colour
			}
		}
    }

    public boolean inCheck(boolean white) {
		return (allPieces[white ? 1 : 0] & bitboard.getPieceTypeBoard(white ? 0 : 1, 6)) != 0;
    }

    int getAttackCount(int colour, int piece) {
		return pieceAttackCount[colour][piece];
    }
}
