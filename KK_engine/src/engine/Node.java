package engine;

import engineIO.Logging;
import engineIO.Transformation;

public class Node {

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
			String row = "";
			for (int j = 0; j < 8; j++) {
				row += Transformation.numberToPiece(squares.charAt(j * 8 + i)) + " ";
			}
			Logging.printLine(row);
		}
		Logging.printLine("");
		Logging.printLine("Eval: " + evaluation + " at depth " + depth + ". Best move from here is: "
				+ Transformation.numberToMove(bestMove));
		Logging.printLine("");
	}
}
