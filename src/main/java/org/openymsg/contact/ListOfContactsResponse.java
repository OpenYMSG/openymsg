package org.openymsg.contact;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.read.MultiplePacketListResponse;
import org.openymsg.network.YMSG9Packet;

public class ListOfContactsResponse extends MultiplePacketListResponse {
	private static final Log log = LogFactory.getLog(ListOfContactsResponse.class);
	private ContactListener listener;

	public ListOfContactsResponse(ContactListener listener) {
		this.listener = listener;
	}

	@Override
	public void execute() // 0x55
	{
		// queueOfList15.add(pkt);
		// if (pkt.status != 0) {
		// return;
		// }
		String username = null;
		YahooProtocol protocol = YahooProtocol.YAHOO;
		String currentListGroup = null;
		// ArrayList<ContactGroupImpl> receivedGroups = new ArrayList<ContactGroupImpl>();

		Set<ContactImpl> usersOnFriendsList = new HashSet<ContactImpl>();
		Set<ContactImpl> usersOnIgnoreList = new HashSet<ContactImpl>();
		Set<ContactImpl> usersOnPendingList = new HashSet<ContactImpl>();

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
						ContactImpl yu = null;
						if (currentListGroup != null) {
							for (ContactImpl friend : usersOnFriendsList) {
								if (friend.getId().equals(username)) {
									yu = friend;
									yu.addGroupId(currentListGroup);
									if (!yu.getProtocol().equals(protocol)
											&& yu.getProtocol().equals(YahooProtocol.YAHOO)) {
										log.debug("Switching protocols because user is in list more that once: "
												+ yu.getId() + " from: " + yu.getProtocol() + " to: " + protocol);
										yu.setProtocol(protocol);
									}
								}
							}
							if (yu == null) {
								/* This buddy is in a group */
								yu = new ContactImpl(username, currentListGroup, protocol);
								usersOnFriendsList.add(yu);
							}
						}
						else {
							/* This buddy is on the ignore list (and therefore in no group) */
							yu = new ContactImpl(username, protocol);
							yu.setIgnored(true);
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
					currentListGroup = value;
					break;
				case 7: /* buddy's s/n */
					username = value;
					break;
				case 241: /* another protocol user */
					protocol = getUserProtocol(value, username);
					break;
				case 59: /* somebody told cookies come here too, but im not sure */
					break;
				case 317: /* Stealth Setting */
					// stealth = Integer.valueOf(value);
					break;
				}
			}
			if (username != null) {
				ContactImpl yu = null;
				if (currentListGroup != null) {
					for (ContactImpl friend : usersOnFriendsList) {
						if (friend.getId().equals(username)) {
							yu = friend;
							yu.addGroupId(currentListGroup);
						}
					}
					if (yu == null) {
						/* This buddy is in a group */
						yu = new ContactImpl(username, currentListGroup, protocol);
						usersOnFriendsList.add(yu);
					}
				}
				else {
					/* This buddy is on the ignore list (and therefore in no group) */
					yu = new ContactImpl(username, protocol);
					yu.setIgnored(true);
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

		if (!usersOnFriendsList.isEmpty()) {
			listener.addContacts(usersOnFriendsList);
			// // receivedListFired = true;
			// eventDispatchQueue.append(new SessionListEvent(this, ContactListType.Friends, usersOnFriendsList),
			// ServiceType.LIST);
		}

		if (!usersOnIgnoreList.isEmpty()) {
			listener.addIgnoredFriends(usersOnIgnoreList);
			// eventDispatchQueue.append(new SessionListEvent(this, ContactListType.Ignored, usersOnIgnoreList),
			// ServiceType.LIST);
		}

		if (!usersOnPendingList.isEmpty()) {
			listener.addPendingFriends(usersOnPendingList);
			// eventDispatchQueue.append(new SessionListEvent(this, ContactListType.Pending, usersOnPendingList),
			// ServiceType.LIST);
		}

		// Now that we've parsed the buddy list, we can consider login succcess
		// sessionStatus = SessionState.LOGGED_ON;
		log.trace("Yahoo logged in successfully");

		// // Only one identity with v16 login
		// identities.put(loginID.getId(), new YahooIdentity(loginID.getId()));
		// primaryID = loginID;

		// // Set the primary and login flags on the relevant YahooIdentity objects
		// primaryID.setPrimaryIdentity(true);
		// loginID.setPrimaryIdentity(true);

		// Set initial presence to 'available'
		// try {
		// setStatus(Status.AVAILABLE);
		// }
		// catch (java.io.IOException e) {
		// log.trace("Failed to set status to available");
		// }
	}

	private YahooProtocol getUserProtocol(String protocolString, String who) {
		try {
			return YahooProtocol.getProtocol(protocolString);
		}
		catch (IllegalArgumentException e) {
			log.error("Failed finding protocol: " + protocolString + " for user: " + who);
			return YahooProtocol.YAHOO;
		}
	}

	@Override
	public int getProceedStatus() {
		return 0;
	}

}