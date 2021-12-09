package test;

import Main.engine.Board;
import Main.engine.BoardInterface;
import Main.engine.MoveGenerator;
import Main.engineIO.Logging;

import java.util.Arrays;

/**
 * This class provides methods for assertion testing.
 * @author Tom Marvolo
 *
 */
public final class Assertions {

	public static boolean attackBoard(BoardInterface board) {
	    int[] attackMoves = board.getAttackBoard().moveGenerator(new int[256], board.getToMove());
		int attackSize = attackMoves[0];
		int[] genMoves = board.getMoveGenerator().collectMoves(board.getToMove(), new int[MoveGenerator.MAX_MOVE_COUNT], new int[6]);
		int moveSize = genMoves[0];
		if (moveSize != -1) {
            if (attackSize != moveSize) {
            	Arrays.sort(attackMoves);
            	Arrays.sort(genMoves);
                assert false;
            }
        }
		return true;
	}
	
	private Assertions() {
	}
}
