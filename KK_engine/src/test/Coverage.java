package test;

import static org.junit.Assert.*;

import org.junit.*;

import engine.Board;
import engine.MoveGenerator;
import engine.Search;
import engineIO.Logging;

public class Coverage {

	@Test
	public void coverage() {
		Logging.setup();
		Board test = new Board();
		int[] movesSize = new int[6];
		test.setRootMoves(MoveGenerator.collectMoves(test, test.getToMove(), movesSize));
		int[] pv = Search.rootMax(test, test.getToMove(), 7, 10000000);
		assertTrue(pv != null);
	}
}
