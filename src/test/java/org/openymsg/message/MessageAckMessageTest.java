package org.openymsg.message;

import org.junit.Test;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;

public class MessageAckMessageTest {
	@Test
	public void testSimple() throws IOException {
		String test =
				"Magic:YMSG Version:16 Length:80 Service:MESSAGE_ACK Status:DEFAULT SessionId:0x45130f  [1] [testuser] [5] [testbuddy] [302] [430] [430] [000000005E810FBD] [303] [430] [450] [0]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String messageId = "000000005E810FBD";
		MessageAckMessage outgoing = new MessageAckMessage(username, contact, messageId);
		MessageAssert.assertEquals(outgoing, test);
	}
}
