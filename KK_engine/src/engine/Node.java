package engine;

import engineIO.Logging;
import engineIO.Transformation;

import java.io.Serializable;

public class Node implements Serializable {

	protected int evaluation;
	protected int depth;
	protected int bestMove;
	protected boolean toMove;
	protected String squares;
	
	public Node(Board board, int eval, int depth, int bestMove, boolean toMove) {
		evaluation = eval;
		this.depth = depth;
		this.bestMove = bestMove;
		this.toMove = toMove;
		squares = "";
		for (int i = 0; i < 8; i++) {
		    for (int j = 0; j < 8; j++) {
		        squares += board.getSquare(i, j);
		    }
		}
		if (toMove) {
		    squares += (byte) 0;
		} else {
		    squares += (byte) 1;
		}
		board.putHashTableElement(this);
	}
	
	public void print() {
		for (int i = 7; i >= 0; i--) {
			StringBuilder row = new StringBuilder();
			for (int j = 0; j < 8; j++) {
				row.append(Transformation.numberToPiece(squares.charAt(j * 8 + i))).append(" ");
			}
			Logging.printLine(row.toString());
		}
		Logging.printLine("");
		Logging.printLine("Eval: " + evaluation + " at depth " + depth + ". Best move from here is: "
				+ Transformation.numberToMove(bestMove));
		Logging.printLine("");
	}
}
