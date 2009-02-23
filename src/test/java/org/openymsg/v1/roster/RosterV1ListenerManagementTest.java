package org.openymsg.v1.roster;

import org.openymsg.network.FriendManager;
import org.openymsg.roster.RosterListenerManagementTest;

public class RosterV1ListenerManagementTest extends RosterListenerManagementTest<RosterV1> {

	@Override
	protected RosterV1 createRoster(final FriendManager manager) {
		return new RosterV1(manager);
	}

}
