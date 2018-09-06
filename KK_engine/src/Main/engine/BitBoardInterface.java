package Main.engine;

import java.io.Serializable;

/**
 *
 */
public interface BitBoardInterface extends Serializable {

	long getBitBoard(int toMove, int piece, int index);

	long getPieceTypeBoard(int colour, int piece);
	long getAllPieces(int colour);
	int getSquare(int square);

	AttackBoard getAttackBoard();

	void setAttackBoard(AttackBoard attackBoard);

	void add(int piece, int colour, int square);

	void remove(int square);

	boolean move(int startSquare, int endSquare, boolean capture, int capturedPiece);

	void printBitBoard();

	void resetBitBoard();
}
