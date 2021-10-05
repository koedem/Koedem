package Main.engine;

import Main.Utility.Constants;
import Main.engineIO.Logging;
import Main.engineIO.UCI;
import test.Assertions;

import java.util.Random;

/**
 *
 * @author Anon
 *
 */
public final class Evaluation implements EvaluationInterface {

	private static boolean materialOnly = false;
	private final int[] storage = new int[MoveGenerator.MAX_MOVE_COUNT];
	private final BoardInterface board;
	
	private static final Random random = new Random(64);
	
	public Evaluation(BoardInterface board) {
		this.board = board;
	}

	/**
	 *
	 * @param toMove : who to move it is
	 * @param lowBound Score that the player is guaranteed. If material eval is far below this, cutoff.
	 * @return evaluation based on material
	 */
	public int evaluation(boolean toMove, int lowBound) {

		assert Assertions.advancement(board);
		assert Assertions.materialCount(board);
		assert correctBitBoard();
		assert Assertions.attackBoard(board);
		assert Assertions.correctPSTs(board);

		board.getSearch().incrementNodes();
		if (isMaterialOnly()) {
			if (toMove) {
				return board.getMaterialCount();
			} else {
				return -board.getMaterialCount();
			}
		}
		int eval = board.getMaterialCount();
		//eval += board.getPieceSquareTable(); //TODO
		eval += advancementEval();
		eval += pieceSquareTables();
		eval += activityEval(board);

		if (!toMove) {
			eval = (short) -eval;
		}
		return eval;
	}

	private int activityEval(BoardInterface board) {
		AttackBoard ab = board.getAttackBoard();
		ab.generateAttackCount();
		int activityEval = 0;
		int piecesLeft = board.getPiecesLeft();
		activityEval += ((PAWN_ACTIVITY_FULL * piecesLeft + PAWN_ACTIVITY_EMPTY * (32 - piecesLeft)) // TODO smarter way to count mid / endgame than just pieces left
		                 * (ab.getAttackCount(0, 0) + ab.getAttackCount(0, 1) - ab.getAttackCount(1, 0) - ab.getAttackCount(1, 1))) / 32;
		activityEval += ((KNIGHT_ACTIVITY_FULL * piecesLeft + KNIGHT_ACTIVITY_EMPTY * (32 - piecesLeft)) * (ab.getAttackCount(0, 2) - ab.getAttackCount(1, 2))) / 32;
		activityEval += ((BISHOP_ACTIVITY_FULL * piecesLeft + BISHOP_ACTIVITY_EMPTY * (32 - piecesLeft)) * (ab.getAttackCount(0, 3) - ab.getAttackCount(1, 3))) / 32;
		activityEval += ((ROOK_ACTIVITY_FULL * piecesLeft + ROOK_ACTIVITY_EMPTY * (32 - piecesLeft)) * (ab.getAttackCount(0, 4) - ab.getAttackCount(1, 4))) / 32;
		activityEval += ((QUEEN_ACTIVITY_FULL * piecesLeft + QUEEN_ACTIVITY_EMPTY * (32 - piecesLeft)) * (ab.getAttackCount(0, 5) - ab.getAttackCount(1, 5))) / 32;
		activityEval += ((KING_ACTIVITY_FULL * piecesLeft + KING_ACTIVITY_EMPTY * (32 - piecesLeft)) * (ab.getAttackCount(0, 6) - ab.getAttackCount(1, 6))) / 32;

		assert correctActivityEval(ab);
		activityEval = (activityEval * UCI.getDynamism()) / 1000;
		return activityEval;
	}

	/**
	 *
	 * @return Eval term how far pieces are advanced.
	 */
	private int advancementEval() {
		int advancementEval = 0;
		int piecesLeft = board.getPiecesLeft();
		advancementEval += (board.getPieceAdvancement(1) * (32 - piecesLeft)) / 16;
														// x2 on empty board, x0 on full board
		advancementEval += board.getPieceAdvancement(2) * 1; // always x1
		advancementEval += (board.getPieceAdvancement(4) * (32 - piecesLeft)) / 32;
														// x1 on empty board, x0 on full board
		if (board.getDangerToWhiteKing() + board.getDangerToBlackKing() > 32) {
			advancementEval += board.getPieceAdvancement(6) * Math.abs(board.getPieceAdvancement(6))
					* (32 - (board.getDangerToWhiteKing() + board.getDangerToBlackKing())); // TODO: change, danger is higher than piecesLeft
												// full board ^2*(-16); Math.abs to not lose the sign of original number
		} else {
			advancementEval += (board.getPieceAdvancement(6) * (32 - (board.getDangerToWhiteKing() + board.getDangerToBlackKing()))) / 8; // empty board x2
		}
		return advancementEval;
	}

