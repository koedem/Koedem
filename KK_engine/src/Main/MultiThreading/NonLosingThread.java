package Main.MultiThreading;

import java.util.concurrent.Callable;

import Main.engine.Board;
import Main.engine.MateFinder;
import Main.engine.MoveGenerator;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

public class NonLosingThread implements Callable<int[]> {
	private Board   board;
	private int     depth;
	private boolean aggressive;
	private String  threadName;
	private boolean logging;
	
	public NonLosingThread(Board board, int depth, boolean aggressive, boolean logging) {
		this.board = board.cloneBoard();
		this.board.setRootMoves(this.board.getMoveGenerator().collectAllPNMoves(new int[MoveGenerator.MAX_MOVE_COUNT], this.board, this.board.getToMove()));
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
		board.getSearch().nodes = 0;
		board.getSearch().abortedNodes = 0;
		board.getSearch().qNodes = 0;
		int[] move = null;
		for (int i = 3; i < depth; i += 2) {
			if (logging) {
				Logging.printLine("");
				Logging.printLine(threadName + "Starting depth " + i + ". Time: " 
					+ Transformation.timeUsedOutput(System.currentTimeMillis() - time));
			}
			
			move = MateFinder.rootMateFinder(board, board.getToMove(), i, time, aggressive);
			
			if (logging) {
				Logging.printLine(threadName + "Non losing moves: " + board.getRootMoves()[0] + ". Nodes: "
					+ Transformation.nodeCountOutput(board.getSearch().nodes));
				for (int index = 1; index <= board.getRootMoves()[0]; index++) {
					Logging.printLine(Transformation.numberToMove(board.getRootMoves()[index]) + " ");
				}
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
