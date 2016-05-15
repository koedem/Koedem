/**
 * 
 * @author Kolja Kuehn
 *
 */
public class Board {

	protected byte[][] square = new byte[10][10];  // Pawn = 1, Knight = 2, Bishop = 3, Rook = 4, Queen = 5, King = 6;
									// White pieces get positive values, black pieces negative ones, empty squares a 0.
	private boolean toMove = true;
	
	/**
	 * Constructor, create new Board and setup the chess start position
	 */
	
	public Board() {
		createStartPosition();
	}
	
	/**
	 * set up the chess start position like this:
	 * 
	 *   12345678
	 * 8 rnbqkbnr
	 * 7 pppppppp
	 * 6 --------
	 * 5 --------
	 * 4 --------
	 * 3 --------
	 * 2 PPPPPPPP
	 * 1 RNBQKBNR
	 */
	
	public void createStartPosition() {
		square[1][1] = 4; 
		square[8][1] = 4; // white rooks on a1 and h1
		square[2][1] = 2;
		square[7][1] = 2; // white knights on b1 and g1
		square[3][1] = 3;
		square[6][1] = 3; // white bishops on c1 and f1
		square[4][1] = 5; // white queen on d1
		square[5][1] = 6; // white king on e1
		square[1][2] = 1;
		square[2][2] = 1;
		square[3][2] = 1;
		square[4][2] = 1;
		square[5][2] = 1;
		square[6][2] = 1;
		square[7][2] = 1;
		square[8][2] = 1; // white pawns on a2, b2, c2, d2, e2, f2, g2, h2
		
		square[1][8] = -4;
		square[8][8] = -4; // black rooks on a8 and h8
		square[2][8] = -2;
		square[7][8] = -2; // black knights on b8 and g8
		square[3][8] = -3;
		square[6][8] = -3; // black bishops on c8 and f8
		square[4][8] = -5; // black queen on d8
		square[5][8] = -6; // black king on e8
		square[1][7] = -1;
		square[2][7] = -1;
		square[3][7] = -1;
		square[4][7] = -1;
		square[5][7] = -1;
		square[6][7] = -1;
		square[7][7] = -1;
		square[8][7] = -1; // black pawns on a7, b7, c7, d7, e7, f7, g7, h7
		
		for (int i = 1; i < 9; i++) {
			for (int j = 3; j < 7; j++) {
				square[i][j] = 0;
			}
		}
	}
	 /**
	  * Print out the board, row by row starting at highest row.
	  * Each row we print file by file from lowest to highest.
	  */
	
	public void printBoard() {
		for (int i = 8; i > 0; i--) {
			
			for (int j = 1; j < 9; j++) {
				System.out.print(Transformation.numberToPiece(square[j][i]) + " ");
			}
			
			System.out.println();
		}
	}
	
	/**
	 * 
	 * @param file : file of the square
	 * @param row : row of the square
	 * @return the value of the square
	 */
	
	public byte getSquare(int file, int row) {
		return square[file][row];
	}
	
	/**
	 * Execute the move on the board.
	 * @param move : the move stored as string. Has to be "decoded" first.
	 */
	
	public void makeMove(String move) {
		if (move.charAt(2) == '-') {
			int startSquare = Transformation.squareToNumber(move.substring(0, 2));
			int endSquare = Transformation.squareToNumber(move.substring(3, 5));
			square[endSquare / 10][endSquare % 10] = square[startSquare / 10][startSquare % 10];
			square[startSquare / 10][startSquare % 10] = 0;
		} else {
			System.out.println("Illegal Move. Try again.");
		}
	}
	
	/**
	 * 
	 * @return who to move it is.
	 */
	
	public boolean getToMove() {
		return toMove;
	}
	
	/**
	 * 
	 * @param newToMove : set the toMove parameter
	 */
	
	public void setToMove(boolean newToMove) {
		this.toMove = newToMove;
	}
	
	/**
	 * negate the toMove parameter
	 */
	
	public void changeToMove() {
		this.toMove = (!toMove);
	}
}
