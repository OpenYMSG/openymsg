package org.openymsg.message;

import org.junit.Test;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;

public class TypingNotificationMessageTest {
	String username = "testuser";
	YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
	YahooContact contactMsn = new YahooContact("testbuddy@live.com", YahooProtocol.MSN);

	@Test
	public void testSimpleTyping() throws IOException {
		String comparisonString =
				"Magic:YMSG Version:16 Length:52 Service:NOTIFY Status:NOTIFY SessionId:0x45130f  [49] [TYPING] [1] [testuser] [14] [ ] [13] [1] [5] [testbuddy]";
		TypingNotificationMessage message = new TypingNotificationMessage(username, contact, true);
		MessageAssert.assertEquals(message, comparisonString);
	}

	@Test
	public void testSimpleDone() throws IOException {
		String comparisonString =
				"Magic:YMSG Version:16 Length:52 Service:NOTIFY Status:NOTIFY SessionId:0x45130f  [49] [TYPING] [1] [testuser] [14] [ ] [13] [0] [5] [testbuddy]";
		TypingNotificationMessage message = new TypingNotificationMessage(username, contact, false);
		MessageAssert.assertEquals(message, comparisonString);
	}

	@Test
	public void testProtocolTyping() throws IOException {
		String comparisonString =
				"Magic:YMSG Version:16 Length:74 Service:NOTIFY Status:NOTIFY SessionId:0x45130f  [49] [TYPING] [1] [testuser] [14] [ ] [13] [1] [5] [testbuddy@live.com] [241] [2]";
		TypingNotificationMessage message = new TypingNotificationMessage(username, contactMsn, true);
		MessageAssert.assertEquals(message, comparisonString);
	}

	@Test
	public void testProtocolDone() throws IOException {
		String comparisonString =
				"Magic:YMSG Version:16 Length:74 Service:NOTIFY Status:NOTIFY SessionId:0x45130f  [49] [TYPING] [1] [testuser] [14] [ ] [13] [0] [5] [testbuddy@live.com] [241] [2]";
		TypingNotificationMessage message = new TypingNotificationMessage(username, contactMsn, false);
		MessageAssert.assertEquals(message, comparisonString);
	}
}
