package org.openymsg.contact.roster;

import java.io.IOException;

import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.testing.MessageAssert;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ContactAddAcceptMessageTest {

	@Test
	public void testYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:37 Service:Y7_BUDDY_AUTHORIZATION Status:DEFAULT SessionId:0x428f66  [1] [testuser] [5] [testbuddy] [13] [1]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		ContactAddAcceptMessage message = new ContactAddAcceptMessage(username, contact);
		MessageAssert.assertEquals(message, test);
	}

	@Test
	public void testMSN() throws IOException {
		Assert.fail("not implemented");
	}

}
