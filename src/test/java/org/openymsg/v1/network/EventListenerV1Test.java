package org.openymsg.v1.network;

import org.openymsg.network.EventListenerTest;
import org.openymsg.network.Session;
import org.openymsg.v1.roster.RosterV1;

public class EventListenerV1Test extends EventListenerTest<RosterV1, YahooUserV1> {

	protected Session<RosterV1, YahooUserV1> createSession() {
		return new SessionV1();
	}

}
