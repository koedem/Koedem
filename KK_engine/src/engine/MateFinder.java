package engine;

import java.util.ArrayList;

import engineIO.UCI;

public final class MateFinder {

	public static int[] rootMateFinder(Board board, boolean toMove, int depth, long time, boolean aggressive) {
		int alpha = -30000;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		ArrayList<Integer> moves = board.getRootMoves();
		int bestMove = 0;
		for (int moveIndex = 0; moveIndex < moves.size(); moveIndex++) {
			board.nodes++;
			byte capturedPiece = board.getSquare((moves.get(moveIndex) / 8) % 8, moves.get(moveIndex) % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(moves.get(moveIndex));
			int[] innerPV = new int[depth + 1];
			if (depth > 1) {
				innerPV = mateFinder(board, !toMove, depth, depth - 1, aggressive);
				innerPV[depth] = -innerPV[depth];
				innerPV[0] = moves.get(moveIndex);
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
			board.unmakeMove(moves.get(moveIndex), capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
			if (innerPV[depth] < -9000) {
				moves.remove(moveIndex);
				moveIndex--;
			}
		}
		
		if (moves.size() > 0) {
			int bestMoveText = moves.get(bestMove);
			moves.set(bestMove, moves.get(0));
			moves.set(0, bestMoveText); // order best move to top
		}
		return principleVariation;
	}
	
	public static int[] mateFinder(Board board, boolean toMove, int depth, int depthLeft, boolean aggressive) {
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -9999;
		ArrayList<Integer> moves = null;
		if (depthLeft % 2 == 1) {
			moves = MoveGenerator.collectAllPNMoves(board, toMove);
		} else {
			if (aggressive) {
				moves = MoveGenerator.collectCheckMoves(board, toMove);
			} else {
				moves = MoveGenerator.collectPNSearchMoves(board, toMove);
			}
		}
		
		if (moves.size() > 0 && moves.get(0) == -1) {
			principleVariation[depth] = 10000;
			return principleVariation;
		} else if (depthLeft % 2 == 0 && moves.size() == 0) {
			principleVariation[depth] = 0;
			return principleVariation;
		} else if (board.getHashTable().get(board.getSquareString()) != null && depthLeft != depth) {
			principleVariation[depth] = 0;
			return principleVariation;
		}
		for (Integer move : moves) {
			board.nodes++;
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			int[] innerPV = new int[depth + 1];
			if (depthLeft > 1) {
				innerPV = mateFinder(board, !toMove, depth, depthLeft - 1, aggressive);
				innerPV[depth] = -innerPV[depth];
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			} else if (depthLeft == 1) {
				innerPV[depth] = 0;
				if (inCheck(board)) {
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
			ArrayList<Integer> captures = MoveGenerator.collectCaptures(board, !toMove);
			if (captures.size() == 0 || captures.get(0) != -1) {
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
	
	public static boolean inCheck(Board board) {
		boolean inCheck = false;
		ArrayList<Integer> captures = MoveGenerator.collectCaptures(board, board.getToMove());
		if (captures.size() > 0 && captures.get(0) == -1) {
			inCheck = true;
		}
		return inCheck;
	}
	
	private MateFinder() {
	}
}
