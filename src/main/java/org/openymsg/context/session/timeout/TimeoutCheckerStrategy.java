package org.openymsg.context.session.timeout;

interface TimeoutCheckerStrategy {
	public void keepAlive();

	public void execute();
}