package Main.engine;

/**
 *
 */
public class CheckMoveGenerator implements CheckMoveGeneratorInterface {

	BoardInterface board;

	public CheckMoveGenerator(BoardInterface board) {
		this.board = board;
	}

	@Override
	public void resetCheckMoveGenerator() {

	}

	public int[] collectAllPNMoves(int[] storage, boolean toMove) {
		int[] movesSize = new int[6];
		storage = board.getMoveGenerator().collectMoves(toMove, storage, movesSize);
		if (storage[0] == -1) {
			return storage;
		}

		for (int index = 1; index <= storage[0]; index++) { // storage[0] = actual size of array excluding that entry itself
			int move = storage[index];
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (inCheck()) {
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
					// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
		}
		return storage;
	}

	public int[] collectPNSearchMoves(int[] storage, int[] checks, boolean toMove) {
		int[] movesSize = new int[6];
		storage = board.getMoveGenerator().collectMoves(toMove, storage, movesSize);
		if (storage[0] == -1) {
			return storage;
		}
		checks[0] = 0;
		for (int index = 1; index <= storage[0]; index++) { // storage[0] = actual size of array excluding that entry itself
			int move = storage[index];
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (inCheck()) {
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
					// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			} else {
				board.changeToMove();
				if (inCheck()) {
					checks[++checks[0]] = move;
					if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
						// in case index is the last element we simply reduce the size by one to delete it
						storage[index] = storage[storage[0]];
					}
					storage[0]--;
					index--;
				}
				board.changeToMove();
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
		}
		System.arraycopy(storage, 1, checks, checks[0] + 1, storage[0]); // add non-checks to the checks; all we did here was move ordering the checks to the front
		checks[0] += storage[0];
		return checks;
	}

	public int[] collectCheckMoves(int[] storage, int[] checks, boolean toMove) {
		int[] movesSize = new int[6];
		storage = board.getMoveGenerator().collectMoves(toMove, storage, movesSize);
		checks[0] = 0;
		if (storage[0] == -1) {
			return checks;
		}
		for (int index = 1; index <= storage[0]; index++) { // storage[0] = actual size of array excluding that entry itself
			int move = storage[index];
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);
			if (inCheck()) {
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
					// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			} else {
				board.changeToMove();
				if (inCheck()) {
					checks[++checks[0]] = move;
					if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
						// in case index is the last element we simply reduce the size by one to delete it
						storage[index] = storage[storage[0]];
					}
					storage[0]--;
					index--;
				}
				board.changeToMove();
			}
			board.setEnPassant(enPassant);
			board.unmakeMove(move, capturedPiece, castlingRights);
			board.addCastlingRights(castlingRights);
		}
		return checks;
	}

	private boolean inCheck() {
		boolean inCheck = false;
		int[] captures = board.getCaptureGenerator().collectCaptures(board.getToMove(), new int[256]); // TODO why toMove and not !toMove ??
		if (captures[0] == -1) {
			inCheck = true;
		}
		return inCheck;
	}
}
