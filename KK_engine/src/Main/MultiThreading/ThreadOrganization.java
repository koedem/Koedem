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

	static TranspositionTableInterface[] globalTT = { new AggressiveMatefinderTT(65536), new NonAggressiveMatefinderTT(65536) };

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

    public static void findMate() {
	    thread[1].setDepth(30);
	    thread[2].setDepth(20);
	    thread[3].setDepth(30);
	    thread[4].setDepth(20);
	    UCI.setThreadFinished(false);
	    for (int threadNumber = 1; threadNumber < 5; threadNumber++) {
	        synchronized (thread[threadNumber]) {
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