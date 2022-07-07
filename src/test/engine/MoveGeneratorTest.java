package test.engine;

import org.junit.jupiter.api.Test;

import Main.engine.Board;
import Main.engine.MoveGenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
public class MoveGeneratorTest {

	Board         board         = new Board("8/8/8/4P3/3p1q2/8/8/8 w - - 0");
	MoveGenerator moveGenerator = new MoveGenerator(board);

	@Test
	public void pawnMove() throws Exception {
		int[] moves = moveGenerator.collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6]);
		assertTrue(moves[0] == 3);
	}
}
