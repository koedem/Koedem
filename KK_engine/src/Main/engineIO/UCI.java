package Main.engineIO;

import Main.MultiThreading.*;
import Main.engine.*;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	public static  boolean logging       = true;

	private static int threadCount = 1;
	private static final int LOWER_THREAD_COUNT = 1;
	private static final int UPPER_THREAD_COUNT = 5;

	private static boolean threadFinished = true;

	static String                  engineName          = "Koedem";
	static BoardInterface                   board               = new Board();
	static Perft perft = new Perft(board);
	public static SearchTT upperBoundsTable = new SearchTT(ttSizeInMB * (1 << 19), false);
	public static SearchTT lowerBoundsTable = new SearchTT(ttSizeInMB * (1 << 19), true);
	private static Scanner         sc                  = new Scanner(System.in);
	
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
				Logging.printLine("info command received at milli: " + Long.toString(System.currentTimeMillis()));
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
				for (Node value : board.getHashTable().values()) {
					value.print();
				}
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
			} else if (command.equals("stop")) {
			    threadFinished = true;
            }
		}
		shuttingDown = true;
		CorrespondenceOrganisation.getInstance().shutDown();
		Logging.close();
	}

    private static void inputUCINewGame() {
		board.setFENPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		for (int i = 0; i < ThreadOrganization.boards.length; i++) {
		    ThreadOrganization.boards[i].resetBoard();
        }
	}

	private static void inputSetOption(String command) {
		String[] parameters = command.split(" ");
		switch (parameters[2]) {
			case "Hash":
				try {
					ttSizeInMB = Integer.parseInt(parameters[4]);
					upperBoundsTable = new SearchTT(ttSizeInMB * (1L << 19), false);
					lowerBoundsTable = new SearchTT(ttSizeInMB * (1L << 19), true);
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
					Evaluation.PAWNACTIVITYFULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "PawnActEmpty":
				try {
					Evaluation.PAWNACTIVITYEMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "KnightActFull":
				try {
					Evaluation.KNIGHTACTIVITYFULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "KnightActEmpty":
				try {
					Evaluation.KNIGHTACTIVITYEMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "BishopActFull":
				try {
					Evaluation.BISHOPACTIVITYFULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "BishopActEmpty":
				try {
					Evaluation.BISHOPACTIVITYEMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "RookActFull":
				try {
					Evaluation.ROOKACTIVITYFULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "RookActEmpty":
				try {
					Evaluation.ROOKACTIVITYEMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "QueenActFull":
				try {
					Evaluation.QUEENACTIVITYFULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "QueenActEmpty":
				try {
					Evaluation.QUEENACTIVITYEMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "KingActFull":
				try {
					Evaluation.KINGACTIVITYFULL = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
			case "KingActEmpty":
				try {
					Evaluation.KINGACTIVITYEMPTY = Integer.parseInt(parameters[4]);
				} catch (NumberFormatException e) {
					Logging.printLine("Illegal value for option 'Dynamism'.");
				}
				break;
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
					board.getHashTable().clear();
					for (int k = 0; k < ThreadOrganization.boards.length; k++) {
						ThreadOrganization.boards[k].getHashTable().clear();
					}
					i++;
					for (int j = 0; j < (parameters.length - i); j++) {
						Node node = new Node(board, 0, 0, 0, board.getToMove());
						board.makeMove(parameters[i + j]);
                        for (int k = 0; k < ThreadOrganization.boards.length; k++) {
                            Node node1 = new Node(ThreadOrganization.boards[k], 0, 0, 0,
                                    ThreadOrganization.boards[k].getToMove());
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
	
	public static void printEngineOutput(String praefix, int[] move, BoardInterface board, boolean toMove, long time) {
		if (UCI.uci) {
			long timeUsed = System.currentTimeMillis() - time;
			StringBuilder pv = new StringBuilder();
			for (int i = 0; i < move.length - 1; i++) {
				pv.append(Transformation.numberToMove(move[i])).append(" ");
			}
			Logging.printLine("info depth " + (move.length - 1) + " score cp " + move[move.length - 1] + " nodes "
			      + (board.getSearch().getNodes() + board.getSearch().getAbortedNodes()) + " nps " + 1000 * (board.getSearch().getNodes() + board.getSearch().getAbortedNodes())
			                                                                                         / (timeUsed > 0 ? timeUsed : 1)
			      + " hashfull " + lowerBoundsTable.getFill() + " time " + (System.currentTimeMillis() - time) + " pv " + pv);
		} else {
			StringBuilder pv = new StringBuilder(praefix);
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
			Logging.printLine(praefix + "Node count: " + Transformation.nodeCountOutput(((board.getSearch().getNodes()
					+ board.getSearch().getAbortedNodes()))) + "(" + Transformation.nodeCountOutput(board.getSearch().getNodes())
					+ ")" + ". Q-nodes: " + Transformation.nodeCountOutput(board.getSearch().getQNodes()) + ". Time used: "
					+ Transformation.timeUsedOutput((System.currentTimeMillis() - time)));
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