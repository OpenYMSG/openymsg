package org.openymsg.contact.roster;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.ListOfContactsResponse;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListOfContactsResponseTest {
	@Test
	public void testSimple() {
		String test =
				"Magic:YMSG Version:16 Length:796 Service:LIST_15 Status:DEFAULT SessionId:0x47e133  [302] [318] [300] [318] [65] [Bros] [302] [319] [300] [319] [7] [dog11] [223] [1] [301] [319] [300] [319] [7] [josephiiiapple] [301] [319] [300] [319] [7] [josephivapple@live.com] [241] [2] [301] [319] [300] [319] [7] [josephnextapple] [301] [319] [300] [319] [7] [josephvapple] [301] [319] [300] [319] [7] [frank21] [301] [319] [300] [319] [7] [frank22] [301] [319] [300] [319] [7] [frank290] [301] [319] [300] [319] [7] [pjpudge1414] [301] [319] [300] [319] [7] [dude1] [301] [319] [300] [319] [7] [lady113] [301] [319] [300] [319] [7] [lady114] [301] [319] [300] [319] [7] [lady402] [223] [1] [301] [319] [300] [319] [7] [lady405] [223] [1] [301] [319] [300] [319] [7] [lady78a] [223] [1] [301] [319] [300] [319] [7] [lady80a] [223] [1] [301] [319] [300] [319] [7] [lady83] [223] [1] [301] [319] [303] [319] [301] [318] [300] [318] [65] [BuddiesTH2] [302] [319] [300] [319] [7] [lady10] [301] [319] [303] [319] [301] [318] [303] [318]";
		SessionRosterImpl sessionContact = mock(SessionRosterImpl.class);
		SessionGroupImpl sessionGroup = mock(SessionGroupImpl.class);
		SessionStatusImpl sessionStatus = mock(SessionStatusImpl.class);
		ListOfContactsResponse response = new ListOfContactsResponse(sessionContact, sessionGroup, sessionStatus);
		YMSG9Packet packet = PacketReader.readString(test);
		List<YMSG9Packet> packets = new ArrayList<YMSG9Packet>();
		packets.add(packet);
		response.execute(packets);
		for (YahooContact contact : this.getContacts()) {
			verify(sessionContact).loadedContact(contact);
		}
		verify(sessionStatus).addedPending(this.getPendingContacts());
		verify(sessionGroup).addedGroups(this.getGroups());
	}

	private Set<YahooContactGroup> getGroups() {
		Set<YahooContactGroup> groups = new HashSet<YahooContactGroup>();
		groups.add(new ContactGroupImpl("Bros"));
		groups.add(new ContactGroupImpl("BuddiesTH2"));
		return groups;
	}

	private Set<YahooContact> getPendingContacts() {
		Set<YahooContact> contacts = new HashSet<YahooContact>();
		contacts.add(new YahooContact("dog11", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady402", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady405", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady78a", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady80a", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady83", YahooProtocol.YAHOO));
		return contacts;
	}

	private Set<YahooContact> getContacts() {
		Set<YahooContact> contacts = new HashSet<YahooContact>();
		contacts.add(new YahooContact("dude1", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady10", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("josephiiiapple", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("josephvapple", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("pjpudge1414", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("frank22", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("frank21", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady402", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady405", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady78a", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("dog11", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("josephnextapple", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady114", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("frank290", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady83", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("lady113", YahooProtocol.YAHOO));
		contacts.add(new YahooContact("josephivapple@live.com", YahooProtocol.MSN));
		contacts.add(new YahooContact("lady80a", YahooProtocol.YAHOO));
		return contacts;
	}
}
