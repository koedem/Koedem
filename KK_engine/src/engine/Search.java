package engine;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public final class Search {
	
	public static int nodeCount = 0;
	
	/**
	 * this function generates all legal moves and then plays a random one
	 * on the board and returns that move as int.
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @return the move we just played
	 */
	public static int makeRandomMove(Board board, boolean toMove) {
		int[] moves = MoveGenerator.collectMoves(board, toMove);
		int randomMove = moves[(int) (moves[99] * Math.random())];
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
		principleVariation[depth] = -1000000;
		int[] moves = MoveGenerator.collectMoves(board, toMove);
		for (int i = 0; i < moves[99]; i++) {
			byte capturedPiece = board.getSquare((moves[i] / 10) % 10, moves[i] % 10);
			board.makeMove(moves[i]);
			int[] innerPV = new int[depth + 1];
			if (depthLeft > 1) {
				innerPV = negaMax(board, !toMove, depth, depthLeft - 1);
				innerPV[depth] = -innerPV[depth];
			} else if (depthLeft == 1) {
				innerPV[depth] = Evaluation.evaluation(board, toMove);
				nodeCount++;
			}
			if (innerPV[depth] > principleVariation[depth]) {
				principleVariation = innerPV;
				principleVariation[depth - depthLeft] = moves[i];
				
			}
			board.unmakeMove(moves[i], capturedPiece);
		}
		return principleVariation;
	}
	
	private Search() {
	}
}
