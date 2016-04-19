package org.openymsg.execute.dispatch;

import org.openymsg.network.ConnectionHandler;

public interface MessageRequest extends Request {
	void setConnection(ConnectionHandler connection);
}
