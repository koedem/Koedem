package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import engine.BitBoard;

public class BitBoardTest {

	@Test
	public void bitboards() {
		BitBoard bitboard = new BitBoard();
		bitboard.setBitBoard(0, 3, 0, 17); // add some pieces
		bitboard.setBitBoard(0, 3, 1, 23);
		bitboard.setBitBoard(0, 3, 4, 45);
		assertTrue(bitboard.move(45, 63)); // we have a piece on 45, should successfully play 45-63
		for (int i = 0; i < 1000000000; i++) { // not needed; just there to measure speed
			bitboard.move(62 - (i % 63), i % 62);
		}
		assertTrue((1L << 63) == bitboard.getBitBoard(0, 3, 4)); // verify that move 45-63 was successful 
																// by checking whether piece landed on 63
	}
}
