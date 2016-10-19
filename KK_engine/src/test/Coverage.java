package test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Ignore;
import org.junit.Test;

import engine.Board;
import engine.MultiThreadSearch;
import engineIO.Logging;
import engineIO.UCI;

public class Coverage {
	
	private static ExecutorService executor = Executors.newFixedThreadPool(5);

	@Test
	public void coverage() throws InterruptedException, ExecutionException {
		Logging.setup();
		Board test = new Board();
		Board test2 = new Board("r1b1k2r/p1pp1ppp/1pn1pqP1/2bn4/2B5/1PN1PN2/PBPPQP1P/R3K2R w KQkq - 0 11");
		Board test3 = new Board("r1bq1b1r/ppNk2pp/2p5/8/2B2Q2/P7/1P1P1PPP/n1BK4 w - - 0 16");
		Board test4 = new Board("8/pk5p/1p6/8/1P4r1/P6P/3P4/3KR3 b - - 0 27");
		MultiThreadSearch thread =  new MultiThreadSearch(test, 10, 1, true, 2000000000);
		MultiThreadSearch thread2 =  new MultiThreadSearch(test2, 10, 2, true, 2000000000);
		MultiThreadSearch thread3 =  new MultiThreadSearch(test3, 12, 3, true, 2000000000);
		MultiThreadSearch thread4 =  new MultiThreadSearch(test4, 13, 4, true, 2000000000);
		UCI.setThreadFinished(false);
		Future<int[]> future = executor.submit(thread);
		Future<int[]> future2 = executor.submit(thread2);
		Future<int[]> future3 = executor.submit(thread3);
		Future<int[]> future4 = executor.submit(thread4);
		
		int[] pv = null;
		
		pv = future.get();
		pv = future2.get();
		pv = future3.get();
		pv = future4.get();
		
		assertTrue(pv != null);
	}
	
	@Ignore
	@Test
	public void uci() {
		UCI.uciCommunication();
	}
	
}
