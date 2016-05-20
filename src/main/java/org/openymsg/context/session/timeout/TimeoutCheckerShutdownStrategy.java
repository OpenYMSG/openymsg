package org.openymsg.context.session.timeout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.connection.YahooConnection;
import org.openymsg.context.session.ShutdownRequest;
import org.openymsg.execute.Executor;

public class TimeoutCheckerShutdownStrategy implements TimeoutCheckerStrategy {
	final static Log log = LogFactory.getLog(TimeoutCheckerShutdownStrategy.class);
	private final YahooConnection connection;
	private final Executor executor;
	private long lastKeepAlive = System.currentTimeMillis();
	private int timeout = 0;

	/**
	 * Create a shutdown strategy
	 * @param timeout minutes before timeout
	 * @param executor executor to run the shutdown command
	 * @param connection connection to shutdown
	 */
	public TimeoutCheckerShutdownStrategy(int timeout, Executor executor, YahooConnection connection) {
		this.connection = connection;
		this.timeout = timeout * 1000;
		this.executor = executor;
	}

	/**
	 * prevent shutdown
	 */
	@Override
	public void keepAlive() {
		log.debug("keep alive");
		lastKeepAlive = System.currentTimeMillis();
	}

	/**
	 * check if timeout has occured and shutdown connection
	 */
	@Override
	public void execute() {
		long current = System.currentTimeMillis();
		if (current > lastKeepAlive + timeout) {
			TimeoutChecker.log.info("Timeout reached.  Shutting down");
			executor.execute(new ShutdownRequest(connection));
		}
	}
}