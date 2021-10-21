package Main.engine;

/**
 *
 */
public class MoveOrdering {

	private static MoveOrdering instance = new MoveOrdering();

	public void orderRootMoves(BoardInterface board) {
		int[] rootMoves = board.getRootMoves();
		int[] evaluations = new int[rootMoves.length];
		evaluations[0] = 40000; // this is bigger than any other eval so the below resorting works

		for (int moveIndex = 1; moveIndex <= rootMoves[0]; moveIndex++) {
			int move = rootMoves[moveIndex];
			byte capturedPiece;
			if (move < (1 << 13)) {
				capturedPiece = board.getSquare((move / 8) % 8, move % 8);
			} else {
				capturedPiece = board.getSquare((move / 64) % 8, (move / 8) % 8);
			}
			byte castlingRights = board.getCastlingRights();
			byte enPassant = board.getEnPassant();
			board.makeMove(move);

			int eval = evaluations[moveIndex] = -board.getSearch().memoryEfficientQSearch(board.getToMove(), -30000, 30000, 0);

			for (int i = moveIndex; i > 0; i--) {
				if (eval > evaluations[i - 1]) {
					rootMoves[i] = rootMoves[i - 1];
					evaluations[i] = evaluations[i - 1];
				} else {
					rootMoves[i] = move;
					evaluations[i] = eval;
					break;
				}
			}

			board.unmakeMove(move, capturedPiece, castlingRights, enPassant);
		}
	}

	public static MoveOrdering getInstance() {
		return instance;
	}

	private MoveOrdering() {
	}
}
