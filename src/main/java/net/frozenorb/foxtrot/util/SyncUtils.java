package net.frozenorb.foxtrot.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Contains static methods for methods relating to synchronous method calls and
 * thread management.
 * 
 * @author Kerem Celik
 *
 */
public class SyncUtils {
	private static ExecutorService es = Executors.newFixedThreadPool(1);

	public static final BlockingQueue<SimpleFuture<?>> queue = new LinkedBlockingQueue<SimpleFuture<?>>();

	static {
		startTicker();
	}

	private static void startTicker() {
		while (true) {
			System.out.println("tick");
			try {
				if (!queue.isEmpty()) {
					queue.take().start();
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Runs a callable in a new thread and returns the future output of the
	 * task.
	 * 
	 * @param callable
	 *            the callable to run
	 * @return future
	 */
	public static <T> Future<T> runSync(Callable<T> callable) {
		final Future<T> future = es.submit(callable);

		return future;
	}

	/**
	 * Runs a task in the main thread and returns a {@link SimpleFuture}
	 * instance with a blocking {@link SimpleFuture#get()} method that returns
	 * the output of the callable.
	 * 
	 * @param callable
	 *            the callable to run
	 * @return FoxtrotFuture instance
	 */
	public static <T> SimpleFuture<T> scheduleSyncCallable(Callable<T> callable) {
		SimpleFuture<T> future = new SimpleFuture<T>(callable);
		queue.add(future);

		return future;
	}

}
