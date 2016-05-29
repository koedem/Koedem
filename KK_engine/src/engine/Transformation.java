package engine;

/**
 * 
 * @author Anon
 *
 */
public final class Transformation {

	/**
	 * Transform the board representation of the piece into a String one for output.
	 * 
	 * @param piece : number between -6 and +6 used as representation of a piece
	 * @return String-representation of the piece
	 */
	public static String numberToPiece(int piece, Board board) {
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
	public static String numberToMove(int move, Board board) {
		String moveText = "";
		moveText = numberToPiece(move / 4096, board);
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
	 * @param piece Should be a char containing a the first letter of a piece.
	 * @return The numerical value of the piece.
	 */
	public static byte stringToPiece(String piece) {
		if ("P".equals(piece)) {
			return 1;
		} else if ("N".equals(piece)) {
			return 2;
		} else if ("B".equals(piece)) {
			return 3;
		} else if ("R".equals(piece)) {
			return 4;
		} else if ("Q".equals(piece)) {
			return 5;
		} else if ("K".equals(piece)) {
			return 6;
		} else if ("p".equals(piece)) {
			return -1;
		} else if ("n".equals(piece)) {
			return -2;
		} else if ("b".equals(piece)) {
			return -3;
		} else if ("r".equals(piece)) {
			return -4;
		} else if ("q".equals(piece)) {
			return -5;
		} else if ("k".equals(piece)) {
			return -6;
		} else {
			return 0;
		}
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
	
	public static String nodeCountOutput(long nodeCount) {
		long count = nodeCount;
		String output = "";
		if (nodeCount > 100000) {
			output = "kN";
			if (nodeCount > 100000000) {
				output = "mN";
				count /= 1000;
			}
			count /= 1000;
		} else {
			output = "N";
		}
		output = count + output;
		return output;
	}
	
	/**
	 * 
	 * @param time The time, given in milli seconds.
	 * @return Time in a human readable format.
	 */
	public static String timeUsedOutput(long time) {
		long timeUsed = time;
		if (time > 60000) {
			timeUsed /= 1000;
			if (time > 3600000) {
				timeUsed /= 60;
				if (time > 86400000) {
					timeUsed /= 60;
					return (timeUsed / 24) + "d " + (timeUsed % 24) + "h";
				}
				return (timeUsed / 60) + "h" + (timeUsed % 24) + "m";
			}
			return (timeUsed / 60) + "m " + (timeUsed % 24) + "s";
		}
		return (timeUsed / 1000) + "s " + (timeUsed % 1000) + "ms";
	}
	
	private Transformation() {
	}
}
