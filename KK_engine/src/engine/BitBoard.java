package engine;

public class BitBoard {

	private long[][][] bitBoards = new long[2][6][10]; // 2 colours, 6 piece types, max. 10 pieces per piece type
	private long[][] pieceTypes = { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } }; // 2 colours, 6 piece types
	private long[] allPieces = { 0, 0 }; // 2 colours
	
	public BitBoard() {
		for (int colour = 0; colour < 2; colour++) {
			for (int piece = 0; piece < 6; piece++) {
				for (int pieceIndex = 0; pieceIndex < 10; pieceIndex++) {
					bitBoards[colour][piece][pieceIndex] = 0; // 0 means no piece on any square yet / empty board
				}
			}
		}
	}
	
	public boolean move(int startSquare, int endSquare) {
		long searchedBB = 1L << startSquare;
		long bitBoardChange = searchedBB ^ (1L << endSquare); // XOR old square out, new square in (XOR o XOR = id)
		boolean success = false;
		
		for (int colour = 0; colour < 2; colour++) {
			if ((allPieces[colour] & searchedBB) != 0) {
				for (int piece = 0; piece < 6; piece++) {
					if ((pieceTypes[colour][piece] & searchedBB) != 0) {
						for (int index = 0; index < 10; index++) {
							if ((bitBoards[colour][piece][index] & searchedBB) != 0) {
								bitBoards[colour][piece][index] ^= bitBoardChange;
								pieceTypes[colour][piece] ^= bitBoardChange;
								allPieces[colour] ^= bitBoardChange;
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
	
	public void setBitBoard(int colour, int piece, int index, int square) {
		long newBB = 1L << square;
		bitBoards[colour][piece][index] = newBB;
		pieceTypes[colour][piece] |= newBB;
		allPieces[colour] |= newBB;
	}
	
	public long getBitBoard(int colour, int piece, int index) {
		return bitBoards[colour][piece][index];
	}
	
	public long getPieceTypeBoard(int colour, int piece) {
		return pieceTypes[colour][piece];
	}
	
	public long getAllPieces(int colour) {
		return allPieces[colour];
	}
}
