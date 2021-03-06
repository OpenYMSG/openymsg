package org.openymsg.contact.roster;

import org.junit.Test;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;

public class ContactAddDeclineMessageTest {
	@Test
	public void testYahoo() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:61 Service:Y7_BUDDY_AUTHORIZATION Status:DEFAULT SessionId:0x428f66  [1] [testuser] [5] [testbuddy] [13] [2] [97] [1] [14] [declinedYou]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String declineMessage = "declinedYou";
		ContactAddDeclineMessage message = new ContactAddDeclineMessage(username, contact, declineMessage);
		MessageAssert.assertEquals(message, test);
	}
}
