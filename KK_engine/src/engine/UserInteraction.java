package engine;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public final class UserInteraction {
	
	/**
	 * Interactive loop for user interaction
	 * 
	 * @param args : not used
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		Board test = new Board();
		test.printBoard();
		
		String command = "";
		
		while (!(command.equals("quit"))) {
			command = sc.nextLine();
			if (command.contains("-")) {
				if (command.length() == 5) {
					boolean gameOver = test.makeMove(command);
					if (gameOver) {
						test.printBoard();
						System.out.println("You captured my King. Congratulations you won the game!");
						break;
					}
					test.changeToMove();
					// int move = Search.makeRandomMove(test, test.getToMove());
					Search.nodeCount = 0;
					short[] move = Search.negaMax(test, test.getToMove(), 4, 4);
					test.makeMove(move[0]);
					test.changeToMove();
					test.printBoard();
					for (int i = 0; i < move.length - 1; i++) {
						System.out.print(Transformation.numberToMove(move[i]) + " ");
					}
					System.out.println(move[move.length - 1]);
					System.out.println("Node count: " + Search.nodeCount);
				} else {
					System.out.println("Illegal Move. Try again.");
				}
			} else if (command.equals("print")) {
				test.printBoard();
			} else if (command.equals("print legal moves")) {
				ArrayList<Short> moves = MoveGenerator.collectMoves(test, test.getToMove());
				for (Short move : moves) {
					System.out.println(Transformation.numberToMove(move));
				}
			} else if (command.contains("fen")) {
				test.setFENPosition(command);
			} else if (command.equals("analyze")) {
				long time = System.currentTimeMillis();
				for (int i = 1; i < 10; i++) {
					Search.nodeCount = 0;
					short[] move = Search.negaMax(test, test.getToMove(), i, i);
					for (int j = 0; j < move.length - 1; j++) {
						System.out.print(Transformation.numberToMove(move[j]) + " ");
					}
					System.out.println(move[move.length - 1]);
					System.out.println("Node count: " + Search.nodeCount + ". Time used: " 
							+ (System.currentTimeMillis() - time));
				}
			}
		}
		sc.close();
	}
	
	private UserInteraction() {
	}
}