	private int pieceSquareTables() {
		int pieceSquares = 0;

		if (board.getDangerToWhiteKing() + board.getDangerToBlackKing() > 40) {
			if (board.getSquare(3, 0) == 6 || board.getSquare(4, 0) == 6 || board.getSquare(5, 0) == 6
					|| board.getSquare(3, 1) == 6 || board.getSquare(4, 1) == 6 || board.getSquare(5, 1) == 6) {
				pieceSquares -= (board.getDangerToWhiteKing() - 20) * 2;
			}

			if (board.getSquare(3, 7) == -6 || board.getSquare(4, 7) == -6 || board.getSquare(5, 7) == -6
					|| board.getSquare(3, 6) == -6 || board.getSquare(4, 6) == -6 || board.getSquare(5, 6) == -6) {
				pieceSquares += (board.getDangerToBlackKing() - 20) * 2;
			}


			if (board.getSquare(6, 0) == 6 || board.getSquare(7, 0) == 6 || board.getSquare(6, 1) == 6
					|| board.getSquare(7, 1) == 6) {
				if (board.getSquare(5, 1) == 1 && board.getSquare(6, 1) == 1
						&& (board.getSquare(7, 1) == 1 || board.getSquare(7, 2) == 1)) {
					pieceSquares += (board.getDangerToWhiteKing() - 15);
				} else if (board.getSquare(5, 1) == 1 && board.getSquare(6, 1) == 3
						&& board.getSquare(6, 2) == 1 && board.getSquare(7, 1) == 1) {
					pieceSquares += (board.getDangerToWhiteKing() - 15) * 2;
				} else if (board.getSquare(6, 1) != 1 && board.getSquare(6, 2) != 1
						&& (board.getSquare(7, 1) == 1 || board.getSquare(7, 2) == 1)) {
					pieceSquares -= (board.getDangerToWhiteKing() - 15) * 3;
				} else if (board.getSquare(6, 1) != 1 && board.getSquare(6, 2) != 1 && board.getSquare(7, 1) != 1
						&& board.getSquare(7, 2) != 1) {
					pieceSquares -= (board.getDangerToWhiteKing() - 15) * 6;
				}
				if (board.getSquare(6, 1) == 3) {
					pieceSquares += (board.getDangerToWhiteKing() - 15);
				}
			}

			if (board.getSquare(6, 7) == -6 || board.getSquare(7, 7) == -6 || board.getSquare(6, 6) == -6
					|| board.getSquare(7, 6) == -6) {
				if (board.getSquare(5, 6) == -1 && board.getSquare(6, 6) == -1
						&& (board.getSquare(7, 6) == -1 || board.getSquare(7, 5) == -1)) {
					pieceSquares -= (board.getDangerToBlackKing() - 15);
				} else if (board.getSquare(5, 6) == -1 && board.getSquare(6, 6) == -3
						&& board.getSquare(6, 5) == -1 && board.getSquare(7, 6) == -1) {
					pieceSquares -= (board.getDangerToBlackKing() - 15) * 2;
				} else if (board.getSquare(6, 6) != -1 && board.getSquare(6, 5) != -1
						&& (board.getSquare(7, 6) == -1 || board.getSquare(7, 5) == -1)) {
					pieceSquares += (board.getDangerToBlackKing() - 15) * 3;
				} else if (board.getSquare(6, 6) != -1 && board.getSquare(6, 5) != -1 && board.getSquare(7, 6) != -1
						&& board.getSquare(7, 5) != -1) {
					pieceSquares += (board.getDangerToBlackKing() - 15) * 6;
				}
				if (board.getSquare(6, 6) == -3) {
					pieceSquares -= (board.getDangerToBlackKing() - 15);
				}
			}
		}
		pieceSquares = (pieceSquares * UCI.getKingSafety()) / 100;

		if (board.getSquare(3, 3) == 1) {
			pieceSquares += 10;
		}
		if (board.getSquare(4, 3) == 1) {
			pieceSquares += 10;
		}
		if (board.getSquare(3, 4) == -1) {
			pieceSquares -= 10;
		}
		if (board.getSquare(4, 4) == -1) {
			pieceSquares -= 10;
		}

		if (board.getPiecesLeft() > 25) {
			if (board.getSquare(3, 0) != 5) {
				pieceSquares -= (board.getPiecesLeft() - 25);
			}
			if (board.getSquare(3, 7) != -5) {
				pieceSquares += (board.getPiecesLeft() - 25);
			}
		}

		return pieceSquares;
	}

