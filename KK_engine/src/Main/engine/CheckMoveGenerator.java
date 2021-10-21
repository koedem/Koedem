package Main.engine;

/**
 *
 */
public class CheckMoveGenerator implements CheckMoveGeneratorInterface {

	BoardInterface board;
	private int[] movesSize = new int[6];

	public CheckMoveGenerator(BoardInterface board) {
		this.board = board;
	}

	@Override
	public void resetCheckMoveGenerator() {

	}

	public int[] collectAllPNMoves(int[] storage, boolean toMove) {
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
			if (board.getAttackBoard().inCheck(!board.getToMove())) { // we make a move, are we in check afterwards? We're not to move anymore so negation
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
					// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			}
			board.unmakeMove(move, capturedPiece, castlingRights, enPassant);
			board.addCastlingRights(castlingRights);
		}
		return storage;
	}

	public int[] collectPNSearchMoves(int[] storage, int[] checks, boolean toMove) {
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
			if (board.getAttackBoard().inCheck(!board.getToMove())) { // are we in check after moving? In that case remove the move
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
					// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			} else {
				if (board.getAttackBoard().inCheck(board.getToMove())) { // is the opponent in check after our move? In that case add to checks
					checks[++checks[0]] = move;
					if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
						// in case index is the last element we simply reduce the size by one to delete it
						storage[index] = storage[storage[0]];
					}
					storage[0]--;
					index--;
				}
			}
			board.unmakeMove(move, capturedPiece, castlingRights, enPassant);
			board.addCastlingRights(castlingRights);
		}
		System.arraycopy(storage, 1, checks, checks[0] + 1, storage[0]); // add non-checks to the checks; all we did here was move ordering the checks to the front
		checks[0] += storage[0];
		return checks;
	}

	public int[] collectCheckMoves(int[] storage, int[] checks, boolean toMove) {
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
			if (board.getAttackBoard().inCheck(!board.getToMove())) { // are we in check after moving? In that case delete that move
				if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
					// in case index is the last element we simply reduce the size by one to delete it
					storage[index] = storage[storage[0]];
				}
				storage[0]--;
				index--;
			} else {
				if (board.getAttackBoard().inCheck(board.getToMove())) { // is the opponent in check after our move? Then add to checks
					checks[++checks[0]] = move;
					if (index < storage[0]) { // "delete" entry index by overwriting it with previous last element;
						// in case index is the last element we simply reduce the size by one to delete it
						storage[index] = storage[storage[0]];
					}
					storage[0]--;
					index--;
				}
			}
			board.unmakeMove(move, capturedPiece, castlingRights, enPassant);
			board.addCastlingRights(castlingRights);
		}
		return checks;
	}
}
