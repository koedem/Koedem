package engine;

import java.util.ArrayList;
import java.util.Hashtable;
import engineIO.Logging;
import engineIO.Transformation;

/**
 * 
 * @author Anon
 *
 * @noinspection ALL
 */
public class Board {
	
	private static final int QUEENDANGER = 12;
	private static final int ROOKDANGER = 5;
	private static final int BISHOPDANGER = 3;
	private static final int KNIGHTDANGER = 3;
	
	private static final int[] PIECEDANGER = { 0, 0, KNIGHTDANGER, BISHOPDANGER, ROOKDANGER, QUEENDANGER, 0 };

	public long nodes = 0;
	public long abortedNodes = 0;
	public long qNodes = 0;
	
	/**
	 * Pawn = 1, Knight = 2, Bishop = 3, Rook = 4, Queen = 5, King = 6. 
	 * White pieces get positive values, black pieces negative ones, empty squares a 0.
	 * Format as fileRow, each reduced by one. Example: d6 becomes 3, 5.
	 */
	protected byte[][] square = new byte[8][8];
	
	/**
	 * True = white to move, false = black to move.
	 */
	private boolean toMove = true;
	
	/**
	 * Consists of following bits: 00abcdef, where a marks the state of the Ra1, b = Ke1, c = Rh1,
	 * d = ra8, e = ke8, f = rh8. 1 means the piece hasn't moved yet.
	 */
	private byte castlingRights = 0;
	
	/**
	 * Square on which a en passant capture would be legal. Default -1 ( = a0).
	 * Format: fileRow, 26 = 3 * 8 + 2 = d3.
	 */
	private byte enPassant = -1;
	
	private int moveNumber = 1;
	
	/**
	 * Based on standard values, *roughly* Pawn = 100 CentiPawns, Knight = Bishop = 300 CP, Rook = 500 CP, Queen = 900 CP.
	 * At every point in search the material count should accurately show the material in the current search position.
	 */
	private short materialCount = 0;
	
	/**
	 * From 2 (King vs. King) to 32.
	 */
	private int piecesLeft = 0;
	
	private int dangerToWhiteKing = 0;
	private int dangerToBlackKing = 0;
	
	private int[] pieceAdvancement = { 0, 0, 0, 0, 0, 0, 0 }; // empty square, pawn, knight, bishop, rook, queen, king
	
	public static final int PAWNVALUE = 100;
	public static final int KNIGHTVALUE = 325;
	public static final int BISHOPVALUE = 335;
	public static final int ROOKVALUE = 500;
	public static final int QUEENVALUE = 975;
	public static final int KINGVALUE = 10000;
	
	public static final int[] PIECEVALUE = { 0, PAWNVALUE, KNIGHTVALUE, BISHOPVALUE,
			ROOKVALUE, QUEENVALUE, KINGVALUE };
	
	/**
	 * We store every position that actually occurred in the game.
	 */
	private Hashtable<String, Node> hashTable = new Hashtable<String, Node>();

	private ArrayList<Integer> rootMoves = new ArrayList<Integer>();
	
