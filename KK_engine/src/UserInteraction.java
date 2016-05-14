import java.util.Scanner;


public class UserInteraction {
	

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		Board test = new Board();
		test.printBoard();
		
		String command = "";
		
		while (!(command.equals("quit"))) {
			command = sc.nextLine();
			if (command.contains("-")) {
				if (command.length() == 5) {
					test.makePawnMove(command);
					test.printBoard();
				} else {
					System.out.println("Illegal Move. Try again.");
				}
			} else if (command.equals("print")) {
				test.printBoard();
			} else if (command.equals("print legal moves")) {
				int[] moves = MoveGenerator.collectMoves(test, true);
				for (int i = 0; i < moves[99]; i++) {
					System.out.println(Transformation.numberToMove(moves[i]));
				}
			}
		}
		sc.close();
	}
	
	
}
