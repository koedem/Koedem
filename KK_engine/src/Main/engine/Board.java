package Main.engine;

import java.io.*;
import java.util.Hashtable;

import Main.Utility.DeepCopy;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;

public class Board implements Serializable {

	private MoveGenerator moveGenerator = new MoveGenerator(this);
	private Evaluation evaluation = new Evaluation(this);
	private Search search = new Search(this);

	private static final int QUEENDANGER = 12;
	private static final int ROOKDANGER = 5;
	private static final int BISHOPDANGER = 3;
	private static final int KNIGHTDANGER = 3;
	
	private static final int[] PIECEDANGER = { 0, 0, KNIGHTDANGER, BISHOPDANGER, ROOKDANGER, QUEENDANGER, 0 };

	public BitBoard bitboard;
	public AttackBoard attackBoard;
	
	/**
	 * Pawn = 1, Knight = 2, Bishop = 3, Rook = 4, Queen = 5, King = 6. 
	 * White pieces get positive values, black pieces negative ones, empty squares a 0.
	 * Format as fileRow, each reduced by one. Example: d6 becomes 3, 5.
	 */
	byte[][] square = new byte[8][8];
	
	/**
	 * True = white to move, false = black to move.
	 */
	private boolean toMove = true;
	
	/**
	 * Consists of following bits: AaaaBbbbCcccDddd, where A marks if White can castle queen side (1 for true),
	 * aaa marks the file of that queen side rook or 000 if A is not set, Bbbb for White king side castling,
	 * Cccc for Black queen side castling and Dddd for Black king side castling.
	 */
	private short castlingRights = 0;
	
	/**
	 * Square on which a en passant capture would be legal. Default -1 ( = a0).
	 * Format: fileRow, 26 = 3 * 8 + 2 = d3.
	 */
	private byte enPassant = -1;
	
	private int moveNumber = 1;
	
	/**
	 * Based on Kaufmann values, Pawn = 100 CentiPawns, Knight = 325, Bishop = 335 CP, Rook = 500 CP, Queen = 975 CP.
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
	
	/**
	 * Material value of a pawn in centi pawns according to Larry Kaufmann (Komodo team).
	 */
	public static final int PAWNVALUE = 100;
	
	/**
	 * Material value of a knight in centi pawns according to Larry Kaufmann (Komodo team).
	 */
	public static final int KNIGHTVALUE = 325;
	
	/**
	 * Material value of a bishop in centi pawns according to Larry Kaufmann (Komodo team).
	 */
	public static final int BISHOPVALUE = 335;
	
	/**
	 * Material value of a rook in centi pawns according to Larry Kaufmann (Komodo team).
	 */
	public static final int ROOKVALUE = 500;
	
	/**
	 * Material value of a queen in centi pawns according to Larry Kaufmann (Komodo team).
	 */
	public static final int QUEENVALUE = 975;
	
	/**
	 * Material value of a king, big enough to outvalue every other eval term. Should not play a role in Search.
	 */
	public static final int KINGVALUE = 10000;
	
	/**
	 * Material values of the pieces where PIECEVALUE[piece] equals the material value of the corresponding piece
	 * in board representation (1 = Pawn, ..., 6 = King)
	 */
	public static final int[] PIECEVALUE = { 0, PAWNVALUE, KNIGHTVALUE, BISHOPVALUE,
			ROOKVALUE, QUEENVALUE, KINGVALUE };
	
	/**
	 * We store every position that actually occurred in the game.
	 */
	private Hashtable<String, Node> hashTable = new Hashtable<>();

	private int[] rootMoves = new int[MoveGenerator.MAX_MOVE_COUNT];

	public String bestmove = "";
	
	/**
	 * Constructor, create new Board and setup the chess start position
	 */
	public Board() {
	    bitboard = new BitBoard(this);
	    attackBoard = bitboard.attackBoard;
		setFENPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w AHah - 0 1"); // fen
																					// of
																					// start
																					// position
	}
	
	public Board(String fen) {
		bitboard = new BitBoard(this);
		attackBoard = bitboard.attackBoard;
		setFENPosition(fen);
	}
	
