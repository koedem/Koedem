package Main.engine;

import Main.engineIO.UCI;

import java.io.Serializable;

public class MateFinder implements Serializable {

	private BoardInterface board;

	public MateFinder(BoardInterface board) {
		this.board = board;
	}

	public int[] rootMateFinder(boolean toMove, int depth, long time, boolean aggressive) {
		int alpha = -30000;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		int[] moves = board.getRootMoves();
		int bestMove = 1;
		for (int moveIndex = 1; moveIndex <= moves[0]; moveIndex++) {
			board.getSearch().incrementNodes();
			byte capturedPiece = board.getSquare((moves[moveIndex] / 8) % 8, moves[moveIndex] % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(moves[moveIndex]);
			int[] innerPV = new int[depth + 1];
			if (depth > 1) {
				innerPV = mateFinder(!toMove, depth, depth - 1, aggressive);
				innerPV[depth] = -innerPV[depth];
				innerPV[0] = moves[moveIndex];
				//UserInteraction.printEngineOutput("NonLosing Search move ", innerPV, board, time);
				
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			}
			if (innerPV[depth] > principleVariation[depth]) {
				principleVariation = innerPV;
				if (innerPV[depth] > alpha) {
					alpha = principleVariation[depth];
				}
				bestMove = moveIndex;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(moves[moveIndex], capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
			if (innerPV[depth] < -9000) {
				moves[moveIndex] = moves[moves[0]--]; // replace moveIndex with the last array element and then decrease array size by one to de facto remove moveIndex
				moveIndex--;
			}
		}
		
		if (moves[0] > 0) {
			int bestMoveText = moves[bestMove];
			moves[bestMove] =  moves[1];
			moves[1] = bestMoveText; // order best move to top
		}
		return principleVariation;
	}
	
	public int[] mateFinder(boolean toMove, int depth, int depthLeft, boolean aggressive) {
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -9999;
		int moves[] = new int[MoveGenerator.MAX_MOVE_COUNT];
		if (depthLeft % 2 == 1) {
			moves = board.getCheckMoveGenerator().collectAllPNMoves(moves, toMove);
		} else {
			if (aggressive) {
				moves = board.getCheckMoveGenerator().collectCheckMoves(new int[MoveGenerator.MAX_MOVE_COUNT], moves, toMove);
			} else {
				moves = board.getCheckMoveGenerator().collectPNSearchMoves(new int[MoveGenerator.MAX_MOVE_COUNT], moves, board, toMove);
			}
		}
		
		if (moves[0] == -1) {
			principleVariation[depth] = 10000;
			return principleVariation;
		} else if (depthLeft % 2 == 0 && moves[0] == 0) {
			principleVariation[depth] = 0;
			return principleVariation;
		} else if (board.getHashTable().get(board.getZobristHash()) != null && depthLeft != depth) {
			principleVariation[depth] = 0;
			return principleVariation;
		}
		for (int index = 1; index <= moves[0]; index++) {
			int move = moves[index];
			board.getSearch().incrementNodes();
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			int[] innerPV = new int[depth + 1];
			if (depthLeft > 1) {
				innerPV = mateFinder(!toMove, depth, depthLeft - 1, aggressive);
				innerPV[depth] = -innerPV[depth];
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			} else if (depthLeft == 1) {
				innerPV[depth] = 0;
				if (inCheck()) {
					innerPV[depth] = -9999;
				}
			}
			if (innerPV[depth] > principleVariation[depth]) {
				principleVariation = innerPV;
				principleVariation[depth - depthLeft] = move;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
			
			if (depthLeft % 2 == 1 && principleVariation[depth] >= 0) {
				break;
			} else if (principleVariation[depth] > 0) {
				break;
			}
		}
		if (principleVariation[depth] == -9999) {
			int[] captures = board.getCaptureGenerator().collectCaptures(!toMove, new int[256]);
			if (captures[0] != -1) {
				principleVariation[depth] = 0;
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
		return principleVariation;
	}
	
	private boolean inCheck() {
		boolean inCheck = false;
		int[] captures = board.getCaptureGenerator().collectCaptures(board.getToMove(), new int[256]); // TODO why toMove and not !toMove ??
		if (captures[0] == -1) {
			inCheck = true;
		}
		return inCheck;
	}

	void resetMateFinder() {
	}
}
