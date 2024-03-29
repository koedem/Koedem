package Main.engine;

import Main.engineIO.Logging;
import Main.engineIO.Transformation;

import java.util.Arrays;

public class BitBoard implements BitBoardInterface {

    private BoardInterface board;
    private AttackBoard attackBoard;

	private long[][][] bitBoards = new long[2][7][10];  // 2 colours, 7 piece types, max. 10 pieces per piece type
                                                        // bitBoards[x][0][y] for padding to normal piece values

	private long[][] pieceTypes = { { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 } };
	                                                            // 2 colours, 6 piece types, pieceTypes[x][0] for
                                                                // padding to normal piece values

	private long[] allPieces = { 0, 0 }; // 2 colours
	
	public BitBoard(BoardInterface board) {
		for (long[][] bitBoard : bitBoards) {
			for (long[] longs : bitBoard) {
				// 0 means no piece on any square yet / empty board
				Arrays.fill(longs, 0);
			}
		}
        attackBoard = new AttackBoard(board, this);
		this.board = board;
	}
	
	public boolean move(int startSquare, int endSquare, boolean capture, int capturedPiece) {
		long searchedBB = 1L << startSquare;
		long bitBoardChange = searchedBB ^ (1L << endSquare); // XOR old square out, new square in (XOR o XOR = id)
		boolean success = false;
		if (capture) { // a capture happens, remove captured piece
			remove(endSquare, false);
		}
		
		for (int colour = 0; colour < bitBoards.length; colour++) {
			if ((allPieces[colour] & searchedBB) != 0) {
				for (int piece = 0; piece < bitBoards[colour].length; piece++) {
					if ((pieceTypes[colour][piece] & searchedBB) != 0) {
						for (int index = 0; index < bitBoards[colour][piece].length; index++) {
							if ((bitBoards[colour][piece][index] & searchedBB) != 0) {
								success = board.getAttackBoard().move(colour, piece, index, startSquare, endSquare, capture, capturedPiece != 0); // TODO
								bitBoards[colour][piece][index] ^= bitBoardChange;
								pieceTypes[colour][piece] ^= bitBoardChange;
								allPieces[colour] ^= bitBoardChange;
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		if (capturedPiece != 0) { // a capture gets undone, put piece back on the board
			add(capturedPiece > 0 ? capturedPiece : -capturedPiece, capturedPiece > 0 ? 0 : 1, startSquare);
		}
		return success;
	}

    /**
     * TODO: return Attackboard
     * @param square
     */
	public void remove(int square, boolean unblockSquare) {
        long searchedBB = 1L << square;

        for (int colour = 0; colour < bitBoards.length; colour++) {
            if ((allPieces[colour] & searchedBB) != 0) {
                for (int piece = 0; piece < bitBoards[colour].length; piece++) {
                    if ((pieceTypes[colour][piece] & searchedBB) != 0) {
                        for (int index = 0; index < bitBoards[colour][piece].length; index++) {
                            if ((bitBoards[colour][piece][index] & searchedBB) != 0) {
                                attackBoard.remove(colour, piece, index, unblockSquare);
                                bitBoards[colour][piece][index] = 0;
                                pieceTypes[colour][piece] ^= searchedBB; // XOR = XOR^-1
                                allPieces[colour] ^= searchedBB;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     *
     * @param piece The piece type to be added. Bigger than 0.
     * @param colour 0 for White piece, 1 for Black piece.
     * @param square The square on which the piece gets added.
     */
	public void add(int piece, int colour, int square) {
        for (int index = 0; index < bitBoards[colour][piece].length; index++) {
            if (bitBoards[colour][piece][index] == 0) {
                setBitBoard(colour, piece, index, square);
                attackBoard.add(colour, piece, index, square);
                break;
            }
        }
	}
	
	public void setBitBoard(int colour, int piece, int index, int square) {
		long newBB = 1L << square;
		bitBoards[colour][piece][index] = newBB;
		pieceTypes[colour][piece] |= newBB;
		allPieces[colour] |= newBB;
	}
	
	public long getBitBoard(int colour, int piece, int index) {
		return bitBoards[colour][piece][index];
	}
	
	@Override
	public long getPieceTypeBoard(int colour, int piece) {
		return pieceTypes[colour][piece];
	}
	
	@Override
	public long getAllPieces(int colour) {
		return allPieces[colour];
	}

    /**
     * This method should only be used for testing
     * @param square The square to be probed. [0, 63]
     * @return The piece on that square according to the bitboard. [-6, 6]
     */
	public int getSquare(int square) {
        long searchedBoard = 1L << square;
        int pieceValue = 0;
        boolean colour = false;
        for (int i = 0; i < 2; i++) {
            if ((searchedBoard & allPieces[i]) != 0) {
                assert !colour; // a piece can't be a black and a white piece at the same time
                colour = true;
                boolean pieceType = false;
                for (int j = 0; j < pieceTypes[i].length; j++) {
                    if ((searchedBoard & pieceTypes[i][j]) != 0) {
                        assert !pieceType;
                        pieceType = true;
                        pieceValue = j;
                        boolean index = false;
                        for (int k = 0; k < bitBoards[i][j].length; k++) {
                            if ((searchedBoard & bitBoards[i][j][k]) != 0) {
                                assert !index;
                                index = true;
                            }
                        }
                        assert index;
                    }
                }
                if (i == 1) {
                    pieceValue *= -1;
                }
                assert pieceType;
            }
        }
        return pieceValue;
    }

    public byte[][] generateBoard() {
	    byte[][] squares = new byte[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = (byte) getSquare(i * 8 + j);
            }
        }
	    return squares;
    }

    public void printBitBoard() {
        byte[][] squares = generateBoard();
        Logging.printLine("");
        for (int i = 7; i >= 0; i--) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                row.append(Transformation.numberToPiece(squares[j][i])).append(" ");
            }

            Logging.printLine(row.toString());
        }
        Logging.printLine("");
    }

	public AttackBoard getAttackBoard() {
		return attackBoard;
	}

	public void setAttackBoard(AttackBoard attackBoard) {
		this.attackBoard = attackBoard;
	}

	public void resetBitBoard() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 7; j++) {
				for (int k = 0; k < 10; k++) {
					bitBoards[i][j][k] = 0;
				}
				pieceTypes[i][j] = 0;
			}
			allPieces[i] = 0;
		}
		attackBoard.resetAttackBoard();
	}
}
