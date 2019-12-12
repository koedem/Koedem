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

	public int rootMateFinder(boolean toMove, int depth, boolean aggressive) {
		MateTTEntry entry = new MateTTEntry();
		int mateScore = -100;
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
			int innerEval = -100;

			innerEval = mateFinder(!toMove, depth, depth - 1, aggressive);
			innerEval = -innerEval;
			//UserInteraction.printEngineOutput("NonLosing Search move ", innerPV, board, time);
			if (innerEval < 0) {
				innerEval++;
			} else if (innerEval > 0) {
				innerEval--;
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
	 *
	 * @param toMove
	 * @param depth
	 * @param depthLeft if even we try to win, if odd we only need to not lose
	 * @param aggressive
	 * @return
	 */
	public int mateFinder(boolean toMove, int depth, int depthLeft, boolean aggressive) {
		MateTTEntry entry = entries[depth - depthLeft];
		if (board.getZobristHash() == -8197142223296564155L || board.getSearch().getNodes() >= 998750) {
			int debug = 0;
		}
		if (ThreadOrganization.globalMateTT.get(board.getZobristHash(), entry) != null) {
			if (entry.getMateScore() != 0) {
				return entry.getMateScore();
			}
			if (depthLeft % 2 == 0) {
				if (entry.getFullDepth() >= depthLeft) {
					return 0;
				} else if (aggressive && entry.getAggressiveDepth() >= depthLeft) {
					return 0;
				}
			} else {
				if (entry.getFullLosingDepth() >= depthLeft) {
					return 0;
				} else if (aggressive && entry.getAggressiveLosingDepth() >= depthLeft) {
					return 0;
				}
			}
		}
		int mateScore = -100;
		int[] moves = movesStorage[0][depth - depthLeft];
		if (depthLeft % 2 == 1) {
			moves = board.getCheckMoveGenerator().collectAllPNMoves(moves, toMove);
		} else {
			if (aggressive) {
				moves = board.getCheckMoveGenerator().collectCheckMoves(movesStorage[1][depth - depthLeft], moves, toMove);
			} else {
				moves = board.getCheckMoveGenerator().collectPNSearchMoves(movesStorage[1][depth - depthLeft], moves, toMove);
			}
		}
		
		if (moves[0] == -1) {
			mateScore = 100;
			return mateScore;
		} else if (depthLeft % 2 == 0 && moves[0] == 0) {
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
			int innerEval = -100;
			if (depthLeft > 1) {
				innerEval = mateFinder(!toMove, depth, depthLeft - 1, aggressive);
				innerEval = -innerEval;
				if (innerEval < 0) {
					innerEval++;
				} else if (innerEval > 0) {
					innerEval--;
				}
			} else if (depthLeft == 1) {
				innerEval = 0;
				if (board.getAttackBoard().inCheck(!board.getToMove())) {
					innerEval = -100;
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

			entry.setMateScore(mateScore);
			entry.setMove(bestMove);
			if (depthLeft % 2 == 1 && mateScore >= 0) {
				entry.setAggressiveDepth(0); // we're trying to not lose here so we
				entry.setFullDepth(0); // don't know anything about trying to win
				if (aggressive) {
					entry.setAggressiveLosingDepth(depthLeft);
					entry.setFullLosingDepth(0);
				} else {
					entry.setAggressiveLosingDepth(0);
					entry.setFullLosingDepth(depthLeft);
				}
				ThreadOrganization.globalMateTT.put(board.getZobristHash(), entry);
				break;
			} else if (mateScore > 0) {
				ThreadOrganization.globalMateTT.put(board.getZobristHash(), entry);
				break;
			}
		}
		if (mateScore == -100) {
			int[] captures = board.getCaptureGenerator().collectCaptures(!toMove, new int[256]);
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
			throw new RuntimeException();
		}
		entry.setMateScore(mateScore);
		entry.setMove(bestMove);
		if (depthLeft % 2 == 0 && mateScore <= 0) {
			entry.setAggressiveLosingDepth(0); // we're trying to win here so we
			entry.setFullLosingDepth(0); // don't know anything about trying not to lose
			if (aggressive) {
				entry.setAggressiveDepth(depthLeft);
				entry.setFullDepth(0);
			} else {
				entry.setAggressiveDepth(0);
				entry.setFullDepth(depthLeft);
			}
			ThreadOrganization.globalMateTT.put(board.getZobristHash(), entry);
		} else if (mateScore < 0) {
			ThreadOrganization.globalMateTT.put(board.getZobristHash(), entry);
		}
		return mateScore;
	}

	void resetMateFinder() {
	}
}
