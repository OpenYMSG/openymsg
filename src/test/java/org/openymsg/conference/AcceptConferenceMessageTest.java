package org.openymsg.conference;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.conference.message.AcceptConferenceMessage;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AcceptConferenceMessageTest {
	@Test
	public void testSingleYahoo() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:69 Service:CONFLOGON Status:DEFAULT SessionId:0x58fe2f  [1] [testuser] [57] [testbuddy-8iVmHcCkflGJpBXpjBbzCw--] [3] [testbuddy]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Set<YahooContact> contacts = new HashSet<YahooContact>();
		contacts.add(contact);
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		ConferenceMembershipImpl membership = new ConferenceMembershipImpl();
		membership.addMember(contact);
		AcceptConferenceMessage outgoing = new AcceptConferenceMessage(username, conference, membership);
		MessageAssert.assertEquals(outgoing, test);
	}

	@Test
	@Ignore
	public void testMultipleYahoo() {
		fail("not implemented");
	}
}
