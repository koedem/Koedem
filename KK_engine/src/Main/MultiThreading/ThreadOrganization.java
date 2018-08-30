package Main.MultiThreading;

import Main.engine.Board;
import Main.engine.BoardInterface;
import Main.engineIO.UCI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class ThreadOrganization {

	private static int threadCount = 1;
	public static BoardInterface[] boards = new Board[5];
	private static MultiThreadSearch[] thread = new MultiThreadSearch[5];

    private static ExecutorService executor            = Executors.newFixedThreadPool(5);

	public static void updateThreadCount(int newCount, BoardInterface board) {
		threadCount = newCount;
		updateBoard(board);
	}

	public static void updateBoard(BoardInterface board) { // TODO
		for (int i = 0; i < threadCount; i++) {
			boards[i] = board.cloneBoard();
		}
	}

	public static void go(int depth, int timeLimit) {
        thread[0].setDepth(depth);
        thread[0].setTimeLimit(timeLimit);
        UCI.setThreadFinished(false);
        synchronized (thread[0]) {
            thread[0].notify();
        }
        Thread.yield(); // give the cpu to the calculating thread
    }

	public static void setUp(BoardInterface board) {
	    for (int threadNumber = 0; threadNumber < boards.length; threadNumber++) {
            boards[threadNumber] = board.cloneBoard();
            thread[threadNumber] = new MultiThreadSearch(boards[threadNumber], threadNumber, true);
            executor.submit(thread[threadNumber]);
        }
	}
}
