package org.openymsg.conference;

import org.junit.Test;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.conference.message.CreateConferenceMessage;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CreateConferenceMessageTest {
	@Test
	public void testSingleYahoo() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:124 Service:CONFINVITE Status:DEFAULT SessionId:0x56cf6d  [1] [testuser] [50] [testuser] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [58] [Invitingtestbuddy] [97] [1] [52] [testbuddy] [13] [0]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Set<YahooContact> contacts = new HashSet<YahooContact>();
		contacts.add(contact);
		String message = "Invitingtestbuddy";
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		CreateConferenceMessage outgoing = new CreateConferenceMessage(username, conference, contacts, message);
		MessageAssert.assertEquals(outgoing, test);
	}
}
