package org.openymsg.v1.roster;

import org.openymsg.network.FriendManager;
import org.openymsg.roster.RosterBulkOperationsForbidden;
import org.openymsg.v1.network.YahooUserV1;

public class RosterV1BulkOperationsForbidden extends RosterBulkOperationsForbidden<RosterV1, YahooUserV1> {

	
	
	@Override
	public void setUp() throws Throwable {
		super.setUp();
		this.roster.syncedAdd(createUser("on roster"));
		this.roster.syncedAdd(createUser("on roster as well"));
	}

	@Override
	protected YahooUserV1 createUser(String userId) {
		return new YahooUserV1(userId);
	}

	@Override
	protected RosterV1 createRoster(final FriendManager manager) {
		return new RosterV1(manager);
	}

}
