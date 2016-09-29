package engine;

import java.util.ArrayList;
import engineIO.Logging;
import engineIO.UCI;

/**
 * 
 * @author Anon
 *
 */
public final class Search {
	
	public static int[] rootMax(Board board, boolean toMove, int depth, long time) {
		Logging.printLine("");
		Logging.printLine("Starting depth " + depth + ".");
		int alpha = -30000;
		int beta = 30000;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		ArrayList<Integer> moves = board.getRootMoves();
		int bestMove = 0;
		for (int moveIndex = 0; moveIndex < moves.size(); moveIndex++) {
			byte capturedPiece = board.getSquare((moves.get(moveIndex) / 8) % 8, moves.get(moveIndex) % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(moves.get(moveIndex));
			int[] innerPV = new int[depth + 1];
			if (depth > 1) {
				innerPV = negaMax(board, !toMove, depth, depth - 1, -beta, -alpha);
				innerPV[depth] = -innerPV[depth];
				innerPV[0] = moves.get(moveIndex);
				
				//UserInteraction.printEngineOutput("Search move ", innerPV, board, time);
				
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
					principleVariation = innerPV;
					
					board.setEnPassant(enPassant);
					board.unmakeMove(moves.get(moveIndex), capturedPiece);
					board.addCastlingRights(castlingRights);
					return principleVariation;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			} else if (depth == 1) {
				ArrayList<Integer> qsearch = qSearch(board, !toMove, -beta, -alpha);
				innerPV[depth] = -qsearch.get(0);
				innerPV[0] = moves.get(moveIndex);
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
				
				if (depth != 1) {
					if (moveIndex == 0) {
						UCI.printEngineOutput("", principleVariation, board, !board.getToMove(), time); // move on board not yet undone
					} else {
						UCI.printEngineOutput("New best move: ", principleVariation, board, !board.getToMove(), time);
					}
				}
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(moves.get(moveIndex), capturedPiece);
			board.addCastlingRights(castlingRights);
		}
		if (principleVariation[depth] == -9999) {
			ArrayList<Integer> captures = MoveGenerator.collectCaptures(board, !toMove);
			if (captures.size() == 0 || captures.get(0) != -1) {
				principleVariation[depth] = 0;
				return principleVariation;
			}
		}
		
		int bestMoveText = moves.get(bestMove);
		moves.set(bestMove, moves.get(0));
		moves.set(0, bestMoveText); // order best move to top
		
		return principleVariation;
	}
	
	/**
	 * 
	 * @param board : board on which we are
	 * @param toMove : who to move it is
	 * @param depth : how many plies the recursion should go from root
	 * @param depthLeft : how many plies are left in the recursion
	 * @param alphaBound The value of the alpha bound for alpha-beta-algorithm.
	 * @param betaBound The value of the beta bound for alpha-beta-algorithm.
	 * 
	 * @return the principle variation we get for the position
	 */
	@SuppressWarnings("unused")
	public static int[] negaMax(Board board, boolean toMove, int depth, int depthLeft, int alphaBound, int betaBound) {
		int alpha = alphaBound;
		int beta = betaBound;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		int[] movesSize = new int[6]; // unused
		ArrayList<Integer> moves = MoveGenerator.collectMoves(board, toMove, movesSize);
		if (moves.get(0) == -1) {
			principleVariation[depth] = 10000;
			return principleVariation;
		} else if (board.getHashTable().get(board.getSquareString()) != null && depthLeft != depth) {
			principleVariation[depth] = 0;
			return principleVariation;
		}
		for (Integer move : moves) {
			if (depthLeft == 1) {
				int a = 0;
			} else if (depthLeft == 2) {
				int a = 0;
			} else if (depthLeft == 3) {
				int a = 0;
			} else if (depthLeft == 4) {
				int a = 0;
			} else if (depthLeft == 5) {
				int a = 0;
			} else if (depthLeft == 6) {
				int a = 0;
			} else if (depthLeft == 7) {
				int a = 0;
			}
			int material = board.getMaterialCount();
			int pawnAdvancement = board.getPawnAdvancement();
			int knightAdvancement = board.getKnightAdvancement();
			int bishopAdvancement = board.getBishopAdvancement();
			int rookAdvancement = board.getRookAdvancement();
			int queenAdvancement = board.getQueenAdvancement();
			int kingAdvancement = board.getKingAdvancement();
			
			byte capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			int[] innerPV = new int[depth + 1];
			if (depthLeft > 1) {
				innerPV = negaMax(board, !toMove, depth, depthLeft - 1, -beta, -alpha);
				innerPV[depth] = -innerPV[depth];
				innerPV[depth - depthLeft] = move;
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
					principleVariation = innerPV;
					
					board.setEnPassant(enPassant);
					board.unmakeMove(move, capturedPiece);
					board.addCastlingRights(castlingRights);
					return principleVariation;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			} else if (depthLeft == 1) {
				ArrayList<Integer> qsearch = qSearch(board, !toMove, -beta, -alpha);
				innerPV[depth] = -qsearch.get(0);
				innerPV[depth - depthLeft] = move;
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
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece);
			board.addCastlingRights(castlingRights);
			
			if (material != board.getMaterialCount() || pawnAdvancement != board.getPawnAdvancement() 
					|| knightAdvancement != board.getKnightAdvancement() 
					|| bishopAdvancement != board.getBishopAdvancement() || rookAdvancement != board.getRookAdvancement()
					|| queenAdvancement != board.getQueenAdvancement() || kingAdvancement != board.getKingAdvancement()) {
				Logging.printLine("Error");
			}
			
			if (principleVariation[depth] >= beta) {
				return principleVariation;
			}
		}
		if (principleVariation[depth] == -9999) {
			ArrayList<Integer> captures = MoveGenerator.collectCaptures(board, !toMove);
			if (captures.size() == 0 || captures.get(0) != -1) {
				principleVariation[depth] = 0;
				return principleVariation;
			}
		}
		if (UCI.isThreadFinished()) {
			throw new RuntimeException();
		}
		return principleVariation;
	}
	
	/**
	 * Perform a q search (only consider captures) on the given board.
	 * Compare evaluation with and without capture and see which one is better i.e. whether the capture is good.
	 * 
	 * @param board Board on which a q search gets performed.
	 * @param toMove Who to move it is.
	 * @param alphaBound Alpha bound for alpha-beta search.
	 * @param betaBound Beta bound for the alpha-beta search.
	 * @return The best chain of captures and its evaluation. (can be empty if captures are bad)
	 */
	public static ArrayList<Integer> qSearch(Board board, boolean toMove, int alphaBound, int betaBound) {
		int alpha = alphaBound;
		int beta = betaBound;
		ArrayList<Integer> principleVariation = new ArrayList<Integer>(1);
		principleVariation.add(-30000);
		int eval = Evaluation.evaluation(board, toMove, alpha);
		if (eval > principleVariation.get(0)) {
			principleVariation.set(0, eval);
			if (eval > alpha) {
				alpha = eval;
			}
		}
		if (Math.abs(eval) > 5000) {
			principleVariation.set(0, -10000);
			return principleVariation;
		}
		if (principleVariation.get(0) >= beta) {
			return principleVariation;
		}
		ArrayList<Integer> captures = MoveGenerator.collectCaptures(board, toMove);
		if (captures.size() > 0 && captures.get(0) == -1) {
			principleVariation.set(0, 10000);
			return principleVariation;
		}
		for (Integer capture : captures) {
			byte capturedPiece = board.getSquare((capture / 8) % 8, capture % 8);
			board.makeMove(capture);
			ArrayList<Integer> innerPV = qSearch(board, !toMove, -beta, -alpha);
			board.qNodes++;
			if (innerPV.get(0) == -10000) {
				principleVariation = new ArrayList<Integer>(1);
				principleVariation.add(0, 10000);
				board.unmakeMove(capture, capturedPiece);
				return principleVariation;
			}
			innerPV.set(0, -innerPV.get(0));
			if (innerPV.get(0) > principleVariation.get(0)) {
				principleVariation = innerPV;
				principleVariation.add(capture);
				if (innerPV.get(0) > alpha) {
					alpha = principleVariation.get(0);
				}
			}
			board.unmakeMove(capture, capturedPiece);
			if (principleVariation.get(0) >= beta) {
				return principleVariation;
			}
		}
		return principleVariation;
	}

	private Search() {
	}
}