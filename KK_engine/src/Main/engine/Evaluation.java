package Main.engine;

import Main.engineIO.Logging;
import Main.engineIO.UCI;
import test.Assertions;

/**
 * 
 * @author Anon
 *
 */
public final class Evaluation implements EvaluationInterface {
	
	private static boolean materialOnly = false;
	private int[] storage = new int[MoveGenerator.MAX_MOVE_COUNT];
	private BoardInterface board;

	public static int PAWNACTIVITYFULL = 38;
	public static int PAWNACTIVITYEMPTY = 10;
	public static int KNIGHTACTIVITYFULL = 54;
	public static int KNIGHTACTIVITYEMPTY = 33;
	public static int BISHOPACTIVITYFULL = 36;
	public static int BISHOPACTIVITYEMPTY = 59;
	public static int ROOKACTIVITYFULL = 42;
	public static int ROOKACTIVITYEMPTY = 18;
	public static int QUEENACTIVITYFULL = 3;
	public static int QUEENACTIVITYEMPTY = 73;
	public static int KINGACTIVITYFULL = -22;
	public static int KINGACTIVITYEMPTY = 44;

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
		
		if (isMaterialOnly()) {
			if (toMove) {
				return board.getMaterialCount();
			} else {
				return -board.getMaterialCount();
			}
		}
		int eval = board.getMaterialCount();
		eval += advancementEval();
		eval += pieceSquareTables();
		eval += activityEval(board);

		if (!toMove) {
			eval = (short) -eval;
		}
		board.getSearch().incrementNodes();
		return eval;
	}
	
	private int activityEval(BoardInterface board) {
		AttackBoard ab = board.getAttackBoard();
		ab.generateAttackCount();
		int activityEval = 0;
		int piecesLeft = board.getPiecesLeft();
		activityEval += ((PAWNACTIVITYFULL * piecesLeft + PAWNACTIVITYEMPTY * (32 - piecesLeft))
		                 * (ab.getAttackCount(0, 0) + ab.getAttackCount(0, 1) - ab.getAttackCount(1, 0) - ab.getAttackCount(1, 1))) / 32;
		activityEval += ((KNIGHTACTIVITYFULL * piecesLeft + KNIGHTACTIVITYEMPTY * (32 - piecesLeft))  * (ab.getAttackCount(0, 2) - ab.getAttackCount(1, 2))) / 32;
		activityEval += ((BISHOPACTIVITYFULL * piecesLeft + BISHOPACTIVITYEMPTY * (32 - piecesLeft))  * (ab.getAttackCount(0, 3) - ab.getAttackCount(1, 3))) / 32;
		activityEval += ((ROOKACTIVITYFULL * piecesLeft + ROOKACTIVITYEMPTY * (32 - piecesLeft))  * (ab.getAttackCount(0, 4) - ab.getAttackCount(1, 4))) / 32;
		activityEval += ((QUEENACTIVITYFULL * piecesLeft + QUEENACTIVITYEMPTY * (32 - piecesLeft))  * (ab.getAttackCount(0, 5) - ab.getAttackCount(1, 5))) / 32;
		activityEval += ((KINGACTIVITYFULL * piecesLeft + KINGACTIVITYEMPTY * (32 - piecesLeft))  * (ab.getAttackCount(0, 6) - ab.getAttackCount(1, 6))) / 32;

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
}