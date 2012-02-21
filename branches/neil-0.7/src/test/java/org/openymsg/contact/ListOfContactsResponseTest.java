package org.openymsg.contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mockito.Mockito;
import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.PacketReader;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.testng.annotations.Test;

public class ListOfContactsResponseTest {

	@Test
	public void testSimple() {
		String test= "Magic:YMSG Version:16 Length:796 Service:LIST_15 Status:DEFAULT SessionId:0x47e133  [302] [318] [300] [318] [65] [Bros] [302] [319] [300] [319] [7] [alfa11] [223] [1] [301] [319] [300] [319] [7] [neiliiihart] [301] [319] [300] [319] [7] [neilivhart] [301] [319] [300] [319] [7] [neilnexthart] [301] [319] [300] [319] [7] [neilvhart] [301] [319] [300] [319] [7] [nybilld21] [301] [319] [300] [319] [7] [nybilld22] [301] [319] [300] [319] [7] [nybilld290] [301] [319] [300] [319] [7] [pjpudge1414] [301] [319] [300] [319] [7] [yfmrfr] [301] [319] [300] [319] [7] [yjaddme113] [301] [319] [300] [319] [7] [yjaddme114] [301] [319] [300] [319] [7] [yjaddme402] [223] [1] [301] [319] [300] [319] [7] [yjaddme405] [223] [1] [301] [319] [300] [319] [7] [yjaddme78a] [223] [1] [301] [319] [300] [319] [7] [yjaddme80a] [223] [1] [301] [319] [300] [319] [7] [yjaddme83] [223] [1] [301] [319] [303] [319] [301] [318] [300] [318] [65] [BuddiesYJ2] [302] [319] [300] [319] [7] [yjaddme10] [301] [319] [303] [319] [301] [318] [303] [318]";
		SessionContactCallback listener = Mockito.mock(SessionContactCallback.class);
		ListOfContactsResponse response = new ListOfContactsResponse(listener);
		YMSG9Packet packet = PacketReader.readString(test);
		List<YMSG9Packet> packets = new ArrayList<YMSG9Packet>();
		packets.add(packet);
		response.execute(packets);
		Mockito.verify(listener).addContacts(this.getContacts());
//		Mockito.verify(listener).addIgnored(new HashSet<Contact>());
		Mockito.verify(listener).addPending(this.getPendingContacts());
		Mockito.verify(listener).addGroups(this.getGroups());
	}
	
	private Set<ContactGroup> getGroups() {
		Set<ContactGroup> groups = new HashSet<ContactGroup>();
		groups.add(new ContactGroupImpl("Bros"));
		groups.add(new ContactGroupImpl("BuddiesYJ2"));
		return groups;
	}

	private Set<Contact> getPendingContacts() {
		Set<Contact> contacts = new HashSet<Contact>();
		contacts.add(new Contact("alfa11", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme402", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme405", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme78a", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme80a", YahooProtocol.YAHOO));
		contacts.add(new Contact("yjaddme83", YahooProtocol.YAHOO)); 
		return contacts;
	}

	private Set<Contact> getContacts() {
		Set<Contact> contacts = new HashSet<Contact>();
		contacts.add(new Contact("yfmrfr", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme10", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("neiliiihart", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("neilvhart", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("pjpudge1414", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("nybilld22", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("nybilld21", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme402", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme405", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme78a", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("alfa11", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("neilnexthart", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme114", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("nybilld290", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme83", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme113", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("neilivhart", YahooProtocol.YAHOO)); 
		contacts.add(new Contact("yjaddme80a", YahooProtocol.YAHOO));
		return contacts;
	}
}
