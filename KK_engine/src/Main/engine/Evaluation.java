package Main.engine;

import Main.engineIO.Logging;
import Main.engineIO.UCI;
import test.Assertions;

import java.io.Serializable;

/**
 * 
 * @author Anon
 *
 */
public final class Evaluation implements Serializable {
	
	private static boolean materialOnly = false;
	private int[] whiteSize = new int[6];
	private int[] blackSize = new int[6];
	private int[] storage = new int[MoveGenerator.MAX_MOVE_COUNT];
	private Board board;
	
	@SuppressWarnings("unused")
	private static final int PAWNACTIVITYFULL = 0;
	@SuppressWarnings("unused")
	private static final int PAWNACTIVITYEMPTY = 0;
	private static final int KNIGHTACTIVITYFULL = 3;
	private static final int KNIGHTACTIVITYEMPTY = 3;
	private static final int BISHOPACTIVITYFULL = 3;
	private static final int BISHOPACTIVITYEMPTY = 3;
	private static final int ROOKACTIVITYFULL = 0;
	private static final int ROOKACTIVITYEMPTY = 4;
	private static final int QUEENACTIVITYFULL = 0;
	private static final int QUEENACTIVITYEMPTY = 2;
	private static final int KINGACTIVITYFULL = -3;
	private static final int KINGACTIVITYEMPTY = 1;

	public Evaluation(Board board) {
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
        /*if (!correctBitBoard()) {
            Logging.printLine("BitBoard-Error.");
            System.exit(1);
        }*/
		
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
		
		if (toMove && eval + 100 < lowBound) {
			board.getSearch().abortedNodes++;
			return eval;
		} else if (!toMove && -eval + 100 < lowBound) {
			board.getSearch().abortedNodes++;
			return -eval;
		}
		
		whiteSize = board.getMoveGenerator().activityEval(storage, true);
		blackSize = board.getMoveGenerator().activityEval(storage, false);
		
		eval += activityEval(board);

		if (!toMove) {
			eval = (short) -eval;
		}
		board.getSearch().nodes++;
		return eval;
	}
	
	private int activityEval(Board board) {
		int activityEval = 0;
		int piecesLeft = board.getPiecesLeft();
		//activityEval += PAWNACTIVITYFULL * (whiteSize[0] - blackSize[0]);
		activityEval += (KNIGHTACTIVITYFULL * piecesLeft / 32 
				+ KNIGHTACTIVITYEMPTY * (32 - piecesLeft) / 32)  * (whiteSize[1] - blackSize[1]);
		activityEval += (BISHOPACTIVITYFULL * piecesLeft / 32 
				+ BISHOPACTIVITYEMPTY * (32 - piecesLeft) / 32)  * (whiteSize[2] - blackSize[2]);
		activityEval += (ROOKACTIVITYFULL * piecesLeft / 32 
				+ ROOKACTIVITYEMPTY * (32 - piecesLeft) / 32)  * (whiteSize[3] - blackSize[3]);
		activityEval += (QUEENACTIVITYFULL * piecesLeft / 32 
				+ QUEENACTIVITYEMPTY * (32 - piecesLeft) / 32)  * (whiteSize[4] - blackSize[4]);
		activityEval += (KINGACTIVITYFULL * piecesLeft / 32 
				+ KINGACTIVITYEMPTY * (32 - piecesLeft) / 32)  * (whiteSize[5] - blackSize[5]);
		
		activityEval = (activityEval * UCI.getDynamism()) / 10;
		return activityEval;
	}

	/**
	 *
	 * @return Eval term how far pieces are advanced.
	 */
	private int advancementEval() {
		int advancementEval = 0;
		int piecesLeft = board.getPiecesLeft();
		advancementEval += (int) (board.getPieceAdvancement(1) * ((32.0 - piecesLeft) / 16.0));
														// x2 on empty board, x1 on full board
		advancementEval += board.getPieceAdvancement(2) * 1; // always x1
		advancementEval += (int) (board.getPieceAdvancement(4) * (32.0 - piecesLeft) / 32.0);
														// x1 on empty board, x0 on full board
		if (board.getDangerToWhiteKing() + board.getDangerToBlackKing() > 32) {
			advancementEval += board.getPieceAdvancement(6) * Math.abs(board.getPieceAdvancement(6)) 
					* (32 - (board.getDangerToWhiteKing() + board.getDangerToBlackKing())); // TODO: change, danger is higher than piecesLeft
			// TODO danger only depends on enemy
												// full board ^2*(-16); Math.abs to not lose the sign of original number
		} else {
			advancementEval += (int) (board.getPieceAdvancement(6) 
					* ((32.0 - (board.getDangerToWhiteKing() + board.getDangerToBlackKing())) / 8.0)); // empty board x2
		}
		return advancementEval;
	}
	
