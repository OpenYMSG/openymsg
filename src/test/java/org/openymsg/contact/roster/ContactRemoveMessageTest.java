package org.openymsg.contact.roster;

import org.junit.Test;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;

public class ContactRemoveMessageTest {
	@Test
	public void testSimple() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:41 Service:REMOVE_BUDDY Status:DEFAULT SessionId:0x59e41a  [1] [testuser] [7] [testbuddy] [65] [groupName]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContactGroup group = new ContactGroupImpl("groupName");
		ContactRemoveMessage message = new ContactRemoveMessage(username, contact, group);
		MessageAssert.assertEquals(message, test);
	}
}
