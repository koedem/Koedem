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
					Search.setNodeCount(0);
					int[] move = Search.negaMax(test, test.getToMove(), 4, 4);
					test.makeMove(move[0]);
					test.changeToMove();
					test.printBoard();
					for (int i = 0; i < move.length - 1; i++) {
						System.out.print(Transformation.numberToMove(move[i]) + " ");
					}
					System.out.println(move[move.length - 1]);
					System.out.println("Node count: " + Search.getNodeCount());
				} else {
					System.out.println("Illegal Move. Try again.");
				}
			} else if (command.equals("print")) {
				test.printBoard();
			} else if (command.equals("print legal moves")) {
				ArrayList<Integer> moves = MoveGenerator.collectMoves(test, test.getToMove());
				for (Integer move : moves) {
					System.out.println(Transformation.numberToMove(move));
				}
			} else if (command.equals("print legal captures")) {
				ArrayList<Integer> captures = MoveGenerator.collectCaptures(test, test.getToMove());
				for (Integer capture : captures) {
					System.out.println(Transformation.numberToMove(capture));
				}
			} else if (command.contains("fen")) {
				test.setFENPosition(command);
			} else if (command.equals("analyze")) {
				long time = System.currentTimeMillis();
				for (int i = 1; i < 10; i++) {
					Search.setNodeCount(0);
					int[] move = Search.negaMax(test, test.getToMove(), i, i);
					for (int j = 0; j < move.length - 1; j++) {
						System.out.print(Transformation.numberToMove(move[j]) + " ");
					}
					System.out.println(move[move.length - 1]);
					System.out.println("Node count: " + Search.getNodeCount() + ". Time used: " 
							+ (System.currentTimeMillis() - time));
				}
			} else if (command.equals("q search")) {
				ArrayList<Integer> reversePV = Search.qSearch(test, test.getToMove());
				System.out.println(reversePV.get(0));
				reversePV.remove(0);
				for (Integer capture : reversePV) {
					System.out.print(Transformation.numberToMove(capture) + " ");
				}
				System.out.println("Node count: " + Search.getNodeCount());
				Search.setNodeCount(0);
			}
		}
		sc.close();
	}
	
	private UserInteraction() {
	}
}
