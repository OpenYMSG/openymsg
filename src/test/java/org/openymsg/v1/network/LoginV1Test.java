package org.openymsg.v1.network;

import org.openymsg.network.LoginTest;
import org.openymsg.network.Session;
import org.openymsg.v1.roster.RosterV1;

public class LoginV1Test extends LoginTest<RosterV1, YahooUserV1> {

	protected Session<RosterV1, YahooUserV1> createSession() {
		return new SessionV1();
	}

}
