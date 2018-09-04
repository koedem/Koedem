package Main.engine;

import java.io.Serializable;

public class AttackBoard implements Serializable {

    private BitBoardInterface bitboard;
    private BoardInterface board;

	// The last initialization line contains overflows because the corresponding squares (e.g. i3) don't exist.
	@SuppressWarnings("NumericOverflow")
	public static final long[] KING_BOARD =
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
	public static final long[] KNIGHT_BOARD =
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
	public static final long[] PAWN_CAPTURES =
		{   0, 0x40004L >> 8, 0x80008L >> 8, 0x100010L >> 8, 0x200020L >> 8, 0x400040L >> 8, 0x800080L >> 8, 0,
		    0, 0x40004L, 0x80008L, 0x100010L, 0x200020L, 0x400040L, 0x800080L, 0,
		    0, 0x40004L << 8, 0x80008L << 8, 0x100010L << 8, 0x200020L << 8, 0x400040L << 8, 0x800080L << 8, 0,
		    0, 0x40004L << 16, 0x80008L << 16, 0x100010L << 16, 0x200020L << 16, 0x400040L << 16, 0x800080L << 16, 0,
		    0, 0x40004L << 24, 0x80008L << 24, 0x100010L << 24, 0x200020L << 24, 0x400040L << 24, 0x800080L << 24, 0,
		    0, 0x40004L << 32, 0x80008L << 32, 0x100010L << 32, 0x200020L << 32, 0x400040L << 32, 0x800080L << 32, 0,
		    0, 0x40004L << 40, 0x80008L << 40, 0x100010L << 40, 0x200020L << 40, 0x400040L << 40, 0x800080L << 40, 0,
		    0, 0x40004L << 48, 0x80008L << 48, 0x100010L << 48, 0x200020L << 48, 0x400040L << 48, 0x800080L << 48, 0,
	};

	public static final long[] NEGATED_ROOK_ROWS = {
		~0x0101010101010101L, ~0x0202020202020202L, ~0x0404040404040404L, ~0x0808080808080808L,
		~0x1010101010101010L, ~0x2020202020202020L, ~0x4040404040404040L, ~0x8080808080808080L
	};

	public static final long[] NEGATED_ROOK_FILES = {
		~(0xFFL), ~(0xFFL << 8), ~(0xFFL << 16), ~(0xFFL << 24), ~(0xFFL << 32), ~(0xFFL << 40), ~(0xFFL << 48), ~(0xFFL << 56)
	};

	/**
	 * Index i holds diagonal for i = 7 + file - row.
	 */
	public static final long[] NEGATED_UP_DIAGONALS = {
		~(0x80L), ~(0x8040L), ~(0x804020L), ~(0x80402010L), ~(0x8040201008L), ~(0x804020100804L), ~(0x80402010080402L), ~(0x8040201008040201L), ~(0x4020100804020100L),
		~(0x2010080402010000L), ~(0x1008040201000000L), ~(0x0804020100000000L), ~(0x0402010000000000L), ~(0x0201000000000000L), ~(0x0100000000000000L)
	};

	/**
	 * Index i holds diagonal for i = file + row.
	 */
	public static final long[] NEGATED_DOWN_DIAGONALS = {
		~(0x1L), ~(0x102L), ~(0x10204L), ~(0x1020408L), ~(0x102040810L), ~(0x10204081020L), ~(0x1020408102040L), ~(0x102040810204080L), ~(0x204081020408000L),
		~(0x408102040800000L), ~(0x810204080000000L), ~(0x1020408000000000L), ~(0x2040800000000000L), ~(0x4080000000000000L), ~(0x8000000000000000L),
	};
	
	private long[][][] attackBoards = new long[2][7][10];
			// 2 colours, 7 piece types (1 padding + 6), max. 10 pieces per piece type(promotions)

	/**
	 * Contains the squares controlled by the piece types. [0][0] for White pawns, [0][1] for White knights, [1][0] for Black pawns etc.
	 * [2][] and [3][] contain the squares controlled by non-promotion pieces, [4][] and [5][] the ones for promoted pieces
	 */
	private long[][] pieceTypes = { { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 } };

	private byte[][][] pieceAttackCount = new byte[2][7][64];
	
	private long[] allPieces = { 0, 0 }; // 2 colours

    private byte[][] allAttackCount = new byte[2][64];

