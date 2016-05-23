package engine;

import java.util.ArrayList;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public final class Search {
	
	/**
	 * Used to count the nodes we calculate in a Search.
	 */
	private static long nodeCount = 0;
	
	/**
	 * this function generates all legal moves and then plays a random one
	 * on the board and returns that move as int.
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @return the move we just played
	 */
	public static int makeRandomMove(Board board, boolean toMove) {
		ArrayList<Integer> moves = MoveGenerator.collectMoves(board, toMove);
		int randomMove = moves.get((int) (moves.size() * Math.random()));
		board.makeMove(randomMove);
		return randomMove;
	}
	
	/**
	 * This method calls itself recursively up to a (fixed) depth 2 (for now).
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @param depth : how many plies the recursion should go from root
	 * @param depthLeft : how many plies are left in the recursion
	 * @return the principle variation we get for the position
	 */
	public static int[] negaMax(Board board, boolean toMove, int depth, int depthLeft) {
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		ArrayList<Integer> moves = MoveGenerator.collectMoves(board, toMove);
		for (Integer move : moves) {
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			if (Math.abs(capturedPiece) == 6) {
				principleVariation[depth] = 10000;
				return principleVariation;
			}
			byte castlingRights = board.getCastlingRights();
			board.makeMove(move);
			int[] innerPV = new int[depth + 1];
			if (depthLeft > 1) {
				innerPV = negaMax(board, !toMove, depth, depthLeft - 1);
				innerPV[depth] = -innerPV[depth];
				if (innerPV[depth] > 0) {
					innerPV[depth]--;
				} else {
					innerPV[depth]++;
				}
			} else if (depthLeft == 1) {
				//ArrayList<Integer> qsearch = qSearch(board, !toMove);
				//innerPV[depth] = -qsearch.get(0);
				innerPV[depth] = Evaluation.evaluation(board, toMove);
			}
			if (innerPV[depth] > principleVariation[depth]) {
				principleVariation = innerPV;
				principleVariation[depth - depthLeft] = move;
				
			}
			board.unmakeMove(move, capturedPiece);
			board.addCastlingRights(castlingRights);
		}
		return principleVariation;
	}
	
	/**
	 * Perform a q search (only consider captures) on the given board.
	 * Compare evaluation with and without capture and see which one is better i.e. whether the capture is good.
	 * 
	 * @param board : Board on which a q search gets performed.
	 * @param toMove : Who to move it is.
	 * @return The best chain of captures and its evaluation. (can be empty if captures are bad)
	 */
	public static ArrayList<Integer> qSearch(Board board, boolean toMove) {
		ArrayList<Integer> principleVariation = new ArrayList<Integer>(1);
		int eval = Evaluation.evaluation(board, toMove);
		principleVariation.add(eval);
		if (Math.abs(eval) > 5000) {
			principleVariation.set(0, -10001);
			return principleVariation;
		}
		if (nodeCount % 10000000 == 0) {
			board.printBoard();
		}
		ArrayList<Integer> captures = MoveGenerator.collectCaptures(board, toMove);
		for (Integer capture : captures) {
			byte capturedPiece = board.getSquare((capture / 8) % 8, capture % 8);
			board.makeMove(capture);
			ArrayList<Integer> innerPV = qSearch(board, !toMove);
			if (innerPV.get(0) == -10001) {
				principleVariation = new ArrayList<Integer>(1);
				principleVariation.add(0, 10000);
				board.unmakeMove(capture, capturedPiece);
				return principleVariation;
			}
			innerPV.set(0, -innerPV.get(0));
			if (innerPV.get(0) > principleVariation.get(0)) {
				principleVariation = innerPV;
				principleVariation.add(capture);
			}
			board.unmakeMove(capture, capturedPiece);
		}
		return principleVariation;
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
	 * Add one to nodeCount.
	 */
	public static void nodeCountPlusOne() {
		nodeCount++;
	}
	private Search() {
	}
}
