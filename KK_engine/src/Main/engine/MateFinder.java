package Main.engine;

import Main.MultiThreading.ThreadOrganization;
import Main.engineIO.UCI;

import java.io.Serializable;

public class MateFinder implements Serializable {

	private BoardInterface board;
	private MateTTEntry[] entries = new MateTTEntry[255];

	private int[][][] movesStorage = new int[2][101][MoveGenerator.MAX_MOVE_COUNT];

	public MateFinder(BoardInterface board) {
		this.board = board;
		for (int i = 0; i < entries.length; i++) {
			entries[i] = new MateTTEntry();
		}
	}

	public int rootMateFinder(int depth, boolean aggressive) {
		MateTTEntry entry = new MateTTEntry();
		int mateScore = -50;
		int[] moves = board.getRootMoves();
		int bestMoveIndex = 1;
		int bestMove = 0;
		for (int index = 1; index <= moves[0]; index++) {
			int move = moves[index];
			board.getSearch().incrementNodes();
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			int innerEval;

			innerEval = mateFinder(true, depth, depth, aggressive ? 4 : 1, aggressive);
			innerEval = -innerEval;
			//UserInteraction.printEngineOutput("NonLosing Search move ", innerPV, board, time);
			if (innerEval > 0) { // TODO we probably don't want this to ever happen
				innerEval = 0;
			}

			if (innerEval > mateScore) {
				mateScore = innerEval;
				bestMoveIndex = index;
				bestMove = move;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(moves[index], capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
			if (innerEval < 0) {
				moves[index] = moves[moves[0]--]; // replace index with the last array element and then decrease array size by one to de facto remove index
				index--;
			}
		}

		entry.setMateScore(mateScore);
		entry.setMove(bestMove);

		entry.setAggressiveDepth(0); // we're trying to not lose here so we
		entry.setFullDepth(0); // don't know anything about trying to win
		if (aggressive) {
			entry.setAggressiveLosingDepth(depth);
			entry.setFullLosingDepth(0);
		} else {
			entry.setAggressiveLosingDepth(0);
			entry.setFullLosingDepth(depth);
		}
		ThreadOrganization.globalMateTT.put(board.getZobristHash(), entry);

		return mateScore;
	}

	/**
	 * @param attacker true if the player currently to move tries to win, false otherwise.
	 * @param depth The total depth in full moves to be calculated. I.e. depth 3 looks 6 (!) half moves deep.
	 * @param depthLeft The remaining depth in full moves to be calculated. I.e. depthLeft 3 means 6 (!) more half moves will be calculated.
	 * @param quietMoveDepthCost The cost of making a quiet move in depth. E.g. if set to 1, checks are treated the same as non checks.
	 *                           If set to a high value, e.g. 100, then only checks are looked at (unless depth > 100).
	 * @param narrow In case there are multiple mate searches going on on the same transposition table, this denotes this search as narrow or not.
	 * @return The number of moves to achieve checkmate, or 0 if none exists.
	 */
	public int mateFinder(boolean attacker, int depth, int depthLeft, int quietMoveDepthCost, boolean narrow) {
		MateTTEntry entry = entries[depth - depthLeft];
		if (board.getZobristHash() == -8197142223296564155L || board.getSearch().getNodes() >= 998750) {
			int debug = 0;
		}
		if (ThreadOrganization.globalMateTT.get(board.getZobristHash(), entry) != null) {
			if (entry.getMateScore() != 0) { // any kind of mate score can immediately be returned
				return entry.getMateScore();
			}
			if (attacker) { // no mate score was found so it's a draw according to the entry
				if (entry.getFullDepth() >= depthLeft) { // check if we have enough depth to return that draw
					return 0;
				} else if (narrow && entry.getAggressiveDepth() >= depthLeft) {
					return 0;
				}
			} else {
				if (entry.getFullLosingDepth() >= depthLeft) {
					return 0;
				} else if (narrow && entry.getAggressiveLosingDepth() >= depthLeft) {
					return 0;
				}
			}
		}
		int mateScore = -50; // the worst score possible is being mated in 1
		int[] moves = movesStorage[0][2 * (depth - depthLeft) + (attacker ? 0 : 1)];
		if (!attacker) {
			moves = board.getCheckMoveGenerator().collectAllPNMoves(moves, board.getToMove());
		} else {
			if (depthLeft <= quietMoveDepthCost) { // the last move has to be a check for it to be checkmate; check if we don't have enough depth for a quiet move PLUS then a check
				moves = board.getCheckMoveGenerator().collectCheckMoves(movesStorage[1][depth - depthLeft], moves, board.getToMove()); // TODO why is moveStorage[1] a full array of arrays?
			} else {
				moves = board.getCheckMoveGenerator().collectPNSearchMoves(movesStorage[1][depth - depthLeft], moves, board.getToMove());
			}
		}
		
		if (moves[0] == -1) { // TODO can this happen?
			mateScore = 50;
			return mateScore;
		} else if (attacker && moves[0] == 0) {
			mateScore = 0;
			return mateScore;
		}
		int bestMove = 0;
		for (int index = 1; index <= moves[0]; index++) {
			int move = moves[index];
			board.getSearch().incrementNodes();
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			long zobrist = board.getZobristHash();
			board.makeMove(move);
			int innerEval = -50;
			if (depthLeft >= 1) {
				int innerDepthLeft = attacker ? depthLeft - (board.getAttackBoard().inCheck(board.getToMove()) ? 1 : quietMoveDepthCost) : depthLeft;
				// if it's a check subtract 1, otherwise the cost of a quiet move; or 0 if we're not the attacker
				innerEval = mateFinder(!attacker, depth, innerDepthLeft, quietMoveDepthCost, narrow);
				innerEval = -innerEval;
				if (innerEval > 0) {
					innerEval--; // if we have a mate in n we have to increment that when backing up
				}
			} else if (depthLeft == 0) {
				assert !attacker;
				innerEval = 0;
				if (board.getAttackBoard().inCheck(!board.getToMove())) {
					innerEval = -50;
				}
			}
			if (innerEval > mateScore) {
				mateScore = innerEval;
				bestMove = move;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
			if (zobrist != board.getZobristHash()) {
				assert false;
			}

			if (!attacker && mateScore >= 0 || mateScore > 0) {
				break;
			}
		}
		if (mateScore == -50) {
			int[] captures = board.getCaptureGenerator().collectCaptures(!board.getToMove(), new int[256]); // TODO is this just a bad inCheck call?
			if (captures[0] != -1) { // stalemate
				mateScore = 0;
				entry.setFullDepth(255);
				entry.setAggressiveDepth(255);
				entry.setFullLosingDepth(255);
				entry.setAggressiveLosingDepth(255);
				ThreadOrganization.globalMateTT.put(board.getZobristHash(), entry);
				return mateScore;
			}
		}
		if (UCI.isThreadFinished()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			throw new RuntimeException(); // TODO fix this nonsense
		}

		entry.setMove(bestMove);

		if (attacker) {
			entry.setAggressiveLosingDepth(0); // we're trying to win here so we
			entry.setFullLosingDepth(0); // don't know anything about trying not to lose
			int entryDepth = depthLeft < quietMoveDepthCost && mateScore < 0 ? quietMoveDepthCost : depthLeft;
			entry.setMateScore(Math.max(mateScore, 0));

			entry.setAggressiveDepth(entryDepth);
			if (narrow) {
				entry.setFullDepth(0);
			} else {
				entry.setFullDepth(entryDepth);
			}
			ThreadOrganization.globalMateTT.put(board.getZobristHash(), entry);
		} else {
			entry.setAggressiveDepth(0); // we're trying to not lose here so we
			entry.setFullDepth(0); // don't know anything about trying to win
			int entryDepth = depthLeft < quietMoveDepthCost && mateScore > 0 ? quietMoveDepthCost : depthLeft;
			entry.setMateScore(Math.min(mateScore, 0));

			entry.setAggressiveLosingDepth(entryDepth);
			if (narrow) {
				entry.setFullLosingDepth(0);
			} else {
				entry.setFullLosingDepth(entryDepth);
			}
			ThreadOrganization.globalMateTT.put(board.getZobristHash(), entry);
		}
		return mateScore;
	}

	void resetMateFinder() {
	}
}
