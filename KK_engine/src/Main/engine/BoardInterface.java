package Main.engine;

import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 */
public interface BoardInterface extends Serializable {

	String getBestmove();

	void setBestmove(String bestmove);

	void printBoard();

	/**
	 * Execute the move and remember possibly lost information. (e.g. in chess captured piece, castling right, e.p.)
	 * These will have to be restored when a corresponding unmakeMove is called.
	 * @param move The move to be made. Usually the format will be startSquare endSquare however details are left
	 *             to the implementation.
	 * @return An integer token to be returned when unmaking the move.
	 */
	int makeMove(int move);

	/**
	 * TODO change
	 * @param move
	 * @param capturedPiece
	 * @param oldCastlingRights
	 */
	void unmakeMove(int move, byte capturedPiece, byte oldCastlingRights);

	int[] getRootMoves();

	void setRootMoves(int[] rootMoves);

	byte getSquare(int file, int row);

	byte getCastlingRights();

	byte getEnPassant();

	void setEnPassant(byte enPassant);

	boolean getToMove();

	int getMoveNumber();

	BoardInterface cloneBoard();

	void addCastlingRights(byte castlingRights);

	Hashtable<Long, Node> getHashTable();

	long getZobristHash();

	void changeToMove();

	int getPieceAdvancement(int piece);

	short getMaterialCount();

	int getPiecesLeft();

	int getDangerToWhiteKing();

	int getDangerToBlackKing();

	void putHashTableElement(Node node);

	void setSquare(int file, int row, byte value);

	void makeMove(String move);

	AttackBoard getAttackBoard();

	void setAttackBoard(AttackBoard attackBoard);

	SearchInterface getSearch();

	MoveGeneratorInterface getMoveGenerator();

	EvaluationInterface getEvaluation();

	BitBoardInterface getBitboard();

	void setBitboard(BitBoardInterface bitboard);
}
