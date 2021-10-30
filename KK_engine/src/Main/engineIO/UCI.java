package Main.engineIO;

import Main.MultiThreading.*;
import Main.engine.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Anon
 * TODO ucinewgame after (none) bestmove leads to more (none)
 */
public final class UCI {
	
	public static boolean uci = true;

	public static boolean shuttingDown = false;

    private static int baseTime = 100;
	private static int incTime = 2;
	private static int     minLeft       = 30;
											// For scenarios with possibly inaccurate system time we have backup:
	private static int     lowerKN_Bound = 0;   // We don't move before we searched lowerKN_Bound * timeLimit many nodes.
											// Should be lower than kN/s
	private static int     upperKN_Bound = 2000; // No matter what the timer says, when upperKN_Bound * timeLimit nodes
											// are exceeded we move. Should be higher than kN/s
	private static int     kingSafety    = 100;
	private static int     dynamism      = 100;
	private static int     ccTimePerMove = 10000;
	private static int     ttSizeInMB = 256;
	public static  boolean logging       = false;

	private static int threadCount = 1;
	private static final int LOWER_THREAD_COUNT = 1;
	private static final int UPPER_THREAD_COUNT = 5;
	public static final int MAX_DEPTH = 100;
	public static final int STANDARD_CHESS = 0, ZOMBIE_CHESS = 1, WAVE_CHESS = 2;
	public static int GAME_MODE = ZOMBIE_CHESS;
	public static final boolean CHECKS_ARE_FORCING = true;

	private static boolean threadFinished = true;

	static         String            engineName       = "Koedem";
	static         BoardInterface    board            = new Board();
	static         Perft             perft            = new Perft(board);
	public static  SearchTTInterface upperBoundsTable = new SearchExactTT(ttSizeInMB * (1 << 19), false);
	public static        SearchTTInterface lowerBoundsTable = new SearchExactTT(ttSizeInMB * (1 << 19), true);
	private static final Scanner           sc               = new Scanner(System.in);
	
	public static void main(String[] args) {
		CorrespondenceOrganisation.getInstance().setup();
	    ThreadOrganization.setUp(board);
		uciCommunication();
		System.exit(0);
	}
	
	private static void uciCommunication() {
		Logging.setup();
		String command = "";
		Logging.printLine(engineName + " by Kolja Kühn.");
		while (!command.equals("quit")) {
			command = sc.nextLine();
			if (logging) {
				Logging.printLine("info command received at milli: " + System.currentTimeMillis());
			}
			Logging.addToLogFile(">> " + command);
			
			if (command.equals("uci")) {
				inputUCI();
			} else if (command.startsWith("setoption name")) {
				inputSetOption(command);
			} else if (command.equals("isready")) {
				inputIsReady();
			} else if (command.equals("ucinewgame")) {
				inputUCINewGame();
			} else if (command.startsWith("position")) {
				inputPosition(command);
			} else if (command.contains("go")) {
				inputGo(command);
			} else if (command.equals("print")) {
				board.printBoard();
			} else if (command.equals("print legal moves")) {
				int[] movesSize = new int[6]; // unused
				int[] moves = new int[MoveGenerator.MAX_MOVE_COUNT];
				moves = board.getMoveGenerator().collectMoves(board.getToMove(), moves, movesSize);
				if (moves[0] == -1) {
					Logging.printLine("Illegal position.");
					continue;
				}
				for (Integer move : moves) {
					Logging.printLine(Transformation.numberToMove(move));
				}
			} else if (command.equals("find mate")) {
				ThreadOrganization.findMate();
			} else if (command.equals("print legal captures")) {
				int[] captures = board.getCaptureGenerator().collectCaptures(board.getToMove(), new int[MoveGenerator.MAX_MOVE_COUNT]);
				if (captures[0] == 0) {
					Logging.printLine("There are no legal captures.");
					continue;
				}
				if (captures[0] == -1) {
					Logging.printLine("Illegal position.");
					continue;
				}
				for (int i = 1; i <= captures[0]; i++) {
					Logging.printLine(Transformation.numberToMove(captures[i]));
				}
			} else if (command.equals("q search")) {
				ArrayList<Integer> reversePV = board.getSearch().qSearch(board.getToMove(), -30000, 30000, 0);
				Logging.printLine(Integer.toString(reversePV.get(0)));
				reversePV.remove(0);
				StringBuilder pv = new StringBuilder();
				for (Integer capture : reversePV) {
					pv.append(Transformation.numberToMove(capture)).append(" ");
				}
				pv.append("Node count: ").append(board.getSearch().getNodes());
				Logging.printLine(pv.toString());
				board.getSearch().setNodes(0);
			} else if (command.equals("q eval")) {
			    Logging.printLine(Integer.toString(board.getSearch().memoryEfficientQSearch(board.getToMove(), -30000, 30000, 0)));
			} else if (command.equals("evaluate")) {
				Logging.printLine(Integer.toString(board.getEvaluation().evaluation(board.getToMove(), -30000)));
			} else if (command.equals("Hashtable")) {
				board.printRepetitionInfo();
			} else if (command.startsWith("perft")) {
				int depth = Integer.parseInt(command.substring(6));
				perft.basePerft(depth, false, false);
			} else if (command.startsWith("detailed perft")) {
				int depth = Integer.parseInt(command.substring(15));
				perft.basePerft(depth, true, false);
			} else if (command.startsWith("fast perft")) {
				int depth = Integer.parseInt(command.substring(11));
				perft.basePerft(depth, false, true);
			} else if (command.equals("materialOnly on")) {
				Evaluation.setMaterialOnly(true);
			} else if (command.equals("materialOnly off")) {
				Evaluation.setMaterialOnly(false);
			} else if (command.equals("print bitboard")) {
				board.getBitboard().printBitBoard();
			} else if (command.startsWith("probe")) {
				probe(Integer.parseInt(command.substring(6, 8)) ,command.substring(9));
			} else if (command.startsWith("recursive probe")) {
				recursiveProbe(Integer.parseInt(command.substring(16, 17)), Integer.parseInt(command.substring(18, 20)),"", new Board(command.substring(21)));
			} else if (command.equals("refute")) {
				ThreadOrganization.refute();
			} else if (command.equals("stop")) {
			    threadFinished = true;
            }
		}
		shuttingDown = true;
		CorrespondenceOrganisation.getInstance().shutDown();
		Logging.close();
	}