    public AttackBoard(BoardInterface board, BitBoardInterface bitboard) {
		for (int colour = 0; colour < attackBoards.length; colour++) {
			for (int piece = 0; piece < attackBoards[colour].length; piece++) {
				for (int pieceIndex = 0; pieceIndex < attackBoards[colour][piece].length; pieceIndex++) {
					attackBoards[colour][piece][pieceIndex] = 0; // initialize empty board
				}
			}
		}
		this.board = board;
		this.bitboard = bitboard;
	}

    /**
     *
     * @param colour 0 for white, 1 for black
     * @param pieceType 1-6 for pawn - king
     * @param pieceIndex Array-index in the bitboards
     * @param startSquare 8 * file + row
     * @param endSquare 8 * file + row
     * @return Whether the move was successful
     */
	public boolean move(int colour, int pieceType, int pieceIndex, int startSquare, int endSquare) {
		boolean success = false;
		if (pieceType == 1) {
			success = pawnMove(colour, pieceIndex, startSquare, endSquare);
		} else if (pieceType == 2) {
			success = knightMove(colour, pieceIndex, startSquare, endSquare);
		} else if (pieceType == 3) {
			success = bishopMove(colour, pieceIndex, startSquare, endSquare);
		} else if (pieceType == 4) {
			success = rookMove(colour, pieceIndex, startSquare, endSquare);
		} else if (pieceType == 5) {
			success = queenMove(colour, pieceIndex, startSquare, endSquare);
		} else if (pieceType == 6) {
			success = kingMove(colour, pieceIndex, startSquare, endSquare);
		} else {
			assert false;
		}
		allPieces[colour] = pieceTypes[colour][1] | pieceTypes[colour][2] | pieceTypes[colour][3]
		                    | pieceTypes[colour][4] | pieceTypes[colour][5] | pieceTypes[colour][6];
		return true;
		//return success;
	}
	
