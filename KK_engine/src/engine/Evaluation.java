package engine;

import java.util.ArrayList;

/**
 * 
 * @author Anon
 *
 */
public final class Evaluation {
	
	protected static boolean materialOnly = false;
	
	private final static int PAWNACTIVITYFULL = 0;
	private final static int PAWNACTIVITYEMPTY = 0;
	private final static int KNIGHTACTIVITYFULL = 3;
	private final static int KNIGHTACTIVITYEMPTY = 3;
	private final static int BISHOPACTIVITYFULL = 3;
	private final static int BISHOPACTIVITYEMPTY = 3;
	private final static int ROOKACTIVITYFULL = 0;
	private final static int ROOKACTIVITYEMPTY = 4;
	private final static int QUEENACTIVITYFULL = 0;
	private final static int QUEENACTIVITYEMPTY = 2;
	private final static int KINGACTIVITYFULL = -3;
	private final static int KINGACTIVITYEMPTY = 1;
	
	/**
	 * Used to count the nodes we calculate in a Search.
	 */
	private static long nodeCount = 0;
	private static long abortedNodes = 0;

	/**
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @param lowBound Score that the player is guaranteed. If material eval is far below this, cutoff.
	 * @return evaluation based on material
	 */
	public static int evaluation(Board board, boolean toMove, int lowBound) {
		if (materialOnly) {
			if (toMove) {
				return board.getMaterialCount();
			} else {
				return -board.getMaterialCount();
			}
		}
		int eval = board.getMaterialCount();
		if (toMove && eval + 100 < lowBound) {
			abortedNodes++;
			return eval;
		} else if (!toMove && -eval + 100 < lowBound) {
			abortedNodes++;
			return -eval;
		}
		int pieceCounter = 0;
		int advancement = 0;
		for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				if (board.square[i][j] != 0) {
					advancement += 2 * j - 7;
					pieceCounter++;
				}
			}
		}

		if (pieceCounter != board.getPiecesLeft()) {
			System.out.println("Piece count error.");
		}
		if (advancement != board.getPawnAdvancement() + board.getKnightAdvancement() + board.getBishopAdvancement()
				+ board.getRookAdvancement() + board.getQueenAdvancement() + board.getKingAdvancement()) {
			System.out.println("Advancement error.");
		}
		
		int[] whiteSize = MoveGenerator.activityEval(board, true);
		int[] blackSize = MoveGenerator.activityEval(board, false);
		int whiteActivity = 0;
		int blackActivity = 0;
		for (int piece = 0; piece < whiteSize.length; piece++) {
			whiteActivity += whiteSize[piece];
			blackActivity += blackSize[piece];
		}
		
		ArrayList<Integer> whiteMove = MoveGenerator.collectMoves(board, true);
		ArrayList<Integer> blackMove = MoveGenerator.collectMoves(board, false);
		
		if (whiteMove.size() != whiteActivity || blackMove.size() != blackActivity) {
			System.out.println("Activity Error.");
		}
		
		eval += activityEval(board, whiteSize, blackSize); // x3 weight of activity
		eval += advancementEval(board);
		eval += pieceSquareTables(board);
		/*if (board.getPiecesLeft() > 20) {
			if (board.square[3][0] != 5) {
				eval -= (board.getPiecesLeft() - 20) * 10;
			}
			if (board.square[3][7] != -5) {
				eval += (board.getPiecesLeft() - 20) * 10;
			}
		}*/
		if (!toMove) {
			eval = (short) -eval;
		}
		nodeCount++;
		return eval;
	}
	
	private static int activityEval(Board board, int[] whiteSize, int[] blackSize) {
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
		return activityEval;
	}

	/**
	 * 
	 * @param board game board
	 * @return Eval term how far pieces are advanced.
	 */
	private static int advancementEval(Board board) {
		int advancementEval = 0;
		int piecesLeft = board.getPiecesLeft();
		advancementEval += (int) (board.getPawnAdvancement() * ((32.0 - piecesLeft) / 16.0));
														// x2 on empty board, x1 on full board
		advancementEval += board.getKnightAdvancement() * 1; // always x1
		advancementEval += (int) (board.getRookAdvancement() * (32.0 - piecesLeft) / 32.0);
														// x1 on empty board, x0 on full board
		if (piecesLeft > 16) {
			advancementEval += board.getKingAdvancement() * 5 * (16 - piecesLeft); // full board x-80
		} else {
			advancementEval += (int) (board.getKingAdvancement() * ((16.0 - piecesLeft) / 8.0)); // empty board x2
		}
		return advancementEval;
	}
	
	public static int pieceSquareTables(Board board) {
		int pieceSquares = 0;
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
		
		if (board.getPiecesLeft() > 20) {
			if (board.square[3][0] == 6 || board.square[4][0] == 6 || board.square[5][0] == 6
					|| board.square[3][1] == 6 || board.square[4][1] == 6 || board.square[5][1] == 6) {
				pieceSquares -= (board.getPiecesLeft() - 20) * 2;
			}
			if (board.square[3][7] == -6 || board.square[4][7] == -6 || board.square[5][7] == -6
					|| board.square[3][6] == -6 || board.square[4][6] == -6 || board.square[5][6] == -6) {
				pieceSquares += (board.getPiecesLeft() - 20) * 2;
			}
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
	
	/**
	 * 
	 * @param newNodeCount 
	 */
	public static void setNodeCount(long newNodeCount) {
		nodeCount = newNodeCount;
	}
	
	/**
	 * 
	 * @return nodeCount
	 */
	public static long getNodeCount() {
		return nodeCount;
	}
	
	/**
	 * 
	 * @param newNodeCount 
	 */
	public static void setAbortedNodes(long newNodeCount) {
		abortedNodes = newNodeCount;
	}
	
	/**
	 * 
	 * @return nodeCount
	 */
	public static long getAbortedNodes() {
		return abortedNodes;
	}
	
	private Evaluation() {
	}
}
