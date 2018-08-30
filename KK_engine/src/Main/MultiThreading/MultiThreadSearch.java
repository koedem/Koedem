package Main.MultiThreading;

import Main.engine.BoardInterface;
import Main.engine.MoveGenerator;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

import java.util.concurrent.Callable;

public class MultiThreadSearch implements Runnable {

	private BoardInterface   board;
    private int     depth;
    private long    timeLimit;
	
	public MultiThreadSearch(BoardInterface board, int threadNumber, boolean moveOrdering) {
        this.board = board;
	}
	
	@Override
	public void run() {
        int[] move = null;
        int[] movesSize = new int[6]; // unused
	    while (true) {
	        if (UCI.shuttingDown) {
	            break;
            }
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            long time = System.currentTimeMillis();
            board.getSearch().setNodes(0);
            board.getSearch().setAbortedNodes(0);
            board.getSearch().setQNodes(0);
            board.setRootMoves(board.getMoveGenerator().collectMoves(board.getToMove(), new int[MoveGenerator.MAX_MOVE_COUNT], movesSize));
            Logging.printLine("info search started at milli: " + System.currentTimeMillis());

            for (int i = 1; i <= depth; i++) {
                move = board.getSearch().rootMax(board.getToMove(), i, time);

                if (Math.abs(move[move.length - 1]) > 9000) {
                    break;
                }

                if (System.currentTimeMillis() - time > timeLimit                   // We break if the time is up
                        && board.getSearch().getNodes() > timeLimit * UCI.getLowerKN_Bound() // and we searched enough nodes.
                        || board.getSearch().getNodes() > timeLimit * UCI.getUpperKN_Bound() // Or when we searched more than enough nodes.
                        || board.getBestmove().equals("(none)")) { // or there are no legal moves
                    break;
                }
                if (UCI.isThreadFinished()) {
                    break;
                }
            }
            assert move != null;
            if (board.getBestmove().equals("(none)")) {
                Logging.printLine("bestmove (none)");
            } else {
                UCI.printEngineOutput("", move, board, board.getToMove(), time);

                Logging.printLine("bestmove " + Transformation.numberToMove(move[0]));
            }
        }
	}

	public void setDepth(int depth) {
	    this.depth = depth;
	}

	public void setTimeLimit(int timeLimit) {
	    this.timeLimit = timeLimit;
    }

    public void setBoard(BoardInterface board) {
        this.board = board;
	}
}
