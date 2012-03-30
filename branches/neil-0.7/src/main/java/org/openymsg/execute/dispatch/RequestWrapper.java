package org.openymsg.execute.dispatch;

public class RequestWrapper implements Runnable {
	private Request request;

	public RequestWrapper(Request request) {
		this.request = request;
	}

	@Override
	public void run() {
		try {
			this.request.execute();
		}
		// TODO - Throwable
		catch (Exception e) {
			// TODO log
			this.request.failure(e);
		}
	}

}
