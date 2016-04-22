package org.openymsg.conference;

import org.junit.Test;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.conference.message.SendConfereneMessage;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;

public class SendConfereneMessageTest {
	@Test
	public void testSingleMember() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:112 Service:CONFMSG Status:DEFAULT SessionId:0x58fe2f  [1] [testuser] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [53] [testbuddy] [97] [1] [14] [myMessage]";
		String username = "testuser";
		YahooContact member1 = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		ConferenceMembershipImpl membership = new ConferenceMembershipImpl();
		membership.addMember(member1);
		String message = "myMessage";
		SendConfereneMessage outgoing = new SendConfereneMessage(username, conference, membership, message);
		MessageAssert.assertEquals(outgoing, test);
	}
}
