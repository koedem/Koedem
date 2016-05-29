package engine;

import java.util.Scanner;

/**
 * 
 * @author Anon
 *
 */
public class Board {

	protected byte[][] square = new byte[8][8]; // Pawn = 1, Knight = 2, Bishop
												// = 3, Rook = 4, Queen = 5,
												// King = 6;
	// White pieces get positive values, black pieces negative ones, empty
	// squares a 0.
	private boolean toMove = true;
	private byte castlingRights = 0; // 00abcdef a = Ra1, b = Ke1, c = Rh1, d =
										// ra8, e = ke8, f = rh8
										// 1 means the piece hasn't moved yet.
	private short materialCount = 0;
	private int piecesLeft = 32;

	/**
	 * Constructor, create new Board and setup the chess start position
	 */
	public Board() {
		setFENPosition("fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq"); // fen
																					// of
																					// start
																					// position
	}

	/**
	 * This method takes a fen code and sets that position on the board. Note
	 * that it only takes position, who to move it is and castling rights, no
	 * move number or 50 move rule counter.
	 * 
	 * @param fen
	 *            : Position that the method sets.
	 */
	public void setFENPosition(String fen) {
		materialCount = 0;
		String position = fen.substring(4);
		byte file = 0;
		byte row = 7;
		for (int i = 0; i < position.length(); i++) {
			if (position.charAt(i) == 'k') {
				this.square[file][row] = -6;
				materialCount -= 10000;
				file++;
			} else if (position.charAt(i) == 'q') {
				this.square[file][row] = -5;
				materialCount -= 900;
				file++;
			} else if (position.charAt(i) == 'r') {
				this.square[file][row] = -4;
				materialCount -= 500;
				file++;
			} else if (position.charAt(i) == 'b') {
				this.square[file][row] = -3;
				materialCount -= 300;
				file++;
			} else if (position.charAt(i) == 'n') {
				this.square[file][row] = -2;
				materialCount -= 300;
				file++;
			} else if (position.charAt(i) == 'p') {
				this.square[file][row] = -1;
				materialCount -= 100;
				file++;
			} else if (position.charAt(i) == 'P') {
				this.square[file][row] = 1;
				materialCount += 100;
				file++;
			} else if (position.charAt(i) == 'N') {
				this.square[file][row] = 2;
				materialCount += 300;
				file++;
			} else if (position.charAt(i) == 'B') {
				this.square[file][row] = 3;
				materialCount += 300;
				file++;
			} else if (position.charAt(i) == 'R') {
				this.square[file][row] = 4;
				materialCount += 500;
				file++;
			} else if (position.charAt(i) == 'Q') {
				this.square[file][row] = 5;
				materialCount += 900;
				file++;
			} else if (position.charAt(i) == 'K') {
				this.square[file][row] = 6;
				materialCount += 10000;
				file++;
			} else if (position.charAt(i) == '/') {
				row--;
				file = 0;
			} else if (position.charAt(i) == ' ') {
				if (position.charAt(i + 1) == 'w') {
					this.toMove = true;
				} else if (position.charAt(i + 1) == 'b') {
					this.toMove = false;
				} else {
					throw new IllegalArgumentException();
				}
				if (position.charAt(i + 2) == ' ') {
					setCastlingRights(position.substring(i + 3));
				} else {
					throw new IllegalArgumentException();
				}
				break;
			} else {
				int emptySquares = Character.getNumericValue(position.charAt(i));
				for (int j = 0; j < emptySquares; j++) {
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
	 * Print out the board, row by row starting at highest row. Each row we
	 * print file by file from lowest to highest.
	 */
	public void printBoard() {
		piecesLeft = 0;
		System.out.println();
		for (int i = 7; i >= 0; i--) {

			for (int j = 0; j < 8; j++) {
				System.out.print(Transformation.numberToPiece(square[j][i], this) + " ");
			}

			System.out.println();
		}
		System.out.println();
		System.out.println(Transformation.numberToCastling(castlingRights) + "\n");
		
	}

	/**
	 * 
	 * @param file
	 *            : file of the square
	 * @param row
	 *            : row of the square
	 * @return the value of the square
	 */
	public byte getSquare(int file, int row) {
		return square[file][row];
	}

	/**
	 * Execute the move on the board. Check whether a king was captured in which
	 * case the game is over.
	 * 
	 * @param move
	 *            : the move stored as string. Has to be "decoded" first.
	 * @return whether the game ends or not
	 */
	public boolean makeMove(String move, Scanner sc) {
		boolean gameEnd = false;
		if (move.charAt(2) == '-') {
			int startSquare = Transformation.squareToNumber(move.substring(0, 2));
			int endSquare = Transformation.squareToNumber(move.substring(3, 5));
			if (Math.abs(square[endSquare / 8][endSquare % 8]) == 6) {
				gameEnd = true;
			}
			if (startSquare == 32 && square[4][0] == 6) {
				removeCastlingRights((byte) 0x38);
				if (endSquare == 48) {
					square[6][0] = 6;
					square[4][0] = 0;
					square[5][0] = 4; // rook move in castling
					square[7][0] = 0;
					return gameEnd;
				} else if (endSquare == 16) {
					square[2][0] = 6;
					square[4][0] = 0;
					square[3][0] = 4; // rook move in castling
					square[0][0] = 0;
					return gameEnd;
				}
			} else if (startSquare == 39 && square[4][7] == -6) {
				removeCastlingRights((byte) 0x7);
				if (endSquare == 55) {
					square[6][7] = -6;
					square[4][7] = 0;
					square[5][7] = -4; // rook move in castling
					square[7][7] = 0;
					return gameEnd;
				} else if (endSquare == 23) {
					square[2][7] = -6;
					square[4][7] = 0;
					square[3][7] = -4; // rook move in castling
					square[0][7] = 0;
					return gameEnd;
				}
			}
			int piece = square[endSquare / 8][endSquare % 8];
			if (piece != 0) {
				if (piece > 0) {
					if (piece == 1) {
						materialCount -= 100;
					} else if (piece == 2 || piece == 3) {
						materialCount -= 300;
					} else if (piece == 4) {
						materialCount -= 500;
					} else if (piece == 5) {
						materialCount -= 900;
					} else if (piece == 6) {
						materialCount -= 10000;
					}
				} else {
					if (piece == -1) {
						materialCount += 100;
					} else if (piece == -2 || piece == -3) {
						materialCount += 300;
					} else if (piece == -4) {
						materialCount += 500;
					} else if (piece == -5) {
						materialCount += 900;
					} else if (piece == -6) {
						materialCount += 10000;
					}
				}
			}
			square[endSquare / 8][endSquare % 8] = square[startSquare / 8][startSquare % 8];
			square[startSquare / 8][startSquare % 8] = 0;
			if (startSquare == 0 || endSquare == 0) {
				removeCastlingRights((byte) 0x30);
			} else if (startSquare == 7 || endSquare == 7) {
				removeCastlingRights((byte) 0x6);
			} else if (startSquare == 56 || endSquare == 56) {
				removeCastlingRights((byte) 0x18);
			} else if (startSquare == 63 || endSquare == 63) {
				removeCastlingRights((byte) 0x3);
			}
			
			if (square[endSquare / 8][endSquare % 8] == 1 && (endSquare % 8) == 7) {
				System.out.println("What piece do you want to promote in? [Q/R/B/N]");
				Scanner sca = new Scanner(System.in);
				String pieceString = sca.next();
				byte pieceByte = Transformation.stringToPiece(pieceString);
				square[endSquare / 8][endSquare % 8] = pieceByte;
				if (pieceByte == 2 || pieceByte == 3) {
					materialCount += 200;
				} else if (pieceByte == 4) {
					materialCount += 400;
				} else if (pieceByte == 5) {
					materialCount += 800;
				}
				sca.close();
			} else if (square[endSquare / 8][endSquare % 8] == -1 && (endSquare % 8) == 0) {
				System.out.println("What piece do you want to promote in? [q/r/b/n]");
				String pieceString = sc.next();
				byte pieceByte = Transformation.stringToPiece(pieceString);
				square[endSquare / 8][endSquare % 8] = pieceByte;
				if (pieceByte == -2 || pieceByte == -3) {
					materialCount -= 200;
				} else if (pieceByte == -4) {
					materialCount -= 400;
				} else if (pieceByte == -5) {
					materialCount -= 800;
				}
			}
		} else {
			System.out.println("Illegal Move. Try again.");
		}
		return gameEnd;
	}

	/**
	 * This method takes a move encoded as int and plays that move.
	 * 
	 * @param move
	 *            : the move we play
	 */
	public void makeMove(int move) {
		int moveWithoutPiece = move % 4096;
		int piece = square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8];
		if (piece != 0) {
			if (piece > 0) {
				if (piece == 1) {
					materialCount -= 100;
				} else if (piece == 2 || piece == 3) {
					materialCount -= 300;
				} else if (piece == 4) {
					materialCount -= 500;
				} else if (piece == 5) {
					materialCount -= 900;
				} else if (piece == 6) {
					materialCount -= 10000;
				}
			} else {
				if (piece == -1) {
					materialCount += 100;
				} else if (piece == -2 || piece == -3) {
					materialCount += 300;
				} else if (piece == -4) {
					materialCount += 500;
				} else if (piece == -5) {
					materialCount += 900;
				} else if (piece == -6) {
					materialCount += 10000;
				}
			}
		}
		if (moveWithoutPiece / 64 == 32) {
			removeCastlingRights((byte) 0x38);
		}
		if (moveWithoutPiece / 64 == 39) {
			removeCastlingRights((byte) 0x7);
		}
		if (moveWithoutPiece / 64 == 0 || moveWithoutPiece % 64 == 0) { // If Ra1 moves or captures we can't castle
																		// queenside anymore.
			removeCastlingRights((byte) 0x30);
		}
		if (moveWithoutPiece / 64 == 7 || moveWithoutPiece % 64 == 7) {
			removeCastlingRights((byte) 0x6);
		}
		if (moveWithoutPiece / 64 == 56 || moveWithoutPiece % 64 == 56) {
			removeCastlingRights((byte) 0x18);
		}
		if (moveWithoutPiece / 64 == 63 || moveWithoutPiece % 64 == 63) {
			removeCastlingRights((byte) 0x3);
		}
		
		if (moveWithoutPiece / 64 == 32 && square[4][0] == 6) {
			if (moveWithoutPiece % 64 == 48) {
				square[6][0] = 6;
				square[4][0] = 0;
				square[5][0] = 4; // rook move in castling
				square[7][0] = 0;
				return;
			} else if (moveWithoutPiece % 64 == 16) {
				square[2][0] = 6;
				square[4][0] = 0;
				square[3][0] = 4; // rook move in castling
				square[0][0] = 0;
				return;
			}
		} else if (moveWithoutPiece / 64 == 39 && square[4][7] == -6) {
			if (moveWithoutPiece % 64 == 55) {
				square[6][7] = -6;
				square[4][7] = 0;
				square[5][7] = -4; // rook move in castling
				square[7][7] = 0;
				return;
			} else if (moveWithoutPiece % 64 == 23) {
				square[2][7] = -6;
				square[4][7] = 0;
				square[3][7] = -4; // rook move in castling
				square[0][7] = 0;
				return;
			}
		}
		square[(moveWithoutPiece / 8) % 8][moveWithoutPiece
				% 8] = square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8];
		square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8] = 0;
		if (square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] == 1 && (moveWithoutPiece % 8) == 7) {
			square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] = 5;
			materialCount += 800;
		} else if (square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] == -1 && (moveWithoutPiece % 8) == 0) {
			square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] = -5;
			materialCount -= 800;
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
	 * @param newToMove
	 *            : set the toMove parameter
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
	 * @param move
	 *            : move which we want to undo
	 * @param capturedPiece
	 *            : piece that got captured in the original move. We put that
	 *            piece back on the board (can also be 0 = empty square)
	 */
	public void unmakeMove(int move, byte capturedPiece) {
		if (move == 26640) { // White castle queen side.
			square[4][0] = 6; // King move gets undone.
			square[2][0] = 0;
			square[3][0] = 0; // Rook move get undone.
			square[0][0] = 4;
			return;
		}
		if (move == 26672) { // White castle king side.
			square[4][0] = 6; 
			square[6][0] = 0;
			square[5][0] = 0; 
			square[7][0] = 4;
			return;
		}
		if (move == 27095) { // Black castle queen side.
			square[4][7] = -6;
			square[2][7] = 0;
			square[3][7] = 0; 
			square[0][7] = -4;
			return;
		}
		if (move == 27127) { // Black castle king side.
			square[4][7] = -6; 
			square[6][7] = 0;
			square[5][7] = 0; 
			square[7][7] = -4;
			return;
		}
		
		int moveWithoutPiece = move % 4096;
		int piece = capturedPiece;
		if (piece != 0) {
			if (piece > 0) {
				if (piece == 1) {
					materialCount += 100;
				} else if (piece == 2 || piece == 3) {
					materialCount += 300;
				} else if (piece == 4) {
					materialCount += 500;
				} else if (piece == 5) {
					materialCount += 900;
				} else if (piece == 6) {
					materialCount += 10000;
				}
			} else {
				if (piece == -1) {
					materialCount -= 100;
				} else if (piece == -2 || piece == -3) {
					materialCount -= 300;
				} else if (piece == -4) {
					materialCount -= 500;
				} else if (piece == -5) {
					materialCount -= 900;
				} else if (piece == -6) {
					materialCount -= 10000;
				}
			}
		}
		square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8]
				= square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8];
		square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] = capturedPiece;
		if (Math.abs(square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8]) != move / 4096) {
			if ((moveWithoutPiece / 64) % 8 == 6) {
				square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8] = 1;
				materialCount -= 800;
			} else if (((moveWithoutPiece / 64) % 8 == 1)) {
				square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8] = -1;
				materialCount += 800;
			}
		}
	}

	/**
	 * 
	 * @return The material count in centi pawns.
	 */
	public int getMaterialCount() {
		return materialCount;
	}
	
	/**
	 * Castling rights is a byte. Check for white King side castle with & 0x18 == 0x18, Q side & 0x30 == 0x30,
	 * black K side 0x3 == 0x3, Q side 0x6 == 0x6.
	 * 
	 * @return Which castlings are still possible.
	 */
	public byte getCastlingRights() {
		return castlingRights;
	}
	
	/**
	 * This method takes a byte and sets the 1s in the byte to 0 in castlingRights.
	 * 
	 * @param change Which castling rights should be removed.
	 */
	public void removeCastlingRights(byte change) {
		this.castlingRights = (byte) (this.castlingRights & (~change));
	}
	
	/**
	 * This method takes a byte and sets the 1s in the byte to 1 in castlingRights.
	 * 
	 * @param change Which castling rights should be added.
	 */
	public void addCastlingRights(byte change) {
		this.castlingRights = (byte) (this.castlingRights | change);
	}
	
	public int getPiecesLeft() {
		return piecesLeft;
	}
	
	public void setPiecesLeft(int newCount) {
		piecesLeft = newCount;
	}
	
	public void incrementPiecesLeft() {
		piecesLeft++;
	}
	
	public void decrementPiecesLeft() {
		piecesLeft--;
	}
}
