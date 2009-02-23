package org.openymsg.v1.roster;

import org.openymsg.network.FriendManager;
import org.openymsg.roster.RosterCreationTest;

public class RosterV1CreationTest extends RosterCreationTest<RosterV1> {

	@Override
	protected RosterV1 createRoster(final FriendManager manager) {
		return new RosterV1(manager);
	}

}
