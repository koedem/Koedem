package Main.MultiThreading;

import Main.engine.BoardInterface;
import Main.engine.MoveGenerator;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

public class NonLosingThread implements SearchThreadInterface {
	private BoardInterface   board;
	private int     depth;
	private boolean aggressive;
	private String  threadName;
	private boolean logging;
	
	public NonLosingThread(BoardInterface board, boolean aggressive, boolean logging) {
		this.board = board;
		this.aggressive = aggressive;
		if (aggressive) {
			threadName = "Aggressive LossFinder ";
		} else {
			threadName = "LossFinder ";
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
            this.board.setRootMoves(this.board.getCheckMoveGenerator().collectAllPNMoves(new int[MoveGenerator.MAX_MOVE_COUNT], this.board.getToMove()));
            board.getSearch().setNodes(0);
            board.getSearch().setAbortedNodes(0);
            board.getSearch().setQNodes(0);
            int mateScore = 0;
            for (int i = 1; i < depth; i ++) {
            	if (!aggressive) {
            		int debug = 0;
	            }
                if (logging) {
                    Logging.printLine("");
                    Logging.printLine(threadName + "Starting depth " + i + ". Time: "
                            + Transformation.timeUsedOutput(System.currentTimeMillis() - time));
                }

                mateScore = board.getMateFinder().rootMateFinder(i, aggressive);

                if (logging) {
                    Logging.printLine(threadName + "Non losing moves: " + board.getRootMoves()[0] + ". Nodes: "
                            + Transformation.nodeCountOutput(board.getSearch().getNodes()));
                    StringBuilder str = new StringBuilder();
                    for (int index = 1; index <= board.getRootMoves()[0]; index++) {
                        str.append(Transformation.numberToMove(board.getRootMoves()[index])).append(", ");
                    }
	                Logging.printLine(str.toString());
                }

                if (mateScore < 0) {
                    if (logging) {
                        UCI.printEngineOutput(threadName, new int[]{mateScore}, board, board.getToMove(), time);
                        ThreadOrganization.globalMateTT.printPV(board);
                    }
                    break;
                }
            }
            if (mateScore < 0) {
                UCI.setThreadFinished(true);
            }
        }
	}
	
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
