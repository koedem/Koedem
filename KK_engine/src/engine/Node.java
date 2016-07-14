package engine;

import java.util.List;
import java.util.ArrayList;

public class Node {

	protected int evaluation;
	protected int depth;
	protected int bestMove;
	protected boolean toMove;
	protected List<Byte> squares;
	
	public Node(Board board, int eval, int depth, int bestMove, boolean toMove) {
		evaluation = eval;
		this.depth = depth;
		this.bestMove = bestMove;
		this.toMove = toMove;
		squares = new ArrayList<Byte>();
		for (int i = 0; i < 8; i++) {
		    for (int j = 0; j < 8; j++) {
		        squares.add(board.getSquare(i, j));
		    }
		}
		if (toMove) {
		    squares.add((byte) 0);
		} else {
		    squares.add((byte) 1);
		}
		board.putHashTableElement(this);
	}
	
	public void print() {
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < 8; j++) {
				System.out.print(Transformation.numberToPiece(squares.get(j * 8 + i)) + " ");
			}
			System.out.println();
		}
		System.out.println("\nEval: " + evaluation + " at depth " + depth + ". Best move from here is: "
				+ Transformation.numberToMove(bestMove) + "\n");
	}
}
