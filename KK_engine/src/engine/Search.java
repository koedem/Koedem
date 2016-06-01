package engine;

import java.util.ArrayList;

/**
 * 
 * @author Anon
 *
 */
public final class Search {
	
	
	public static long qNodes = 0;
	
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
	 *
	 * This method calls itself recursively up to a (fixed) depth 2 (for now).
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @param depth : how many plies the recursion should go from root
	 * @param depthLeft : how many plies are left in the recursion
	 * @param alphaBound The value of the alpha bound for alpha-beta-algorithm.
	 * @param betaBound The value of the beta bound for alpha-beta-algorithm.
	 * 
	 * @return the principle variation we get for the position
	 */
	@SuppressWarnings("unused")
	public static int[] negaMax(Board board, boolean toMove, int depth, int depthLeft, int alphaBound, int betaBound) {
		int alpha = alphaBound;
		int beta = betaBound;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = alpha - 1;
		ArrayList<Integer> moves = MoveGenerator.collectMoves(board, toMove);
		for (Integer move : moves) {
			/*if (depthLeft == 1) {
				int a = 0;
			} else if (depthLeft == 2) {
				int a = 0;
			} else if (depthLeft == 3) {
				int a = 0;
			} else if (depthLeft == 4) {
				int a = 0;
			} else if (depthLeft == 5) {
				int a = 0;
			} else if (depthLeft == 6) {
				int a = 0;
			} else if (depthLeft == 7) {
				int a = 0;
			}*/
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			if (Math.abs(capturedPiece) == 6) {
				principleVariation[depth] = 10000;
				return principleVariation;
			}
			byte castlingRights = board.getCastlingRights();
			board.makeMove(move);
			int[] innerPV = new int[depth + 1];
			if (depthLeft > 1) {
				innerPV = negaMax(board, !toMove, depth, depthLeft - 1, -beta, -alpha);
				innerPV[depth] = -innerPV[depth];
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			} else if (depthLeft == 1) {
				if (UserInteraction.qSearch) {
					ArrayList<Integer> qsearch = qSearch(board, !toMove, -beta, -alpha);
					innerPV[depth] = -qsearch.get(0);
				} else {
					innerPV[depth] = Evaluation.evaluation(board, toMove, alpha);
				}
			}
			if (innerPV[depth] > principleVariation[depth]) {
				principleVariation = innerPV;
				principleVariation[depth - depthLeft] = move;
				alpha = principleVariation[depth];
				
			}
			board.unmakeMove(move, capturedPiece);
			board.addCastlingRights(castlingRights);
			if (principleVariation[depth] >= beta) {
				return principleVariation;
			}
		}
		return principleVariation;
	}
	
	/**
	 * Perform a q search (only consider captures) on the given board.
	 * Compare evaluation with and without capture and see which one is better i.e. whether the capture is good.
	 * 
	 * @param board Board on which a q search gets performed.
	 * @param toMove Who to move it is.
	 * @param alphaBound Alpha bound for alpha-beta search.
	 * @param betaBound Beta bound for the alpha-beta search.
	 * @return The best chain of captures and its evaluation. (can be empty if captures are bad)
	 */
	public static ArrayList<Integer> qSearch(Board board, boolean toMove, int alphaBound, int betaBound) {
		int alpha = alphaBound;
		int beta = betaBound;
		ArrayList<Integer> principleVariation = new ArrayList<Integer>(1);
		int eval = Evaluation.evaluation(board, toMove, alpha);
		principleVariation.add(alpha);
		if (eval > principleVariation.get(0)) {
			principleVariation.set(0, eval);
			alpha = eval;
		}
		if (Math.abs(eval) > 5000) {
			principleVariation.set(0, -10001);
			return principleVariation;
		}
		if (principleVariation.get(0) >= beta) {
			return principleVariation;
		}
		//if (nodeCount % 10000000 == 0) {
			//board.printBoard();
		//}
		ArrayList<Integer> captures = MoveGenerator.collectCaptures(board, toMove);
		if (captures.size() > 0 && captures.get(0) == -1) {
			principleVariation.set(0, 10000);
			return principleVariation;
		}
		for (Integer capture : captures) {
			byte capturedPiece = board.getSquare((capture / 8) % 8, capture % 8);
			board.makeMove(capture);
			ArrayList<Integer> innerPV = qSearch(board, !toMove, -beta, -alpha);
			qNodes++;
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
				alpha = principleVariation.get(0);
			}
			board.unmakeMove(capture, capturedPiece);
			if (principleVariation.get(0) >= beta) {
				return principleVariation;
			}
		}
		return principleVariation;
	}

	private Search() {
	}
}
