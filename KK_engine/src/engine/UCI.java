package engine;

import java.util.Scanner;

/**
 * 
 * @author Anon
 * 
 */
public final class UCI {

	static String engineName = "KK_engine";
	static Board board = new Board();
	
	public static void uciCommunication() {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		String command = input.nextLine();
		
		if (command.equals("uci")) {
			inputUCI();
		} else if (command.startsWith("setoption")) {
			inputSetOption();
		} else if (command.equals("isready")) {
			inputIsReady();
		} else if (command.equals("ucinewgame")) {
			inputUCINewGame();
		} else if (command.startsWith("position")) {
			inputPosition(command);
		} else if (command.equals("go")) {
			inputGo();
		} else if (command.equals("print")) {
			board.printBoard();
		}
	}
	
	private static void inputUCINewGame() {
		// TODO Auto-generated method stub
		
	}

	private static void inputSetOption() {
		// TODO Auto-generated method stub
		
	}

	public static void inputUCI() {
		System.out.println("id name " + engineName);
		System.out.println("id author Tom Marvolo");
		// TODO: options
		System.out.println("uciok");
	}
	
	public static void inputIsReady() {
		System.out.println("readyok");
	}
	
	public static void inputPosition(String input) {
		String command = input.substring(9).concat(" ");
		if (input.contains("startpos ")) {
			command = command.substring(9);
			board.setFENPosition("fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq");
		}
	}
	
	public static void inputGo() {
		
	}
	
	private UCI() {
	}
}
