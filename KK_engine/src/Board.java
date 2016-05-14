
public class Board {

	protected int[][] square = new int[10][10];  // Pawn = 1, Knight = 2, Bishop = 3, Rook = 4, Queen = 5, King = 6;
									// White pieces get positive values, black pieces negative ones, empty squares a 0.
	
	public Board() {
		createStartPosition();
	}
	
	public void createStartPosition() {
		square[1][1] = square[8][1] = 4;
		square[2][1] = square[7][1] = 2;
		square[3][1] = square[6][1] = 3;
		square[4][1] = 5;
		square[5][1] = 6;
		square[1][2] = square[2][2] = square[3][2] = square[4][2] = square[5][2]
				= square[6][2] = square[7][2] = square[8][2] = 1;
		
		square[1][8] = square[8][8] = -4;
		square[2][8] = square[7][8] = -2;
		square[3][8] = square[6][8] = -3;
		square[4][8] = -5;
		square[5][8] = -6;
		square[1][7] = square[2][7] = square[3][7] = square[4][7] = square[5][7]
				= square[6][7] = square[7][7] = square[8][7] = -1;
		
		for (int i = 1; i < 9; i++) {
			for (int j = 3; j < 7; j++) {
				square[i][j] = 0;
			}
		}
	}
	
	public void printBoard() {
		for (int i = 8; i > 0; i--) {
			
			for (int j = 1; j < 9; j++) {
				System.out.print(Transformation.numberToPiece(square[j][i]) + " ");
			}
			
			System.out.println();
		}
	}
	
	public int getSquare(int file, int row) {
		return square[file][row];
	}
	
	public void makePawnMove(String move) {
		if (move.charAt(2) == '-') {
			int startSquare = Transformation.squareToNumber(move.substring(0, 2));
			int endSquare = Transformation.squareToNumber(move.substring(3, 5));
			square[endSquare / 10][endSquare % 10] = square[startSquare / 10][startSquare % 10];
			square[startSquare / 10][startSquare % 10] = 0;
		} else {
			System.out.println("Illegal Move. Try again.");
		}
	}
	
}
