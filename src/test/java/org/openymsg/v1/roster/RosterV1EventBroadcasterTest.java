package org.openymsg.v1.roster;

import org.openymsg.network.FriendManager;
import org.openymsg.roster.RosterEvent;
import org.openymsg.roster.RosterEventBroadcasterTest;
import org.openymsg.roster.RosterEventType;
import org.openymsg.v1.network.YahooUserV1;

public class RosterV1EventBroadcasterTest extends RosterEventBroadcasterTest<RosterV1> {

	@Override
	protected RosterV1 createRoster(final FriendManager manager) {
		return new RosterV1(manager);
	}
	
	protected void broadcastAddRosterEvent() {
		roster.broadcastEvent(new RosterEvent(roster, new YahooUserV1("dummy"), RosterEventType.add));
	}


}
