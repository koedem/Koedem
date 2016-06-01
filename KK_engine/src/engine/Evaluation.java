package engine;

import java.util.ArrayList;

/**
 * 
 * @author Anon
 *
 */
public final class Evaluation {
	
	/**
	 * Used to count the nodes we calculate in a Search.
	 */
	private static long nodeCount = 0;
	private static long abortedNodes = 0;

	/**
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @return evaluation based on material
	 */
	public static int evaluation(Board board, boolean toMove, int lowBound) {
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
		board.setPiecesLeft(pieceCounter);
		/*for (byte i = 0; i < 8; i++) {
			for (byte j = 0; j < 8; j++) {
				if (board.square[i][j] == 1) {
					eval += 100;
				} else if (board.square[i][j] == 2) {
					eval += 300;
				} else if (board.square[i][j] == 3) {
					eval += 300;
				} else if (board.square[i][j] == 4) {
					eval += 500;
				} else if (board.square[i][j] == 5) {
					eval += 900;
				} else if (board.square[i][j] == 6) {
					eval += 10000;
				} else if (board.square[i][j] == -1) {
					eval -= 100;
				} else if (board.square[i][j] == -2) {
					eval -= 300;
				} else if (board.square[i][j] == -3) {
					eval -= 300;
				} else if (board.square[i][j] == -4) {
					eval -= 500;
				} else if (board.square[i][j] == -5) {
					eval -= 900;
				} else if (board.square[i][j] == -6) {
					eval -= 10000;
				}
			}
		}
		if (!toMove) {
			eval = (short) -eval;
		}
		eval += (Math.random() * 200) - 100;
		
		return eval;*/
		ArrayList whiteMove = MoveGenerator.collectMoves(board, true);
		ArrayList blackMove = MoveGenerator.collectMoves(board, false);
		eval += whiteMove.size() - blackMove.size();
		eval += advancement;
		if (board.getPiecesLeft() > 25) {
			if (board.square[3][0] != 5) {
				eval -= (board.getPiecesLeft() - 25) * 5;
			}
			if (board.square[3][7] != -5) {
				eval += (board.getPiecesLeft() - 25) * 5;
			}
		}
		if (!toMove) {
			eval = (short) -eval;
		}
		//eval += (Math.random() * 100) - 50;
		nodeCount++;
		return eval;
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
