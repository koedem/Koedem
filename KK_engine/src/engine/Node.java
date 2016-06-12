package engine;

import java.util.List;

public class Node {

	protected int evaluation;
	protected int depth;
	protected int bestMove;
	protected List<Byte> squares;
	
	public Node(Board board, int eval, int depth, int bestMove, List<Byte> squares) {
		evaluation = eval;
		this.depth = depth;
		this.bestMove = bestMove;
		this.squares = squares;
		board.putHashTableElement(this);
	}
	
	public void print() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				System.out.print(squares.get(i * 8 + j) + " ");
			}
			System.out.println();
			System.out.println("Eval: " + evaluation + " at depth " + depth + ". Best move from here is: "
					+ Transformation.numberToMove(bestMove));
		}
	}
}
