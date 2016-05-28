package engine;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public final class UserInteraction {
	
	public static boolean qSearch = false;
	
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
					boolean gameOver = test.makeMove(command, sc);
					if (gameOver) {
						test.printBoard();
						System.out.println("You captured my King. Congratulations you won the game!");
						break;
					}
					test.changeToMove();
					// int move = Search.makeRandomMove(test, test.getToMove());
					Evaluation.setNodeCount(0);
					int[] move = null;
					int depthCap = 15;
					long time = System.currentTimeMillis();
					for (int i = 1; i <= depthCap; i++) {
						move = Search.negaMax(test, test.getToMove(), i, i, 30000);
						for (int j = 0; j < move.length - 1; j++) {
							System.out.print(Transformation.numberToMove(move[j], test) + " ");
						}
						System.out.println(move[move.length - 1]);
						System.out.println("Node count: " + Transformation.nodeCountOutput((Evaluation.getNodeCount()
								+ Evaluation.getAbortedNodes())) + " (" 
								+ Transformation.nodeCountOutput(Evaluation.getNodeCount()) + ")" + ". Time used: "
								+ Transformation.timeUsedOutput((System.currentTimeMillis() - time)));
						
						if (System.currentTimeMillis() - time > 1000) {
							break;
						}
					}
					if (move[move.length - 1] == -9999) {
						test.printBoard();
						System.out.println("Mate, you win! \"newgame\" starts a new game.");
						continue;
					}
					test.makeMove(move[0]);
					test.changeToMove();
					test.printBoard();
					if (test.getPiecesLeft() < 25) {
						qSearch = true;
					}
					for (int i = 0; i < move.length - 1; i++) {
						System.out.print(Transformation.numberToMove(move[i], test) + " ");
					}
					System.out.println(move[move.length - 1]);
					System.out.println("Node count: " + Evaluation.getNodeCount());
				} else {
					System.out.println("Illegal Move. Try again.");
				}
			} else if (command.equals("print")) {
				test.printBoard();
			} else if (command.equals("print legal moves")) {
				ArrayList<Integer> moves = MoveGenerator.collectMoves(test, test.getToMove());
				for (Integer move : moves) {
					System.out.println(Transformation.numberToMove(move, test));
				}
			} else if (command.equals("print legal captures")) {
				ArrayList<Integer> captures = MoveGenerator.collectCaptures(test, test.getToMove());
				for (Integer capture : captures) {
					System.out.println(Transformation.numberToMove(capture, test));
				}
			} else if (command.contains("fen")) {
				test.setFENPosition(command);
				test.printBoard();
			} else if (command.equals("analyze")) {
				long time = System.currentTimeMillis();
				Evaluation.setNodeCount(0);
				Evaluation.setAbortedNodes(0);
				for (int i = 1; i < 15; i++) {
					int[] move = Search.negaMax(test, test.getToMove(), i, i, 30000);
					if (Math.abs(move[move.length - 1]) < 9000) {
						for (int j = 0; j < move.length - 1; j++) {
							if (j % 2 == 0) {
								System.out.print((j + 2) / 2 + ".");
							}
							System.out.print(Transformation.numberToMove(move[j], test) + " ");
						}
						System.out.println(move[move.length - 1]);
						System.out.println("Node count: " + Transformation.nodeCountOutput((Evaluation.getNodeCount()
								+ Evaluation.getAbortedNodes())) + " (" 
								+ Transformation.nodeCountOutput(Evaluation.getNodeCount()) + ")" + ". Time used: "
								+ Transformation.timeUsedOutput((System.currentTimeMillis() - time)));
					} else {
						for (int j = 0; j < move.length - 3; j++) {
							if (j % 2 == 0) {
								System.out.print((j + 2) / 2 + ".");
							}
							System.out.print(Transformation.numberToMove(move[j], test) + " ");
						}
						System.out.println(move[move.length - 1]);
						System.out.println(Transformation.nodeCountOutput(Evaluation.getNodeCount()) + ". Time used: " 
								+ Transformation.timeUsedOutput((System.currentTimeMillis() - time)));
						break;
					}
				}
			} else if (command.equals("q search")) {
				ArrayList<Integer> reversePV = Search.qSearch(test, test.getToMove(), 30000);
				System.out.println(reversePV.get(0));
				reversePV.remove(0);
				for (Integer capture : reversePV) {
					System.out.print(Transformation.numberToMove(capture, test) + " ");
				}
				System.out.println("Node count: " + Evaluation.getNodeCount());
				Evaluation.setNodeCount(0);
			} else if (command.equals("evaluate")) {
				System.out.println(Evaluation.evaluation(test, test.getToMove(), -30000));
			} else if (command.equals("newgame")) {
				test = new Board();
				test.printBoard();
				qSearch = false;
			}
		}
		sc.close();
	}
	
	private UserInteraction() {
	}
}
