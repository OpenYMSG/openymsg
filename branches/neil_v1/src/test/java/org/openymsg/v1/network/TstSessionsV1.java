package org.openymsg.v1.network;

import org.openymsg.network.Session;
import org.openymsg.network.TstSessions;
import org.openymsg.network.event.WaitListener;
import org.openymsg.v1.roster.RosterV1;

public class TstSessionsV1 extends TstSessions<RosterV1, YahooUserV1> {

	public TstSessionsV1() throws Throwable {
		super();
	}

	@Override
	protected Session<RosterV1, YahooUserV1> createSession() {
		return new SessionV1();
	}
	
	@Override
	public Session<RosterV1, YahooUserV1> getSess1() {
		return sess1;
	}

	@Override
	public Session<RosterV1, YahooUserV1> getSess2() {
		return sess2;
	}

	@Override
	public WaitListener getListener1() {
		return listener1;
	}

	@Override
	public WaitListener getListener2() {
		return listener2;
	}


}
