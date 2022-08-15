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
	private static SearchThreadInterface[] thread = new SearchThreadInterface[5];

	public static TranspositionTableInterface globalMateTT = new AggressiveMatefinderTT(0x10000000L);

    private static ExecutorService executor            = Executors.newFixedThreadPool(5);

	public static void updateThreadCount(int newCount, BoardInterface board) {
		threadCount = newCount;
		updateBoard(board);
	}

	public static void updateBoard(BoardInterface board) { // TODO
		for (int i = 0; i < threadCount; i++) {
			boards[i] = board.cloneBoard();
			thread[i].setBoard(boards[i]);
		}
	}

	public static void go(int depth, int timeLimit, long hardTimeLimit) {
		for (int i = 0; i < threadCount; i++) {
			thread[i].setDepth(depth);
			thread[i].setTimeLimit(timeLimit);
			thread[i].setHardTimeLimit(hardTimeLimit);
			((MultiThreadSearch) thread[i]).setStandard(true);
			synchronized (thread[i]) {
				UCI.setThreadFinished(false);
				thread[i].notify();
			}
		}
        Thread.yield(); // give the cpu to the calculating thread
    }

	public static void refute() {
		thread[0].setDepth(100);
		thread[0].setTimeLimit(Integer.MAX_VALUE / 2);
		thread[0].setHardTimeLimit(Integer.MAX_VALUE / 2);
		((MultiThreadSearch) thread[0]).setStandard(false);
		synchronized (thread[0]) {
			UCI.setThreadFinished(false);
			thread[0].notify();
		}
		Thread.yield(); // give the cpu to the calculating thread
	}

    public static void findMate() {
	    thread[1].setDepth(60);
	    thread[2].setDepth(20);
	    thread[3].setDepth(60);
	    thread[4].setDepth(20);
	    for (int threadNumber = 1; threadNumber < 5; threadNumber++) {
	        synchronized (thread[threadNumber]) {
		        UCI.setThreadFinished(false);
                thread[threadNumber].notify();
            }
        }
        Thread.yield();
    }

	public static void setUp(BoardInterface board) {
	    executor.submit(thread[0] = new MultiThreadSearch(boards[0] = board.cloneBoard(), 0, true));
	    executor.submit(thread[1] = new MultiThreadSearch(boards[1] = board.cloneBoard(), 1, true));
        executor.submit(thread[2] = new MultiThreadSearch(boards[2] = board.cloneBoard(), 2, true));
        executor.submit(thread[3] = new MultiThreadSearch(boards[3] = board.cloneBoard(), 3, true));
        executor.submit(thread[4] = new MultiThreadSearch(boards[4] = board.cloneBoard(), 4, true));
	}
}