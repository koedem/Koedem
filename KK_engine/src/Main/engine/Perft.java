package Main.engine;

import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

/**
 *
 */
public class Perft {

	private BoardInterface board;
	private PerftTT_8 perftTT = new PerftTT_8(UCI.TT_SIZE_MB);
	private PerftTT_8 counterHelp = new PerftTT_8(8192);
	private ForbiddenTT oldUnique = new ForbiddenTT(256);

	private int[][] movesStorage = new int[101][MoveGenerator.MAX_MOVE_COUNT];

	private static int[] unused = new int[6];

	public void basePerft(int depth) {
		perftTT.reset();
		counterHelp.reset();
		long oldTime = System.currentTimeMillis();
		long result = 0;

		result = generateEmptyPositions(depth); // prepare shallow positions
		long elapsedTime = System.currentTimeMillis() - oldTime;
		elapsedTime = elapsedTime != 0 ? elapsedTime : 1; // make sure it's not 0 so we don't divide by 0 for speed
		Logging.printLine(depth + ": " + result + "\ttime: " + elapsedTime + " knps: " + (result / elapsedTime));
		perftTT.printCounts();

		oldTime = System.currentTimeMillis(); // count deep positions
		result = countDeepPositions(depth + 2); // TODO if changed, change the inCheck check
		elapsedTime = System.currentTimeMillis() - oldTime;
		elapsedTime = elapsedTime != 0 ? elapsedTime : 1; // make sure it's not 0 so we don't divide by 0 for speed
		Logging.printLine(depth + ": " + result + "\ttime: " + elapsedTime + " knps: " + (result / elapsedTime));
		perftTT.printFrequencies();
		counterHelp.printCounts();

		collectUniquePositions(depth);
		oldUnique.printCounts();
	}

	private long generateEmptyPositions(int depth) {
		long nodes = 0;
		if (board.getAttackBoard().inCheck(!board.getToMove())) { // illegal position
			return 0;
		} else if (depth == 0) {
			perftTT.putEmpty(board.getZobristHash());
			return 1;
		}
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[depth], unused);

		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			board.makeMove(move);

			nodes += generateEmptyPositions(depth - 1);
			board.unmakeMove(move);
		}
		return nodes;
	}

	private long collectUniquePositions(int depth) {
		long nodes = 0;
		if (board.getAttackBoard().inCheck(!board.getToMove())) { // illegal position
			return 0;
		} else if (depth == 0) {
			if (perftTT.isUnique(board.getZobristHash())) { // in odd depth settings && !board.getAttackBoard().inCheck(board.getToMove())
				//board.printBoard();
				oldUnique.put(board.getZobristHash());
			}
			return 1;
		}
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[depth], unused);

		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			board.makeMove(move);

			nodes += collectUniquePositions(depth - 1);
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

			long innerPerft = countDeepPositions(depth - 1);
			Logging.printLine(Transformation.numberToMove(move) + ": " + innerPerft);
			nodes += innerPerft;
			board.unmakeMove(move);
		}
		return nodes;
	}

	private long countDeepPositions(int depth) {
		if (depth == 1) {
			return incrementFinalDepth();
		}
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[depth], unused);
		long nodes = 0;
		if (moves[0] == -1) { // illegal position
			return 0;
		}

		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			board.makeMove(move);
			if (counterHelp.incrementToLimit(board.getZobristHash() + depth, 2) >= 2) {
				board.unmakeMove(move);
				nodes++;
				continue;
			}
			nodes += countDeepPositions(depth - 1);
			board.unmakeMove(move);
		}
		return nodes;
	}

	private long incrementFinalDepth() {
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[0], unused);
		if (moves[0] == -1) { // illegal position
			return 0;
		}
		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			perftTT.incrementPosition(board.zobristAfterMove(move));
		}
		return 1; // makes the perft become perft - 1 because we don't check for legality in this depth
	}

	public Perft(BoardInterface board) {
		this.board = board;
	}
}
