package org.openymsg.v1.network;

import org.openymsg.network.Session;
import org.openymsg.network.SessionTest;
import org.openymsg.v1.roster.RosterV1;

public class SessionV1Test extends SessionTest<RosterV1, YahooUserV1> {

	protected Session<RosterV1, YahooUserV1> createSession() {
		return new SessionV1();
	}

}
