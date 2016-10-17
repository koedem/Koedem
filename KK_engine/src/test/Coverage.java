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
		MultiThreadSearch thread =  new MultiThreadSearch(test, 9, 1, true, 2000000000);
		UCI.setThreadFinished(false);
		Future<int[]> future = executor.submit(thread);
		
		int[] pv = null;
		
		pv = future.get();
		
		assertTrue(pv != null);
	}
	
	@Ignore
	@Test
	public void uci() {
		UCI.uciCommunication();
	}
	
}
