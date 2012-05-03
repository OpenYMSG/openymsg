package org.openymsg.contact;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.execute.read.MultiplePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class ListOfContactsResponse implements MultiplePacketResponse {
	/** logger */
	private static final Log log = LogFactory.getLog(ListOfContactsResponse.class);
	private SessionRosterImpl sessionContact;
	private SessionGroupImpl sessionGroup;
	private SessionStatusImpl sessionStatus;

	public ListOfContactsResponse(SessionRosterImpl sessionContact, SessionGroupImpl sessionGroup,
			SessionStatusImpl sessionStatus) {
		this.sessionContact = sessionContact;
		this.sessionGroup = sessionGroup;
		this.sessionStatus = sessionStatus;
	}

	@Override
	public void execute(List<YMSG9Packet> packets) {
		String username = null;
		YahooProtocol protocol = YahooProtocol.YAHOO;
		ContactGroupImpl currentListGroup = null;
		Map<String, ContactGroupImpl> receivedGroups = new HashMap<String, ContactGroupImpl>();

		Set<YahooContact> usersOnFriendsList = new HashSet<YahooContact>();
		Set<YahooContact> usersOnIgnoreList = new HashSet<YahooContact>();
		Set<YahooContact> usersOnPendingList = new HashSet<YahooContact>();

		boolean isPending = false;

		for (YMSG9Packet qPkt : packets) {
			Iterator<String[]> iter = qPkt.entries().iterator();
			while (iter.hasNext()) {
				String[] s = iter.next();

				int key = Integer.valueOf(s[0]);
				String value = s[1];

				switch (key) {
				case 302:
					/*
					 * This is always 318 before a group, 319 before the first s/n in a group, 320 before any ignored
					 * s/n. It is not sent for s/n's in a group after the first. All ignored s/n's are listed last, so
					 * when we see a 320 we clear the group and begin marking the s/n's as ignored. It is always
					 * followed by an identical 300 key.
					 */
					if (value != null && value.equals("320")) {
						currentListGroup = null;
					}
					break;
				case 301:
					/* This is 319 before all s/n's in a group after the first. It is followed by an identical 300. */
					if (username != null) {
						YahooContact yu = null;
						if (currentListGroup != null) {
							for (YahooContact friend : usersOnFriendsList) {
								// TODO - don't compare id
								if (friend.getName().equals(username)) {
									yu = friend;
									currentListGroup.add(yu);
									if (!yu.getProtocol().equals(protocol)
											&& yu.getProtocol().equals(YahooProtocol.YAHOO)) {
										log.error("Switching protocols because user is in list more that once: "
												+ yu.getName() + " from: " + yu.getProtocol() + " to: " + protocol);
										// TODO remove old contact?
									}
								}
							}
							if (yu == null) {
								/* This buddy is in a group */
								yu = new YahooContact(username, protocol);
								currentListGroup.add(yu);
								usersOnFriendsList.add(yu);
							}
						} else {
							/* This buddy is on the ignore list (and therefore in no group) */
							yu = new YahooContact(username, protocol);
							usersOnIgnoreList.add(yu);
						}
						if (isPending) {
							usersOnPendingList.add(yu);
						}
						username = null;
						isPending = false;
						protocol = YahooProtocol.YAHOO;
					}
					break;
				case 223: /* Pending add user request */
					isPending = true;
					break;
				case 300: /* This is 318 before a group, 319 before any s/n in a group, and 320 before any ignored s/n. */
					break;
				case 65: /* This is the group */
					currentListGroup = receivedGroups.get(value);
					if (currentListGroup == null) {
						currentListGroup = new ContactGroupImpl(value);
						receivedGroups.put(value, currentListGroup);
					}
					break;
				case 7: /* buddy's s/n */
					username = value;
					break;
				case 241: /* another protocol user */
					protocol = YahooProtocol.getProtocolOrDefault(value, username);
					break;
				case 59: /* somebody told cookies come here too, but im not sure */
					break;
				case 317: /* Stealth Setting */
					// stealth = Integer.valueOf(value);
					break;
				}
			}
			if (username != null) {
				YahooContact yu = null;
				if (currentListGroup != null) {
					for (YahooContact friend : usersOnFriendsList) {
						if (friend.getName().equals(username)) {
							yu = friend;
							currentListGroup.add(yu);
						}
					}
					if (yu == null) {
						/* This buddy is in a group */
						yu = new YahooContact(username, protocol);
						currentListGroup.add(yu);
						usersOnFriendsList.add(yu);
					}
				} else {
					/* This buddy is on the ignore list (and therefore in no group) */
					yu = new YahooContact(username, protocol);
					usersOnIgnoreList.add(yu);
				}
				if (isPending) {
					usersOnPendingList.add(yu);
				}
				username = null;
				isPending = false;
				protocol = YahooProtocol.YAHOO;
			}

		}

		for (YahooContact contact : usersOnFriendsList) {
			sessionContact.loadedContact(contact);
		}

		if (!usersOnIgnoreList.isEmpty()) {
			sessionStatus.addedIgnored(usersOnIgnoreList);
		}

		if (!usersOnPendingList.isEmpty()) {
			sessionStatus.addedPending(usersOnPendingList);
		}

		if (!receivedGroups.values().isEmpty()) {
			sessionGroup.addedGroups(new HashSet<YahooContactGroup>(receivedGroups.values()));
		}

		sessionContact.rosterLoaded();

		// Now that we've parsed the buddy list, we can consider login succcess
		// sessionStatus = SessionState.LOGGED_ON;
		// log.trace("Yahoo logged in successfully");

		// // Only one identity with v16 login
		// identities.put(loginID.getId(), new YahooIdentity(loginID.getId()));
		// primaryID = loginID;

		// // Set the primary and login flags on the relevant YahooIdentity objects
		// primaryID.setPrimaryIdentity(true);
		// loginID.setPrimaryIdentity(true);

	}

	@Override
	public int getProceedStatus() {
		return 0;
	}

}
