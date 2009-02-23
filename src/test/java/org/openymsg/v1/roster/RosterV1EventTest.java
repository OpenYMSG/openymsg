package org.openymsg.v1.roster;

import org.openymsg.network.FriendManager;
import org.openymsg.roster.RosterEventTest;
import org.openymsg.v1.network.YahooUserV1;

public class RosterV1EventTest extends RosterEventTest<RosterV1, YahooUserV1> {

	@Override
	protected RosterV1 createRoster(final FriendManager manager) {
		return new RosterV1(manager);
	}

	@Override
	protected YahooUserV1 createUser(String userId) {
		return new YahooUserV1(userId);
	}

}
