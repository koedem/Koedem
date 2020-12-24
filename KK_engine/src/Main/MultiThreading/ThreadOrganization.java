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
        thread[0].setDepth(depth);
        thread[0].setTimeLimit(timeLimit);
        thread[0].setHardTimeLimit(hardTimeLimit);
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
	    executor.submit(thread[1] = new MateFinderThread(boards[1] = board.cloneBoard(), true, true));
        executor.submit(thread[2] = new MateFinderThread(boards[2] = board.cloneBoard(), false, true));
        executor.submit(thread[3] = new NonLosingThread(boards[3] = board.cloneBoard(), true, true));
        executor.submit(thread[4] = new NonLosingThread(boards[4] = board.cloneBoard(), false, true));
	}
}