	private int pieceSquareTables() {
		int pieceSquares = 0;
		
		if (board.getDangerToWhiteKing() + board.getDangerToBlackKing() > 40) {
			if (board.square[3][0] == 6 || board.square[4][0] == 6 || board.square[5][0] == 6
					|| board.square[3][1] == 6 || board.square[4][1] == 6 || board.square[5][1] == 6) {
				pieceSquares -= (board.getDangerToWhiteKing() - 20) * 2;
			}
			
			if (board.square[3][7] == -6 || board.square[4][7] == -6 || board.square[5][7] == -6
					|| board.square[3][6] == -6 || board.square[4][6] == -6 || board.square[5][6] == -6) {
				pieceSquares += (board.getDangerToBlackKing() - 20) * 2;
			}
			
			
			if (board.square[6][0] == 6 || board.square[7][0] == 6 || board.square[6][1] == 6 
					|| board.square[7][1] == 6) {
				if (board.square[5][1] == 1 && board.square[6][1] == 1 
						&& (board.square[7][1] == 1 || board.square[7][2] == 1)) {
					pieceSquares += (board.getDangerToWhiteKing() - 15);
				} else if (board.square[5][1] == 1 && board.square[6][1] == 3 
						&& board.square[6][2] == 1 && board.square[7][1] == 1) {
					pieceSquares += (board.getDangerToWhiteKing() - 15) * 2;
				} else if (board.square[6][1] != 1 && board.square[6][2] != 1 
						&& (board.square[7][1] == 1 || board.square[7][2] == 1)) {
					pieceSquares -= (board.getDangerToWhiteKing() - 15) * 3;
				} else if (board.square[6][1] != 1 && board.square[6][2] != 1 && board.square[7][1] != 1 
						&& board.square[7][2] != 1) {
					pieceSquares -= (board.getDangerToWhiteKing() - 15) * 6;
				}
				if (board.square[6][1] == 3) {
					pieceSquares += (board.getDangerToWhiteKing() - 15);
				}
			}
			
			if (board.square[6][7] == -6 || board.square[7][7] == -6 || board.square[6][6] == -6 
					|| board.square[7][6] == -6) {
				if (board.square[5][6] == -1 && board.square[6][6] == -1 
						&& (board.square[7][6] == -1 || board.square[7][5] == -1)) {
					pieceSquares -= (board.getDangerToBlackKing() - 15);
				} else if (board.square[5][6] == -1 && board.square[6][6] == -3 
						&& board.square[6][5] == -1 && board.square[7][6] == -1) {
					pieceSquares -= (board.getDangerToBlackKing() - 15) * 2;
				} else if (board.square[6][6] != -1 && board.square[6][5] != -1 
						&& (board.square[7][6] == -1 || board.square[7][5] == -1)) {
					pieceSquares += (board.getDangerToBlackKing() - 15) * 3;
				} else if (board.square[6][6] != -1 && board.square[6][5] != -1 && board.square[7][6] != -1 
						&& board.square[7][5] != -1) {
					pieceSquares += (board.getDangerToBlackKing() - 15) * 6;
				}
				if (board.square[6][6] == -3) {
					pieceSquares -= (board.getDangerToBlackKing() - 15);
				}
			}
		}
		pieceSquares = (pieceSquares * UCI.getKingSafety()) / 10;
		
		if (board.square[3][3] == 1) {
			pieceSquares += 10;
		}
		if (board.square[4][3] == 1) {
			pieceSquares += 10;
		}
		if (board.square[3][4] == -1) {
			pieceSquares -= 10;
		}
		if (board.square[4][4] == -1) {
			pieceSquares -= 10;
		}
		
		if (board.getPiecesLeft() > 25) {
			if (board.square[3][0] != 5) {
				pieceSquares -= (board.getPiecesLeft() - 25);
			}
			if (board.square[3][7] != -5) {
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
                if (board.getSquare(i, j) != board.bitboard.getSquare(i * 8 + j)) {
                    Logging.printLine("BitBoard wrong." + i + " " + j);
                    board.printBoard();
                    board.bitboard.printBitBoard();
                    return false;
                }
            }
        }
		return true;
	}

	/**
	 * This method does nothing right now. If we ever have add state to the Evaluation we need to implement that state being resetted here.
	 */
	public void resetEvaluation() {

	}
}