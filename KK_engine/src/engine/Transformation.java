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
		int squareNumber = Character.getNumericValue(square.charAt(1)) - 1;
		
		switch(square.charAt(0)) {
			case 'a': squareNumber += 0; break;
			case 'b': squareNumber += 8; break;
			case 'c': squareNumber += 16; break;
			case 'd': squareNumber += 24; break;
			case 'e': squareNumber += 32; break;
			case 'f': squareNumber += 40; break;
			case 'g': squareNumber += 48; break;
			case 'h': squareNumber += 56; break;
			default: squareNumber = -1; break;
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
		
		switch(square / 8) {
			case 0: squareText = "a"; break;
			case 1: squareText = "b"; break;
			case 2: squareText = "c"; break;
			case 3: squareText = "d"; break;
			case 4: squareText = "e"; break;
			case 5: squareText = "f"; break;
			case 6: squareText = "g"; break;
			case 7: squareText = "h"; break;
			default: return "x";
		}
		squareText += Integer.toString((square % 8) + 1);
		
		return squareText;
	}
	
	/**
	 * Transform move which is stored as number internally to a String
	 * 
	 * @param move Number of the move in internal representation
	 * @return Move as String (readable to human)
	 */
	public static String numberToMove(int move) {
		String moveText = "";
		moveText = numberToPiece(move / 4096);
		if (moveText.equals("P")) {
			moveText = "";
		}
		moveText += numberToSquare((move / 64) % 64);
		moveText += "-";
		moveText += numberToSquare(move % 64);
		return moveText;
	}
	
	/**
	 * 
	 * @param castlingRights Byte castling rights (saved in the board).
	 * @return String of format KQkq.
	 */
	public static String numberToCastling(byte castlingRights) {
		String castling = "";
		if ((castlingRights & 0x18) == 0x18) {
			castling += "K";
		}
		if ((castlingRights & 0x30) == 0x30) {
			castling += "Q";
		}
		if ((castlingRights & 0x3) == 0x3) {
			castling += "k";
		}
		if ((castlingRights & 0x6) == 0x6) {
			castling += "q";
		}
		return castling;
	}
	
	private Transformation() {
	}
}