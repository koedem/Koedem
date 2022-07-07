package Main.engine;

import Main.engineIO.Logging;
import Main.engineIO.Transformation;

/**
 *
 */
public class Perft {

	private BoardInterface board;

	private int[][] movesStorage = new int[101][MoveGenerator.MAX_MOVE_COUNT];

	private static int[] unused = new int[6];

	public void basePerft(int depth, boolean detailed, boolean fast) {
		for (int i = 1; i <= depth; i++) {
			long oldTime = System.currentTimeMillis();
			long result = 0;
			if (detailed) {
				result = rootPerft(i);
			} else {
				if (fast) {
					result = fastPerft(i);
				} else {
					result = perft(i);
				}
			}
			long elapsedTime = System.currentTimeMillis() - oldTime;
			elapsedTime = elapsedTime != 0 ? elapsedTime : 1; // make sure it's not 0 so we don't divide by 0 for speed
			Logging.printLine(i + ": " + result + "\ttime: " + elapsedTime + " knps: " + (result / elapsedTime));
		}
	}

	private long fastPerft(int depth) {
		long nodes = 0;
		if (board.getAttackBoard().inCheck(!board.getToMove())) { // illegal position
			return 0;
		} else if (depth == 0) {
			return 1;
		}
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[depth], unused);

		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			board.makeMove(move);

			nodes += fastPerft(depth - 1);
			board.unmakeMove(move);
		}
		return nodes;
	}

	private long rootPerft(int depth)
	{
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[depth], unused);
		long nodes = 0;
		if (moves[0] == -1) { // illegal position
			return 0;
		} else if (depth == 0) {
			return 1;
		}

		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			board.makeMove(move);

			long innerPerft = perft(depth - 1);
			Logging.printLine(Transformation.numberToMove(move) + ": " + innerPerft);
			nodes += innerPerft;
			board.unmakeMove(move);
		}
		return nodes;
	}

	private long perft(int depth)
	{
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[depth], unused);
		long nodes = 0;
		if (moves[0] == -1) { // illegal position
			return 0;
		} else if (depth == 0) {
			return 1;
		}

		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			board.makeMove(move);
			nodes += perft(depth - 1);
			board.unmakeMove(move);
		}
		return nodes;
	}

	public Perft(BoardInterface board) {
		this.board = board;
	}
}
