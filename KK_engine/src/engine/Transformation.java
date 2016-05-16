package engine;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public final class Transformation {

	/**
	 * Transform the board representation of the piece into a String one for output.
	 * 
	 * @param piece : number between -6 and +6 used as representation of a piece
	 * @return String-representation of the piece
	 */
	public static String numberToPiece(int piece) {
		switch (piece) {
			case 6: return "K"; 
			case 5: return "Q"; 
			case 4: return "R"; 
			case 3: return "B";
			case 2: return "N"; 
			case 1: return "P";
			case 0: return "-"; 
			case -6: return "k"; 
			case -5: return "q"; 
			case -4: return "r";
			case -3: return "b";
			case -2: return "n";
			case -1: return "p"; 
			default: return "X"; 

		}
	}
	
	/**
	 * Transform the String representation of a square from input into an internal one.
	 * 
	 * @param square : Input square representation as String
	 * @return internal representation as number
	 */
	public static int squareToNumber(String square) {
		int squareNumber = Character.getNumericValue(square.charAt(1));
		
		switch(square.charAt(0)) {
			case 'a': squareNumber += 10; break;
			case 'b': squareNumber += 20; break;
			case 'c': squareNumber += 30; break;
			case 'd': squareNumber += 40; break;
			case 'e': squareNumber += 50; break;
			case 'f': squareNumber += 60; break;
			case 'g': squareNumber += 70; break;
			case 'h': squareNumber += 80; break;
			default: squareNumber = 0; break;
		}	
		
		return squareNumber;
	}
	
	/**
	 * Transform internal square representation to a String for output.
	 * 
	 * @param square : a number, used to internally store squares
	 * @return square in String form
	 */
	public static String numberToSquare(int square) {
		String squareText = "";
		
		switch(square / 10) {
			case 1: squareText = "a"; break;
			case 2: squareText = "b"; break;
			case 3: squareText = "c"; break;
			case 4: squareText = "d"; break;
			case 5: squareText = "e"; break;
			case 6: squareText = "f"; break;
			case 7: squareText = "g"; break;
			case 8: squareText = "h"; break;
			default: return "x";
		}
		squareText += Integer.toString(square % 10);
		
		return squareText;
	}
	
	/**
	 * Transform move which is stored as number internally to a String
	 * 
	 * @param move : number of the move in internal representation
	 * @return move as String (readable to human)
	 */
	public static String numberToMove(int move) {
		String moveText = "";
		moveText = numberToPiece(move / 10000);
		if (moveText.equals("P")) {
			moveText = "";
		}
		moveText += numberToSquare((move / 100) % 100);
		moveText += "-";
		moveText += numberToSquare(move % 100);
		return moveText;
	}
	
	private Transformation() {
	}
}
