package Main.engine;

import java.io.Serializable;

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
	 * @return a boolean that is true if the resulting position is present in the move repetition table and false if it is not.
	 */
	boolean makeMove(int move);

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

	/**
	 * Castling rights is a byte. Check for white King side castle with & 0x18 == 0x18, Q side & 0x30 == 0x30,
	 * black K side 0x3 == 0x3, Q side 0x6 == 0x6.
	 *
	 * @return Which castlings are still possible.
	 */
	byte getCastlingRights();

	byte getEnPassant();

	void setEnPassant(byte enPassant);

	boolean getToMove();

	int getMoveNumber();

	BoardInterface cloneBoard();

	void setFENPosition(String fen);

	void addCastlingRights(byte castlingRights);

	//void putRepetitionEntry(long zobristHash);

	//void removeRepetitionEntry(long zobristHash);

	//boolean repetitionContained(long zobristHash);

	void printRepetitionInfo();

	long getZobristHash();

	void changeToMove();

	int getPieceAdvancement(int piece);

	short getMaterialCount();

	int getPiecesLeft();

	int getDangerToWhiteKing();

	int getDangerToBlackKing();

	void setSquare(int file, int row, byte value);

	void makeMove(String move);

	AttackBoard getAttackBoard();

	SearchInterface getSearch();

	MoveGeneratorInterface getMoveGenerator();

	CaptureGeneratorInterface getCaptureGenerator();

	CheckMoveGeneratorInterface getCheckMoveGenerator();

	EvaluationInterface getEvaluation();

	MateFinder getMateFinder();

	BitBoardInterface getBitboard();

	void resetBoard();

	int isFreeSquare(int file, int row, boolean toMove);

	int getPieceSquareTable();

}
