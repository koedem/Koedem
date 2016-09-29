package engine;

import java.util.concurrent.Callable;

import engineIO.Logging;
import engineIO.Transformation;
import engineIO.UCI;

public class MateFinderThread implements Callable<int[]> {

	private Board board;
	private int depth;
	private boolean aggressive;
	private String threadName;
	private boolean logging;
	
	public MateFinderThread(Board board, int depth, boolean aggressive, boolean logging) {
		this.board = board.cloneBoard();
		this.depth = depth;
		this.aggressive = aggressive;
		if (aggressive) {
			threadName = "Aggressive MateFinder ";
		} else {
			threadName = "MateFinder ";
		}
		this.logging = logging;
	}
	
	@Override
	public int[] call() throws Exception {
		long time = System.currentTimeMillis();
		board.nodes = 0;
		board.abortedNodes = 0;
		board.qNodes = 0;
		int[] move = null;
		for (int i = 2; i < depth; i += 2) {
			if (logging) {
				Logging.printLine("");
				Logging.printLine(threadName + "Starting depth " + i + ". Time: " + Transformation.timeUsedOutput(System.currentTimeMillis() - time));
			}
			
			move = MateFinder.mateFinder(board, board.getToMove(), i, i, aggressive);
			
			if (logging) {
				Logging.printLine(threadName + "Finished depth " + i  + ". Nodes: " + Transformation.nodeCountOutput(board.nodes));
			}
			
			if (move[move.length - 1] > 0) {
				if (logging) {
					UCI.printEngineOutput(threadName, move, board, board.getToMove(), time);
				}
				break;
			}
		}
		if (move != null && move[move.length - 1] > 9000) {
			UCI.setThreadFinished(true);
		}
		return move;
	}
}
