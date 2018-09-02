package Main.engine;

import Main.engineIO.Logging;
import Main.engineIO.Transformation;
import Main.engineIO.UCI;

import java.util.ArrayList;

/**
 * 
 * @author Anon
 *
 */
public class Search implements SearchInterface {

	public static final int MAX_DEPTH = 100;

	private BoardInterface board;
	private int[][] movesStorage = new int[101][MoveGenerator.MAX_MOVE_COUNT];
	private int[][] capturesStorage = new int[30][MoveGenerator.MAX_MOVE_COUNT]; // 30 because thats max number of captures;
                                                                                // TODO: Less than MAX_MOVE_COUNT
    private int[] utilityCaptures = new int[MoveGenerator.MAX_MOVE_COUNT];

    private static int[] unused = new int[6];

    private int[] evaluations = new int[MAX_DEPTH];
    private int[][] principleVariations = new int[MAX_DEPTH][MAX_DEPTH]; // TODO use in search and create additional one for qSearch

	private long nodes = 0;
	private long abortedNodes = 0;
	private long qNodes = 0;

	public Search(BoardInterface board) {
		this.board = board;
	}
	
	public int[] rootMax(boolean toMove, int depth, long time) {
		Logging.printLine("");
		Logging.printLine("Starting depth " + depth + ".");
		int alpha = -30000;
		int beta = 30000;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		int[] moves = board.getRootMoves();
		int bestMove = 1;
		for (int moveIndex = 1; moveIndex <= moves[0]; moveIndex++) {
			if (System.currentTimeMillis() - time > 1000) {
				Logging.printLine("info depth " + depth + " currmove " 
						+ Transformation.numberToMove(moves[moveIndex]) + " currmovenumber " + (moveIndex));
			}
			byte capturedPiece;
			if (moves[moveIndex] < (1 << 13)) {
				capturedPiece = board.getSquare((moves[moveIndex] / 8) % 8, moves[moveIndex] % 8);
			} else {
				capturedPiece = board.getSquare((moves[moveIndex] / 64) % 8, (moves[moveIndex] / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(moves[moveIndex]);
			int[] innerPV = new int[depth + 1];
			if (depth > 1) {
				innerPV = negaMax(!toMove, depth, depth - 1, -beta, -alpha);
				innerPV[depth] = -innerPV[depth];
				innerPV[0] = moves[moveIndex];
				
				//UserInteraction.printEngineOutput("Search move ", innerPV, board, time);
				
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
					principleVariation = innerPV;
					
					board.setEnPassant(enPassant);
					board.unmakeMove(moves[moveIndex], capturedPiece, castlingRights);
					return principleVariation;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			} else if (depth == 1) {
				int qsearch = -memoryEfficientQSearch(!toMove, -beta, -alpha, 0);
				innerPV[depth] = qsearch;
				innerPV[0] = moves[moveIndex];
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
					if (moveIndex == 1) {
						UCI.printEngineOutput("", principleVariation, board, !board.getToMove(), time); 
															// move on board not yet undone, thus !toMove
					} else {
						UCI.printEngineOutput("New best move: ", principleVariation, board, !board.getToMove(), time);
					}
				}
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(moves[moveIndex], capturedPiece, castlingRights);
			if (UCI.isThreadFinished()) {
			    return principleVariation;
            }
		}
		if (principleVariation[depth] == -9999) {
            board.setBestmove("(none)");
			utilityCaptures = board.getCaptureGenerator().collectCaptures(!toMove, utilityCaptures);
			if (utilityCaptures[0] != -1) { // stalemate detection
				principleVariation[depth] = 0;
				return principleVariation;
			}
		}
		
		int bestMoveText = moves[bestMove];
		moves[bestMove] = moves[1];
		moves[1] = bestMoveText; // order best move to top
		
		return principleVariation;
	}
	
	/**
	 *
	 * @param toMove : who to move it is
	 * @param depth : how many plies the recursion should go from root
	 * @param depthLeft : how many plies are left in the recursion
	 * @param alphaBound The value of the alpha bound for alpha-beta-algorithm.
	 * @param betaBound The value of the beta bound for alpha-beta-algorithm.
	 * 
	 * @return the principle variation we get for the position
	 */
	public int[] negaMax(boolean toMove, int depth, int depthLeft, int alphaBound, int betaBound) {
		int alpha = alphaBound;
		int beta = betaBound;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		int[] moves = board.getMoveGenerator().collectMoves(toMove, movesStorage[depth - depthLeft], unused);
		if (moves[0] == -1) {
			principleVariation[depth] = 10000;
			return principleVariation;
		} else if (board.getHashTable().get(board.getZobristHash()) != null && depthLeft != depth) {
			principleVariation[depth] = 0;
			return principleVariation;
		}
		for (int index = 1; index <= moves[0]; index++) {
			int move = moves[index];
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
			
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			
			int[] innerPV = new int[depth + 1];
			if (depthLeft > 1) {
				innerPV = negaMax(!toMove, depth, depthLeft - 1, -beta, -alpha);
				innerPV[depth] = -innerPV[depth];
				innerPV[depth - depthLeft] = move;
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
					principleVariation = innerPV;
					
					board.setEnPassant(enPassant);
					board.unmakeMove(move, capturedPiece, castlingRights);
					return principleVariation;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			} else if (depthLeft == 1) {
				int qsearch = -memoryEfficientQSearch(!toMove, -beta, -alpha, 0);
				innerPV[depth] = qsearch;
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
			board.unmakeMove(move, capturedPiece, castlingRights);
			
			if (principleVariation[depth] >= beta) {
				return principleVariation;
			}
			innerPV = null;
			if (UCI.isThreadFinished()) { // we should stop calculating here
			    return principleVariation;
            }
		}
		if (principleVariation[depth] == -9999) {
			utilityCaptures = board.getCaptureGenerator().collectCaptures(!toMove, utilityCaptures);
			if (utilityCaptures[0] != -1) { // stalemate detection
				principleVariation[depth] = 0;
				return principleVariation;
			}
		}
		moves = null;
		return principleVariation;
	}
	
	/**
	 * Perform a q search (only consider captures) on the given board.
	 * Compare evaluation with and without capture and see which one is better i.e. whether the capture is good.
	 *
	 * @param toMove Who to move it is.
	 * @param alphaBound Alpha bound for alpha-beta search.
	 * @param betaBound Beta bound for the alpha-beta search.
	 * @return The best chain of captures and its evaluation. (can be empty if captures are bad)
	 */
	public ArrayList<Integer> qSearch(boolean toMove, int alphaBound, int betaBound, int depthSoFar) {
	    // IMPORTANT: If anything other than captures should be calculated in this method, the ArraySizes might need to be changed.

		int alpha = alphaBound;
		int beta = betaBound;
		ArrayList<Integer> principleVariation = new ArrayList<>(1);
		principleVariation.add(-30000);
		int eval = board.getEvaluation().evaluation(toMove, alpha);
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
		capturesStorage[depthSoFar] = board.getCaptureGenerator().collectCaptures(toMove, capturesStorage[depthSoFar]);
		if (capturesStorage[depthSoFar][0] == -1) {
			principleVariation.set(0, 10000);
			return principleVariation;
		}
		for (int i = 1; i <= capturesStorage[depthSoFar][0]; i++) {
		    int capture = capturesStorage[depthSoFar][i];
			byte capturedPiece;
			if (capture < (1 << 13)) {
				capturedPiece = board.getSquare((capture / 8) % 8, capture % 8);
			} else {
				capturedPiece = board.getSquare((capture / 64) % 8, (capture / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(capture);
			ArrayList<Integer> innerPV = qSearch(!toMove, -beta, -alpha, depthSoFar + 1);
			qNodes++;
			if (innerPV.get(0) == -10000) {
				principleVariation = new ArrayList<>(1);
				principleVariation.add(0, 10000);
				board.setEnPassant(enPassant);
				board.unmakeMove(capture, capturedPiece, castlingRights);
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
			board.setEnPassant(enPassant);
			board.unmakeMove(capture, capturedPiece, castlingRights);
			if (principleVariation.get(0) >= beta) {
				return principleVariation;
			}
			innerPV = null;
		}
		return principleVariation;
	}

    /**
     * Perform a q search (only consider captures) on the given board.
     * Compare evaluation with and without capture and see which one is better i.e. whether the capture is good.
     *
     * @param toMove Who to move it is.
     * @param alphaBound Alpha bound for alpha-beta search.
     * @param betaBound Beta bound for the alpha-beta search.
     * @return The evaluation of the best chain of captures.
     */
    public int memoryEfficientQSearch(boolean toMove, int alphaBound, int betaBound, int depthSoFar) {
        // IMPORTANT: If anything other than captures should be calculated in this method, the ArraySizes might need to be changed.

        int alpha = alphaBound;
        int beta = betaBound;
        int eval = board.getEvaluation().evaluation(toMove, alpha);
        if (eval > alpha) {
            alpha = eval;
        }

        /*if (Math.abs(eval) > 5000) { // TODO ????
            principleVariation.set(0, -10000);
            return principleVariation;
        }*/
        if (eval >= beta) {
            return eval;
        }
        capturesStorage[depthSoFar] = board.getCaptureGenerator().collectCaptures(toMove, capturesStorage[depthSoFar]);
        if (capturesStorage[depthSoFar][0] == -1) {
            return 10000;
        }
        for (int i = 1; i <= capturesStorage[depthSoFar][0]; i++) {
            int capture = capturesStorage[depthSoFar][i];
            byte capturedPiece;
            if (capture < (1 << 13)) {
                capturedPiece = board.getSquare((capture / 8) % 8, capture % 8);
            } else {
                capturedPiece = board.getSquare((capture / 64) % 8, (capture / 8) % 8);
            }
            byte castlingRights = board.getCastlingRights();
            byte enPassant = board.getEnPassant();
            board.makeMove(capture);
            int innerEval = -memoryEfficientQSearch(!toMove, -beta, -alpha, depthSoFar + 1);
            qNodes++;
            if (innerEval == 10000) {
                board.setEnPassant(enPassant);
                board.unmakeMove(capture, capturedPiece, castlingRights);
                return innerEval;
            }
            if (innerEval > eval) {
                eval = innerEval;
                if (eval > alpha) {
                    alpha = eval;
                }
            }
            board.setEnPassant(enPassant);
            board.unmakeMove(capture, capturedPiece, castlingRights);
            if (eval >= beta) {
                return eval;
            }
        }
        return eval;
    }

    /**
	 * This method does nothing right now. If we ever have add state to the Search we need to implement that state being resetted here.
	 */
	public void resetSearch() {
		nodes = 0;
		abortedNodes = 0;
		qNodes = 0;
	}

	public long getNodes() {
		return nodes;
	}

	public void setNodes(long nodes) {
		this.nodes = nodes;
	}

	public void incrementNodes() {
		nodes++;
	}

	public long getAbortedNodes() {
		return abortedNodes;
	}

	public void setAbortedNodes(long abortedNodes) {
		this.abortedNodes = abortedNodes;
	}

	public void incrementAbortedNodes() {
		abortedNodes++;
	}

	public long getQNodes() {
		return qNodes;
	}

	public void setQNodes(long qNodes) {
		this.qNodes = qNodes;
	}

	public void incrementQNodes() {
		qNodes++;
	}
}