package org.openymsg.execute;

public interface Request {
	
	void execute();
	void failure(Exception ex);
}
