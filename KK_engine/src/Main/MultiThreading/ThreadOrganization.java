package Main.MultiThreading;

import Main.engine.Board;

/**
 *
 */
public class ThreadOrganization {

	private static int threadCount = 1;
	private static Board[] boards = new Board[5];
	private static Thread[] threads = new Thread[5];

	public static void updateThreadCount(int newCount, Board board) {
		threadCount = newCount;
		updateBoard(board);
	}

	public static void updateBoard(Board board) {
		for (int i = 0; i < threadCount; i++) {
			boards[i] = board.cloneBoard();
		}
	}

	public static void setUp() {
	}
}
