package engineIO;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import engine.Board;
import engine.Evaluation;
import engine.MateFinderThread;
import engine.MoveGenerator;
import engine.MultiThreadSearch;
import engine.Node;
import engine.NonLosingThread;
import engine.Search;

/**
 * 
 * @author Anon
 * 
 */
public final class UCI {
	
	public static boolean uci = true;
	
	public static String logfile = "";
	
	private static int baseTime = 100;
	private static int incTime = 2;
	private static int minLeft = 20;
	private static int kingSafety = 10;
	private static int dynamism = 10;

	private static boolean threadFinished = false;

	private static ExecutorService executor = Executors.newFixedThreadPool(5);
	static String engineName = "Koedem";
	static Board board = new Board();
	private static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		uciCommunication();
		System.exit(0);
	}
	
	public static void uciCommunication() {
		Logging.setup();
		String command = "";
		Logging.printLine(engineName + " by Tom Marvolo.");
		while (!command.equals("quit")) {
			command = sc.nextLine();
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
				ArrayList<Integer> moves = MoveGenerator.collectMoves(board, board.getToMove(), movesSize);
				if (moves.get(0) == -1) {
					Logging.printLine("Illegal position.");
					continue;
				}
				for (Integer move : moves) {
					Logging.printLine(Transformation.numberToMove(move));
				}
			} else if (command.equals("find mate")) {
				mateFinder(board, true);
			} else if (command.equals("print legal captures")) {
				ArrayList<Integer> captures = MoveGenerator.collectCaptures(board, board.getToMove());
				if (captures.size() == 0) {
					Logging.printLine("There are no legal captures.");
					continue;
				}
				if (captures.get(0) == -1) {
					Logging.printLine("Illegal position.");
					continue;
				}
				for (Integer capture : captures) {
					Logging.printLine(Transformation.numberToMove(capture));
				}
			} else if (command.equals("q search")) {
				ArrayList<Integer> reversePV = Search.qSearch(board, board.getToMove(), -30000, 30000);
				Logging.printLine(Integer.toString(reversePV.get(0)));
				reversePV.remove(0);
				StringBuilder pv = new StringBuilder();
				for (Integer capture : reversePV) {
					pv.append(Transformation.numberToMove(capture)).append(" ");
				}
				pv.append("Node count: ").append(board.nodes);
				Logging.printLine(pv.toString());
				board.nodes = 0;
			} else if (command.equals("evaluate")) {
				Logging.printLine(Integer.toString(Evaluation.evaluation(board, board.getToMove(), -30000)));
			} else if (command.equals("Hashtable")) {
				for (Node value : board.getHashTable().values()) {
					value.print();
				}
			} else if (command.equals("print bitboard")) {
				board.bitboard.printBitBoard();
			}
		}
		Logging.close();
	}

    private static void inputUCINewGame() {
		board = new Board();
	}

	private static void inputSetOption(String command) {
		String[] parameters = command.split(" ");
		switch (parameters[2]) {
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
		}
	}

	public static void inputUCI() {
		Logging.printLine("id name " + engineName);
		Logging.printLine("id author Tom Marvolo");
		
		Logging.printLine("option name BaseTime type spin default 100 min 1 max 10000");
		Logging.printLine("option name IncTime type spin default 5000 min 1 max 10000");
		Logging.printLine("option name MinTime type spin default 500 min 1 max 10000");
		Logging.printLine("option name KingSafety type spin default 10 min 1 max 100");
		Logging.printLine("option name Dynamism type spin default 10 min 1 max 100");
		
		Logging.printLine("uciok");
	}
	
	public static void inputIsReady() {
		Logging.printLine("readyok");
	}
	
	public static void inputPosition(String input) {
		String command = input.substring(9);
		String[] parameters = command.split(" ");
		for (int i = 0; i < parameters.length; i++) {
			switch (parameters[i]) {
				case "startpos":
					board = new Board();
					break;
				case "fen":
					StringBuilder fen = new StringBuilder();
					for (int j = 0; j < 6; j++) {
						fen.append(parameters[i + 1 + j]).append(" ");
					}
					board = new Board(fen.toString());
					i += 6;
					break;
				case "moves":
					board.getHashTable().clear();
					i++;
					for (int j = 0; j < (parameters.length - i); j++) {
						Node node = new Node(board, 0, 0, 0, board.getToMove());
						board.makeMove(parameters[i + j]);
					}
					break;
			}
		}
	}
	
	public static void inputGo(String command) {
		MultiThreadSearch thread = null;
		
		if (command.contains("depth")) {
			String[] parameters = command.split(" ");
			int depth = Integer.parseInt(parameters[2]);
			thread =  new MultiThreadSearch(board, depth, 1, true, 2000000000);
		} else if (command.contains("infinite")) {
			thread =  new MultiThreadSearch(board, 100, 1, true, 2000000000);
		} else {
			String[] parameters = command.split(" ");
			int searchTime = 0;
			if (board.getToMove()) {
				for (int i = 1; i < parameters.length; i++) {
					if (parameters[i].equals("wtime")) {
						searchTime += Integer.parseInt(parameters[i + 1]) / baseTime;
					} else if (parameters[i].equals("winc")) {
						if (searchTime + Integer.parseInt(parameters[i + 1]) / incTime < searchTime * baseTime / minLeft) {
							searchTime += Integer.parseInt(parameters[i + 1]) / incTime;
						} else {
							searchTime *= baseTime / minLeft;
						}
					}
				}
			} else {
				for (int i = 1; i < parameters.length; i++) {
					if (parameters[i].equals("btime")) {
						searchTime += Integer.parseInt(parameters[i + 1]) / baseTime;
					} else if (parameters[i].equals("binc")) {
						if (searchTime + Integer.parseInt(parameters[i + 1]) / incTime < searchTime * baseTime / minLeft) {
							searchTime += Integer.parseInt(parameters[i + 1]) / incTime;
						} else {
							searchTime *= baseTime / minLeft;
						}
					}
				}
			}
			Logging.printLine("Trying to use " + searchTime + "ms + finishing current ply.");
			thread =  new MultiThreadSearch(board, 100, 1, true, searchTime);
		}
		
		UCI.setThreadFinished(false);
		Future<int[]> future = executor.submit(thread);
		
		
		int[] move = null;
		
		String stop = "";
		while (true) {
			if (future.isDone()) {
				try {
					move = future.get();
				} catch (Exception e) {
					int[] movesSize = new int[6];
					ArrayList<Integer> moves = MoveGenerator.collectMoves(board, board.getToMove(), movesSize);
					Logging.printLine("bestmove " + board.bestmove);
				}
				break;
			} else {
				if (sc.hasNextLine()) {
					UCI.setThreadFinished(true);
				}
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void mateFinder(Board board, boolean logging) {
		NonLosingThread nonLosingMoves = new NonLosingThread(board, 20, false, logging);
		NonLosingThread aggressiveNonLosing = new NonLosingThread(board, 30, true, logging);
		MateFinderThread mateFinder = new MateFinderThread(board, 20, false, logging);
		MateFinderThread aggressiveMateFinder = new MateFinderThread(board, 30, true, logging);
		long time = System.currentTimeMillis();
		threadFinished = false;
		Future<int[]>[] future = new Future[4];
		future[0] = executor.submit(mateFinder);
		future[1] = executor.submit(nonLosingMoves);
		future[2] = executor.submit(aggressiveMateFinder);
		future[3] = executor.submit(aggressiveNonLosing);
		int[][] move = new int[4][];
		boolean keepGoing = true;
		try {
			while(keepGoing) {
				if (threadFinished) {
					for (int j = 0; j < 4; j++) {
						if (future[j].isDone()) {
							move[j] = future[j].get();
							if (move[j] != null && move[j][move[j].length - 1] != 0) {
								keepGoing = false;
								break;
							} else {
								move[j] = null;
							}
						}
					}
				} else if (future[0].isDone() && future[1].isDone() && future[2].isDone() && future[3].isDone()) {
					if (logging) {
						Logging.printLine("No forced mate for either side.");
					}
					break;
				} else {
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
		}
		int[] correctMove;
		if (move[0] != null) {
			correctMove = move[0];
		} else if (move[1] != null) {
			correctMove = move[1];
		} else if (move[2] != null) {
			correctMove = move[2];
		} else if (move[3] != null) {
			correctMove = move[3];
		} else {
			return;
		}
		Logging.printLine("");
		printEngineOutput("MateFinder found mate: ", correctMove, board, board.getToMove(), time);
	}
	
	public static boolean isThreadFinished() {
		return threadFinished;
	}

	public static void setThreadFinished(boolean threadFinished) {
		UCI.threadFinished = threadFinished;
	}
	
	public static void printEngineOutput(String praefix, int[] move, Board board, boolean toMove, long time) {
		if (UCI.uci) {
			StringBuilder pv = new StringBuilder();
			for (int i = 0; i < move.length - 1; i++) {
				pv.append(Transformation.numberToMove(move[i])).append(" ");
			}
			Logging.printLine("info depth " + (move.length - 1) + " score cp " + move[move.length - 1] + " nodes " 
					+ board.nodes + " time " + (System.currentTimeMillis() - time) + " pv " 
					+ pv);
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
			Logging.printLine(praefix + "Node count: " + Transformation.nodeCountOutput(((board.nodes
					+ board.abortedNodes))) + "(" + Transformation.nodeCountOutput(board.nodes)
					+ ")" + ". Q-nodes: " + Transformation.nodeCountOutput(board.getqNodes()) + ". Time used: "
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
}