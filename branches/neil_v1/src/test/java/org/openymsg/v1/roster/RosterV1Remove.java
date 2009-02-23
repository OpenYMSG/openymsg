package org.openymsg.v1.roster;

import org.openymsg.network.FriendManager;
import org.openymsg.roster.RosterRemove;
import org.openymsg.v1.network.YahooUserV1;

public class RosterV1Remove extends RosterRemove<RosterV1, YahooUserV1> {

	@Override
	protected YahooUserV1 createUser(String userId, String group) {
		return new YahooUserV1(userId, group);
	}

	@Override
	protected RosterV1 createRoster(final FriendManager manager) {
		return new RosterV1(manager);
	}

}
