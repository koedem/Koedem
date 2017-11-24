package Main.engine;

import java.io.Serializable;
import java.util.ArrayList;

public class AttackBoard implements Serializable {

    private BitBoard bitboard;

	public static final long[] KINGBOARD = 
		{ 	0xC040L, 0xE0A0L, 0x7050L, 0x3828L, 0x1C14L, 0xE0AL, 0x705L, 0x302L,
			0xC040C0L, 0xE0A0E0L, 0x705070L, 0x382838L, 0x1C141CL, 0xE0A0EL, 0x70507L, 0x30203L,
			0xC040C000L, 0xE0A0E000L, 0x70507000L, 0x38283800L, 0x1C141C00L, 0xE0A0E00L, 0x7050700L, 0x3020300L,
			0xC040C0L << 16, 0xE0A0E0L << 16, 0x705070L << 16, 0x382838L << 16,
			0x1C141CL << 16, 0xE0A0EL << 16, 0x70507L << 16, 0x30203L << 16,
			0xC040C0L << 24, 0xE0A0E0L << 24, 0x705070L << 24, 0x382838L << 24,
			0x1C141CL << 24, 0xE0A0EL << 24, 0x70507L << 24, 0x30203L << 24,
			0xC040C0L << 32, 0xE0A0E0L << 32, 0x705070L << 32, 0x382838L << 32,
			0x1C141CL << 32, 0xE0A0EL << 32, 0x70507L << 32, 0x30203L << 32,
			0xC040C0L << 40, 0xE0A0E0L << 40, 0x705070L << 40, 0x382838L << 40,
			0x1C141CL << 40, 0xE0A0EL << 40, 0x70507L << 40, 0x30203L << 40,
			0x40C0L << 48, 0xA0E0L << 48, 0x5070L << 48, 0x2838L << 48,
			0x141CL << 48, 0xA0EL << 48, 0x507L << 48, 0x203L << 48
	};
	
	public static final long[] KNIGHTBOARD = 
		{ 	0x402000L, 0xA01000L, 0x508800L, 0x284400L, 0x142200L, 0xA1100L, 0x50800L, 0x20400L,
			0x40200020L, 0xA0100010L, 0x50880088L, 0x28440044, 0x14220022, 0xA110011L, 0x5080008L, 0x2040004L,
			0x4020002040L, 0xA0100010A0L, 0x5088008850L, 0x2844004428L,
			0x1422002214L, 0xA1100110AL, 0x508000805L, 0x204000402L,
			0x4020002040L << 8, 0xA0100010A0L << 8, 0x5088008850L << 8, 0x2844004428L << 8,
			0x1422002214L << 8, 0xA1100110AL << 8, 0x508000805L << 8, 0x204000402L << 8,
			0x4020002040L << 16, 0xA0100010A0L << 16, 0x5088008850L << 16, 0x2844004428L << 16,
			0x1422002214L << 16, 0xA1100110AL << 16, 0x508000805L << 16, 0x204000402L << 16,
			0x4020002040L << 24, 0xA0100010A0L << 24, 0x5088008850L << 24, 0x2844004428L << 24,
			0x1422002214L << 24, 0xA1100110AL << 24, 0x508000805L << 24, 0x204000402L << 24,
			0x20002040L << 32, 0x100010A0L << 32, 0x88008850L << 32, 0x44004428L << 32,
			0x22002214L << 32, 0x1100110AL << 32, 0x8000805L << 32, 0x4000402L << 32,
			0x2040L << 40, 0x10A0L << 40, 0x8850L << 40, 0x4428L << 40,
			0x2214L << 40, 0x110AL << 40, 0x805L << 40, 0x402L << 40
	};
	
