package engine;

import java.util.concurrent.Callable;

import engineIO.Logging;
import engineIO.Transformation;
import engineIO.UCI;

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
		board.qNodes = 0;
		int[] move = null;
		int[] movesSize = new int[6]; // unused
		board.setRootMoves(MoveGenerator.collectMoves(board, board.getToMove(), movesSize));
		
		for (int i = 1; i <= depth; i++) {
			if (moveOrdering) {
				move = Search.rootMax(board, board.getToMove(), i, time);
			} else {
				move = Search.negaMax(board, board.getToMove(), i, i, -30000, 30000);
			}
			
			if (Math.abs(move[move.length - 1]) > 9000) {
				break;
			}
			
			if (System.currentTimeMillis() - time > timeLimit) {
				break;
			}
		}
		UCI.printEngineOutput("", move, board, board.getToMove(), time);

		Logging.printLine("bestmove " + Transformation.numberToSquare((move[0] / 64) % 64) 
				+ Transformation.numberToSquare(move[0] % 64));
		return move;
	}
}
