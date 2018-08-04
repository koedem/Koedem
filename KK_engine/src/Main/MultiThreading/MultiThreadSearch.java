package Main.MultiThreading;

import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;

import Main.engine.Board;
import Main.engine.BoardInterface;
import Main.engine.MoveGenerator;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

public class MultiThreadSearch implements Callable<int[]> {

	private BoardInterface   board;
	private BoardInterface   oldBoard;
	private int     depth;
	private boolean moveOrdering;
	private long    timeLimit;
	
	public MultiThreadSearch(BoardInterface board, int depth, int threadNumber, boolean moveOrdering, long timeLimit) {
		this.oldBoard = board;
		this.board = board.cloneBoard();
		this.depth = depth;
		this.moveOrdering = moveOrdering;
		this.timeLimit = timeLimit;
	}
	
	@Override
	public int[] call() {
		long time = System.currentTimeMillis();
		board.getSearch().setNodes(0);
		board.getSearch().setAbortedNodes(0);
		board.getSearch().setQNodes(0);
		int[] move = null;
		int[] movesSize = new int[6]; // unused
		board.setRootMoves(board.getMoveGenerator().collectMoves(board.getToMove(), new int[MoveGenerator.MAX_MOVE_COUNT], movesSize));
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
			
			if (System.currentTimeMillis() - time > timeLimit                   // We break if the time is up
			    && board.getSearch().getNodes() > timeLimit * UCI.getLowerKN_Bound() // and we searched enough nodes.
			    || board.getSearch().getNodes() > timeLimit * UCI.getUpperKN_Bound() // Or when we searched more than enough nodes.
                    || board.getBestmove().equals("(none)")) { // or there are no legal moves
				break;
			}
			oldBoard.setBestmove(Transformation.numberToMove(move[0])); // tell the uci thread the current best move
		}
		assert move != null;
		if (board.getBestmove().equals("(none)")) {
		    oldBoard.setBestmove("(none)");
			Logging.printLine("bestmove (none)");
		} else {
			UCI.printEngineOutput("", move, board, board.getToMove(), time);

			Logging.printLine("bestmove " + Transformation.numberToMove(move[0]));
		}
		return move;
	}
}
