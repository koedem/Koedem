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
	private int[][] ttMoves = new int[101][5];

    private int[] utilityCaptures = new int[MoveGenerator.MAX_MOVE_COUNT];
    private TTEntry entry = new TTEntry();
    private static final int[] moveOrder = { 4, 3, 2, 1 };

    private static int[] unused = new int[6];

    private int[] evaluations = new int[MAX_DEPTH];
    private int[][] principleVariations = new int[MAX_DEPTH][MAX_DEPTH]; // TODO use in search and create additional one for qSearch

	private long nodes = 0;
	private long abortedNodes = 0;
	private long qNodes = 0;

	private long exactNodes = 0;

	public Search(BoardInterface board) {
		this.board = board;
	}
	
	public int[] rootMax(boolean toMove, int depth, long time, long maxTime) {
		Logging.printLine("");
		if (UCI.logging) {
			Logging.printLine("Starting depth " + depth + ".");
		}
		int alpha = -30000;
		int beta = 30000;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		int[] moves = board.getRootMoves();
		for (int moveIndex = 1; moveIndex <= moves[0]; moveIndex++) {
			int move = moves[moveIndex];
			if (System.currentTimeMillis() - time > 1000) {
				Logging.printLine("info depth " + depth + " nodes " + (board.getSearch().getNodes() + board.getSearch().getAbortedNodes()) + " nps "
				                  + 1000 * (board.getSearch().getNodes() + board.getSearch().getAbortedNodes()) / ((System.currentTimeMillis() - time) > 0 ?
				                                                                                                   (System.currentTimeMillis() - time) : 1)
				                  + " hashfull " + UCI.lowerBoundsTable.getFill() + " time " + (System.currentTimeMillis() - time));
				UCI.lowerBoundsTable.printStats();
				//UCI.upperBoundsTable.printStats();
				Logging.printLine("info depth " + depth + " currmove " 
						+ Transformation.numberToMove(move) + " currmovenumber " + (moveIndex));
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
			if (depth > 1) {
				if (beta == alpha + 1) { // this is a null window
					innerPV[depth] = -nullWindowSearch(!toMove, depth, depth - 1, -beta, time + maxTime);
				} else {
					if (depth == 2) {
						innerPV = openWindowDepthOneSearch(!toMove, depth, -beta, -alpha);
					} else {
						innerPV = negaMax(!toMove, depth, depth - 1, -beta, -alpha, time + maxTime); // TODO !toMove is confusing, as board.toMove changes upon moving but not this local
					}
					// variable
					innerPV[depth] = -innerPV[depth];
					innerPV[0] = move;

					//UserInteraction.printEngineOutput("Search move ", innerPV, board, time);

					if (innerPV[depth] > 9000) {
						innerPV[depth]--;
						principleVariation = innerPV;

						board.setEnPassant(enPassant);
						board.unmakeMove(move, capturedPiece, castlingRights);
						return principleVariation;
					} else if (innerPV[depth] < -9000) {
						innerPV[depth]++;
					}
				}
			} else if (depth == 1) {
				int qsearch = -memoryEfficientQSearch(!toMove, -beta, -alpha, 0);
				innerPV[depth] = qsearch;
				innerPV[0] = move;
				if (innerPV[depth] > 9000) {
					innerPV[depth]--;
				} else if (innerPV[depth] < -9000) {
					innerPV[depth]++;
				}
			}
			if (innerPV[depth] > principleVariation[depth] && !UCI.isThreadFinished() && System.currentTimeMillis() - time < maxTime) {
				if (innerPV[depth] < beta) {
					principleVariation = innerPV;
					if (innerPV[depth] > alpha) {
						alpha = principleVariation[depth];
						beta = alpha + 1; // trying out null move pruning, if we fail high we need to set beta to a higher value
					} else {
						int ignore = 0;
					}

					if (depth != 1) {
						if (moveIndex == 1) {
							UCI.printEngineOutput("", principleVariation, board, !board.getToMove(), time);
							// move on board not yet undone, thus !toMove
						} else {
							UCI.printEngineOutput("New best move: ", principleVariation, board, !board.getToMove(), time);
						}
					}

					for (int i = moveIndex; i > 1; i--) {
						moves[i] = moves[i - 1];
					}
					moves[1] = move; // order best move to top
				} else {
					beta = 30000;
					moveIndex--;
					Logging.printLine("Null window fail high.");
				}
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
			if (UCI.isThreadFinished() || System.currentTimeMillis() - time > maxTime) {
				Logging.printLine("Search interrupted.");
				if (principleVariation[0] == 0) { // if we don't have a move yet use our best move ordering guess
					principleVariation[0] = moves[1];  // i.e. last depths result
				}
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
	public int[] negaMax(boolean toMove, int depth, int depthLeft, int alphaBound, int betaBound, long finishUntil) {
		if (depthLeft == 1) {
			Logging.printLine("Error, search should have entered depthOneSearch but did not.");
			System.exit(1);
		}

		int alpha = alphaBound;
		int beta = betaBound;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		int[] moves = board.getMoveGenerator().collectMoves(toMove, movesStorage[depth - depthLeft], unused);
		ttMoves[depthLeft][0] = 4; // set array to full since we're going to fill it with tt moves
		ttMoves[depthLeft][1] = 0;
		ttMoves[depthLeft][2] = 0;
		ttMoves[depthLeft][3] = 0;
		ttMoves[depthLeft][4] = 0;
		if (moves[0] == -1) {
			principleVariation[depth] = 10000;
			return principleVariation;
		} else if (board.getHashTable().get(board.getZobristHash()) != null && depthLeft != depth) {
			principleVariation[depth] = 0;
			return principleVariation;
		}

		if (UCI.lowerBoundsTable.get(board.getZobristHash(), entry, depthLeft) != null) {
			if (entry.getDepth() == depthLeft) { // TODO for now only for exact depth matches, in the future >=
				if (entry.getEval() >= beta) { // we get a beta cutoff
					principleVariation[depth] = entry.getEval();
					nodes++;
					return principleVariation;
				} else if (entry.getEval() > alpha) {
					alpha = entry.getEval(); // we have at least this score proven so it becomes alpha
				}
				ttMoves[depthLeft][moveOrder[0]] = entry.getMove();
			} else {
				Logging.printLine("Probably hash collision, depth is not what it should be. Search lower bounds table, Position:");
				board.printBoard();
			}
		}

		if (UCI.upperBoundsTable.get(board.getZobristHash(), entry, depthLeft) != null) {
			if (entry.getDepth() == depthLeft) { // TODO for now only for exact depth matches, in the future >=
				if (entry.getEval() <= alpha) { // we are worse than alpha so can't possibly improve the score
					principleVariation[depth] = entry.getEval();
					nodes++;
					return principleVariation;
				} else if (entry.getEval() < beta) {
					beta = entry.getEval(); // we have at least this score proven so it becomes alpha
				}
				ttMoves[depthLeft][moveOrder[1]] = entry.getMove();
			} else {
				Logging.printLine("Probably hash collision, depth is not what it should be. Search upper bounds table, Position:");
				board.printBoard();
			}
		}

		if (UCI.lowerBoundsTable.get(board.getZobristHash(), entry, depthLeft - 1) != null) { // use old depth entries
			ttMoves[depthLeft][moveOrder[2]] = entry.getMove();                                           // for better initial move ordering
		}

		if (UCI.upperBoundsTable.get(board.getZobristHash(), entry, depthLeft - 1) != null) { // use old depth entries
			ttMoves[depthLeft][moveOrder[3]] = entry.getMove();                                           // for better initial move ordering
		}

		for (int i = 1; i <= ttMoves[depthLeft][0]; i++) {
			if (ttMoves[depthLeft][i] == 0) {
				ttMoves[depthLeft][0]--;
				for (int j = i; j <= ttMoves[depthLeft][0]; j++) { // ttMove[0] already got reduced here
					ttMoves[depthLeft][j] = ttMoves[depthLeft][j + 1];
				}
				i--;
			}
		}

		for (int i = 1; i <= ttMoves[depthLeft][0]; i++) {
			for (int j = i + 1; j <= ttMoves[depthLeft][0]; j++) {
				if (ttMoves[depthLeft][i] == ttMoves[depthLeft][j]) {
					ttMoves[depthLeft][0]--;
					for (int k = j; k <= ttMoves[depthLeft][0]; k++) {
						ttMoves[depthLeft][k] = ttMoves[depthLeft][k + 1];
					}
					j--;
				}
			}
		}

		for (int move = 1; move <= ttMoves[depthLeft][0]; move++) {
			for (int i = 1; i <= moves[0]; i++) {
				if (moves[i] == ttMoves[depthLeft][move]) {
					for (int j = i; j > 1; j--) { // reorder moves to put TT move to top
						moves[j] = moves[j - 1];
					}
					moves[1] = ttMoves[depthLeft][move];
					break;
				}
			}
		}

		int preAlpha = alpha;
		int bestMove = 0;

		for (int index = 1; index <= moves[0]; index++) {
			int move = moves[index];
			if (depthLeft == 2) {
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
			
			int[] innerPV;

			if (depthLeft == 2) {
				innerPV = openWindowDepthOneSearch(!toMove, depth, -beta, -alpha);
			} else {
				innerPV = negaMax(!toMove, depth, depthLeft - 1, -beta, -alpha, finishUntil);
			}
			innerPV[depth] = -innerPV[depth];
			innerPV[depth - depthLeft] = move;
			if (innerPV[depth] > 9000) {
				innerPV[depth]--;
				//principleVariation = innerPV;

				//board.setEnPassant(enPassant);
				//board.unmakeMove(move, capturedPiece, castlingRights);
				//return principleVariation;
			} else if (innerPV[depth] < -9000) {
				innerPV[depth]++;
			}

			if (innerPV[depth] > principleVariation[depth]) {
				principleVariation = innerPV;
				if (innerPV[depth] > alpha) {
					alpha = principleVariation[depth];
				}
				bestMove = move;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);

			if (System.currentTimeMillis() > finishUntil) { // if we exceeded the maximum allotted time we return
				return principleVariation;
			}
			
			if (principleVariation[depth] >= beta) {
				entry.setEval(principleVariation[depth]);
				entry.setDepth(depthLeft);
				entry.setMove(move);
				UCI.lowerBoundsTable.put(board.getZobristHash(), entry);
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
			}
		}
		moves = null;
		entry.setEval(principleVariation[depth]);
		entry.setDepth(depthLeft);
		entry.setMove(bestMove);
		UCI.upperBoundsTable.put(board.getZobristHash(), entry);

		if (principleVariation[depth] > preAlpha) { // this is an exact score, so a lower bound too
			entry.setEval(principleVariation[depth]);
			entry.setDepth(depthLeft);
			entry.setMove(bestMove);
			UCI.lowerBoundsTable.put(board.getZobristHash(), entry);
			exactNodes++;
		}
		return principleVariation;
	}

	/**
	 *
	 * @param toMove : who to move it is
	 * @param depth : how many plies the recursion should go from root
	 * @param alphaBound The value of the alpha bound for alpha-beta-algorithm.
	 * @param betaBound The value of the beta bound for alpha-beta-algorithm.
	 *
	 * @return the principle variation we get for the position
	 */
	public int[] openWindowDepthOneSearch(boolean toMove, int depth, int alphaBound, int betaBound) {
		int alpha = alphaBound;
		int beta = betaBound;
		int[] principleVariation = new int[depth + 1];
		principleVariation[depth] = -30000;
		int[] moves = board.getMoveGenerator().collectMoves(toMove, movesStorage[depth - 1], unused);
		ttMoves[1][0] = 2; // without lower depth moves we can gain at most two moves
		ttMoves[1][1] = 0;
		ttMoves[1][2] = 0;
		if (moves[0] == -1) {
			principleVariation[depth] = 10000;
			return principleVariation;
		} else if (board.getHashTable().get(board.getZobristHash()) != null && 1 != depth) {
			principleVariation[depth] = 0;
			return principleVariation;
		}

		if (UCI.lowerBoundsTable.get(board.getZobristHash(), entry, 1) != null) {
			if (entry.getDepth() == 1) { // TODO for now only for exact depth matches, in the future >=
				if (entry.getEval() >= beta) { // we get a beta cutoff
					principleVariation[depth] = entry.getEval();
					nodes++;
					return principleVariation;
				} else if (entry.getEval() > alpha) {
					alpha = entry.getEval(); // we have at least this score proven so it becomes alpha
				}
				ttMoves[1][2] = entry.getMove();
			} else {
				Logging.printLine("Probably hash collision, depth is not what it should be. D1-search lower bounds table, Position:");
				board.printBoard();
			}
		}

		if (UCI.upperBoundsTable.get(board.getZobristHash(), entry, 1) != null) {
			if (entry.getDepth() == 1) { // TODO for now only for exact depth matches, in the future >=
				if (entry.getEval() <= alpha) { // we are worse than alpha so can't possibly improve the score
					principleVariation[depth] = entry.getEval();
					nodes++;
					return principleVariation;
				} else if (entry.getEval() < beta) {
					beta = entry.getEval(); // we have at least this score proven so it becomes alpha
				}
				ttMoves[1][1] = entry.getMove();
			} else {
				Logging.printLine("Probably hash collision, depth is not what it should be. D1-search upper bounds table, Position:");
				board.printBoard();
			}
		}

		if (ttMoves[1][1] == ttMoves[1][2]) {
			if (ttMoves[1][1] == 0) {
				ttMoves[1][0] = 0;
			} else {
				ttMoves[1][0] = 1;
			}
		} else {
			if (ttMoves[1][1] == 0){
				ttMoves[1][0] = 1;
				ttMoves[1][1] = ttMoves[1][2];
			} else if (ttMoves[1][2] == 0) {
				ttMoves[1][0] = 1;
			}
		}

		for (int move = 1; move <= ttMoves[1][0]; move++) {
			for (int i = 1; i <= moves[0]; i++) {
				if (moves[i] == ttMoves[1][move]) {
					for (int j = i; j > 1; j--) { // reorder moves to put TT move to top
						moves[j] = moves[j - 1];
					}
					moves[1] = ttMoves[1][move];
					break;
				}
			}
		}

		int preAlpha = alpha;
		int bestMove = 0;

		for (int index = 1; index <= moves[0]; index++) {
			int move = moves[index];

			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);

			int qsearch = -memoryEfficientQSearch(!toMove, -beta, -alpha, 0);
			if (qsearch > 9000) {
				qsearch--;
			} else if (qsearch < -9000) {
				qsearch++;
			}

			if (qsearch > principleVariation[depth]) {
				principleVariation[depth] = qsearch;
				principleVariation[depth - 1] = move;
				if (qsearch > alpha) {
					alpha = principleVariation[depth];
				}
				bestMove = move;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);

			if (principleVariation[depth] >= beta) {
				entry.setEval(principleVariation[depth]);
				entry.setDepth(1);
				entry.setMove(move);
				UCI.lowerBoundsTable.put(board.getZobristHash(), entry);
				return principleVariation;
			}
			if (UCI.isThreadFinished()) { // we should stop calculating here
				return principleVariation;
			}
		}
		if (principleVariation[depth] == -9999) {
			utilityCaptures = board.getCaptureGenerator().collectCaptures(!toMove, utilityCaptures);
			if (utilityCaptures[0] != -1) { // stalemate detection
				principleVariation[depth] = 0;
			}
		}
		moves = null;
		entry.setEval(principleVariation[depth]);
		entry.setDepth(1);
		entry.setMove(bestMove);
		UCI.upperBoundsTable.put(board.getZobristHash(), entry);

		if (principleVariation[depth] > preAlpha) { // this is an exact score, so a lower bound too
			entry.setEval(principleVariation[depth]);
			entry.setDepth(1);
			entry.setMove(bestMove);
			UCI.lowerBoundsTable.put(board.getZobristHash(), entry);
			exactNodes++;
		}
		return principleVariation;
	}

	/**
	 *
	 * @param toMove : who to move it is
	 * @param depthLeft : how many plies are left in the recursion
	 * @param scoreToBeat The value of the alpha bound for alpha-beta-algorithm. TODO
	 *
	 * @return the estimated eval, either <= or > scoreToBeat.
	 */
	public int nullWindowSearch(boolean toMove, int depth, int depthLeft, int scoreToBeat, long finishUntil) {
		int eval = -30000;
		int[] moves = board.getMoveGenerator().collectMoves(toMove, movesStorage[depth - depthLeft], unused); // todo remove depth dependency
		ttMoves[depthLeft][0] = 4; // set array to full since we're going to fill it with tt moves
		ttMoves[depthLeft][1] = 0;
		ttMoves[depthLeft][2] = 0;
		ttMoves[depthLeft][3] = 0;
		ttMoves[depthLeft][4] = 0;
		if (moves[0] == -1) {
			return 10000;
		} else if (board.getHashTable().get(board.getZobristHash()) != null && depthLeft != depth) {
			return 0;
		}

		if (UCI.lowerBoundsTable.get(board.getZobristHash(), entry, depthLeft) != null) {
			if (entry.getDepth() == depthLeft) { // TODO for now only for exact depth matches, in the future >=
				if (entry.getEval() > scoreToBeat) { // we beat the requested score -> return score
					nodes++;
					return entry.getEval();
				}
				ttMoves[depthLeft][moveOrder[0]] = entry.getMove();
			} else {
				Logging.printLine("Probably hash collision, depth is not what it should be. NW-search lower bounds table, Position:");
				board.printBoard();
			}
		}

		if (UCI.upperBoundsTable.get(board.getZobristHash(), entry, depthLeft) != null) {
			if (entry.getDepth() == depthLeft) { // TODO for now only for exact depth matches, in the future >=
				if (entry.getEval() <= scoreToBeat) { // we are worse than alpha so can't possibly improve the score
					nodes++;
					return entry.getEval();
				}
				ttMoves[depthLeft][moveOrder[1]] = entry.getMove();
			} else {
				Logging.printLine("Probably hash collision, depth is not what it should be. NW-search upper bounds table, Position:");
				board.printBoard();
			}
		}

		if (UCI.lowerBoundsTable.get(board.getZobristHash(), entry, depthLeft - 1) != null) { // use old depth entries
			ttMoves[depthLeft][moveOrder[2]] = entry.getMove();                                           // for better initial move ordering
		}

		if (UCI.upperBoundsTable.get(board.getZobristHash(), entry, depthLeft - 1) != null) { // use old depth entries
			ttMoves[depthLeft][moveOrder[3]] = entry.getMove();                                           // for better initial move ordering
		}

		for (int i = 1; i <= ttMoves[depthLeft][0]; i++) {
			if (ttMoves[depthLeft][i] == 0) {
				ttMoves[depthLeft][0]--;
				for (int j = i; j <= ttMoves[depthLeft][0]; j++) { // ttMove[0] already got reduced here
					ttMoves[depthLeft][j] = ttMoves[depthLeft][j + 1];
				}
				i--;
			}
		}

		for (int i = 1; i <= ttMoves[depthLeft][0]; i++) {
			for (int j = i + 1; j <= ttMoves[depthLeft][0]; j++) {
				if (ttMoves[depthLeft][i] == ttMoves[depthLeft][j]) {
					ttMoves[depthLeft][0]--;
					for (int k = j; k <= ttMoves[depthLeft][0]; k++) {
						ttMoves[depthLeft][k] = ttMoves[depthLeft][k + 1];
					}
					j--;
				}
			}
		}

		if (ttMoves[depthLeft][0] >= 3) {
			int a = 0;
		}

		for (int move = 1; move <= ttMoves[depthLeft][0]; move++) {
			for (int i = 1; i <= moves[0]; i++) {
				if (moves[i] == ttMoves[depthLeft][move]) {
					for (int j = i; j > 1; j--) { // reorder moves to put TT move to top
						moves[j] = moves[j - 1];
					}
					moves[1] = ttMoves[depthLeft][move];
					break;
				}
			}
		}

		int bestMove = 0;

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

			int innerEval = -30000;
			if (depthLeft > 1) {
				innerEval = nullWindowSearch(!toMove, depth, depthLeft - 1, -scoreToBeat - 1, finishUntil);
				innerEval = -innerEval;
				if (innerEval > 9000) {
					innerEval--;
					//principleVariation = innerEval;

					//board.setEnPassant(enPassant);
					//board.unmakeMove(move, capturedPiece, castlingRights);
					//return principleVariation;
				} else if (innerEval < -9000) {
					innerEval++;
				}
			} else if (depthLeft == 1) {
				innerEval = -memoryEfficientQSearch(!toMove, -scoreToBeat - 1, -scoreToBeat, 0);
				if (innerEval > 9000) {
					innerEval--;
				} else if (innerEval < -9000) {
					innerEval++;
				}
			}
			if (innerEval > eval) {
				eval = innerEval;
				//if (innerEval > alpha) {
				//	alpha = innerEval;
				//}
				bestMove = move;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);

			if (System.currentTimeMillis() > finishUntil) { // if we exceeded the maximum allotted time we return
				return eval;
			}

			if (eval > scoreToBeat) {
				entry.setEval(eval);
				entry.setDepth(depthLeft);
				entry.setMove(move);
				UCI.lowerBoundsTable.put(board.getZobristHash(), entry);
				return eval;
			}
			if (UCI.isThreadFinished()) { // we should stop calculating here
				return eval;
			}
		}
		if (eval == -9999) {
			utilityCaptures = board.getCaptureGenerator().collectCaptures(!toMove, utilityCaptures);
			if (utilityCaptures[0] != -1) { // stalemate detection
				eval = 0;
			}
		}
		moves = null;
		entry.setEval(eval);
		entry.setDepth(depthLeft);
		entry.setMove(bestMove);
		UCI.upperBoundsTable.put(board.getZobristHash(), entry);

		if (eval > scoreToBeat) { // this is an exact score, so a lower bound too
			entry.setEval(eval);
			entry.setDepth(depthLeft);
			entry.setMove(bestMove);
			UCI.lowerBoundsTable.put(board.getZobristHash(), entry);
			exactNodes++;
			Logging.printLine("NW-search gave exact score.");
			System.exit(1); // this is impossible
		}
		return eval;
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

	    if (depthSoFar == 30) { // i.e. only two kings left
	    	return 0;
	    }
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
            if (capture == 0b00011010_00001100) {
                int breakPoint = 0;
            }
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

	public long getExactNodes() {
		return exactNodes;
	}
}