	private boolean kingMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		attackBoards[colour][6][pieceIndex] = KING_BOARD[endSquare];
		pieceTypes[colour][6] = KING_BOARD[endSquare];
		return true;
	}
	
	private boolean knightMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		attackBoards[colour][2][pieceIndex] = KNIGHT_BOARD[endSquare];

		if (pieceIndex <= 1) {
			pieceTypes[colour + 2][2] = attackBoards[colour][2][0] | attackBoards[colour][2][1];
		} else {
			pieceTypes[colour + 4][2] = attackBoards[colour][2][2] | attackBoards[colour][2][3]
					| attackBoards[colour][2][4] | attackBoards[colour][2][5] | attackBoards[colour][2][6]
					| attackBoards[colour][2][7] | attackBoards[colour][2][8] | attackBoards[colour][2][9];
		}

		pieceTypes[colour][2] = pieceTypes[colour + 2][2] | pieceTypes[colour + 4][2];
		return true;
	}

	private boolean rookMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		long board = attackBoards[colour][4][pieceIndex];
		int endFile = endSquare >> 3;
		int endRow = (endSquare & 7);
		if (((startSquare ^ endSquare) & 7) == 0) { // i.e. we move along a row
			board &= NEGATED_ROOK_FILES[startSquare >> 3]; // we don't control the old file anymore
			board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, board)); // build the new file
		} else if (((startSquare ^ endSquare) >> 3) == 0) { // i.e. we move along a file
			board &= NEGATED_ROOK_ROWS[startSquare & 7]; // we don't control the old row anymore
			board = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row
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
		return true;
	}

	private boolean bishopMove(int colour, int pieceIndex, int startSquare, int endSquare) { // TODO + endsquare
		long board = attackBoards[colour][3][pieceIndex];
		int endFile = endSquare >> 3;
		int endRow = (endSquare & 7);
		if (((startSquare - endSquare) % 9) == 0) { // i.e. we move up a diagonal; NOTE, a8-h1 also enters this branch however it doesn't matter
			board &= NEGATED_DOWN_DIAGONALS[(startSquare >> 3) + (startSquare & 7)]; // we don't control the old down-diagonal anymore
			board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, board)); // build the new down diagonal
		} else if (((startSquare - endSquare) % 7) == 0) { // i.e. we move down a diagonal
			board &= NEGATED_UP_DIAGONALS[7 + (startSquare >> 3) - (startSquare & 7)]; // we don't control the old up-diagonal anymore
			board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal
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
		return true;
	}
	
	private boolean queenMove(int colour, int pieceIndex, int startSquare, int endSquare) {
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
		} else if (((startSquare - endSquare) % 7) == 0) { // i.e. we move down a diagonal
			board &= NEGATED_UP_DIAGONALS[7 + (startSquare >> 3) - (startSquare & 7)]; // we don't control the old up-diagonal anymore
			board &= NEGATED_ROOK_FILES[startSquare >> 3]; // we don't control the old file anymore
			board &= NEGATED_ROOK_ROWS[startSquare & 7]; // we don't control the old row anymore

			board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal
			board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, board)); // build the new file
			board = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row
		} else if (((startSquare ^ endSquare) & 7) == 0) { // i.e. we move along a row
			board &= NEGATED_UP_DIAGONALS[7 + (startSquare >> 3) - (startSquare & 7)]; // we don't control the old up-diagonal anymore
			board &= NEGATED_DOWN_DIAGONALS[(startSquare >> 3) + (startSquare & 7)]; // we don't control the old down-diagonal anymore
			board &= NEGATED_ROOK_FILES[startSquare >> 3]; // we don't control the old file anymore

			board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal
			board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, board)); // build the new down diagonal
			board = buildRookFileUp(endFile, endRow, buildRookFileDown(endFile, endRow, board)); // build the new file
		} else if (((startSquare ^ endSquare) >> 3) == 0) { // i.e. we move along a file
			board &= NEGATED_UP_DIAGONALS[7 + (startSquare >> 3) - (startSquare & 7)]; // we don't control the old up-diagonal anymore
			board &= NEGATED_DOWN_DIAGONALS[(startSquare >> 3) + (startSquare & 7)]; // we don't control the old down-diagonal anymore
			board &= NEGATED_ROOK_ROWS[startSquare & 7]; // we don't control the old row anymore

			board = buildUpDiagonalUp(endFile, endRow, buildUpDiagonalDown(endFile, endRow, board)); // build the new up diagonal
			board = buildDownDiagonalUp(endFile, endRow, buildDownDiagonalDown(endFile, endRow, board)); // build the new down diagonal
			board = buildRookRowUp(endFile, endRow, buildRookRowDown(endFile, endRow, board)); // build the new row
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
		return true;
	}

	private boolean pawnMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		boolean success = false;
		
		return success;
	}

	private long buildRookFileUp(int endFile, int endRow, long board) {
		for (int row = endRow + 1; row < 8; row++) { // move up the file
			board |= 1L << (endFile * 8 + row);
			if (this.board.getSquare(endFile, row) != 0) {
				break;
			}
		}
		return board;
	}

	private long buildRookFileDown(int endFile, int endRow, long board) {
		for (int row = endRow - 1; row >= 0; row--) { // move down the file
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
		for (int file = endFile - 1; file >= 0; file--) { // move to the right
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
		for (int row = endRow + 1, file = endFile - 1; row < 8 && file >= 0; row++, file--) { // move up the diagonal
			board |= 1L << (file * 8 + row);
			if (this.board.getSquare(file, row) != 0) {
				break;
			}
		}
		return board;
	}

	private long buildDownDiagonalDown(int endFile, int endRow, long board) {
		for (int row = endRow - 1, file = endFile + 1; row >= 0 && file < 8; row--, file++) { // move down the diagonal
			board |= 1L << (file * 8 + row);
			if (this.board.getSquare(file, row) != 0) {
				break;
			}
		}
		return board;
	}

	public int[] moveGenerator(int[] storage, boolean whoToMove) {
        int toMove = whoToMove ? 0 : 1;
        for (int piece = 0; piece < attackBoards[toMove].length; piece++) {
            for (int index = 0; index < attackBoards[toMove][piece].length; index++) {
                int startSquare = Long.numberOfTrailingZeros(bitboard.getBitBoard(toMove, piece, index));
                long currentBoard = attackBoards[toMove][piece][index];
                for (int square = 0; square < 64; square++) {
                    if (((1L << square) & currentBoard) != 0) {
                        if (bitboard.getSquare(square) * (whoToMove ? 1 : -1) <= 0) {
                            storage[++storage[0]] = (1 << 12) + (startSquare << 6) + square;
                        }
                    }
                }
            }
        }
        return storage;
    }
}
