package org.openymsg.conference;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openymsg.YahooConference;
import org.openymsg.util.CollectionUtils;

public class ConferenceServiceState {
	protected final Map<String, YahooConference> conferences = new ConcurrentHashMap<String, YahooConference>();
	private final Map<String, ConferenceMembershipImpl> conferenceMemberships = new ConcurrentHashMap<String, ConferenceMembershipImpl>();

	public ConferenceMembershipImpl getMembership(String conferenceId) {
		return conferenceMemberships.get(conferenceId);
	}

	public void addMembership(String conferenceId, ConferenceMembershipImpl membership) {
		conferenceMemberships.put(conferenceId, membership);
	}

	public boolean hasConference(String conferenceId) {
		return conferences.containsKey(conferenceId);
	}

	public YahooConference getConference(String conferenceId) {
		return conferences.get(conferenceId);
	}

	public Set<YahooConference> getConferences() {
		return CollectionUtils.protectedSet(this.conferences.values());
	}

}
