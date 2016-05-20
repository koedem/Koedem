package engine;

/**
 * 
 * @author Kolja Kuehn
 *
 */
public class Board {

	protected byte[][] square = new byte[10][10];  // Pawn = 1, Knight = 2, Bishop = 3, Rook = 4, Queen = 5, King = 6;
									// White pieces get positive values, black pieces negative ones, empty squares a 0.
	private boolean toMove = true;
	private byte castlingRights = 0; // 00abcdef a = Ra1, b = Ke1, c = Rh1, d = ra8, e = ke8, f = rh8
										// 1 means the piece hasn't moved yet.
	private byte materialCount = 0;
	
	/**
	 * Constructor, create new Board and setup the chess start position
	 */
	public Board() {
		setFENPosition("fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq"); // fen of start position
	}
	
	public void setFENPosition(String fen) {
		String position = fen.substring(4);
		byte file = 1;
		byte row = 8;
		for (int i = 0; i < position.length(); i++) {
			if (position.charAt(i) == 'k') {
				this.square[file][row] = -6;
				file++;
			} else if (position.charAt(i) == 'q') {
				this.square[file][row] = -5;
				file++;
			} else if (position.charAt(i) == 'r') {
				this.square[file][row] = -4;
				file++;
			} else if (position.charAt(i) == 'b') {
				this.square[file][row] = -3;
				file++;
			} else if (position.charAt(i) == 'n') {
				this.square[file][row] = -2;
				file++;
			} else if (position.charAt(i) == 'p') {
				this.square[file][row] = -1;
				file++;
			} else if (position.charAt(i) == 'P') {
				this.square[file][row] = 1;
				file++;
			} else if (position.charAt(i) == 'N') {
				this.square[file][row] = 2;
				file++;
			} else if (position.charAt(i) == 'B') {
				this.square[file][row] = 3;
				file++;
			} else if (position.charAt(i) == 'R') {
				this.square[file][row] = 4;
				file++;
			} else if (position.charAt(i) == 'Q') {
				this.square[file][row] = 5;
				file++;
			} else if (position.charAt(i) == 'K') {
				this.square[file][row] = 6;
				file++;
			} else if (position.charAt(i) == '/') {
				row--;
				file = 1;
			} else if (position.charAt(i) == ' ') {
				if (position.charAt(i + 1) == 'w') {
					this.toMove = true;
				} else if (position.charAt(i + 1) == 'b') {
					this.toMove = false;
				} else {
					throw new IllegalArgumentException();
				} if (position.charAt(i + 2) == ' ') {
					setCastlingRights(position.substring(i + 3));
				} else {
					throw new IllegalArgumentException();
				}
				break;
			} else {
				int emptySquares = Character.getNumericValue(position.charAt(i));
				for (int j  = 0; j < emptySquares; j++) {
					this.square[file][row] = 0;
					file++;
				}
			}
		}
	}
	
	private void setCastlingRights(String castling) {
		this.castlingRights = 0;
		for (int i = 0; i < castling.length(); i++) {
			if (castling.charAt(i) == 'K') {
				this.castlingRights = (byte) (this.castlingRights | 0x18);
			} else if (castling.charAt(i) == 'Q') {
				this.castlingRights = (byte) (this.castlingRights | 0x30);
			} else if (castling.charAt(i) == 'k') {
				this.castlingRights = (byte) (this.castlingRights | 0x3);
			} else if (castling.charAt(i) == 'q') {
				this.castlingRights = (byte) (this.castlingRights | 0x6);
			}
		}
	}
	
	 /**
	  * Print out the board, row by row starting at highest row.
	  * Each row we print file by file from lowest to highest.
	  */
	public void printBoard() {
		System.out.println();
		for (int i = 8; i > 0; i--) {
			
			for (int j = 1; j < 9; j++) {
				System.out.print(Transformation.numberToPiece(square[j][i]) + " ");
			}
			
			System.out.println();
		}
		System.out.println();
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
	 * Execute the move on the board. Check whether a king was captured in which case the game is over.
	 * @param move : the move stored as string. Has to be "decoded" first.
	 * @return whether the game ends or not
	 */
	public boolean makeMove(String move) {
		boolean gameEnd = false;
		if (move.charAt(2) == '-') {
			int startSquare = Transformation.squareToNumber(move.substring(0, 2));
			int endSquare = Transformation.squareToNumber(move.substring(3, 5));
			if (Math.abs(square[endSquare / 10][endSquare % 10]) == 6) {
				gameEnd = true;
			}
			if (startSquare == 51 && square[5][1] == 6) {
				if (endSquare == 71) {
					square[7][1] = 6;
					square[5][1] = 0;
					square[6][1] = 4; // rook move in castling
					square[8][1] = 0;
				} else if (endSquare == 31) {
					square[3][1] = 6;
					square[5][1] = 0;
					square[4][1] = 4; // rook move in castling
					square[1][1] = 0;
				}
			} else if (startSquare == 58 && square[5][8] == -6) {
				if (endSquare == 78) {
					square[7][8] = -6;
					square[5][8] = 0;
					square[6][8] = -4; // rook move in castling
					square[8][8] = 0;
				} else if (endSquare == 38) {
					square[3][8] = -6;
					square[5][8] = 0;
					square[4][8] = -4; // rook move in castling
					square[1][8] = 0;
				}
			} else {
				square[endSquare / 10][endSquare % 10] = square[startSquare / 10][startSquare % 10];
				square[startSquare / 10][startSquare % 10] = 0;
			}
		} else {
			System.out.println("Illegal Move. Try again.");
		}
		return gameEnd;
	}
	
	/**
	 * This method takes a move encoded as int and plays that move.
	 * 
	 * @param move : the move we play
	 */
	public void makeMove(int move) {
		int moveWithoutPiece = move % 10000;
		square[(moveWithoutPiece / 10) % 10][moveWithoutPiece % 10] 
				= square[moveWithoutPiece / 1000][(moveWithoutPiece / 100) % 10];
		square[moveWithoutPiece / 1000][(moveWithoutPiece / 100) % 10] = 0;
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

	/**
	 * This method undoes the given move.
	 * 
	 * @param move : move which we want to undo
	 * @param capturedPiece : piece that got captured in the original move.
	 * 				We put that piece back on the board (can also be 0 = empty square)
	 */
	public void unmakeMove(int move, byte capturedPiece) {
		int moveWithoutPiece = move % 10000;
		square[moveWithoutPiece / 1000][(moveWithoutPiece / 100) % 10] 
				= square[(moveWithoutPiece / 10) % 10][moveWithoutPiece % 10];
		square[(moveWithoutPiece / 10) % 10][moveWithoutPiece % 10] = capturedPiece;
	}
}