	public static final long[] PAWNCAPTURES = 
		{	0, 0, 0, 0, 0, 0, 0, 0, 
			0x40L << 16, 0xA0L << 16, 0x50L << 16, 0x28L << 16, 0x14L << 16, 0xAL << 16, 0x5L << 16, 0x2L << 16,
			0x40L << 24, 0xA0L << 24, 0x50L << 24, 0x28L << 24, 0x14L << 24, 0xAL << 24, 0x5L << 24, 0x2L << 24,
			0x40L << 32, 0xA0L << 32, 0x50L << 32, 0x28L << 32, 0x14L << 32, 0xAL << 32, 0x5L << 32, 0x2L << 32,
			0x40L << 40, 0xA0L << 40, 0x50L << 40, 0x28L << 40, 0x14L << 40, 0xAL << 40, 0x5L << 40, 0x2L << 40,
			0x40L << 48, 0xA0L << 48, 0x50L << 48, 0x28L << 48, 0x14L << 48, 0xAL << 48, 0x5L << 48, 0x2L << 48,
			0x40L << 56, 0xA0L << 56, 0x50L << 56, 0x28L << 56, 0x14L << 56, 0xAL << 56, 0x5L << 56, 0x2L << 56,
			0, 0, 0, 0, 0, 0, 0, 0
	};
	
	private long[][][] attackBoards = new long[2][7][10];
			// 2 colours, 7 piece types (1 padding + 6), max. 10 pieces per piece type(promotions)
	
	private long[][] pieceTypes = { { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 } };
	                                            // 2 colours, 7 piece types (1 padding + 6)
	                                            // + (first two vs other 8 pieces) * 4

	private byte[][][] pieceAttackCount = new byte[2][7][64];
	
	private long[] allPieces = { 0, 0 }; // 2 colours

    private byte[][] allAttackCount = new byte[2][64];

    public AttackBoard(BitBoard bitboard) {
		for (int colour = 0; colour < attackBoards.length; colour++) {
			for (int piece = 0; piece < attackBoards[colour].length; piece++) {
				for (int pieceIndex = 0; pieceIndex < attackBoards[colour][piece].length; pieceIndex++) {
					attackBoards[colour][piece][pieceIndex] = 0; // initialize empty board
				}
			}
		}
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
		return true;
		//return success;
	}
	
	private boolean kingMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		attackBoards[colour][6][pieceIndex] = KINGBOARD[endSquare];
		pieceTypes[colour][6] = KINGBOARD[endSquare];
		allPieces[colour] = pieceTypes[colour][1] | pieceTypes[colour][2] | pieceTypes[colour][3]
                | pieceTypes[colour][4] | pieceTypes[colour][5] | pieceTypes[colour][6];
		return true;
	}
	
	private boolean knightMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		attackBoards[colour][2][pieceIndex] = KNIGHTBOARD[endSquare];

		if (pieceIndex <= 1) {
			pieceTypes[colour + 2][2] = attackBoards[colour][2][0] | attackBoards[colour][2][1];
		} else {
			pieceTypes[colour + 4][2] = attackBoards[colour][2][2] | attackBoards[colour][2][3]
					| attackBoards[colour][2][4] | attackBoards[colour][2][5] | attackBoards[colour][2][6]
					| attackBoards[colour][2][7] | attackBoards[colour][2][8] | attackBoards[colour][2][9];
		}

		pieceTypes[colour][2] = pieceTypes[colour + 2][2] | pieceTypes[colour + 4][2];
		allPieces[colour] = pieceTypes[colour][1] | pieceTypes[colour][2] | pieceTypes[colour][3]
                | pieceTypes[colour][4] | pieceTypes[colour][5] | pieceTypes[colour][6];
		return true;
	}
	
	private boolean rookMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		boolean success = false;
		
		return success;
	}
	
	private boolean bishopMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		boolean success = false;
		
		return success;
	}
	
	private boolean queenMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		boolean success = false;
		
		return success;
	}
	
	private boolean pawnMove(int colour, int pieceIndex, int startSquare, int endSquare) {
		boolean success = false;
		
		return success;
	}

	public ArrayList<Integer> moveGenerator(boolean whoToMove) {
        ArrayList<Integer> moves = new ArrayList<>();
        int toMove = whoToMove ? 0 : 1;
        for (int piece = 0; piece < attackBoards[toMove].length; piece++) {
            for (int index = 0; index < attackBoards[toMove][piece].length; index++) {
                int startSquare = (int) Long.highestOneBit(bitboard.getBitBoard(toMove, piece, index));
                long currentBoard = attackBoards[toMove][piece][index];
                for (int square = 0; square < 64; square++) {
                    if (((1L << square) & currentBoard) != 0) {
                        if (bitboard.getSquare(square) * (whoToMove ? 1 : -1) >= 0) {
                            moves.add(1 << 12 + startSquare << 6 + square);
                        }
                    }
                }
            }
        }
        return moves;
    }
}
