package engine;

import java.util.ArrayList;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public final class Search {
	
	public static long nodeCount = 0;
	
	/**
	 * this function generates all legal moves and then plays a random one
	 * on the board and returns that move as int.
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @return the move we just played
	 */
	public static int makeRandomMove(Board board, boolean toMove) {
		ArrayList<Short> moves = MoveGenerator.collectMoves(board, toMove);
		short randomMove = moves.get((int) (moves.size() * Math.random()));
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
	public static short[] negaMax(Board board, boolean toMove, int depth, int depthLeft) {
		short[] principleVariation = new short[depth + 1];
		principleVariation[depth] = -30000;
		ArrayList<Short> moves = MoveGenerator.collectMoves(board, toMove);
		for (Short move : moves) {
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			board.makeMove(move);
			short[] innerPV = new short[depth + 1];
			if (depthLeft > 1) {
				innerPV = negaMax(board, !toMove, depth, depthLeft - 1);
				innerPV[depth] = (short) -innerPV[depth];
			} else if (depthLeft == 1) {
				innerPV[depth] = Evaluation.evaluation(board, toMove);
				nodeCount++;
			}
			if (innerPV[depth] > principleVariation[depth]) {
				principleVariation = innerPV;
				principleVariation[depth - depthLeft] = move;
				
			}
			board.unmakeMove(move, capturedPiece);
		}
		return principleVariation;
	}
	
	private Search() {
	}
}
