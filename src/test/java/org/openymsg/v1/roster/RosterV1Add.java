package org.openymsg.v1.roster;

import org.openymsg.network.FriendManager;
import org.openymsg.roster.RosterAdd;
import org.openymsg.v1.network.YahooUserV1;

public class RosterV1Add extends RosterAdd<RosterV1, YahooUserV1> {

	@Override
	protected YahooUserV1 createUser() {
		return new YahooUserV1("user", "group");
	}

	@Override
	protected RosterV1 createRoster(final FriendManager manager) {
		return new RosterV1(manager);
	}

}
