package engine;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import engineIO.Logging;
import engineIO.Transformation;

/**
 * 
 * @author Anon
 *
 * @noinspection ALL
 */
public class Board {
	
	private static int QUEENDANGER = 12;
	private static int ROOKDANGER = 5;
	private static int BISHOPDANGER = 3;
	private static int KNIGHTDANGER = 3;	

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
	
	private int kingAdvancement = 0;
	private int queenAdvancement = 0;
	private int rookAdvancement = 0;
	private int bishopAdvancement = 0;
	private int knightAdvancement = 0;
	private int pawnAdvancement = 0;
	
	private static final int PAWNVALUE = 100;
	private static final int KNIGHTVALUE = 325;
	private static final int BISHOPVALUE = 335;
	private static final int ROOKVALUE = 500;
	private static final int QUEENVALUE = 975;
	private static final int KINGVALUE = 10000;
	
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
		
		clone.setKingAdvancement(kingAdvancement);
		clone.setQueenAdvancement(queenAdvancement);
		clone.setRookAdvancement(rookAdvancement);
		clone.setBishopAdvancement(bishopAdvancement);
		clone.setKnightAdvancement(knightAdvancement);
		clone.setPawnAdvancement(pawnAdvancement);
		
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
				kingAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'q') {
				this.square[file][row] = -5;
				materialCount -= QUEENVALUE;
				dangerToWhiteKing += QUEENDANGER;
				piecesLeft++;
				queenAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'r') {
				this.square[file][row] = -4;
				materialCount -= ROOKVALUE;
				dangerToWhiteKing += ROOKDANGER;
				piecesLeft++;
				rookAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'b') {
				this.square[file][row] = -3;
				materialCount -= BISHOPVALUE;
				dangerToWhiteKing += BISHOPDANGER;
				piecesLeft++;
				bishopAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'n') {
				this.square[file][row] = -2;
				materialCount -= KNIGHTVALUE;
				dangerToWhiteKing += KNIGHTDANGER;
				piecesLeft++;
				knightAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'p') {
				this.square[file][row] = -1;
				materialCount -= PAWNVALUE;
				piecesLeft++;
				pawnAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'P') {
				this.square[file][row] = 1;
				materialCount += PAWNVALUE;
				piecesLeft++;
				pawnAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'N') {
				this.square[file][row] = 2;
				materialCount += KNIGHTVALUE;
				setDangerToBlackKing(getDangerToBlackKing() + KNIGHTDANGER);
				piecesLeft++;
				knightAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'B') {
				this.square[file][row] = 3;
				materialCount += BISHOPVALUE;
				setDangerToBlackKing(getDangerToBlackKing() + BISHOPDANGER);
				piecesLeft++;
				bishopAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'R') {
				this.square[file][row] = 4;
				materialCount += ROOKVALUE;
				setDangerToBlackKing(getDangerToBlackKing() + ROOKDANGER);
				piecesLeft++;
				rookAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'Q') {
				this.square[file][row] = 5;
				materialCount += QUEENVALUE;
				setDangerToBlackKing(getDangerToBlackKing() + QUEENDANGER);
				piecesLeft++;
				queenAdvancement += 2 * row - 7;
				file++;
			} else if (positions[0].charAt(i) == 'K') {
				this.square[file][row] = 6;
				materialCount += KINGVALUE;
				piecesLeft++;
				kingAdvancement += 2 * row - 7;
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

	public void makeMove(String move) {
		int startSquare = Transformation.squareToNumber(move.substring(0, 2));
		int endSquare = Transformation.squareToNumber(move.substring(2, 4));
		makeMove(startSquare * 64 + endSquare);
	}
	
	/**
	 * This method takes a move encoded as int and plays that move.
	 * 
	 * @param move
	 *            : the move we play
	 */
	public void makeMove(int move) {
		int moveWithoutPiece = move % 4096;
		int capturedPiece = square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8];
		if (capturedPiece != 0) {
			piecesLeft--;
			if (capturedPiece > 0) {
				if (capturedPiece == 1) {
					materialCount -= PAWNVALUE;
					pawnAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == 2) {
					materialCount -= KNIGHTVALUE;
					setDangerToBlackKing(getDangerToBlackKing() - KNIGHTDANGER);
					knightAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == 3) {
					materialCount -= BISHOPVALUE;
					setDangerToBlackKing(getDangerToBlackKing() - BISHOPDANGER);
					bishopAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == 4) {
					materialCount -= ROOKVALUE;
					setDangerToBlackKing(getDangerToBlackKing() - ROOKDANGER);
					rookAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == 5) {
					materialCount -= QUEENVALUE;
					setDangerToBlackKing(getDangerToBlackKing() - QUEENDANGER);
					queenAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == 6) {
					materialCount -= KINGVALUE;
					kingAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				}
			} else {
				if (capturedPiece == -1) {
					materialCount += PAWNVALUE;
					pawnAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == -2) {
					materialCount += KNIGHTVALUE;
					dangerToWhiteKing -= KNIGHTDANGER;
					knightAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == -3) {
					materialCount += BISHOPVALUE;
					dangerToWhiteKing -= BISHOPDANGER;
					bishopAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == -4) {
					materialCount += ROOKVALUE;
					dangerToWhiteKing -= ROOKDANGER;
					rookAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == -5) {
					materialCount += QUEENVALUE;
					dangerToWhiteKing -= QUEENDANGER;
					queenAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
				} else if (capturedPiece == -6) {
					materialCount += KINGVALUE;
					kingAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
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
			removeCastlingRights((byte) 0x20);
		}
		if (moveWithoutPiece / 64 == 7 || moveWithoutPiece % 64 == 7) {
			removeCastlingRights((byte) 0x4);
		}
		if (moveWithoutPiece / 64 == 56 || moveWithoutPiece % 64 == 56) {
			removeCastlingRights((byte) 0x8);
		}
		if (moveWithoutPiece / 64 == 63 || moveWithoutPiece % 64 == 63) {
			removeCastlingRights((byte) 0x1);
		}
		if (moveWithoutPiece / 64 == 32 && square[4][0] == 6) {
			if (moveWithoutPiece % 64 == 48) {
				square[6][0] = 6;
				square[4][0] = 0;
				square[5][0] = 4; // rook move in castling
				square[7][0] = 0;
				changeToMove();
				return;
			} else if (moveWithoutPiece % 64 == 16) {
				square[2][0] = 6;
				square[4][0] = 0;
				square[3][0] = 4; // rook move in castling
				square[0][0] = 0;
				changeToMove();
				return;
			}
		} else if (moveWithoutPiece / 64 == 39 && square[4][7] == -6) {
			if (moveWithoutPiece % 64 == 55) {
				square[6][7] = -6;
				square[4][7] = 0;
				square[5][7] = -4; // rook move in castling
				square[7][7] = 0;
				changeToMove();
				return;
			} else if (moveWithoutPiece % 64 == 23) {
				square[2][7] = -6;
				square[4][7] = 0;
				square[3][7] = -4; // rook move in castling
				square[0][7] = 0;
				changeToMove();
				return;
			}
		}
		if (Math.abs(square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8]) == 1
				&& (moveWithoutPiece % 64) == getEnPassant()) {
			piecesLeft--;
			if (toMove) {
				square[getEnPassant() / 8][(getEnPassant() % 8) - 1] = 0;
				materialCount += PAWNVALUE;
				pawnAdvancement -= 2 * ((getEnPassant() % 8) - 1) - 7;
			} else {
				square[getEnPassant() / 8][(getEnPassant() % 8) + 1] = 0;
				materialCount -= PAWNVALUE;
				pawnAdvancement -= 2 * ((getEnPassant() % 8) + 1) - 7;
			}
		}
		square[(moveWithoutPiece / 8) % 8][moveWithoutPiece
				% 8] = square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8];
		square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8] = 0;
		
		if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 1) {
			pawnAdvancement += 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 2) {
			knightAdvancement += 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 3) {
			bishopAdvancement += 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 4) {
			rookAdvancement += 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 5) {
			queenAdvancement += 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 6) {
			kingAdvancement += 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		}
		
		setEnPassant((byte) -1);
		if (Math.abs(square[(moveWithoutPiece % 64) / 8][moveWithoutPiece % 8]) == 1 
				&& Math.abs(moveWithoutPiece / 64 - moveWithoutPiece % 64) == 2) {
			setEnPassant((byte) ((moveWithoutPiece / 64 + moveWithoutPiece % 64) / 2));
		}
		
		if (square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] == 1 && (moveWithoutPiece % 8) == 7) {
			pawnAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
			
			square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] = 5;
			materialCount += QUEENVALUE - PAWNVALUE;
			setDangerToBlackKing(getDangerToBlackKing() + QUEENDANGER);
			queenAdvancement += 2 * (moveWithoutPiece % 8) - 7;
		} else if (square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] == -1 && (moveWithoutPiece % 8) == 0) {
			pawnAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
			
			square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] = -5;
			materialCount -= QUEENVALUE - PAWNVALUE;
			dangerToWhiteKing += QUEENDANGER;
			queenAdvancement += 2 * (moveWithoutPiece % 8) - 7;
		}
		changeToMove();
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
			changeToMove();
			return;
		}
		if (move == 26672) { // White castle king side.
			square[4][0] = 6; 
			square[6][0] = 0;
			square[5][0] = 0; 
			square[7][0] = 4;
			changeToMove();
			return;
		}
		if (move == 27095) { // Black castle queen side.
			square[4][7] = -6;
			square[2][7] = 0;
			square[3][7] = 0; 
			square[0][7] = -4;
			changeToMove();
			return;
		}
		if (move == 27127) { // Black castle king side.
			square[4][7] = -6; 
			square[6][7] = 0;
			square[5][7] = 0; 
			square[7][7] = -4;
			changeToMove();
			return;
		}
		
		int moveWithoutPiece = move % 4096;
		int piece = capturedPiece;
		if (piece != 0) {
			piecesLeft++;
			if (piece > 0) {
				if (piece == 1) {
					materialCount += PAWNVALUE;
					pawnAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == 2) {
					materialCount += KNIGHTVALUE;
					setDangerToBlackKing(getDangerToBlackKing() + KNIGHTDANGER);
					knightAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == 3) {
					materialCount += BISHOPVALUE;
					setDangerToBlackKing(getDangerToBlackKing() + BISHOPDANGER);
					bishopAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == 4) {
					materialCount += ROOKVALUE;
					setDangerToBlackKing(getDangerToBlackKing() + ROOKDANGER);
					rookAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == 5) {
					materialCount += QUEENVALUE;
					setDangerToBlackKing(getDangerToBlackKing() + QUEENDANGER);
					queenAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == 6) {
					materialCount += KINGVALUE;
					kingAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				}
			} else {
				if (piece == -1) {
					materialCount -= PAWNVALUE;
					pawnAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == -2) {
					materialCount -= KNIGHTVALUE;
					dangerToWhiteKing += KNIGHTDANGER;
					knightAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == -3) {
					materialCount -= BISHOPVALUE;
					dangerToWhiteKing += BISHOPDANGER;
					bishopAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == -4) {
					materialCount -= ROOKVALUE;
					dangerToWhiteKing += ROOKDANGER;
					rookAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == -5) {
					materialCount -= QUEENVALUE;
					dangerToWhiteKing += QUEENDANGER;
					queenAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				} else if (piece == -6) {
					materialCount -= KINGVALUE;
					kingAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				}
			}
		}
		
		if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) != move / 4096) {
			if ((moveWithoutPiece / 64) % 8 == 6) {
				pawnAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				
				square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] = 1;
				materialCount -= QUEENVALUE - PAWNVALUE;
				setDangerToBlackKing(getDangerToBlackKing() - QUEENDANGER);
				queenAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
			} else if (((moveWithoutPiece / 64) % 8 == 1)) {
				pawnAdvancement += 2 * (moveWithoutPiece % 8) - 7;
				
				square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] = -1;
				materialCount += QUEENVALUE - PAWNVALUE;
				dangerToWhiteKing -= QUEENDANGER;
				queenAdvancement -= 2 * (moveWithoutPiece % 8) - 7;
			}
		}

		if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 1) {
			pawnAdvancement -= 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 2) {
			knightAdvancement -= 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 3) {
			bishopAdvancement -= 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 4) {
			rookAdvancement -= 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 5) {
			queenAdvancement -= 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		} else if (Math.abs(square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8]) == 6) {
			kingAdvancement -= 2 * ((moveWithoutPiece % 8) - (moveWithoutPiece / 64) % 8);
		}
		
		square[moveWithoutPiece / 512][(moveWithoutPiece / 64) % 8]
				= square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8];
		square[(moveWithoutPiece / 8) % 8][moveWithoutPiece % 8] = capturedPiece;
		
		if ((move / 4096 == 1) && (moveWithoutPiece % 64) == getEnPassant()) {
			piecesLeft++;
			if ((moveWithoutPiece % 8) == 5) { // white captured en passant.
				square[(moveWithoutPiece / 8) % 8][(moveWithoutPiece % 8) - 1] = -1;
				materialCount -= PAWNVALUE;
				pawnAdvancement += 2 * ((getEnPassant() % 8) - 1) - 7;
			} else {
				square[(moveWithoutPiece / 8) % 8][(moveWithoutPiece % 8) + 1] = 1;
				materialCount += PAWNVALUE;
				pawnAdvancement += 2 * ((getEnPassant() % 8) + 1) - 7;
			}
		}
		changeToMove();
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

	public int getQueenAdvancement() {
		return queenAdvancement;
	}

	public void setQueenAdvancement(int queenAdvancement) {
		this.queenAdvancement = queenAdvancement;
	}

	public int getKingAdvancement() {
		return kingAdvancement;
	}

	public void setKingAdvancement(int kingAdvancement) {
		this.kingAdvancement = kingAdvancement;
	}

	public int getBishopAdvancement() {
		return bishopAdvancement;
	}

	public void setBishopAdvancement(int bishopAdvancement) {
		this.bishopAdvancement = bishopAdvancement;
	}

	public int getRookAdvancement() {
		return rookAdvancement;
	}

	public void setRookAdvancement(int rookAdvancement) {
		this.rookAdvancement = rookAdvancement;
	}

	public int getKnightAdvancement() {
		return knightAdvancement;
	}

	public void setKnightAdvancement(int knightAdvancement) {
		this.knightAdvancement = knightAdvancement;
	}

	public int getPawnAdvancement() {
		return pawnAdvancement;
	}

	public void setPawnAdvancement(int pawnAdvancement) {
		this.pawnAdvancement = pawnAdvancement;
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