	public Board cloneBoard() {
		return (Board) DeepCopy.copy(this);
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
            if (positions[0].charAt(i) == '/') {
                row--;
                file = 0;
            } else if (Character.isDigit(positions[0].charAt(i))){
                int emptySquares = Character.getNumericValue(position.charAt(i));
                for (int j = 0; j < emptySquares; j++) {
                    this.square[file][row] = 0;
                    file++;
                }
            } else {
                byte piece = pieceLetterToByte(positions[0].charAt(i));
                int colour = piece / Math.abs(piece);
                this.square[file][row] = piece;
                materialCount += PIECEVALUE[Math.abs(piece)] * colour;
                if (colour == 1) {
                    dangerToBlackKing += PIECEDANGER[piece * colour];
                } else {
                    dangerToWhiteKing += PIECEDANGER[piece * colour];
                }
                piecesLeft++;
                pieceAdvancement[Math.abs(piece)] += 2 * row - 7;
                bitboard.add(Math.abs(piece), (piece > 0) ? 0 : 1, (8 * file + row));
                file++;
            }
		}
		switch (positions[1]) {
			case "w":
				this.toMove = true;
				break;
			case "b":
				this.toMove = false;
				break;
			default:
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
		byte whiteKingPosition = 0;
		byte blackKingPosition = 0;

		for (byte i = 0; i < 8; i++) {
			if (this.square[i][0] == 6) {
				whiteKingPosition = i;
			}
			if (this.square[i][7] == -6) {
				blackKingPosition = i;
			}
		}

		if (!castling.equals("-")) {
			for (int i = 0; i < castling.length(); i++) {
				char c = castling.charAt(i);
				if (Character.isUpperCase(c)) {
					switch (c) {
						case 'A': castlingRights |= 0x8000; break;
						case 'B': castlingRights |= 0x9000; break;
						case 'C': castlingRights |= (whiteKingPosition > 2 ? 0xA000 : 0x0A00); break;
						case 'D': castlingRights |= (whiteKingPosition > 3 ? 0xB000 : 0x0B00); break;
						case 'E': castlingRights |= (whiteKingPosition > 4 ? 0xC000 : 0x0C00); break;
						case 'F': castlingRights |= (whiteKingPosition > 5 ? 0xD000 : 0x0D00); break;
						case 'G': castlingRights |= 0x0E00; break;
						case 'H': castlingRights |= 0x0F00; break;
						case 'K':   for (int j = 7; j > 0; j--) {
										if (square[j][0] == 4) {
											castlingRights |= (0x0800 + (j << 8)); break;
										}
									} break;
						case 'Q':   for (int j = 0; j < 7; j++) {
										if (square[j][0] == 4) {
											castlingRights |= (0x8000 + (j << 12)); break;
										}
									} break;
					}
				} else if (Character.isUpperCase(c)) {
					switch (c) {
						case 'a': castlingRights |= 0x0080; break;
						case 'b': castlingRights |= 0x0090; break;
						case 'c': castlingRights |= (blackKingPosition > 2 ? 0x00A0 : 0x000A); break;
						case 'd': castlingRights |= (blackKingPosition > 3 ? 0x00B0 : 0x000B); break;
						case 'e': castlingRights |= (blackKingPosition > 4 ? 0x00C0 : 0x000C); break;
						case 'f': castlingRights |= (blackKingPosition > 5 ? 0x00D0 : 0x000D); break;
						case 'g': castlingRights |= 0x000E; break;
						case 'h': castlingRights |= 0x000F; break;
						case 'k':   for (int j = 7; j > 0; j--) {
										if (square[j][7] == -4) {
											castlingRights |= (0x0008 + j); break;
										}
									} break;
						case 'q':   for (int j = 0; j < 7; j++) {
										if (square[j][0] == -4) {
											castlingRights |= (0x80 + (j << 4)); break;
										}
									} break;
					}
				}
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
			StringBuilder row = new StringBuilder();
			for (int j = 0; j < 8; j++) {
				row.append(Transformation.numberToPiece(square[j][i])).append(" ");
			}

			Logging.printLine(row.toString());
		}
		Logging.printLine("");
		//Logging.printLine(Transformation.numberToCastling(castlingRights) + " "
		//		+ Transformation.numberToSquare(getEnPassant()) + "\n");
		
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
					+ Math.abs(Transformation.stringToPiece(move.charAt(4))));
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

		if (toMove && capturedPiece == 4 || !toMove && capturedPiece == -4) { // castling in the king moves onto rook notation
			if (toMove) {
				int startSquare = (move / 64) % 64;
				if (square[startSquare / 8][startSquare % 8] == 6) {
					removeCastlingRights((short) 0xFF00); // White can't castle anymore
					square[startSquare / 8][startSquare % 8] = 0;
					square[endSquare / 8][endSquare % 8] = 0;
					if (startSquare > endSquare) { // queenside castling
						square[2][0] = 6;
						square[3][0] = 4;
					} else {
						square[6][0] = 6;
						square[5][0] = 4;
					}
				} else {
					Logging.printLine("Error: Illegal castling:");
					printBoard();
				}
			} else {
				int startSquare = (move / 64) % 64;
				if (square[startSquare / 8][startSquare % 8] == -6) {
					removeCastlingRights((short) 0xFF); // Black can't castle anymore
					square[startSquare / 8][startSquare % 8] = 0;
					square[endSquare / 8][endSquare % 8] = 0;
					if (startSquare > endSquare) { // queenside castling
						square[2][7] = -6;
						square[3][7] = -4;
					} else {
						square[6][7] = -6;
						square[5][7] = -4;
					}
				} else {
					Logging.printLine("Error: Illegal castling:");
					printBoard();
				}
			}
			changeToMove();
			return;
		}
		if (capturedPiece != 0) {
			piecesLeft--;
			bitboard.remove(endSquare);
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

            bitboard.move(startSquare, endSquare);

            if (square[startSquare / 8][startSquare % 8] == 6) {
            	removeCastlingRights((short) 0xFF00);
            } else if (square[startSquare / 8][startSquare % 8] == -6) {
            	removeCastlingRights((short) 0xFF);
            } else if (square[startSquare / 8][startSquare % 8] == 4) {
            	if (((castlingRights >> 12) & 0x7) == startSquare / 8) {
		            removeCastlingRights((short) 0xF000);
	            } else if (((castlingRights >> 8) & 0x7) == startSquare / 8) {
            		removeCastlingRights((short) 0x0F00);
				}
			} else if (square[startSquare / 8][startSquare % 8] == -4) {
	            if (((castlingRights >> 4) & 0x7) == startSquare / 8) {
		            removeCastlingRights((short) 0xF0);
	            } else if ((castlingRights & 0x7) == startSquare / 8) {
		            removeCastlingRights((short) 0x0F);
	            }
            }

            if (square[endSquare / 8][endSquare % 8] == 4) { // rook gets captured
	            if (((castlingRights >> 12) & 0x7) == endSquare / 8) {
		            removeCastlingRights((short) 0xF000);
	            } else if (((castlingRights >> 8) & 0x7) == endSquare / 8) {
		            removeCastlingRights((short) 0x0F00);
	            }
            } else if (square[endSquare / 8][endSquare % 8] == -4) {
	            if (((castlingRights >> 4) & 0x7) == endSquare / 8) {
		            removeCastlingRights((short) 0xF0);
	            } else if ((castlingRights & 0x7) == endSquare / 8) {
		            removeCastlingRights((short) 0x0F);
	            }
            }
			
			if (Math.abs(square[startSquare / 8][startSquare % 8]) == 1
					&& endSquare == enPassant) {
				piecesLeft--;
				if (toMove) {
					square[enPassant / 8][(enPassant % 8) - 1] = 0; // capture the pawn that is on the square before ep
					materialCount += PAWNVALUE;
					pieceAdvancement[1] -= 2 * ((enPassant % 8) - 1) - 7;
					bitboard.remove(endSquare - 1);
				} else {
					square[enPassant / 8][(enPassant % 8) + 1] = 0;
					materialCount -= PAWNVALUE;
					pieceAdvancement[1] -= 2 * ((enPassant % 8) + 1) - 7;
                    bitboard.remove(endSquare + 1);
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

		} else if (move < (1 << 16) && move > (1 << 15)) {
			int startSquare = (move - (1 << 15)) / (1 << 9);
			endSquare = (move % (1 << 9)) / (1 << 3);
			
			byte promotion = (byte) (move % (1 << 3));

			bitboard.remove(startSquare);
			bitboard.add(promotion, (toMove) ? 0 : 1, endSquare);
			
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
	public void unmakeMove(int move, byte capturedPiece, short oldCastlingRights) {
		int piece = capturedPiece;
		int endSquare = 0; // will get changed to correct endSquare
		boolean gotoBelow = false; // using deprecated goto TODO don't do that
		
		/*if (oldCastlingRights != castlingRights && move < (1 << 13)) {
			int startSquare = (move / 64) % 64;
			endSquare = move % 64;
			if (toMove && (oldCastlingRights & 0xFF00) != (castlingRights & 0xFF00)) {
				if (endSquare / 8 == ((oldCastlingRights & 0x7000) >> 8) && endSquare % 8 == 0) { // White moving onto a White rook = castling
					square[2][0] = 0;
					square[3][0] = 0;
					square[startSquare / 8][startSquare % 8] = 6;
					square[endSquare / 8][endSquare % 8] = 4;
					gotoBelow = true;
				} else if (endSquare / 8 == ((oldCastlingRights & 0x0700) >> 8) && endSquare % 8 == 0) { // White moving onto a White rook = castling
					square[5][0] = 0;
					square[6][0] = 0;
					square[startSquare / 8][startSquare % 8] = 6;
					square[endSquare / 8][endSquare % 8] = 4;
					gotoBelow = true;
				}
			} else if (!toMove && (oldCastlingRights & 0xFF) != (castlingRights & 0xFF)) {
				if (endSquare / 8 == ((oldCastlingRights & 0x70)) && endSquare % 8 == 7) { // Black moving onto a Black rook = castling
					square[2][7] = 0;
					square[3][7] = 0;
					square[startSquare / 8][startSquare % 8] = -6;
					square[endSquare / 8][endSquare % 8] = -4;
					gotoBelow = true;
				} else if (endSquare / 8 == ((oldCastlingRights & 0x07)) && endSquare % 8 == 7) { // Black moving onto a Black rook = castling
					square[5][7] = 0;
					square[6][7] = 0;
					square[startSquare / 8][startSquare % 8] = -6;
					square[endSquare / 8][endSquare % 8] = -4;
					gotoBelow = true;
				}
			}
		}*/

		if (move < (1 << 13) && move > (1 << 12) && !gotoBelow) {
			int startSquare = (move / 64) % 64;
			endSquare = move % 64;
			
			pieceAdvancement[Math.abs(square[endSquare / 8][endSquare % 8])] 
					-= 2 * ((endSquare % 8) - (startSquare % 8)); // subtract/undo the advancement change the move made
			
			square[startSquare / 8][startSquare % 8] = square[endSquare / 8][endSquare % 8]; // actual moving
			square[endSquare / 8][endSquare % 8] = capturedPiece; // put captured piece back on its square
            boolean success = bitboard.move(endSquare, startSquare);
            assert success;
			if (Math.abs(square[startSquare / 8][startSquare % 8]) == 1 && endSquare == enPassant) {
				
											// a pawn moving and ending on the en passant square ALWAYS means capture
				piecesLeft++;
				if (endSquare % 8 == 5) { // white captured en passant.
					assert endSquare - startSquare == -7 || endSquare - startSquare == 9;
					assert square[endSquare / 8][(endSquare % 8) - 1] == 0; // should be empty
					square[endSquare / 8][(endSquare % 8) - 1] = -1; // pawn added back on; -1 because of en passant
                    bitboard.add(1, 1, endSquare - 1);
					materialCount -= PAWNVALUE;
					pieceAdvancement[1] += 2 * ((enPassant % 8) - 1) - 7;
				} else if (endSquare % 8 == 2) {
					assert endSquare - startSquare == 7 || endSquare - startSquare == -9;
					assert square[endSquare / 8][(endSquare % 8) + 1] == 0;
					square[endSquare / 8][(endSquare % 8) + 1] = 1;
					bitboard.add(1, 0, endSquare + 1);
					materialCount += PAWNVALUE;
					pieceAdvancement[1] += 2 * ((enPassant % 8) + 1) - 7;
				} else {
					assert false;
				}
			}
		} else if (move < (1 << 16) && move > (1 << 15)) {
			int startSquare = (move - (1 << 15)) / (1 << 9);
			endSquare = (move % (1 << 9)) / (1 << 3);
			byte promotion = (byte) (move % (1 << 3));
			
			assert square[startSquare / 8][startSquare % 8] == 0;
			pieceAdvancement[1] += 2 * (startSquare % 8) - 7;

			pieceAdvancement[promotion] -= 2 * (endSquare % 8) - 7;
			
			if (endSquare % 8 == 7) {
				square[startSquare / 8][startSquare % 8] = 1;
				bitboard.add(1, 0, startSquare);
				bitboard.remove(endSquare);
				
				square[endSquare / 8][endSquare % 8] = capturedPiece; // bitboard change done below
				materialCount -= PIECEVALUE[promotion] - PAWNVALUE;
				dangerToBlackKing -= PIECEDANGER[promotion];
			} else if (endSquare % 8 == 0) {
				square[startSquare / 8][startSquare % 8] = -1;
				bitboard.add(1, 1, startSquare);
				bitboard.remove(endSquare);
				
				square[endSquare / 8][endSquare % 8] = capturedPiece;
				materialCount += PIECEVALUE[promotion] - PAWNVALUE;
				dangerToWhiteKing -= PIECEDANGER[promotion];
			} else {
				assert false;
			}
		} else {
			assert false;
		}
		changeToMove();
		castlingRights = oldCastlingRights;
		
		if (piece != 0) {
            piecesLeft++;
            if (piece > 0) {
                materialCount += PIECEVALUE[piece]; // piece gets back on the board, so added to materialCount
                dangerToBlackKing += PIECEDANGER[piece]; // and to danger-numbers
                pieceAdvancement[piece] += 2 * (endSquare % 8) - 7; // and add back its advancement
				bitboard.add(piece, 0, endSquare);
            } else {
                materialCount -= PIECEVALUE[Math.abs(piece)];
                dangerToWhiteKing += PIECEDANGER[Math.abs(piece)];
                pieceAdvancement[Math.abs(piece)] += 2 * (endSquare % 8) - 7;
                bitboard.add(-piece, 1, endSquare);
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
	public short getCastlingRights() {
		return castlingRights;
	}
	
	public void setCastlingRights(short castlingRights) {
		this.castlingRights = castlingRights;
	}
	
	/**
	 * This method takes a byte and sets the 1s in the byte to 0 in castlingRights.
	 * 
	 * @param change Which castling rights should be removed.
	 */
	public void removeCastlingRights(short change) {
		this.castlingRights = (short) (this.castlingRights & (~change));
	}
	
	/**
	 * This method takes a byte and sets the 1s in the byte to 1 in castlingRights.
	 * 
	 * @param change Which castling rights should be added.
	 */
	public void addCastlingRights(short change) {
		this.castlingRights = (short) (this.castlingRights | change);
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
		StringBuilder squareString = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				squareString.append(square[i][j]);
			}
		}
		if (toMove) {
		    squareString.append((byte) 0);
		} else {
		    squareString.append((byte) 1);
		}
		return squareString.toString();
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

	public int[] getRootMoves() {
		return rootMoves;
	}

	public void setRootMoves(int[] rootMoves) {
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

	private static byte pieceLetterToByte(char piece) {
	    byte pieceInt = 0;
	    switch (piece) {
	        case 'k': pieceInt = -6; break;
            case 'q': pieceInt = -5; break;
            case 'r': pieceInt = -4; break;
            case 'b': pieceInt = -3; break;
            case 'n': pieceInt = -2; break;
            case 'p': pieceInt = -1; break;
            case 'P': pieceInt = 1; break;
            case 'N': pieceInt = 2; break;
            case 'B': pieceInt = 3; break;
            case 'R': pieceInt = 4; break;
            case 'Q': pieceInt = 5; break;
            case 'K': pieceInt = 6; break;
        }

        return pieceInt;
    }

	public MoveGenerator getMoveGenerator() {
		return moveGenerator;
	}

	public void setMoveGenerator(MoveGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public void resetBoard() {
		moveGenerator.resetMoveGenerator();
		evaluation.resetEvaluation();
		search.resetSearch();

	}
}
