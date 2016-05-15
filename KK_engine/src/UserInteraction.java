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
					test.makeMove(command);
					test.printBoard();
					test.changeToMove();
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
