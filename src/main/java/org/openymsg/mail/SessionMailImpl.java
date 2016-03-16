package org.openymsg.mail;

import org.openymsg.connection.YahooConnection;
import org.openymsg.network.ServiceType;

public class SessionMailImpl implements SessionMail {
	private YahooConnection executor;

	public SessionMailImpl(YahooConnection executor) {
		this.executor = executor;
		this.executor.register(ServiceType.NEWMAIL, new NewMailResponse());
	}

}
