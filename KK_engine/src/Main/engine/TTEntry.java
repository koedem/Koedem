package Main.engine;

import java.io.Serializable;

/**
 *
 */
public class TTEntry implements Serializable {

	private int depth = 0;
	private int move = 0;
	private int eval = 0;
	private long allInformation = 0;
		// made of 16 Bit move, 16 Bit eval, 8 Bit depth. TODO more bits for hashing

	public TTEntry() {
	}

	public void setDepth(int depth) {
		this.depth = depth;
		allInformation &= 0xFFFFFFFFFFFFFF00L;
		allInformation |= depth;
	}

	public void setMove(int move) {
		this.move = move;
		allInformation &= 0xFFFFFF0000FFFFFFL;
		allInformation |= ((long) move) << 24;
	}

	public void setEval(int eval) {
		this.eval = eval;
		allInformation &= 0xFFFFFFFFFF0000FFL;
		allInformation |= eval << 8;
	}

	public void setAllInformation(long allInformation) {
		this.allInformation = allInformation;
		this.depth = (int)(byte) allInformation & 0xFF;
		this.eval = (int)(short)((allInformation >> 8) & 0xFFFF);
		this.move = (int)(short)((allInformation >> 24) & 0xFFFF);
	}

	public long getAllInformation() {
		return allInformation;
	}

	public int getDepth() {
		return depth;
	}

	public int getMove() {
		return move;
	}

	public int getEval() {
		return eval;
	}
}
