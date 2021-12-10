package Main.engineIO;

import Main.engine.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Anon
 */
public final class UCI {
	
	public static boolean uci = true;

	public static boolean shuttingDown = false;

	public static final int     TT_SIZE_MB = 4096;
	public static  boolean logging       = false;

	private static int threadCount = 1;

	static         String            engineName       = "Koedem";
	static         BoardInterface    board            = new Board();
	static         Perft             perft            = new Perft(board);
	
	public static void main(String[] args) {
		Logging.setup();
		Logging.printLine(engineName + " by Kolja Kühn.");
		for (int i = 1; i < 8; i++) {
			perft.basePerft(i);
		}
		shuttingDown = true;
		Logging.close();

		System.exit(0);
	}
	
	private static void inputPosition(String input) {
		String command = input.substring(9);

		String[] parameters = command.split(" ");
		for (int i = 0; i < parameters.length; i++) {
			switch (parameters[i]) {
				case "startpos":
					board.resetBoard();
					break;
				case "fen":
					StringBuilder fen = new StringBuilder();
					for (int j = 0; j < 6; j++) {
						fen.append(parameters[i + 1 + j]).append(" ");
					}
					board.setFENPosition(fen.toString());
					i += 6;
					break;
				case "moves":
					i++;
					for (int j = 0; j < (parameters.length - i); j++) {
						board.makeMove(parameters[i + j]);
					}
					break;
			}
		}
	}
	
	private UCI() {
	}
}