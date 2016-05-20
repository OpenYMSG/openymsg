package org.openymsg.mail;

import org.openymsg.connection.YahooConnection;
import org.openymsg.network.ServiceType;

public class SessionMailImpl implements SessionMail {
	public SessionMailImpl(YahooConnection connection) {
		connection.getReaderRegistry().register(ServiceType.NEWMAIL, new NewMailResponse());
	}
}
