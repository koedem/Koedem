package Main.MultiThreading;

import Main.engine.Board;
import Main.engine.BoardInterface;

/**
 *
 */
public class ThreadOrganization {

	private static int threadCount = 1;
	private static BoardInterface[] boards = new Board[5];
	private static Thread[] threads = new Thread[5];

	public static void updateThreadCount(int newCount, BoardInterface board) {
		threadCount = newCount;
		updateBoard(board);
	}

	public static void updateBoard(BoardInterface board) {
		for (int i = 0; i < threadCount; i++) {
			boards[i] = board.cloneBoard();
		}
	}

	public static void setUp() {
	}
}
