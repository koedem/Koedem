package Main.MultiThreading;

import Main.engine.BoardInterface;
import Main.engine.MateFinder;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

import java.util.concurrent.Callable;

public class MateFinderThread implements SearchThreadInterface {

	private BoardInterface   board;
	private int     depth;
	private boolean aggressive;
	private String  threadName;
	private boolean logging;
	
	public MateFinderThread(BoardInterface board, boolean aggressive, boolean logging) {
		this.board = board;
		this.aggressive = aggressive;
		if (aggressive) {
			threadName = "Aggressive MateFinder ";
		} else {
			threadName = "MateFinder ";
		}
		this.logging = logging;
	}
	
	@Override
	public void run() {
        while (!UCI.shuttingDown) {
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
            int mateScore = 0;
            for (int i = 1; i < depth; i++) { // depth is in full moves so only increment one at a time
                if (logging) {
                    Logging.printLine("");
                    Logging.printLine(threadName + "Starting depth " + i + ". Time: "
                            + Transformation.timeUsedOutput(System.currentTimeMillis() - time));
                }

                mateScore = board.getMateFinder().mateFinder(true, i, i, aggressive ? 4 : 1, aggressive);

                if (logging) {
                    Logging.printLine(threadName + "Finished depth " + i + ". Nodes: "
                            + Transformation.nodeCountOutput(board.getSearch().getNodes()));
                }

                if (mateScore > 0) {
                    if (logging) {
                        UCI.printEngineOutput(threadName, new int[]{mateScore}, board, board.getToMove(), time);
                        ThreadOrganization.globalMateTT.printPV(board);
                    }
                    break;
                }
            }
            if (mateScore > 0) {
                UCI.setThreadFinished(true);
            }
        }
	}

    @Override
    public BoardInterface getBoard() {
        return board;
    }

    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public void setTimeLimit(int timeLimit) {
	    // TODO ?
    }

    @Override
    public void setBoard(BoardInterface board) {
        this.board = board;
    }

	@Override
	public void setHardTimeLimit(long hardTimeLimit) {
		// TODO implement
	}
}
