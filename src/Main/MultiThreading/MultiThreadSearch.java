package Main.MultiThreading;

import Main.engine.BoardInterface;
import Main.engine.MoveGenerator;
import Main.engine.MoveOrdering;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

public class MultiThreadSearch implements SearchThreadInterface {

	private BoardInterface   board;
    private int     depth;
    private long    timeLimit;
    private long hardTimeLimit;
	private boolean standard = true;
	
	public MultiThreadSearch(BoardInterface board, int threadNumber, boolean moveOrdering) {
        this.board = board;
	}
	
	@Override
	public void run() {
        while (!UCI.shuttingDown) {
        	synchronized (this) {
        		while (UCI.isThreadFinished()) {
			        try {
				        wait();
			        } catch (InterruptedException e) {
				        e.printStackTrace();
			        }
		        }
	        }
	        if (standard) {
				standardSearch();
	        } else {
				refutationSearch();
	        }
        }
	}

	private void standardSearch() {
		int[] move = null;
		int[] movesSize = new int[6]; // unused

		if (hardTimeLimit == Integer.MAX_VALUE / 2) {
			CorrespondenceOrganisation.getInstance().getLock();
			Logging.printLine("Acquiring lock.");
		}

		long time = System.currentTimeMillis();
		board.getSearch().setNodes(0);
		board.getSearch().setAbortedNodes(0);
		board.getSearch().setQNodes(0);
		board.setRootMoves(board.getMoveGenerator().collectMoves(board.getToMove(), new int[MoveGenerator.MAX_MOVE_COUNT], movesSize));
		if (UCI.logging) {
			Logging.printLine("info search started at milli: " + System.currentTimeMillis());
		}
		MoveOrdering.getInstance().orderRootMoves(board);

		for (int i = 2; i <= depth; i++) { // our root ordering already does a depth 1 search essentially
			move = board.getSearch().rootMax(board.getToMove(), i, time, hardTimeLimit);

			if (Math.abs(move[move.length - 1]) > 9000) {
				break;
			}

			if (System.currentTimeMillis() - time > timeLimit                   // We break if the time is up
			    && (board.getSearch().getNodes() + board.getSearch().getAbortedNodes()) > timeLimit * UCI.getLowerKN_Bound() // and we searched enough nodes.
			    || (board.getSearch().getNodes() + board.getSearch().getAbortedNodes()) > timeLimit * UCI.getUpperKN_Bound() // Or when we searched more than enough nodes.
			    || board.getBestmove().equals("(none)")) { // or there are no legal moves
				break;
			}
			if (UCI.isThreadFinished()) {
				break;
			}
		}
		assert move != null;
		UCI.setThreadFinished(true);
		if (board.getBestmove().equals("(none)")) {
			Logging.printLine("bestmove (none)");
		} else {
			board.makeMove(move[0]); // we don't have a TT entry for the root position
			UCI.printEngineOutput("", move, board, board.getToMove(), time);
			board.unmakeMove(move[0]);

			Logging.printLine("bestmove " + Transformation.numberToMove(move[0]));
		}
		if (hardTimeLimit == Integer.MAX_VALUE / 2) {
			CorrespondenceOrganisation.getInstance().returnLock();
			Logging.printLine("Returning lock.");
		}
	}

	private void refutationSearch() {
		int[] move = null;
		int[] movesSize = new int[6]; // unused

		long time = System.currentTimeMillis();
		board.getSearch().setNodes(0);
		board.getSearch().setAbortedNodes(0);
		board.getSearch().setQNodes(0);
		int[] rootMoves = board.getMoveGenerator().collectMoves(board.getToMove(), new int[MoveGenerator.MAX_MOVE_COUNT], movesSize);
		if (UCI.logging) {
			Logging.printLine("info search started at milli: " + System.currentTimeMillis());
		}

		for (int i = 3; i <= 100; i++) { // our root ordering already does a depth 1 search essentially
			for (int j = 1; j <= rootMoves[0]; j++) {
				board.makeMove(rootMoves[j]);
				move = board.getSearch().negaMax(board.getToMove(), i, i - 1, -30000, 30000, System.currentTimeMillis() + hardTimeLimit);
				board.unmakeMove(rootMoves[j]);

				if (Math.abs(move[move.length - 1]) < -9000) {
					break;
				}
				if (Math.abs(move[move.length - 1]) > 9000) {
					move[0] = rootMoves[j];
					UCI.printEngineOutput("\n", move, board, board.getToMove(), time);
					rootMoves[j] = rootMoves[rootMoves[0]--]; // replace index with the last array element and then decrease array size by one to de facto remove index
					j--;
				}
			}
			Logging.printLine("\ninfo depth " + i + " nodes " + (board.getSearch().getNodes() + board.getSearch().getAbortedNodes()) + " nps "
			                  + 1000 * (board.getSearch().getNodes() + board.getSearch().getAbortedNodes()) / ((System.currentTimeMillis() - time) > 0 ?
			                                                                                                   (System.currentTimeMillis() - time) : 1)
			                  + " hashfull " + UCI.lowerBoundsTable.getFill() + " time " + (System.currentTimeMillis() - time));
			UCI.lowerBoundsTable.printStats();
			Logging.printLine("Non losing moves: " + rootMoves[0] + "   ");
			StringBuilder str = new StringBuilder();
			for (int index = 1; index <= rootMoves[0]; index++) {
				str.append(Transformation.numberToMove(rootMoves[index])).append(", ");
			}
			Logging.printLine(str.toString() + "\n");
			if (UCI.isThreadFinished() || rootMoves[0] == 0) {
				break;
			}
		}
		UCI.setThreadFinished(true);
	}

	public BoardInterface getBoard() {
	    return board;
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

	public void setHardTimeLimit(long hardTimeLimit) {
		this.hardTimeLimit = hardTimeLimit;
	}

	public void setStandard(boolean standard) {
		this.standard = standard;
	}
}