    private static void inputUCINewGame() {
		board.resetBoard();
		for (int i = 0; i < ThreadOrganization.boards.length; i++) {
		    ThreadOrganization.boards[i].resetBoard();
        }
		upperBoundsTable.clearTT();
		lowerBoundsTable.clearTT();
	}

	private static void inputSetOption(String command) {
		String[] parameters = command.split(" ");
		switch (parameters[2]) {
			case "Hash":
				try {
					ttSizeInMB = Integer.parseInt(parameters[4]);
					upperBoundsTable = new SearchExactTT(ttSizeInMB * (1L << 19), false);
					lowerBoundsTable = new SearchExactTT(ttSizeInMB * (1L << 19), true);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'BaseTime'.");
				}
				break;
			case "BaseTime":
				try {
					baseTime = 10000 / Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'BaseTime'.");
				}
				break;
			case "IncTime":
				try {
					incTime = 10000 / Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'IncTime'.");
				}
				break;
			case "MinLeft":
				try {
					minLeft = 10000 / Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'MinLeft'.");
				}
				break;
			case "Lower_KN_searched_bound":
				try {
					lowerKN_Bound = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Lower_KN_searched_bound'.");
				}
				break;
			case "Upper_KN_searched_bound":
				try {
					upperKN_Bound = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Upper_KN_searched_bound'.");
				}
				break;
			case "KingSafety":
				try {
					kingSafety = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'KingSafety'.");
				}
				break;
			case "Dynamism":
				try {
					dynamism = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "Threads":
				try {
					int newCount = Integer.parseInt(parameters[4]);
					if (newCount >= LOWER_THREAD_COUNT && newCount <= UPPER_THREAD_COUNT) {
						threadCount = newCount;
						ThreadOrganization.updateThreadCount(threadCount, board);
					} else {
						Logging.printLine("Illegal value for option 'Threads'. Should be between " + LOWER_THREAD_COUNT + " and " + UPPER_THREAD_COUNT + ".");
					}
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Threads'. Should be an integer.");
				}
				break;
			case "CorrTimeSeconds":
				try {
					ccTimePerMove = Integer.parseInt(parameters[4]) * 1000;
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'CorrTimeSeconds'.");
				}
				break;

			case "PawnActFull":
				try {
					Evaluation.PAWN_ACTIVITY_FULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "PawnActEmpty":
				try {
					Evaluation.PAWN_ACTIVITY_EMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "KnightActFull":
				try {
					Evaluation.KNIGHT_ACTIVITY_FULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "KnightActEmpty":
				try {
					Evaluation.KNIGHT_ACTIVITY_EMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "BishopActFull":
				try {
					Evaluation.BISHOP_ACTIVITY_FULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "BishopActEmpty":
				try {
					Evaluation.BISHOP_ACTIVITY_EMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "RookActFull":
				try {
					Evaluation.ROOK_ACTIVITY_FULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "RookActEmpty":
				try {
					Evaluation.ROOK_ACTIVITY_EMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "QueenActFull":
				try {
					Evaluation.QUEEN_ACTIVITY_FULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "QueenActEmpty":
				try {
					Evaluation.QUEEN_ACTIVITY_EMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "KingActFull":
				try {
					Evaluation.KING_ACTIVITY_FULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "KingActEmpty":
				try {
					Evaluation.KING_ACTIVITY_EMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "Logging":
				logging = true;
				Logging.setup();
				break;
			default:
				if (parameters[2].startsWith("PST_")) {
					int colour = parameters[2].charAt(4) == 'W' ? 0 : 1;
					int piece = Transformation.stringToPiece(parameters[2].charAt(5));
					int square = Transformation.squareToNumber(parameters[2].substring(6, 8));
					Evaluation.PIECE_SQUARE_TABLES[colour][piece][square] = Integer.parseInt(parameters[4]);
				}
		}
	}

	private static void inputUCI() {
		Logging.printLine("id name " + engineName);
		Logging.printLine("id author Tom Marvolo");

		Logging.printLine("option name Hash type spin default 256 min 1 max 16384");
		Logging.printLine("option name BaseTime type spin default 100 min 1 max 10000");
		Logging.printLine("option name IncTime type spin default 5000 min 1 max 10000");
		Logging.printLine("option name MinTime type spin default 333 min 1 max 10000");
		Logging.printLine("option name Lower_KN_searched_bound type spin default 0 min 0 max 1000");
		Logging.printLine("option name Upper_KN_searched_bound type spin default 2000 min 1 max 10000");
		Logging.printLine("option name KingSafety type spin default 100 min 1 max 1000");
		Logging.printLine("option name Dynamism type spin default 100 min 1 max 1000");
		Logging.printLine("option name Threads type spin default 1 min 1 max 5");
		Logging.printLine("option name CorrTimeSeconds type spin default 10 min 0 max 100000");

		Logging.printLine("option name PawnActFull type spin default 38 min -100 max 1000");
		Logging.printLine("option name PawnActEmpty type spin default 10 min -100 max 1000");
		Logging.printLine("option name KnightActFull type spin default 54 min -100 max 1000");
		Logging.printLine("option name KnightActEmpty type spin default 33 min -100 max 1000");
		Logging.printLine("option name BishopActFull type spin default 36 min -100 max 1000");
		Logging.printLine("option name BishopActEmpty type spin default 59 min -100 max 1000");
		Logging.printLine("option name RookActFull type spin default 42 min -100 max 1000");
		Logging.printLine("option name RookActEmpty type spin default 18 min -100 max 1000");
		Logging.printLine("option name QueenActFull type spin default 3 min -100 max 1000");
		Logging.printLine("option name QueenActEmpty type spin default 73 min -100 max 1000");
		Logging.printLine("option name KingActFull type spin default -22 min -100 max 1000");
		Logging.printLine("option name KingActEmpty type spin default 44 min -100 max 1000");

		Logging.printLine("uciok");
	}
	
	private static void inputIsReady() {
		while (!isThreadFinished()) {
			threadFinished = true;
			System.out.println("Thread not finished.");
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Logging.printLine("readyok");
	}
	
	private static void inputPosition(String input) {
		String command = input.substring(9);

		String[] parameters = command.split(" ");
		for (int i = 0; i < parameters.length; i++) {
			switch (parameters[i]) {
				case "startpos":
					board.resetBoard();
					for (int k = 0; k < ThreadOrganization.boards.length; k++) {
                        ThreadOrganization.boards[k].resetBoard();
                    }
					break;
				case "fen":
					StringBuilder fen = new StringBuilder();
					for (int j = 0; j < 6; j++) {
						fen.append(parameters[i + 1 + j]).append(" ");
					}
					board.setFENPosition(fen.toString());
                    for (int k = 0; k < ThreadOrganization.boards.length; k++) {
                        ThreadOrganization.boards[k].setFENPosition(fen.toString());
                    }
					i += 6;
					break;
				case "moves":
					i++;
					for (int j = 0; j < (parameters.length - i); j++) {
						board.makeMove(parameters[i + j]);
                        for (int k = 0; k < ThreadOrganization.boards.length; k++) {
                            ThreadOrganization.boards[k].makeMove(parameters[i + j]);
                        }
					}
					break;
			}
		}
		board.setBestmove("");
		for (int k = 0; k < ThreadOrganization.boards.length; k++) {
			ThreadOrganization.boards[k].setBestmove("");
		}
	}
	
	private static void inputGo(String command) {
		if (command.equals("go movetime 10000") && ccTimePerMove != 0) {
			ThreadOrganization.go(100, ccTimePerMove, Integer.MAX_VALUE / 2);
		} else if (command.contains("depth")) {
			String[] parameters = command.split(" ");
			int depth = Integer.parseInt(parameters[2]);
			ThreadOrganization.go(depth, Integer.MAX_VALUE, Integer.MAX_VALUE);
		} else if (command.contains("infinite")) {
		    ThreadOrganization.go(100, Integer.MAX_VALUE, Integer.MAX_VALUE);
		} else {
			long hardTimeLimit = Integer.MAX_VALUE;
			String[] parameters = command.split(" ");
			int searchTime = 0;

			for (int i = 1; i < parameters.length; i++) {
				if (board.getToMove() && parameters[i].equals("wtime") || !board.getToMove() && parameters[i].equals("btime")) {
					searchTime += Integer.parseInt(parameters[i + 1]) / baseTime;
					hardTimeLimit = searchTime * baseTime / 2;
					if (Integer.parseInt(parameters[i + 1]) == Integer.MAX_VALUE) { // workaround for lichess bug with Correspondence games
						searchTime = ccTimePerMove;
						hardTimeLimit = Integer.MAX_VALUE / 2;
					}
				} else if (board.getToMove() && parameters[i].equals("winc") || !board.getToMove() && parameters[i].equals("binc")) {
					if (searchTime + Integer.parseInt(parameters[i + 1]) / incTime < searchTime * baseTime / minLeft) {
						searchTime += Integer.parseInt(parameters[i + 1]) / incTime;
					} else {
						searchTime *= baseTime / minLeft;
					}
				}
			}
			if (logging) {
				Logging.printLine("Trying to use " + searchTime + "ms + finishing current ply or at most " + hardTimeLimit + "ms.");
			}
			ThreadOrganization.go(100, searchTime, hardTimeLimit);
		}
	}
	
	public static boolean isThreadFinished() {
		return threadFinished;
	}

	public static void setThreadFinished(boolean threadFinished) {
		UCI.threadFinished = threadFinished;
	}

	public static String printPV(BoardInterface board) {
		BoardInterface copy = board.cloneBoard();
		TTEntry entry = new TTEntry();
		StringBuilder str = new StringBuilder(" ");
		if (UCI.lowerBoundsTable instanceof SearchExactTT) {
			int startingDepth = 100;
			while (startingDepth > 0 && (UCI.lowerBoundsTable.get(copy.getZobristHash(), entry, startingDepth) == null
			        || UCI.upperBoundsTable.get(copy.getZobristHash(), entry, startingDepth) == null)) {
				startingDepth--;
			}
			while ((UCI.lowerBoundsTable.get(copy.getZobristHash(), entry, startingDepth) != null
			        || UCI.upperBoundsTable.get(copy.getZobristHash(), entry, startingDepth) != null) && entry.getMove() != 0 && str.length() < 200) {
				str.append(Transformation.numberToMove(entry.getMove())).append(" ");
				copy.makeMove(entry.getMove());
				startingDepth--;
			}
		} else {
			while ((UCI.lowerBoundsTable.get(copy.getZobristHash(), entry, 0) != null ||
			        UCI.upperBoundsTable.get(copy.getZobristHash(), entry, 0) != null) && entry.getMove() != 0 && str.length() < 200) {
				str.append(Transformation.numberToMove(entry.getMove())).append(" ");
				copy.makeMove(entry.getMove());
			}
		}
		return str.toString();
	}
	
	public static void printEngineOutput(String prefix, int[] move, BoardInterface board, boolean toMove, long time) {
		if (UCI.uci) {
			long timeUsed = System.currentTimeMillis() - time;
			StringBuilder pv = new StringBuilder();
			for (int i = 0; i < move.length - 1; i++) {
				pv.append(Transformation.numberToMove(move[i])).append(" ");
				if (move[i] == 0) {
					break;
				}
			}
			pv.append(printPV(board));

			Logging.printLine("info depth " + (move.length - 1) + " score cp " + move[move.length - 1] + " nodes "
			      + (board.getSearch().getNodes() + board.getSearch().getAbortedNodes()) + " nps " + 1000 * (board.getSearch().getNodes() + board.getSearch().getAbortedNodes())
			                                                                                         / (timeUsed > 0 ? timeUsed : 1)
			      + " hashfull " + lowerBoundsTable.getFill() + " time " + (System.currentTimeMillis() - time) + " pv " + pv);
			Logging.printLine("Exact nodes: " + board.getSearch().getExactNodes());
		} else {
			StringBuilder pv = new StringBuilder(prefix);
			for (int j = 0; j < move.length - 1; j++) {
				if (move[j] == -1) {
					break;
				}
				if (toMove) {
					if (j % 2 == 0) {
						pv.append(board.getMoveNumber() + j / 2).append(".");
					}
					pv.append(Transformation.numberToMove(move[j])).append(" ");
				} else {
					if (j == 0) {
						pv.append(board.getMoveNumber()).append("...");
					}
					if (j % 2 == 1) {
						pv.append(board.getMoveNumber() + j / 2 + 1).append(".");
					}
					pv.append(Transformation.numberToMove(move[j])).append(" ");
				}
			}
			Logging.printLine(pv.toString() + move[move.length - 1]);
			Logging.printLine(prefix + "Node count: " + Transformation.nodeCountOutput(((board.getSearch().getNodes()
					+ board.getSearch().getAbortedNodes()))) + "(" + Transformation.nodeCountOutput(board.getSearch().getNodes())
					+ ")" + ". Q-nodes: " + Transformation.nodeCountOutput(board.getSearch().getQNodes()) + ". Time used: "
					+ Transformation.timeUsedOutput((System.currentTimeMillis() - time)));
		}
	}

	private static void probe(int depth, String fen) {
		Board probing = new Board(fen);
		TTEntry probeEntry = new TTEntry();
		for (int probeDepth = depth; probeDepth < 100; probeDepth++) {
			if (upperBoundsTable.get(probing.getZobristHash(), probeEntry, probeDepth) != null) {
				Logging.printLine("Depth " + probeDepth + ", UPPER bound: " + probeEntry.getEval() + ", best move: " + Transformation.numberToMove(probeEntry.getMove()));
			}
			if (lowerBoundsTable.get(probing.getZobristHash(), probeEntry, probeDepth) != null) {
				Logging.printLine("Depth " + probeDepth + ", lower bound: " + probeEntry.getEval() + ", best move: " + Transformation.numberToMove(probeEntry.getMove()));
			}
		}
	}

	private static void recursiveProbe(int recursionDepth, int probeMinDepth, String pv, Board probing) {
		TTEntry probeEntry = new TTEntry();
		for (int probeDepth = 100; probeDepth >= probeMinDepth; probeDepth--) {
			if (upperBoundsTable.get(probing.getZobristHash(), probeEntry, probeDepth) != null) {
				Logging.printLine(pv + "; Depth " + probeDepth + ", UPPER bound: " + probeEntry.getEval() + ", best move: " + Transformation.numberToMove(probeEntry.getMove()));
				break;
			}
		}
		for (int probeDepth = 100; probeDepth >= probeMinDepth; probeDepth--) {
			if (lowerBoundsTable.get(probing.getZobristHash(), probeEntry, probeDepth) != null) {
				Logging.printLine(pv + "; Depth " + probeDepth + ", lower bound: " + probeEntry.getEval() + ", best move: " + Transformation.numberToMove(probeEntry.getMove()));
				break;
			}
		}
		if (recursionDepth > 1) {
			int[] moves = new int[MoveGenerator.MAX_MOVE_COUNT], movesSize = new int[6];
			moves = probing.getMoveGenerator().collectMoves(probing.getToMove(), moves, movesSize);
			for (int i = 1; i <= moves[0]; i++) {
				probing.makeMove(moves[i]);
				recursiveProbe(recursionDepth - 1, probeMinDepth, new String(pv + " " + Transformation.numberToMove(moves[i])), probing);
				probing.unmakeMove(moves[i]);
			}
		}
	}
	
	private UCI() {
	}

	public static int getKingSafety() {
		return kingSafety;
	}

	public static void setKingSafety(int kingSafety) {
		UCI.kingSafety = kingSafety;
	}

	public static int getDynamism() {
		return dynamism;
	}

	public static void setDynamism(int dynamism) {
		UCI.dynamism = dynamism;
	}

	public static int getLowerKN_Bound() {
		return lowerKN_Bound;
	}

	public static int getUpperKN_Bound() {
		return upperKN_Bound;
	}
}