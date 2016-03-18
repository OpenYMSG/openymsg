package org.openymsg.context.session.timeout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.Executor;
import org.openymsg.execute.dispatch.Request;

public class TimeoutChecker implements Request {
	final static Log log = LogFactory.getLog(TimeoutChecker.class);
	private TimeoutCheckerStrategy strategy;

	public TimeoutChecker(Integer timeout, Executor executor, YahooConnection connection) {
		if (timeout == null || timeout == 0) {
			strategy = new TimeoutCheckerNoopStrategy();
		} else {
			strategy = new TimeoutCheckerShutdownStrategy(timeout, executor, connection);
		}
	}

	public void keepAlive() {
		strategy.keepAlive();
	}

	@Override
	public void execute() {
		strategy.execute();
	}

	@Override
	public void failure(Exception ex) {
		log.warn("Failure", ex);
	}
}
