package org.openymsg.conference;

import org.junit.Test;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ExtendConferenceMessageTest {
	@Test
	public void test() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:220 Service:CONFADDINVITE Status:DEFAULT SessionId:0x56cf6d  [1] [testuser] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [51] [testbuddy2] [53] [testbuddy] [58] [joinconference] [97] [1] [13] [0] [234] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [233] [grFkPBBAjXW0JFb50yovkfmdm567JexLc-]";
		String username = "testuser";
		YahooContact member = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact invited = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited);
		String message = "joinconference";
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		ConferenceMembershipImpl membership = new ConferenceMembershipImpl();
		membership.addMember(member);
		ExtendConferenceMessage outgoing =
				new ExtendConferenceMessage(username, conference, membership, invitedContacts, message);
		MessageAssert.assertEquals(outgoing, test, "233", "234");
	}

	@Test
	public void testMutlipleInvitesMultipleMembers() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:247 Service:CONFADDINVITE Status:DEFAULT SessionId:0x58fe2f  [1] [testuser] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [51] [testbuddy3,testbuddy4,testbuddy5] [53] [testbuddy] [53] [testbuddy2] [58] [joinconference] [97] [1] [13] [0] [234] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [233] [grFkPBIo9ofg.TLbOmEWP2ceP3iZMlbEY-]";
		String username = "testuser";
		YahooContact member1 = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact member2 = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		YahooContact invited1 = new YahooContact("testbuddy3", YahooProtocol.YAHOO);
		YahooContact invited2 = new YahooContact("testbuddy4", YahooProtocol.YAHOO);
		YahooContact invited3 = new YahooContact("testbuddy5", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited1);
		invitedContacts.add(invited2);
		invitedContacts.add(invited3);
		String message = "joinconference";
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		ConferenceMembershipImpl membership = new ConferenceMembershipImpl();
		membership.addMember(member1);
		membership.addMember(member2);
		ExtendConferenceMessage outgoing =
				new ExtendConferenceMessage(username, conference, membership, invitedContacts, message);
		MessageAssert.assertEquals(outgoing, test, "233", "234");
	}

	@Test
	public void testMultipleOutstandingInvites() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:257 Service:CONFADDINVITE Status:DEFAULT SessionId:0x56cf6d  [1] [testuser] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [51] [testbuddy] [53] [testbuddy2] [52] [testbuddy3] [52] [testbuddy4] [52] [testbuddy5] [58] [joinconference] [97] [1] [13] [0] [234] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [233] [grFkPBBAjXW0JFb50yovkfmdm567JexLc-]";
		String username = "testuser";
		YahooContact invited = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact member = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		YahooContact outstandingInvited1 = new YahooContact("testbuddy3", YahooProtocol.YAHOO);
		YahooContact outstandingInvited2 = new YahooContact("testbuddy4", YahooProtocol.YAHOO);
		YahooContact outstandingInvited3 = new YahooContact("testbuddy5", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited);
		String message = "joinconference";
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		ConferenceMembershipImpl membership = new ConferenceMembershipImpl();
		membership.addMember(member);
		membership.addInvited(outstandingInvited1);
		membership.addInvited(outstandingInvited2);
		membership.addInvited(outstandingInvited3);
		ExtendConferenceMessage outgoing =
				new ExtendConferenceMessage(username, conference, membership, invitedContacts, message);
		MessageAssert.assertEquals(outgoing, test, "233", "234");
	}
}
