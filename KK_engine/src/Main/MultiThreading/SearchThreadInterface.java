package Main.MultiThreading;

import Main.engine.BoardInterface;

public interface SearchThreadInterface extends Runnable {

    BoardInterface getBoard();

    void setDepth(int depth);

    void setTimeLimit(int timeLimit);

    void setBoard(BoardInterface board);

    void setHardTimeLimit(long hardTimeLimit);
}
