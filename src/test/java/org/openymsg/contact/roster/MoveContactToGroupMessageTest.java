package org.openymsg.contact.roster;

import org.junit.Test;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.MoveContactToGroupMessage;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;

public class MoveContactToGroupMessageTest {
	@Test
	public void testSimple() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:100 Service:Y7_CHANGE_GROUP Status:DEFAULT SessionId:0x41cee0  [1] [testuser] [302] [240] [300] [240] [7] [testbuddy] [224] [fromgroup] [264] [togroup] [301] [240] [303] [240]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContactGroup from = new ContactGroupImpl("fromgroup");
		YahooContactGroup to = new ContactGroupImpl("togroup");
		MoveContactToGroupMessage message = new MoveContactToGroupMessage(username, contact, from, to);
		MessageAssert.assertEquals(message, test);
	}
}
