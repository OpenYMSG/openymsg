package org.openymsg.contact.roster;

import org.junit.Test;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;

public class ContactAddAcceptMessageTest {
	@Test
	public void testYahoo() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:37 Service:Y7_BUDDY_AUTHORIZATION Status:DEFAULT SessionId:0x428f66  [1] [testuser] [5] [testbuddy] [13] [1]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		ContactAddAcceptMessage message = new ContactAddAcceptMessage(username, contact);
		MessageAssert.assertEquals(message, test);
	}
}
