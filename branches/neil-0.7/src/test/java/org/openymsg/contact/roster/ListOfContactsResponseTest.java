package org.openymsg.contact.roster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mockito.Mockito;
import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.PacketReader;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.network.YMSG9Packet;
import org.testng.annotations.Test;

public class ListOfContactsResponseTest {

	@Test
	public void testSimple() {
		String test = "Magic:YMSG Version:16 Length:796 Service:LIST_15 Status:DEFAULT SessionId:0x47e133  [302] [318] [300] [318] [65] [Bros] [302] [319] [300] [319] [7] [dog11] [223] [1] [301] [319] [300] [319] [7] [josephiiiapple] [301] [319] [300] [319] [7] [josephivapple] [301] [319] [300] [319] [7] [josephnextapple] [301] [319] [300] [319] [7] [josephvapple] [301] [319] [300] [319] [7] [frank21] [301] [319] [300] [319] [7] [frank22] [301] [319] [300] [319] [7] [frank290] [301] [319] [300] [319] [7] [pjpudge1414] [301] [319] [300] [319] [7] [dude1] [301] [319] [300] [319] [7] [lady113] [301] [319] [300] [319] [7] [lady114] [301] [319] [300] [319] [7] [lady402] [223] [1] [301] [319] [300] [319] [7] [lady405] [223] [1] [301] [319] [300] [319] [7] [lady78a] [223] [1] [301] [319] [300] [319] [7] [lady80a] [223] [1] [301] [319] [300] [319] [7] [lady83] [223] [1] [301] [319] [303] [319] [301] [318] [300] [318] [65] [BuddiesTH2] [302] [319] [300] [319] [7] [lady10] [301] [319] [303] [319] [301] [318] [303] [318]";
		SessionRosterImpl sessionContact = Mockito.mock(SessionRosterImpl.class);
		SessionGroupImpl sessionGroup = Mockito.mock(SessionGroupImpl.class);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		ListOfContactsResponse response = new ListOfContactsResponse(sessionContact, sessionGroup, sessionStatus);
		YMSG9Packet packet = PacketReader.readString(test);
		List<YMSG9Packet> packets = new ArrayList<YMSG9Packet>();
		packets.add(packet);
		response.execute(packets);
		for (Contact contact : this.getContacts()) {
			Mockito.verify(sessionContact).addedContact(contact);
		}
		Mockito.verify(sessionStatus).addedPending(this.getPendingContacts());
		Mockito.verify(sessionGroup).addedGroups(this.getGroups());
	}

	private Set<ContactGroup> getGroups() {
		Set<ContactGroup> groups = new HashSet<ContactGroup>();
		groups.add(new ContactGroupImpl("Bros"));
		groups.add(new ContactGroupImpl("BuddiesTH2"));
		return groups;
	}

	private Set<Contact> getPendingContacts() {
		Set<Contact> contacts = new HashSet<Contact>();
		contacts.add(new Contact("dog11", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady402", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady405", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady78a", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady80a", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady83", YahooProtocol.YAHOO));
		return contacts;
	}

	private Set<Contact> getContacts() {
		Set<Contact> contacts = new HashSet<Contact>();
		contacts.add(new Contact("dude1", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady10", YahooProtocol.YAHOO));
		contacts.add(new Contact("josephiiiapple", YahooProtocol.YAHOO));
		contacts.add(new Contact("josephvapple", YahooProtocol.YAHOO));
		contacts.add(new Contact("pjpudge1414", YahooProtocol.YAHOO));
		contacts.add(new Contact("frank22", YahooProtocol.YAHOO));
		contacts.add(new Contact("frank21", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady402", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady405", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady78a", YahooProtocol.YAHOO));
		contacts.add(new Contact("dog11", YahooProtocol.YAHOO));
		contacts.add(new Contact("josephnextapple", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady114", YahooProtocol.YAHOO));
		contacts.add(new Contact("frank290", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady83", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady113", YahooProtocol.YAHOO));
		contacts.add(new Contact("josephivapple", YahooProtocol.YAHOO));
		contacts.add(new Contact("lady80a", YahooProtocol.YAHOO));
		return contacts;
	}
}
