package engine;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Anon
 *
 */
public final class UserInteraction {
	
	protected static boolean qSearch = true;
	
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
			if (command.contains("move")) {
					boolean gameOver = test.makeMove(command, sc);
					if (gameOver) {
						test.printBoard();
						System.out.println("You captured my King. Congratulations you won the game!");
						break;
					}
					test.printBoard();
					Node node = new Node(test, 0, 0, 0, test.getToMove());
					Evaluation.setNodeCount(0);
					Evaluation.setAbortedNodes(0);
					int[] move = null;
					int depthCap = 15;
					long time = System.currentTimeMillis();
					for (int i = 1; i <= depthCap; i++) {
						move = Search.negaMax(test, test.getToMove(), i, i, -30000, 30000);
						for (int j = 0; j < move.length - 1; j++) {
							System.out.print(Transformation.numberToMove(move[j]) + " ");
						}
						System.out.println(move[move.length - 1]);
						System.out.println("Node count: " + Transformation.nodeCountOutput((Evaluation.getNodeCount()
								+ Evaluation.getAbortedNodes())) + " (" 
								+ Transformation.nodeCountOutput(Evaluation.getNodeCount()) + ")" + ". Time used: "
								+ Transformation.timeUsedOutput((System.currentTimeMillis() - time)));
						
						if (System.currentTimeMillis() - time > 2000) {
							break;
						}
					}
					if (move[move.length - 1] == -9999) {
						test.printBoard();
						System.out.println("Mate, you win! \"newgame\" starts a new game.");
						continue;
					}
					test.makeMove(move[0]);
					node = new Node(test, 0, 0, 0, test.getToMove());
					test.printBoard();
					for (int i = 0; i < move.length - 1; i++) {
						System.out.print(Transformation.numberToMove(move[i]) + " ");
					}
					System.out.println(move[move.length - 1]);
					System.out.println("Node count: " + Evaluation.getNodeCount());
			} else if (command.equals("print")) {
				test.printBoard();
			} else if (command.equals("print legal moves")) {
				ArrayList<Integer> moves = MoveGenerator.collectMoves(test, test.getToMove());
				if (moves.get(0) == -1) {
					System.out.println("Illegal position.");
					continue;
				}
				for (Integer move : moves) {
					System.out.println(Transformation.numberToMove(move));
				}
			} else if (command.equals("print legal captures")) {
				ArrayList<Integer> captures = MoveGenerator.collectCaptures(test, test.getToMove());
				if (captures.size() == 0) {
					System.out.println("There are no legal captures.");
					continue;
				}
				if (captures.get(0) == -1) {
					System.out.println("Illegal position.");
					continue;
				}
				for (Integer capture : captures) {
					System.out.println(Transformation.numberToMove(capture));
				}
			} else if (command.contains("fen")) {
				test.setFENPosition(command);
				test.printBoard();
			} else if (command.equals("analyze")) {
				long time = System.currentTimeMillis();
				Evaluation.setNodeCount(0);
				Evaluation.setAbortedNodes(0);
				for (int i = 1; i < 50; i++) {
					int[] move = Search.negaMax(test, test.getToMove(), i, i, -30000, 30000);
					if (Math.abs(move[move.length - 1]) < 9000) {
						for (int j = 0; j < move.length - 1; j++) {
							if (j % 2 == 0) {
								System.out.print((j + 2) / 2 + ".");
							}
							System.out.print(Transformation.numberToMove(move[j]) + " ");
						}
						System.out.println(move[move.length - 1]);
						System.out.println("Node count: " + Transformation.nodeCountOutput(((Evaluation.getNodeCount()
					+ Evaluation.getAbortedNodes()))) + "("  + Transformation.nodeCountOutput( Evaluation.getNodeCount())
					+ ")" + ". Q-nodes: " + Transformation.nodeCountOutput(Search.qNodes) + ". Time used: "
								+ Transformation.timeUsedOutput((System.currentTimeMillis() - time)));
					} else {
						for (int j = 0; j < move.length - 2; j++) {
							if (j % 2 == 0) {
								System.out.print((j + 2) / 2 + ".");
							}
							System.out.print(Transformation.numberToMove(move[j]) + " ");
						}
						System.out.println(move[move.length - 1]);
						System.out.println(Transformation.nodeCountOutput(Evaluation.getNodeCount()) + ". Time used: " 
								+ Transformation.timeUsedOutput((System.currentTimeMillis() - time)));
						break;
					}
				}
			} else if (command.equals("q search")) {
				ArrayList<Integer> reversePV = Search.qSearch(test, test.getToMove(), -30000, 30000);
				System.out.println(reversePV.get(0));
				reversePV.remove(0);
				for (Integer capture : reversePV) {
					System.out.print(Transformation.numberToMove(capture) + " ");
				}
				System.out.println("Node count: " + Evaluation.getNodeCount());
				Evaluation.setNodeCount(0);
			} else if (command.equals("evaluate")) {
				System.out.println(Evaluation.evaluation(test, test.getToMove(), -30000));
			} else if (command.equals("newgame")) {
				test = new Board();
				test.printBoard();
			} else if (command.equals("Hashtable")) {
			  test.getHashTable();
			} else if (command.equals("materialOnly on")) {
				Evaluation.materialOnly = true;
			} else if (command.equals("materialOnly off")) {
				Evaluation.materialOnly = false;
			}
		}
		sc.close();
	}
	
	private UserInteraction() {
	}
}
