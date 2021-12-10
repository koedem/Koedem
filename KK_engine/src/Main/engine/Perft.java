package Main.engine;

import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

import java.util.LinkedList;

/**
 *
 */
public class Perft {

	private BoardInterface board;
	private static PerftTT_8 counterHelp[] = { new PerftTT_8(4096), new PerftTT_8(4096), new PerftTT_8(4096) };
	private static PerftTT_8 perftTT = new PerftTT_8(UCI.TT_SIZE_MB);
	private static ForbiddenTT oldUnique = new ForbiddenTT(64);
	private static final boolean memoryIsSpare = false;

	LinkedList<String> dissimilarPositions = new LinkedList<String>();

	private int[][] movesStorage = new int[101][MoveGenerator.MAX_MOVE_COUNT];

	private static int[] unused = new int[6];

	public void basePerft(int depth) {
		perftTT.reset();
		counterHelp[0].reset();
		counterHelp[1].reset();
		counterHelp[2].reset();
		long oldTime = System.currentTimeMillis();
		long result = 0;

		result = generateEmptyPositions(depth); // prepare shallow positions
		long elapsedTime = System.currentTimeMillis() - oldTime;
		elapsedTime = elapsedTime != 0 ? elapsedTime : 1; // make sure it's not 0 so we don't divide by 0 for speed
		Logging.printLine(depth + ": " + result + "\ttime: " + elapsedTime + " knps: " + (result / elapsedTime));
		perftTT.printCounts();

		oldTime = System.currentTimeMillis(); // count deep positions
		result = countDeepPositions(depth + 5, 0, false); // TODO if changed, change the inCheck check
		elapsedTime = System.currentTimeMillis() - oldTime;
		elapsedTime = elapsedTime != 0 ? elapsedTime : 1; // make sure it's not 0 so we don't divide by 0 for speed
		Logging.printLine(depth + ": " + result + "\ttime: " + elapsedTime + " knps: " + (result / elapsedTime));
		perftTT.printFrequencies();
		counterHelp[0].printCounts();
		counterHelp[1].printCounts();
		counterHelp[2].printCounts();

		collectUniquePositions(depth, 0);
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

	private long collectUniquePositions(int depth, int depthSoFar) {
		long nodes = 0;
		if (board.getAttackBoard().inCheck(!board.getToMove())) { // illegal position
			return 0;
		} else if (depth == 0) {
			if (perftTT.isUnique(board.getZobristHash()) && dissimilar() && !board.getAttackBoard().inCheck(board.getToMove())) { // in odd depth settings && !board.getAttackBoard().inCheck(board.getToMove())
				board.printBoard();
				oldUnique.put(board.getZobristHash(), depthSoFar);
			}
			return 1;
		}
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[depth], unused);

		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			board.makeMove(move);

			nodes += collectUniquePositions(depth - 1, depthSoFar + 1);
			board.unmakeMove(move);
		}
		return nodes;
	}

	private long countDeepPositions(int depth, int depthSoFar, boolean ruined) {
		if (depth == 1) {
			return incrementFinalDepth(ruined);
		}

		if (memoryIsSpare) { // if our bottleneck is memory, don't store depth one results, therefore store up here
			long hash = board.getZobristHash() + depth;
			if (counterHelp[(int) ((hash % 3) + 3) % 3].incrementToLimit(hash, 2, depth) >= 2) {
				return 1;
			}
		}

		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[depth], unused);
		long nodes = 0;
		if (moves[0] == -1) { // illegal position
			return 0;
		}

		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			board.makeMove(move);
			if (!memoryIsSpare) {
				long hash = board.getZobristHash() + depth;
				if (counterHelp[(int) ((hash % 3) + 3) % 3].incrementToLimit(hash, 2, depth) >= 2) {
					board.unmakeMove(move);
					nodes++;
					continue;
				}
			}
			nodes += countDeepPositions(depth - 1, depthSoFar + 1, ruined || oldUnique.isPresent(board.getZobristHash()) < depthSoFar); // we get ruined by an old unique position
			board.unmakeMove(move);
		}
		return nodes;
	}

	private long incrementFinalDepth(boolean ruined) {
		int[] moves = board.getMoveGenerator().collectMoves(board.getToMove(), movesStorage[0], unused);
		if (moves[0] == -1) { // illegal position
			return 0;
		}
		for (int i = 1; i <= moves[0]; i++) {
			int move = moves[i];
			perftTT.incrementPosition(board.zobristAfterMove(move), ruined);
		}
		return 1; // makes the perft become perft - 1 because we don't check for legality in this depth
	}

	public Perft(BoardInterface board) {
		this.board = board;
	}

	private boolean dissimilar() {
		String squareStr = board.squareString();
		for (String other : dissimilarPositions) {
			if (similar(other, squareStr)) {
				return false;
			}
		}
		dissimilarPositions.add(squareStr);
		return true;
	}

	private boolean similar(String a, String b) {
		int differences = 0;
		if (a.length() != 64) {
			int c = 0;
		}
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				differences++;
			}
		}
		return differences <= 2;
	}
}