	/**
	 * Constructor, create new Board and setup the chess start position
	 */
	public Board() {
		setFENPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); // fen
																					// of
																					// start
																					// position
	}
	
	public Board(String fen) {
		setFENPosition(fen);
	}
	
	public Board cloneBoard() {
		Board clone = new Board();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				clone.square[i][j] = square[i][j];
			}
		}
		clone.setToMove(toMove);
		clone.setCastlingRights(castlingRights);
		clone.setEnPassant(enPassant);
		clone.setMoveNumber(moveNumber);
		clone.setMaterialCount(materialCount);
		clone.setPiecesLeft(piecesLeft);
		clone.setDangerToWhiteKing(dangerToWhiteKing);
		clone.setDangerToBlackKing(dangerToBlackKing);
		
		for (int piece = 1; piece <= 6; piece++) {
			clone.setPieceAdvancement(piece, pieceAdvancement[piece]);
		}
		
		clone.setHashTable(hashTable);
		return clone;
	}
	
	/**
	 * This method takes a fen code and sets that position on the board.
	 * 
	 * @param fen
	 *            : Position that the method sets.
	 */
	private void setFENPosition(String fen) {
		String position = fen;
		String[] positions = position.split(" ");
		byte file = 0;
		byte row = 7;
		for (int i = 0; i < positions[0].length(); i++) {
			if (positions[0].charAt(i) == 'k') {
				this.square[file][row] = -6;
				materialCount -= KINGVALUE;
				piecesLeft++;
				pieceAdvancement[6] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'q') {
				this.square[file][row] = -5;
				materialCount -= QUEENVALUE;
				dangerToWhiteKing += QUEENDANGER;
				piecesLeft++;
				pieceAdvancement[5] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'r') {
				this.square[file][row] = -4;
				materialCount -= ROOKVALUE;
				dangerToWhiteKing += ROOKDANGER;
				piecesLeft++;
				pieceAdvancement[4] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'b') {
				this.square[file][row] = -3;
				materialCount -= BISHOPVALUE;
				dangerToWhiteKing += BISHOPDANGER;
				piecesLeft++;
				pieceAdvancement[3] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'n') {
				this.square[file][row] = -2;
				materialCount -= KNIGHTVALUE;
				dangerToWhiteKing += KNIGHTDANGER;
				piecesLeft++;
				pieceAdvancement[2] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'p') {
				this.square[file][row] = -1;
				materialCount -= PAWNVALUE;
				piecesLeft++;
				pieceAdvancement[1] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'P') {
				this.square[file][row] = 1;
				materialCount += PAWNVALUE;
				piecesLeft++;
				pieceAdvancement[1] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'N') {
				this.square[file][row] = 2;
				materialCount += KNIGHTVALUE;
				setDangerToBlackKing(getDangerToBlackKing() + KNIGHTDANGER);
				piecesLeft++;
				pieceAdvancement[2] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'B') {
				this.square[file][row] = 3;
				materialCount += BISHOPVALUE;
				setDangerToBlackKing(getDangerToBlackKing() + BISHOPDANGER);
				piecesLeft++;
				pieceAdvancement[3] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'R') {
				this.square[file][row] = 4;
				materialCount += ROOKVALUE;
				setDangerToBlackKing(getDangerToBlackKing() + ROOKDANGER);
				piecesLeft++;
				pieceAdvancement[4] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'Q') {
				this.square[file][row] = 5;
				materialCount += QUEENVALUE;
				setDangerToBlackKing(getDangerToBlackKing() + QUEENDANGER);
				piecesLeft++;
				pieceAdvancement[5] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'K') {
				this.square[file][row] = 6;
				materialCount += KINGVALUE;
				piecesLeft++;
				pieceAdvancement[6] += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == '/') {
				row--;
				file = 0;
			} else {
				int emptySquares = Character.getNumericValue(position.charAt(i));
				for (int j = 0; j < emptySquares; j++) {
					this.square[file][row] = 0;
					file++;
				}
			}
		}
		if (positions[1].equals("w")) {
			this.toMove = true;
		} else if (positions[1].equals("b")) {
			this.toMove = false;
		} else {
			throw new IllegalArgumentException();
		}
		setCastlingRights(positions[2]);
		if (!(positions[3].equals("-"))) {
			setEnPassant((byte) (Character.getNumericValue(positions[3].charAt(1) - 1) // a6 becomes 5
					+ (Character.getNumericValue(positions[3].charAt(0)) - 10) * 8)); // + 0 * 8
		}
		setMoveNumber(Integer.parseInt(positions[positions.length - 1]));
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
		Logging.printLine("");
		for (int i = 7; i >= 0; i--) {
			String row = "";
			for (int j = 0; j < 8; j++) {
				row += Transformation.numberToPiece(square[j][i]) + " ";
			}

			Logging.printLine(row);
		}
		Logging.printLine("");
		Logging.printLine(Transformation.numberToCastling(castlingRights) + " " 
				+ Transformation.numberToSquare(getEnPassant()) + "\n");
		
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
	 * This method takes a move in UCI communication style and executes that move on the board.
	 * Examples: e2e4 = Pawn e2 to e4; e1g1 = short castle; e7e8q = Pawn e7 to e8 promotes into queen
	 * 
	 * @param move The move to play.
	 */
	public void makeMove(String move) {
		int startSquare = Transformation.squareToNumber(move.substring(0, 2));
		int endSquare = Transformation.squareToNumber(move.substring(2, 4));
		if (move.length() == 4) {
			makeMove((1 << 12) + (startSquare << 6) + endSquare);
		} else if (move.length() == 5) {
			makeMove((1 << 15) + (startSquare << 9) + (endSquare << 3) 
					+ Math.abs(Transformation.stringToPiece(move.charAt(5))));
		}
	}
	
	/**
	 * This method takes a move encoded as int and executes that move on the board.
	 * Syntax for non pawn promotions: 0...01ssseee, for pawn promotions: 0...01ssseeeppp
	 * s = bits of the start square, e = bits of the end square,
	 * p = promotion piece according to Transformation.stringToPiece
	 * 
	 * @param move The move to play.
	 */
	public void makeMove(int move) {
		int endSquare = 0; // will get changed to "true" endSquare
		if (move < (1 << 13)) {
			endSquare = move % 64;
		} else {
			endSquare = (move / 8) % 64;
		}
		
		int capturedPiece = square[endSquare / 8][endSquare % 8];
		
		if (capturedPiece != 0) {
			piecesLeft--;
			if (capturedPiece > 0) {
				materialCount -= PIECEVALUE[capturedPiece];
				dangerToBlackKing -= PIECEDANGER[capturedPiece];
				pieceAdvancement[capturedPiece] -= 2 * (endSquare % 8) - 7;
			} else {
				materialCount += PIECEVALUE[Math.abs(capturedPiece)];
				dangerToWhiteKing -= PIECEDANGER[Math.abs(capturedPiece)];
				pieceAdvancement[Math.abs(capturedPiece)] -= 2 * (endSquare % 8) - 7;
			}
		}
		
		if (move < (1 << 13) && move > (1 << 12)) {
			int startSquare = (move / 64) % 64;
			endSquare = move % 64;
			
			if (startSquare == 32) { // if we move with the King (or e1 isn't even the king) we can't castle anymore
				removeCastlingRights((byte) 0x38);
			}
			
			if (startSquare == 39) {
				removeCastlingRights((byte) 0x7);
			}
			
			if (startSquare == 0 || endSquare == 0) { // If Ra1 moves or gets captured we can't castle
																			// queenside anymore.
				removeCastlingRights((byte) 0x20);
			}
			
			if (startSquare == 7 || endSquare == 7) {
				removeCastlingRights((byte) 0x4);
			}
			
			if (startSquare == 56 || endSquare == 56) {
				removeCastlingRights((byte) 0x8);
			}
			
			if (startSquare == 63 || endSquare == 63) {
				removeCastlingRights((byte) 0x1);
			}
			
			if (startSquare == 32 && square[4][0] == 6) {
				if (endSquare == 48) {
					square[6][0] = 6;
					square[4][0] = 0;
					square[5][0] = 4; // rook move in castling
					square[7][0] = 0;
					changeToMove();
					return;
				} else if (endSquare == 16) {
					square[2][0] = 6;
					square[4][0] = 0;
					square[3][0] = 4; // rook move in castling
					square[0][0] = 0;
					changeToMove();
					return;
				}
			} else if (startSquare == 39 && square[4][7] == -6) {
				if (endSquare == 55) {
					square[6][7] = -6;
					square[4][7] = 0;
					square[5][7] = -4; // rook move in castling
					square[7][7] = 0;
					changeToMove();
					return;
				} else if (endSquare == 23) {
					square[2][7] = -6;
					square[4][7] = 0;
					square[3][7] = -4; // rook move in castling
					square[0][7] = 0;
					changeToMove();
					return;
				}
			}
			
			if (Math.abs(square[startSquare / 8][startSquare % 8]) == 1
					&& endSquare == enPassant) {
				piecesLeft--;
				if (toMove) {
					square[enPassant / 8][(enPassant % 8) - 1] = 0; // capture the pawn that is on the square before ep
					materialCount += PAWNVALUE;
					pieceAdvancement[1] -= 2 * ((enPassant % 8) - 1) - 7;
				} else {
					square[enPassant / 8][(enPassant % 8) + 1] = 0;
					materialCount -= PAWNVALUE;
					pieceAdvancement[1] -= 2 * ((enPassant % 8) + 1) - 7;
				}
			}
			
			square[endSquare / 8][endSquare % 8] = square[startSquare / 8][startSquare % 8]; // the actual moving
			square[startSquare / 8][startSquare % 8] = 0; // start square becomes empty
			
			pieceAdvancement[Math.abs(square[endSquare / 8][endSquare % 8])] 
					+= 2 * ((endSquare % 8) - (startSquare % 8));
																	// add the advancement change caused by the move
			
			setEnPassant((byte) -1); // remove old en passant values
			
			if (Math.abs(square[endSquare / 8][endSquare % 8]) == 1 && Math.abs(startSquare - endSquare) == 2) {
																				// if a pawn moves two squares far
				setEnPassant((byte) ((startSquare + endSquare) / 2));  			// we update the en passant to be 
																				// in the middle of start/end square
			}
			
			changeToMove();
		} else if (move < (1 << 16) && move > (1 << 15)) {
			int startSquare = (move - (1 << 15)) / (1 << 9);
			endSquare = (move % (1 << 9)) / (1 << 3);
			
			byte promotion = (byte) (move % (1 << 3));
			
			square[startSquare / 8][startSquare % 8] = 0;
			pieceAdvancement[1] -= 2 * (startSquare % 8) - 7;
			
			pieceAdvancement[promotion] += 2 * (endSquare % 8) - 7;
			
			if (endSquare % 8 == 7) {
				square[endSquare / 8][endSquare % 8] = promotion;
				materialCount += PIECEVALUE[promotion] - PAWNVALUE;
				dangerToBlackKing += PIECEDANGER[promotion];
			} else if (endSquare % 8 == 0) {
				square[endSquare / 8][endSquare % 8] = (byte) -promotion;
				materialCount -= PIECEVALUE[promotion] - PAWNVALUE;
				dangerToWhiteKing += PIECEDANGER[promotion];
			}
		} else {
			assert false;
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
	 * This method undoes the given integer format move.
	 * Syntax for non pawn promotions: 0...01ssseee, for pawn promotions: 0...01ssseeeppp
	 * s = bits of the start square, e = bits of the end square,
	 * p = promotion piece according to Transformation.stringToPiece
	 * 
	 * @param move Move which gets undone.
	 * @param capturedPiece Piece that got captured in the original move. It gets put back on the board
	 * 		(can also be 0 = empty square).
	 * @param oldCastlingRights The castling rights from before the move was executed on the board.
	 */
	public void unmakeMove(int move, byte capturedPiece, byte oldCastlingRights) {
		int piece = capturedPiece;
		int endSquare = 0; // will get changed to correct endSquare
		
		if (oldCastlingRights != castlingRights && (move == 6160 || move == 6192 || move == 6615 || move == 6647)) {
			if (move == (1 << 12) + (32 << 6) + 16) { // White castle queen side.
				assert square[4][0] == 0 && square[3][0] == 4 && square[2][0] == 6 
						&& square[1][0] == 0 && square[0][0] == 0;
				square[4][0] = 6; // King move gets undone.
				square[2][0] = 0;
				square[3][0] = 0; // Rook move get undone.
				square[0][0] = 4;
			} else if (move == (1 << 12) + (32 << 6) + 48) { // White castle king side.
				assert square[4][0] == 0 && square[5][0] == 4 && square[6][0] == 6 && square[7][0] == 0;
				square[4][0] = 6; 
				square[6][0] = 0;
				square[5][0] = 0; 
				square[7][0] = 4;
			} else if (move == (1 << 12) + (39 << 6) + 23) { // Black castle queen side.
				assert square[4][7] == 0 && square[3][7] == -4 && square[2][7] == -6 
						&& square[1][7] == 0 && square[0][7] == 0;
				square[4][7] = -6;
				square[2][7] = 0;
				square[3][7] = 0; 
				square[0][7] = -4;
			} else if (move == (1 << 12) + (39 << 6) + 55) { // Black castle king side.
				assert square[4][7] == 0 && square[5][7] == -4 && square[6][7] == -6 && square[7][7] == 0;
				square[4][7] = -6; 
				square[6][7] = 0;
				square[5][7] = 0; 
				square[7][7] = -4;
			} else {
				assert false;
			}
			changeToMove();
			return;
		} else if (move < (1 << 13) && move > (1 << 12)) {
			int startSquare = (move / 64) % 64;
			endSquare = move % 64;
			
			pieceAdvancement[Math.abs(square[endSquare / 8][endSquare % 8])] 
					-= 2 * ((endSquare % 8) - (startSquare % 8)); // subtract/undo the advancement change the move made
			
			square[startSquare / 8][startSquare % 8] = square[endSquare / 8][endSquare % 8]; // actual moving
			square[endSquare / 8][endSquare % 8] = capturedPiece; // put captured piece back on its square
			
			if (Math.abs(square[startSquare / 8][startSquare % 8]) == 1 && endSquare == enPassant) {
				
											// a pawn moving and ending on the en passant square ALWAYS means capture
				piecesLeft++;
				if (endSquare % 8 == 5) { // white captured en passant.
					assert endSquare - startSquare == -7 || endSquare - startSquare == 9;
					assert square[endSquare / 8][(endSquare % 8) - 1] == 0; // should be empty
					square[endSquare / 8][(endSquare % 8) - 1] = -1; // pawn added back on; -1 because of en passant
					materialCount -= PAWNVALUE;
					pieceAdvancement[1] += 2 * ((enPassant % 8) - 1) - 7;
				} else if (endSquare % 8 == 2) {
					assert endSquare - startSquare == 7 || endSquare - startSquare == -9;
					assert square[endSquare / 8][(endSquare % 8) + 1] == 0;
					square[endSquare / 8][(endSquare % 8) + 1] = 1;
					materialCount += PAWNVALUE;
					pieceAdvancement[1] += 2 * ((enPassant % 8) + 1) - 7;
				} else {
					assert false;
				}
			}
			changeToMove();
		} else if (move < (1 << 16) && move > (1 << 15)) {
			int startSquare = (move - (1 << 15)) / (1 << 9);
			endSquare = (move % (1 << 9)) / (1 << 3);
			byte promotion = (byte) (move % (1 << 3));
			
			assert square[startSquare / 8][startSquare % 8] == 0;
			pieceAdvancement[1] += 2 * (startSquare % 8) - 7;

			pieceAdvancement[promotion] -= 2 * (endSquare % 8) - 7;
			
			if (endSquare % 8 == 7) {
				square[startSquare / 8][startSquare % 8] = 1;
				
				square[endSquare / 8][endSquare % 8] = capturedPiece;
				materialCount -= PIECEVALUE[promotion] - PAWNVALUE;
				dangerToBlackKing -= PIECEDANGER[promotion];
			} else if (endSquare % 8 == 0) {
				square[startSquare / 8][startSquare % 8] = -1;
				
				square[endSquare / 8][endSquare % 8] = capturedPiece;
				materialCount += PIECEVALUE[promotion] - PAWNVALUE;
				dangerToWhiteKing -= PIECEDANGER[promotion];
			} else {
				assert false;
			}
		} else {
			assert false;
		}
		if (piece != 0) {
			piecesLeft++;
			if (piece > 0) {
				materialCount += PIECEVALUE[piece]; // piece gets back on the board, so added to materialCount
				dangerToBlackKing += PIECEDANGER[piece]; // and to danger-numbers
				pieceAdvancement[piece] += 2 * (endSquare % 8) - 7; // and add back its advancement
																// move % 8 is equivalent to endSquare % 8
			} else {
				materialCount -= PIECEVALUE[Math.abs(piece)];
				dangerToWhiteKing += PIECEDANGER[Math.abs(piece)];
				pieceAdvancement[Math.abs(piece)] += 2 * (endSquare % 8) - 7;
			}
		}
		
	}

	/**
	 * 
	 * @return The material count in centi pawns.
	 */
	public short getMaterialCount() {
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
	
	public void setCastlingRights(byte castlingRights) {
		this.castlingRights = castlingRights;
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
	
	public void putHashTableElement(Node node) {
		hashTable.put(node.squares, node);
	}
	
	public Hashtable<String, Node> getHashTable() {
		return hashTable;
	}

	public int getPieceAdvancement(int index) {
		return pieceAdvancement[index];
	}
	
	public void setPieceAdvancement(int index, int advancement) {
		pieceAdvancement[index] = advancement;
	}

	public int getMoveNumber() {
		return moveNumber;
	}

	public void setMoveNumber(int moveNumber) {
		this.moveNumber = moveNumber;
	}
	
	public String getSquareString() {
		String squareString = "";
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				squareString += square[i][j];
			}
		}
		if (toMove) {
		    squareString += (byte) 0;
		} else {
		    squareString += (byte) 1;
		}
		return squareString;
	}

	public byte getEnPassant() {
		return enPassant;
	}

	public void setEnPassant(byte enPassant) {
		this.enPassant = enPassant;
	}

	public void setMaterialCount(short materialCount) {
		this.materialCount = materialCount;
	}

	public void setHashTable(Hashtable<String, Node> hashTable) {
		this.hashTable = hashTable;
	}

	public ArrayList<Integer> getRootMoves() {
		return rootMoves;
	}

	public void setRootMoves(ArrayList<Integer> rootMoves) {
		this.rootMoves = rootMoves;
	}

	public int getDangerToWhiteKing() {
		return dangerToWhiteKing;
	}

	public void setDangerToWhiteKing(int dangerToWhiteKing) {
		this.dangerToWhiteKing = dangerToWhiteKing;
	}

	public int getDangerToBlackKing() {
		return dangerToBlackKing;
	}

	public void setDangerToBlackKing(int dangerToBlackKing) {
		this.dangerToBlackKing = dangerToBlackKing;
	}
}
