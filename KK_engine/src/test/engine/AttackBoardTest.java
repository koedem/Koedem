package test.engine;

import Main.engine.Board;

import Main.engine.AttackBoard;
import Main.engine.BoardInterface;
import Main.engine.MoveGenerator;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AttackBoardTest {

    private BoardInterface board = new Board();

	@Test
	@Ignore
	public void pawnCaptureTables() {
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					int square = 7 + 8 * k - j;
					boolean set = ((1L << (square)) & AttackBoard.PAWN_CAPTURES[i]) != 0L;
					if (set) {
						System.out.print(1 + " ");
					} else {
						System.out.print(0 + " ");
					}
				}
				System.out.println();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\n\n\n");
		}
	}

	@Test
	@Ignore
	public void knightTables() {
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					int square = 7 + 8 * k - j;
					boolean set = ((1L << (square)) & AttackBoard.KNIGHT_BOARD[i]) != 0L;
					if (set) {
						System.out.print(1 + " ");
					} else {
						System.out.print(0 + " ");
					}
				}
				System.out.println();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\n\n\n");
		}
	}

	@Test
	@Ignore
	public void kingTables() {
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					int square = 7 + 8 * k - j;
					boolean set = ((1L << (square)) & AttackBoard.KING_BOARD[i]) != 0L;
					if (set) {
						System.out.print(1 + " ");
					} else {
						System.out.print(0 + " ");
					}
				}
				System.out.println();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\n\n\n");
		}
	}

	@Test
	@Ignore
	public void rookFiles() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					int square = 7 + 8 * k - j;
					boolean set = ((1L << (square)) & AttackBoard.NEGATED_ROOK_FILES[i]) != 0L;
					if (set) {
						System.out.print(1 + " ");
					} else {
						System.out.print(0 + " ");
					}
				}
				System.out.println();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\n\n\n");
		}
	}

	@Test
	@Ignore
	public void rookRows() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					int square = 7 + 8 * k - j;
					boolean set = ((1L << (square)) & AttackBoard.NEGATED_ROOK_ROWS[i]) != 0L;
					if (set) {
						System.out.print(1 + " ");
					} else {
						System.out.print(0 + " ");
					}
				}
				System.out.println();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\n\n\n");
		}
	}

	@Test
	@Ignore
	public void diagonalsUp() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					int square = 7 + 8 * k - j;
					boolean set = ((1L << (square)) & AttackBoard.NEGATED_UP_DIAGONALS[i]) != 0L;
					if (set) {
						System.out.print(1 + " ");
					} else {
						System.out.print(0 + " ");
					}
				}
				System.out.println();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\n\n\n");
		}
	}

	@Test
	@Ignore
	public void diagonalsDown() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					int square = 7 + 8 * k - j;
					boolean set = ((1L << (square)) & AttackBoard.NEGATED_DOWN_DIAGONALS[i]) != 0L;
					if (set) {
						System.out.print(1 + " ");
					} else {
						System.out.print(0 + " ");
					}
				}
				System.out.println();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\n\n\n");
		}
	}

	@Test
	public void correctBoards() {
        board = new Board("8/4kn2/4nn2/8/8/8/4NN2/4KN2 w - - 0 1");
	    AttackBoard attack = board.getAttackBoard();
	    attack.move(0, 2, 0, 0, 33);
        attack.move(0, 2, 1, 0, 40);
        attack.move(0, 2, 2, 0, 41);
        attack.move(0, 6, 0, 0, 32);
        attack.move(1, 2, 0, 0, 37);
        attack.move(1, 2, 1, 0, 45);
        attack.move(1, 2, 2, 0, 46);
        attack.move(1, 6, 0, 0, 38);
        int        attackSize = attack.moveGenerator(new int[256], true)[0];
        int        moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
        Assertions.assertEquals(attackSize, moveSize);
    }

    @Test
	public void standAloneRookMoves() {
		board.setFENPosition("3k4/3p4/p5p1/4p3/1p5p/5p2/8/R7 w - - 0 9");
		AttackBoard attackBoard = board.getAttackBoard();
		board.makeMove((((1 << 6) + 0) << 6) + 32);
		board.changeToMove();
	    board.makeMove((((1 << 6) + 32) << 6) + 33);
	    board.changeToMove();
	    int        attackSize = attackBoard.moveGenerator(new int[256], true)[0];
	    int        moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
	    Assertions.assertEquals(attackSize, moveSize);
	    board.makeMove((((1 << 6) + 33) << 6) + 1);
	    board.changeToMove();
	    attackSize = attackBoard.moveGenerator(new int[256], true)[0];
	    moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
	    Assertions.assertEquals(attackSize, moveSize);
	    board.makeMove((((1 << 6) + 1) << 6) + 4);
	    board.changeToMove();
	    attackSize = attackBoard.moveGenerator(new int[256], true)[0];
	    moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
	    Assertions.assertEquals(attackSize, moveSize);
	    board.makeMove((((1 << 6) + 4) << 6) + 12);
	    board.changeToMove();
	    attackSize = attackBoard.moveGenerator(new int[256], true)[0];
	    moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
	    Assertions.assertEquals(attackSize, moveSize);
	    board.makeMove((((1 << 6) + 12) << 6) + 13);
	    board.changeToMove();
	    attackSize = attackBoard.moveGenerator(new int[256], true)[0];
	    moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
	    Assertions.assertEquals(attackSize, moveSize);
	    board.makeMove((((1 << 6) + 13) << 6) + 45);
	    board.changeToMove();
	    attackSize = attackBoard.moveGenerator(new int[256], true)[0];
	    moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
	    Assertions.assertEquals(attackSize, moveSize);
	    board.makeMove((((1 << 6) + 45) << 6) + 43);
	    board.changeToMove();
	    attackSize = attackBoard.moveGenerator(new int[256], true)[0];
	    moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
	    Assertions.assertEquals(attackSize, moveSize);
	    board.makeMove((((1 << 6) + 43) << 6) + 35);
	    board.changeToMove();
	    attackSize = attackBoard.moveGenerator(new int[256], true)[0];
	    moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
	    Assertions.assertEquals(attackSize, moveSize);
    }

	@Test
	public void standAloneBishopMoves() {
		board.setFENPosition("3k4/1b5p/4pp2/prp1p2n/3pqb2/1p5n/B7/8 w - - 0 1");
		AttackBoard attackBoard = board.getAttackBoard();
		board.makeMove((((1 << 6) + 1) << 6) + 8);
		board.changeToMove();
		board.makeMove((((1 << 6) + 8) << 6) + 26);
		board.changeToMove();
		int attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		int moveSize   = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 26) << 6) + 19);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 19) << 6) + 28);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 28) << 6) + 21);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 21) << 6) + 39);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 39) << 6) + 53);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 53) << 6) + 44);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 44) << 6) + 51);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 51) << 6) + 42);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
	}

	@Test
	public void standAloneQueenMoves() {
		board.setFENPosition("3b4/1b5p/4pp2/prp1p2n/1Q1pqk2/1p5n/8/8 w - - 0 6");
		AttackBoard attackBoard = board.getAttackBoard();
		board.makeMove((((1 << 6) + 11) << 6) + 3);
		board.changeToMove();
		board.makeMove((((1 << 6) + 3) << 6) + 1);
		board.changeToMove();
		board.makeMove((((1 << 6) + 1) << 6) + 8);
		board.changeToMove();
		board.makeMove((((1 << 6) + 8) << 6) + 26);
		board.changeToMove();
		int        attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		int        moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 26) << 6) + 19);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 19) << 6) + 28);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 28) << 6) + 30);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 30) << 6) + 39);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 39) << 6) + 63);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 63) << 6) + 54);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
		board.makeMove((((1 << 6) + 54) << 6) + 48);
		board.changeToMove();
		attackSize = attackBoard.moveGenerator(new int[256], true)[0];
		moveSize = board.getMoveGenerator().collectMoves(true, new int[MoveGenerator.MAX_MOVE_COUNT], new int[6])[0];
		Assertions.assertEquals(attackSize, moveSize);
	}
}
