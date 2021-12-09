package Main.engine;

import Main.Utility.Constants;
import Main.Utility.DeepCopy;
import Main.engineIO.Logging;
import Main.engineIO.Transformation;

import java.io.Serializable;
import java.util.*;

public class Board implements BoardInterface {

	private final MoveGeneratorInterface    moveGenerator    = new MoveGenerator(this);
	private final CaptureGeneratorInterface captureGenerator = new CaptureGenerator(this);

	private long zobristHash;
	private static final Random random = new Random(1234567890);
	private static final long[] zobristKeys = initializeZobrist();
	private static final long blackToMove = random.nextLong();

	private final BitBoardInterface bitboard;
	private final AttackBoard       attackBoard;

	/**
	 * Pawn = 1, Knight = 2, Bishop = 3, Rook = 4, Queen = 5, King = 6. 
	 * White pieces get positive values, black pieces negative ones, empty squares a 0.
	 * Format as fileRow, each reduced by one. Example: d6 becomes 3, 5.
	 */
	private final byte[][] square = new byte[8][8];
	
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
	 * Square on which an en passant capture would be legal. Default -1 ( = a0).
	 * Format: fileRow, 26 = 3 * 8 + 2 = d3.
	 */
	private byte enPassant = -1;
	
	private int moveNumber = 1;

	private int[] rootMoves = new int[MoveGenerator.MAX_MOVE_COUNT];

	private final MakeMoveInfoStack infoStack = new MakeMoveInfoStack();
	
