package org.openymsg.mail;

import org.openymsg.execute.Executor;
import org.openymsg.network.ServiceType;

public class SessionMailImpl implements SessionMail {
	private Executor executor;

	public SessionMailImpl(Executor executor) {
		this.executor = executor;
		this.executor.register(ServiceType.NEWMAIL, new NewMailResponse());
	}


}
