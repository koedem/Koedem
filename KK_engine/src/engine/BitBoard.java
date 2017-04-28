package engine;

public class BitBoard {

	private long[][][] BBs = new long[2][6][10]; // 2 colours, 6 piece types, max. 10 pieces per piece type(promotions)
	private long[][] pieceTypes = { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } }; // 2 colours, 6 piece types
	private long[] allPieces = { 0, 0 }; // 2 colours
	
	public BitBoard() {
		for (int colour = 0; colour < 2; colour++) {
			for (int piece = 0; piece < 6; piece++) {
				for (int pieceIndex = 0; pieceIndex < 10; pieceIndex++) {
					BBs[colour][piece][pieceIndex] = 0; // 0 means no piece on any square yet / empty board
				}
			}
		}
	}
	
	public boolean move(int startSquare, int endSquare) {
		long searchedBB = 1L << startSquare;
		long BBchange = searchedBB ^ (1l << endSquare); // XOR old square out, new square in (XOR o XOR = id)
		boolean success = false;
		
		for (int colour = 0; colour < 2; colour++) {
			if ((allPieces[colour] & searchedBB) != 0) {
				for (int piece = 0; piece < 6; piece++) {
					if ((pieceTypes[colour][piece] & searchedBB) != 0) {
						for (int index = 0; index < 10; index++) {
							if ((BBs[colour][piece][index] & searchedBB) != 0) {
								BBs[colour][piece][index] ^= BBchange;
								pieceTypes[colour][piece] ^= BBchange;
								allPieces[colour] ^= BBchange;
								success = true;
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return success;
	}
	
	public void setBB(int colour, int piece, int index, int square) {
		long newBB = 1L << square;
		BBs[colour][piece][index] = newBB;
		pieceTypes[colour][piece] |= newBB;
		allPieces[colour] |= newBB;
	}
	
	public long getBitBoard(int colour, int piece, int index) {
		return BBs[colour][piece][index];
	}
}