	/**
	 * Constructor, create new Board and set up the chess start position
	 */
	public Board() {
	    bitboard = new BitBoard(this);
	    attackBoard = bitboard.getAttackBoard();
		setFENPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); // fen
																					// of
																					// start
																					// position
	}
	
	public Board(String fen) {
		bitboard = new BitBoard(this);
		attackBoard = bitboard.getAttackBoard();
		setFENPosition(fen);
	}
	
	public BoardInterface cloneBoard() {
		return (BoardInterface) DeepCopy.copy(this);
	}
	
	/**
	 * This method takes a fen code and sets that position on the board.
	 * 
	 * @param fen
	 *            : Position that the method sets.
	 */
	public void setFENPosition(String fen) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				setSquare(i, j, (byte) 0);
			}
		}
		setEnPassant((byte) -1);
		setCastlingRights((byte) 0);
        bitboard.resetBitBoard();

		String[] positions = fen.split(" ");
		byte file = 0;
		byte row = 7;
		for (int i = 0; i < positions[0].length(); i++) {
            if (positions[0].charAt(i) == '/') {
                row--;
                file = 0;
            } else if (Character.isDigit(positions[0].charAt(i))){
                int emptySquares = Character.getNumericValue(fen.charAt(i));
                for (int j = 0; j < emptySquares; j++) {
                    setSquare(file, row, (byte) 0);
                    file++;
                }
            } else {
                byte piece = pieceLetterToByte(positions[0].charAt(i));
                setSquare(file, row, piece);
                bitboard.add(Math.abs(piece), (piece > 0) ? 0 : 1, (8 * file + row));
                file++;
            }
		}
		switch (positions[1]) {
			case "w":
				setToMove(true);
				break;
			case "b":
				setToMove(false);
				break;
			default:
				throw new IllegalArgumentException();
		}
		setCastlingRights(positions[2]);
		if (!(positions[3].equals("-"))) {
			setEnPassant((byte) (Character.getNumericValue(positions[3].charAt(1) - 1) // a6 becomes 5
					+ (Character.getNumericValue(positions[3].charAt(0)) - 10) * 8)); // + 0 * 8
		} else {
			setEnPassant((byte) -1);
		}
		setMoveNumber(Integer.parseInt(positions[positions.length - 1]));
		attackBoard.generateAttackCount();
	}

	private void setCastlingRights(String castling) {
		byte castlingRights = 0;
		for (int i = 0; i < castling.length(); i++) {
			if (castling.charAt(i) == 'K') {
				castlingRights = (byte) (castlingRights | 0x18);
			} else if (castling.charAt(i) == 'Q') {
				castlingRights = (byte) (castlingRights | 0x30);
			} else if (castling.charAt(i) == 'k') {
				castlingRights = (byte) (castlingRights | 0x3);
			} else if (castling.charAt(i) == 'q') {
				castlingRights = (byte) (castlingRights | 0x6);
			}
		}
		setCastlingRights(castlingRights);
	}

	/**
	 * Set the private castlingRights variable to a new value, updating the zobrist hash in the process
	 * @param newValue
	 */
	private void setCastlingRights(byte newValue) {
		int change = this.castlingRights ^ newValue;
		this.castlingRights = newValue;
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
				row.append(Transformation.numberToPiece(getSquare(j, i))).append(" ");
			}

			Logging.printLine(row.toString());
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

	@Override
	public void setSquare(int file, int row, byte value) {
		zobristHash ^= zobristKeys[(getSquare(file, row) + 6) * 64 + (file * 8 + row)]; // remove old value
		square[file][row] = value;
		zobristHash ^= zobristKeys[(getSquare(file, row) + 6) * 64 + (file * 8 + row)]; // add new value
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
	 *
	 * TODO rework control flow especially with castling
	 */
	public boolean makeMove(int move) {
		int endSquare; // will get changed to "true" endSquare
		if (move < (1 << 13)) {
			endSquare = move % 64;
		} else {
			endSquare = (move / 8) % 64;
		}

		int capturedPiece = getSquare(endSquare / 8, endSquare % 8);
		infoStack.push(getEnPassant(), getCastlingRights(), (byte) capturedPiece);
		
		if (move < (1 << 13) && move > (1 << 12)) {
			int startSquare = (move / 64) % 64;
			int movingPiece = getSquare(startSquare / 8, startSquare % 8);
			int movingPieceType = Math.abs(movingPiece);
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
			
			if (startSquare == 32 && getSquare(4, 0) == 6 && (endSquare == 48 || endSquare == 16)) {
				if (endSquare == 48) {
					setSquare(6, 0, (byte) 6);
					setSquare(4, 0, (byte) 0);
					setSquare(5, 0, (byte) 4); // rook move in castling
					setSquare(7, 0, (byte) 0);

					bitboard.move(startSquare, endSquare, capturedPiece != 0, 0);
					bitboard.remove(56, true);
					bitboard.add(4, 0, 40);
				} else {
					setSquare(2, 0, (byte) 6);
					setSquare(4, 0, (byte) 0);
					setSquare(3, 0, (byte) 4); // rook move in castling
					setSquare(0, 0, (byte) 0);

					bitboard.move(startSquare, endSquare, capturedPiece != 0, 0);
					bitboard.remove(0, true);
					bitboard.add(4, 0, 24);
				}
			} else if (startSquare == 39 && getSquare(4, 7) == -6 && (endSquare == 55 || endSquare == 23)) {
				if (endSquare == 55) {
					setSquare(6, 7, (byte) -6);
					setSquare(4, 7, (byte) 0);
					setSquare(5, 7, (byte) -4); // rook move in castling
					setSquare(7, 7, (byte) 0);

					bitboard.move(startSquare, endSquare, capturedPiece != 0, 0);
					bitboard.remove(63, true);
					bitboard.add(4, 1, 47);
				} else {
					setSquare(2, 7, (byte) -6);
					setSquare(4, 7, (byte) 0);
					setSquare(3, 7, (byte) -4); // rook move in castling
					setSquare(0, 7, (byte) 0);

					bitboard.move(startSquare, endSquare, capturedPiece != 0, 0);
					bitboard.remove(7, true);
					bitboard.add(4, 1, 31);
				}
			} else {
				if (movingPieceType == Constants.PAWN && endSquare == enPassant) {
					if (toMove) {
						setSquare(enPassant / 8, (enPassant % 8) - 1, (byte) 0); // capture the pawn that is on the square before ep
						bitboard.remove(endSquare - 1, true);
					} else {
						setSquare(enPassant / 8, (enPassant % 8) + 1, (byte) 0);
						bitboard.remove(endSquare + 1, true);
					}
				}

				setSquare(endSquare / 8, endSquare % 8, getSquare(startSquare / 8, startSquare % 8)); // the actual moving
				setSquare(startSquare / 8, startSquare % 8, (byte) 0); // start square becomes empty

				bitboard.move(startSquare, endSquare, capturedPiece != 0, 0);
			}
			setEnPassant((byte) -1); // remove old en passant values
			
			if (movingPieceType == Constants.PAWN && Math.abs(startSquare - endSquare) == 2
				&& (endSquare > 8 && getSquare((endSquare - 8) / 8, endSquare % 8) == -movingPiece  // only set if there is an opposing pawn that could take
				    || endSquare < 56 && getSquare((endSquare + 8) / 8, endSquare % 8) == -movingPiece)) {
																				// if a pawn moves two squares far
				setEnPassant((byte) ((startSquare + endSquare) / 2));  			// we update the en passant to be 
																				// in the middle of start/end square
			}

		} else if (move < (1 << 16) && move > (1 << 15)) {
			int startSquare = (move - (1 << 15)) / (1 << 9);
			endSquare = (move % (1 << 9)) / (1 << 3); // TODO why is this already initialized?
			if (endSquare == Constants.A1) { // If Ra1 gets captured we can't castle queenside anymore.
				removeCastlingRights((byte) 0x20);
			}

			if (endSquare == 7) {
				removeCastlingRights((byte) 0x4);
			}

			if (endSquare == 56) {
				removeCastlingRights((byte) 0x8);
			}

			if (endSquare == 63) {
				removeCastlingRights((byte) 0x1);
			}
			
			byte promotion = (byte) (move % (1 << 3));
			
			setSquare(startSquare / 8, startSquare % 8, (byte) 0);
			
			if (endSquare % 8 == 7) {
				setSquare(endSquare / 8, endSquare % 8, promotion);
			} else if (endSquare % 8 == 0) {
				setSquare(endSquare / 8, endSquare % 8, (byte) -promotion);
			}

			bitboard.move(startSquare, endSquare, capturedPiece != 0, 0);
			bitboard.remove(endSquare, false); // the actual promotion, remove the pawn
			bitboard.add(promotion, (toMove) ? 0 : 1, endSquare); // add the piece
		} else {
			assert false;
		}
		changeToMove();
		return false;
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
		if (toMove != newToMove) {
			changeToMove();
		}
	}

	/**
	 * negate the toMove parameter
	 */
	public void changeToMove() {
		this.toMove = (!toMove);
		zobristHash ^= blackToMove;
	}

	/**
	 * This method undoes the given integer format move.
	 * Syntax for non pawn promotions: 0...01ssseee, for pawn promotions: 0...01ssseeeppp
	 * s = bits of the start square, e = bits of the end square,
	 * p = promotion piece according to Transformation.stringToPiece
	 * TODO why don't we remember the incrementally updated eval changed from move instead of recalculating
	 * 
	 * @param move Move which gets undone.
	 */
	public void unmakeMove(int move) {
		MakeMoveCache info = infoStack.pop();
		byte enPassant = info.enPassant;
		byte oldCastlingRights = info.castlingRights;
		byte capturedPiece = info.capturedPiece;

		setEnPassant(enPassant);
		int endSquare = 0; // will get changed to correct endSquare
		
		if (oldCastlingRights != castlingRights && (move == 6160 || move == 6192 || move == 6615 || move == 6647)) {
			if (move == (1 << 12) + (32 << 6) + 16) { // White castle queen side.
				assert getSquare(4, 0) == 0 && getSquare(3, 0) == 4 && getSquare(2, 0) == 6 
						&& getSquare(1,0) == 0 && getSquare(0, 0) == 0;
				setSquare(4, 0, (byte) 6); // King move gets undone.
				setSquare(2, 0, (byte) 0);
				setSquare(3, 0, (byte) 0); // Rook move get undone.
				setSquare(0, 0, (byte) 4);
				bitboard.remove(24, true);
				bitboard.add(4, 0, 0);
				bitboard.move(16, 32, false, 0);
			} else if (move == (1 << 12) + (32 << 6) + 48) { // White castle king side.
				assert getSquare(4, 0) == 0 && getSquare(5, 0) == 4 && getSquare(6, 0) == 6 && getSquare(7 , 0) == 0;
				setSquare(4, 0, (byte) 6); 
				setSquare(6, 0, (byte) 0);
				setSquare(5, 0, (byte) 0); 
				setSquare(7, 0, (byte) 4);
				bitboard.remove(40, true);
				bitboard.add(4, 0, 56);
				bitboard.move(48, 32, false, 0);
			} else if (move == (1 << 12) + (39 << 6) + 23) { // Black castle queen side.
				assert getSquare(4, 7) == 0 && getSquare(3, 7) == -4 && getSquare(2, 7) == -6 
						&& getSquare(1, 7) == 0 && getSquare(0, 7) == 0;
				setSquare(4, 7, (byte) -6);
				setSquare(2, 7, (byte) 0);
				setSquare(3, 7, (byte) 0); 
				setSquare(0, 7, (byte) -4);
				bitboard.remove(31, true);
				bitboard.add(4, 1, 7);
				bitboard.move(23, 39, false, 0);
			} else { // Black castle king side.
				assert getSquare(4,7) == 0 && getSquare(5, 7) == -4 && getSquare(6, 7) == -6 && getSquare(7, 7) == 0;
				setSquare(4, 7, (byte) -6);
				setSquare(6, 7, (byte) 0);
				setSquare(5, 7, (byte) 0);
				setSquare(7, 7, (byte) -4);
				bitboard.remove(47, true);
				bitboard.add(4, 1, 63);
				bitboard.move(55, 39, false, 0);
			}
		} else if (move < (1 << 13) && move > (1 << 12)) {
			int startSquare = (move / 64) % 64;
			endSquare = move % 64;
			int movedPieceType = Math.abs(getSquare(endSquare / 8, endSquare % 8));
			
			setSquare(startSquare / 8, startSquare % 8, getSquare(endSquare / 8, endSquare % 8)); // actual moving
			setSquare(endSquare / 8, endSquare % 8, capturedPiece); // put captured piece back on its square
            boolean success = bitboard.move(endSquare, startSquare, false, capturedPiece);
            assert success;
			if (movedPieceType == 1 && endSquare == enPassant) {
				if (endSquare % 8 == 5) { // white captured en passant.
					assert endSquare - startSquare == -7 || endSquare - startSquare == 9;
					assert getSquare(endSquare / 8, (endSquare % 8) - 1) == 0; // should be empty
					setSquare(endSquare / 8, (endSquare % 8) - 1, (byte) -1); // pawn added back on; -1 because of en passant
                    bitboard.add(1, 1, endSquare - 1);
				} else if (endSquare % 8 == 2) {
					assert endSquare - startSquare == 7 || endSquare - startSquare == -9;
					assert getSquare(endSquare / 8, (endSquare % 8) + 1) == 0;
					setSquare(endSquare / 8, (endSquare % 8) + 1, (byte) 1);
					bitboard.add(1, 0, endSquare + 1);
				} else {
					assert false;
				}
			}
		} else if (move < (1 << 16) && move > (1 << 15)) {
			int startSquare = (move - (1 << 15)) / (1 << 9);
			endSquare = (move % (1 << 9)) / (1 << 3);
			byte promotion = (byte) (move % (1 << 3));
			
			assert getSquare(startSquare / 8, startSquare % 8) == 0;
			
			if (endSquare % 8 == 7) {
				setSquare(startSquare / 8, startSquare % 8, (byte) 1);
				bitboard.remove(endSquare, false); // the un-promotion, remove the piece
				bitboard.add(1, 0, endSquare); // add the pawn
				
				setSquare(endSquare / 8, endSquare % 8, capturedPiece); // bitboard change done below
			} else if (endSquare % 8 == 0) {
				setSquare(startSquare / 8, startSquare % 8, (byte) -1);
				bitboard.remove(endSquare, false);
				bitboard.add(1, 1, endSquare);
				
				setSquare(endSquare / 8, endSquare % 8, capturedPiece);
			} else {
				assert false;
			}
			bitboard.move(endSquare, startSquare, false, capturedPiece);
		} else {
			assert false;
		}
		changeToMove();
		setCastlingRights(oldCastlingRights);
	}
	
	@Override
	public byte getCastlingRights() {
		return castlingRights;
	}

	/**
	 * This method takes a byte and sets the 1s in the byte to 0 in castlingRights.
	 * 
	 * @param change Which castling rights should be removed.
	 */
	public void removeCastlingRights(byte change) {
		setCastlingRights((byte) (this.castlingRights & (~change)));
	}
	
	/**
	 * This method takes a byte and sets the 1s in the byte to 1 in castlingRights.
	 * 
	 * @param change Which castling rights should be added.
	 */
	public void addCastlingRights(byte change) {
		setCastlingRights((byte) (this.castlingRights | change));
	}

	public int getMoveNumber() {
		return moveNumber;
	}

	public void setMoveNumber(int moveNumber) {
		this.moveNumber = moveNumber;
	}
	
	public long calculateZobristHash() {
		long zobristHash = 0;
		int squareNumber = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				zobristHash ^= zobristKeys[(getSquare(i, j) + 6) * 64 + squareNumber];
				squareNumber++;
			}
		}
		if (!toMove) {
			zobristHash ^= blackToMove;
		}
		return zobristHash;
	}

	public long getZobristHash() {
		assert zobristHash == calculateZobristHash();
		return zobristHash;
	}

	public byte getEnPassant() {
		return enPassant;
	}

	public void setEnPassant(byte enPassant) {
		this.enPassant = enPassant;
	}

	public int[] getRootMoves() {
		return rootMoves;
	}

	public void setRootMoves(int[] rootMoves) {
		this.rootMoves = rootMoves;
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

	public MoveGeneratorInterface getMoveGenerator() {
		return moveGenerator;
	}

	public void resetBoard() {
        setFENPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		moveGenerator.resetMoveGenerator();
		captureGenerator.resetCaptureGenerator();
		infoStack.reset();
	}

	private static long[] initializeZobrist() {
		long[] zobristKeys = new long[13 * 64];
		for (int i = 0; i < 6 * 64; i++) {
			zobristKeys[i] = random.nextLong();
			zobristKeys[i + 7 * 64] = random.nextLong();
		}

		for (int i = 0; i < 64; i++) {
			zobristKeys[64 * 6 + i] = 0;
		}
		return zobristKeys;
	}


	/**
	 *
	 * @param file : position of the to be checked square is
	 * @param row : " "
	 * @param toMove : who to move it is
	 * @return 1 if the square is empty, 0 if the square is occupied by an enemy piece,
	 *  -1 if the square is either not on the board or occupied by a friendly piece
	 */
	public int isFreeSquare(int file, int row, boolean toMove) {
		byte isFree;
		if (file < 0 || file > 7 || row < 0 || row > 7) {
			isFree = -1;
		} else {
			byte squareValue = getSquare(file, row);
			if (toMove) {
				if (squareValue == 0) {
					isFree = 1;
				} else if (squareValue < 0) {
					isFree = 0;
				} else {
					isFree = -1;
				}
			} else {
				if (squareValue == 0) {
					isFree = 1;
				} else if (squareValue > 0) {
					isFree = 0;
				} else {
					isFree = -1;
				}
			}
		}
		return isFree;
	}

	@Override
	public AttackBoard getAttackBoard() {
		return attackBoard;
	}

	@Override
	public BitBoardInterface getBitboard() {
		return bitboard;
	}

	@Override
	public CaptureGeneratorInterface getCaptureGenerator() {
		return captureGenerator;
	}

	private static class MakeMoveCache implements Serializable {
		byte enPassant;
		byte castlingRights;
		byte capturedPiece;
	}

	private static class MakeMoveInfoStack implements Serializable {
		private final int capacity = 1000;
		int counter = 0;
		private final MakeMoveCache[] makeMoveInfo = new MakeMoveCache[capacity];

		MakeMoveInfoStack() {
			for (int i = 0; i < makeMoveInfo.length; i++) {
				makeMoveInfo[i] = new MakeMoveCache();
			}
		}

		private void push(byte enPassant, byte castlingRights, byte capturedPiece) {
			makeMoveInfo[counter].enPassant = enPassant;
			makeMoveInfo[counter].castlingRights = castlingRights;
			makeMoveInfo[counter].capturedPiece = capturedPiece;
			counter++;
		}

		private MakeMoveCache pop() {
			counter--;
			return makeMoveInfo[counter];
		}

		private void reset() {
			counter = 0;
		}
	}

}
