package Main.engine;

import java.util.concurrent.Callable;

import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

public class MultiThreadSearch implements Callable<int[]> {

	private Board board;
	private int depth;
	private boolean moveOrdering;
	private long timeLimit;
	
	public MultiThreadSearch(Board board, int depth, int threadNumber, boolean moveOrdering, long timeLimit) {
		this.board = board.cloneBoard();
		this.depth = depth;
		this.moveOrdering = moveOrdering;
		this.timeLimit = timeLimit;
	}
	
	@Override
	public int[] call() throws Exception {
		long time = System.currentTimeMillis();
		board.nodes = 0;
		board.abortedNodes = 0;
		board.setqNodes(0);
		int[] move = null;
		int[] movesSize = new int[6]; // unused
		board.setRootMoves(board.getMoveGenerator().collectMoves(board.getToMove(), new int[256], movesSize));
		Logging.printLine("info search started at milli: " + System.currentTimeMillis());
		
		for (int i = 1; i <= depth; i++) {
			if (moveOrdering) {
				move = board.getSearch().rootMax(board.getToMove(), i, time);
			} else {
				move = board.getSearch().negaMax(board.getToMove(), i, i, -30000, 30000);
			}
			
			if (Math.abs(move[move.length - 1]) > 9000) {
				break;
			}
			
			if (System.currentTimeMillis() - time > timeLimit) {
				break;
			}
			board.bestmove = Transformation.numberToMove(move[0]);
		}
		UCI.printEngineOutput("", move, board, board.getToMove(), time);

		Logging.printLine("bestmove " + Transformation.numberToMove(move[0]));
		return move;
	}
}
