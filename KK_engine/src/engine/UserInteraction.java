package engine;

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
					int[] move = Search.negaMax(test, test.getToMove(), 4, 4);
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
				int[] moves = MoveGenerator.collectMoves(test, test.getToMove());
				for (int i = 0; i < moves[99]; i++) {
					System.out.println(Transformation.numberToMove(moves[i]));
				}
			}
		}
		sc.close();
	}
	
	private UserInteraction() {
	}
}
