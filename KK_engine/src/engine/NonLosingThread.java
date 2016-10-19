package engine;

import java.util.concurrent.Callable;

import engineIO.Logging;
import engineIO.Transformation;
import engineIO.UCI;

public class NonLosingThread implements Callable<int[]> {
	private Board board;
	private int depth;
	private boolean aggressive;
	private String threadName;
	private boolean logging;
	
	public NonLosingThread(Board board, int depth, boolean aggressive, boolean logging) {
		this.board = board.cloneBoard();
		this.board.setRootMoves(MoveGenerator.collectAllPNMoves(this.board, this.board.getToMove()));
		this.depth = depth;
		this.aggressive = aggressive;
		if (aggressive) {
			threadName = "Aggressive LossFinder ";
		} else {
			threadName = "LossFinder ";
		}
		this.logging = logging;
	}
	
	@Override
	public int[] call() throws Exception {
		long time = System.currentTimeMillis();
		board.nodes = 0;
		board.abortedNodes = 0;
		board.setqNodes(0);
		int[] move = null;
		for (int i = 3; i < depth; i += 2) {
			if (logging) {
				Logging.printLine("");
				Logging.printLine(threadName + "Starting depth " + i + ". Time: " 
					+ Transformation.timeUsedOutput(System.currentTimeMillis() - time));
			}
			
			move = MateFinder.rootMateFinder(board, board.getToMove(), i, time, aggressive);
			
			if (logging) {
				Logging.printLine(threadName + "Non losing moves: " + board.getRootMoves().size() + ". Nodes: " 
					+ Transformation.nodeCountOutput(board.nodes));
			}
			
			if (move[move.length - 1] < 0) {
				if (logging) {
					UCI.printEngineOutput(threadName, move, board, board.getToMove(), time);
				}
				break;
			}
		}
		if (move != null && move[move.length - 1] < -9000) {
			UCI.setThreadFinished(true);
		}
		return move;
	}
	
	public Board getBoard() {
		return board;
	}
}
