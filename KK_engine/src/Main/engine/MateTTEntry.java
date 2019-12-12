package Main.engine;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 *
 */
public class MateTTEntry implements Serializable {

	private int     aggressiveDepth       = 0;
	private int     fullDepth             = 0;
	private int     aggressiveLosingDepth = 0;
	private int     fullLosingDepth       = 0;
	private int     move                  = 0;
	private int     mateScore             = 0;
	/**
	 * 0xMMmmmmLLllDDdd00 ;  M = mate score (positive or negative), m move, l non losing, d non winning, uppercase full
	 */
	private long    allInformation        = 0;

	public MateTTEntry() {
	}

	public void setMove(int move) {
		this.move = move;
		allInformation &= 0xFF0000FFFFFFFFFFL;
		allInformation |= ((long) move) << 40;
	}

	public void setMateScore(int mateScore) {
		this.mateScore = mateScore;
		allInformation &= 0x00FFFFFFFFFFFFFFL;
		allInformation |= ((long) mateScore) << 56;
	}

	public void setAggressiveDepth(int aggressiveDepth) {
		this.aggressiveDepth = aggressiveDepth;
		allInformation &= 0xFFFFFFFFFFFF00FFL;
		allInformation |= ((long) aggressiveDepth) << 8;
	}

	public void setFullDepth(int fullDepth) {
		this.fullDepth = fullDepth;
		allInformation &= 0xFFFFFFFFFF00FFFFL;
		allInformation |= ((long) fullDepth) << 16;
	}

	public void setAggressiveLosingDepth(int aggressiveLosingDepth) {
		this.aggressiveLosingDepth = aggressiveLosingDepth;
		allInformation &= 0xFFFFFFFF00FFFFFFL;
		allInformation |= ((long) aggressiveLosingDepth) << 24;
	}

	public void setFullLosingDepth(int fullLosingDepth) {
		this.fullLosingDepth = fullLosingDepth;
		allInformation &= 0xFFFFFF00FFFFFFFFL;
		allInformation |= ((long) fullLosingDepth) << 32;
	}

	public void setAllInformation(long allInformation) {
		this.allInformation = allInformation;
		this.move = (int) ((allInformation >> 40) & 0xFFFF);
		this.mateScore = (byte) ((allInformation >> 56) & 0xFF);
		this.aggressiveDepth = (int) ((allInformation >> 8) & 0xFF);
		this.fullDepth = (int) ((allInformation >> 16) & 0xFF);
		this.aggressiveLosingDepth = (int) ((allInformation >> 24) & 0xFF);
		this.fullLosingDepth = (int) ((allInformation >> 32) & 0xFF);
	}

	public long getAllInformation() {
		return allInformation;
	}

	public int getAggressiveDepth() {
		return aggressiveDepth;
	}

	public int getMove() {
		return move;
	}

	public int getMateScore() {
		return mateScore;
	}

	public int getFullDepth() {
		return fullDepth;
	}

	public int getAggressiveLosingDepth() {
		return aggressiveLosingDepth;
	}

	public int getFullLosingDepth() {
		return fullLosingDepth;
	}

	public void merge(@NotNull MateTTEntry entry) {
		if (entry.mateScore != 0) { // TODO rethink this, current idea: if so far no mate then iff the other score is mate our new score will be too
			setMateScore(entry.mateScore);
			setMove(entry.move);
		}
		if (entry.aggressiveDepth > aggressiveDepth) {
			setAggressiveDepth(entry.aggressiveDepth);
		}
		if (entry.fullDepth > fullDepth) {
			setFullDepth(entry.fullDepth);
		}
		if (entry.aggressiveLosingDepth > aggressiveLosingDepth) {
			setAggressiveLosingDepth(entry.aggressiveLosingDepth);
		}
		if (entry.fullLosingDepth > fullLosingDepth) {
			setFullLosingDepth(entry.fullLosingDepth);
		}
	}
}
