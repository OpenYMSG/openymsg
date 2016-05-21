package org.openymsg.context.session.timeout;

class TimeoutCheckerNoopStrategy implements TimeoutCheckerStrategy {
	@Override
	public void keepAlive() {
	}

	@Override
	public void execute() {
	}

	@Override
	public String toString() {
		return String.format("TimeoutCheckerNoopStrategy []");
	}
}