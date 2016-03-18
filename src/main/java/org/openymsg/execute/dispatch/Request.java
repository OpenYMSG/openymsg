package org.openymsg.execute.dispatch;

public interface Request {
	void execute();

	void failure(Exception ex);
}