	public static boolean isMaterialOnly() {
		return materialOnly;
	}

	public static void setMaterialOnly(boolean materialOnly) {
		Evaluation.materialOnly = materialOnly;
	}

	public int fullPST() {
		int pst = 0;
		int piece = 0;
		for (int square = 0; square < 64; square++) {
			if ((piece = board.getSquare(square / 8, square % 8)) != 0) {
				if (piece > 0) {
					pst += PIECE_SQUARE_TABLES[Constants.WHITE][piece][square];
				} else {
					pst += PIECE_SQUARE_TABLES[Constants.BLACK][-piece][square];
				}
			}
		}
		return pst;
	}

	private boolean correctBitBoard() {
		for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getSquare(i, j) != board.getBitboard().getSquare(i * 8 + j)) {
                    Logging.printLine("BitBoard wrong." + i + " " + j);
                    board.printBoard();
                    board.getBitboard().printBitBoard();
                    return false;
                }
            }
        }
		return true;
	}

	private boolean correctActivityEval(AttackBoard ab) {
		int[] whiteSize = new int[6];
		int[] blackSize = new int[6];
		whiteSize = board.getMoveGenerator().activityEval(true, storage, whiteSize);
		blackSize = board.getMoveGenerator().activityEval(false, storage, blackSize);

		assert whiteSize[0] == ab.getAttackCount(0, 0) + ab.getAttackCount(0, 1) && whiteSize[1] == ab.getAttackCount(0, 2)
		       && whiteSize[2] == ab.getAttackCount(0, 3) && whiteSize[3] == ab.getAttackCount(0, 4)
		       && whiteSize[4] == ab.getAttackCount(0, 5) && whiteSize[5] == ab.getAttackCount(0, 6);
		assert blackSize[0] == ab.getAttackCount(1, 0) + ab.getAttackCount(1, 1) && blackSize[1] == ab.getAttackCount(1, 2)
		       && blackSize[2] == ab.getAttackCount(1, 3) && blackSize[3] == ab.getAttackCount(1, 4)
		       && blackSize[4] == ab.getAttackCount(1, 5) && blackSize[5] == ab.getAttackCount(1, 6);
		return true;
	}

	/**
	 * This method does nothing right now. If we ever have add state to the Evaluation we need to implement that state being resetted here.
	 */
	public void resetEvaluation() {

	}

	public static int PAWN_ACTIVITY_FULL    = 38;
	public static int PAWN_ACTIVITY_EMPTY   = 10;
	public static int KNIGHT_ACTIVITY_FULL  = 54;
	public static int KNIGHT_ACTIVITY_EMPTY = 33;
	public static int BISHOP_ACTIVITY_FULL  = 36;
	public static int BISHOP_ACTIVITY_EMPTY = 59;
	public static int ROOK_ACTIVITY_FULL    = 42;
	public static int ROOK_ACTIVITY_EMPTY   = 18;
	public static int QUEEN_ACTIVITY_FULL   = 3;
	public static int QUEEN_ACTIVITY_EMPTY  = 73;
	public static int KING_ACTIVITY_FULL    = -22;
	public static int KING_ACTIVITY_EMPTY   = 44;

	/**
	 * Indexed by colour, piece type, and square.
	 * 8x8 squares are a to h file for rows and 1 to 8 for indices in that rows, i.e. a1 to a8 then b1 to b8 etc because reasons.
	 */
	public static int[][][] PIECE_SQUARE_TABLES =   { { new int[64],
	                                                    {		-2,		-3,		-1,		-3,		2,		0,		2,		0,
	                                                             9,		0,		-4,		0,		5,		-3,		-1,		0,
	                                                             1,		1,		-2,		0,		4,		8,		1,		-1,
	                                                             1,		-4,		2,		1,		1,		0,		2,		-1,
	                                                             3,		0,		4,		0,		-5,		9,		-5,		1,
	                                                             0,		8,		-3,		4,		0,		10,		7,		6,
	                                                             -5,		7,		1,		-4,		-4,		6,		0,		0,
	                                                             -1,		-4,		-2,		-1,		1,		-2,		3,		-3, },

	                                                    {		4,		1,		1,		3,		0,		0,		-1,		0,
	                                                             -6,	3,		1,		-3,		0,		4,		0,		0,
	                                                             4,		-2,		0,		3,		5,		3,		2,		2,
	                                                             -7,	0,		2,		2,		5,		-3,		-7,		-2,
	                                                             1,		2,		3,		1,		7,		-8,		-3,		2,
	                                                             1,		0,		12,		2,		5,		0,		0,		3,
	                                                             -5,	0,		0,		-3,		-4,		4,		-1,		-6,
	                                                             4,		-2,		1,		2,		-3,		4,		2,		-2, },

	                                                    {		0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0, },

	                                                    {		0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0, },

	                                                    {		0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0, },

	                                                    {		0,		1,		-4,		0,		1,		-1,		1,		1,
	                                                             -1,	-2,		3,		1,		2,		1,		0,		0,
	                                                             0,		1,		3,		2,		2,		-3,		-2,		2,
	                                                             -3,	3,		-2,		-2,		2,		-5,		1,		0,
	                                                             1,		2,		-1,		2,		4,		-7,		3,		5,
	                                                             4,		8,		2,		6,		3,		-1,		2,		-3,
	                                                             14,	7,		-6,		-1,		2,		2,		6,		-7,
	                                                             -3,	-6,		6,		2,		-4,		-2,		-5,		5, },

	                                                    },

	                                                  { new int[64],
	                                                    {		-1,		0,		-1,		-3,		1,		1,		4,		1,
	                                                             6,		1,		-2,		-4,		-5,		4,		-2,		0,
	                                                             -1,		-3,		3,		-5,		-1,		1,		7,		8,
	                                                             -5,		4,		-6,		5,		0,		0,		6,		-2,
	                                                             0,		5,		-1,		-3,		5,		-6,		0,		-2,
	                                                             0,		-4,		5,		0,		4,		-2,		-3,		-10,
	                                                             0,		-2,		1,		-2,		7,		0,		0,		7,
	                                                             -2,		-5,		-2,		-4,		2,		6,		5,		0, },

	                                                    {		3,		2,		0,		3,		2,		7,		-2,		0,
	                                                             -4,	-1,		-2,		4,		2,		0,		-5,		4,
	                                                             0,		4,		-2,		2,		-4,		-6,		0,		-7,
	                                                             -5,	-9,		2,		-2,		-5,		-5,		0,		0,
	                                                             1,		2,		-5,		-5,		-3,		-8,		-3,		-5,
	                                                             3,		-1,		1,		0,		-2,		-12,	4,		-1,
	                                                             3,		0,		-1,		2,		-3,		-1,		-2,		1,
	                                                             -7,	-3,		2,		-7,		0,		3,		0,		-4, },

	                                                    {		0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0, },

	                                                    {		0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0, },

	                                                    {		0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0,
	                                                             0,		0,		0,		0,		0,		0,		0,		0, },

	                                                    {		-1,		1,		3,		2,		0,		3,		-4,		2,
	                                                             -1,	0,		2,		-5,		2,		0,		2,		-8,
	                                                             -2,	0,		-6,		2,		-1,		-4,		-1,		2,
	                                                             0,		0,		-3,		-3,		-3,		-3,		0,		11,
	                                                             0,		2,		3,		-2,		-1,		3,		-7,		2,
	                                                             1,		-2,		6,		0,		1,		-3,		-5,		5,
	                                                             -5,	-3,		1,		3,		-1,		3,		-2,		-6,
	                                                             -2,	3,		2,		-1,		-3,		0,		2,		5, },

	                                                    } };
}