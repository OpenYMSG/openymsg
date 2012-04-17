package org.openymsg.message;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.MessageAssert;
import org.openymsg.YahooProtocol;
import org.testng.annotations.Test;

public class TypingNotificationMessageTest {
	String username = "testuser";
	Contact contact = new Contact("testbuddy", YahooProtocol.YAHOO);
	Contact contactMsn = new Contact("testbuddy@live.com", YahooProtocol.MSN);

	@Test
	public void testSimpleTyping() throws IOException {
		String comparisonString = "Magic:YMSG Version:16 Length:52 Service:NOTIFY Status:NOTIFY SessionId:0x45130f  [49] [TYPING] [1] [testuser] [14] [ ] [13] [1] [5] [testbuddy]";
		TypingNotificationMessage message = new TypingNotificationMessage(username, contact, true);
		MessageAssert.assertEquals(message, comparisonString);
	}

	@Test
	public void testSimpleDone() throws IOException {
		String comparisonString = "Magic:YMSG Version:16 Length:52 Service:NOTIFY Status:NOTIFY SessionId:0x45130f  [49] [TYPING] [1] [testuser] [14] [ ] [13] [0] [5] [testbuddy]";
		TypingNotificationMessage message = new TypingNotificationMessage(username, contact, false);
		MessageAssert.assertEquals(message, comparisonString);
	}

	@Test
	public void testProtocolTyping() throws IOException {
		String comparisonString = "Magic:YMSG Version:16 Length:74 Service:NOTIFY Status:NOTIFY SessionId:0x45130f  [49] [TYPING] [1] [testuser] [14] [ ] [13] [1] [5] [testbuddy@live.com] [241] [2]";
		TypingNotificationMessage message = new TypingNotificationMessage(username, contactMsn, true);
		MessageAssert.assertEquals(message, comparisonString);
	}

	@Test
	public void testProtocolDone() throws IOException {
		String comparisonString = "Magic:YMSG Version:16 Length:74 Service:NOTIFY Status:NOTIFY SessionId:0x45130f  [49] [TYPING] [1] [testuser] [14] [ ] [13] [0] [5] [testbuddy@live.com] [241] [2]";
		TypingNotificationMessage message = new TypingNotificationMessage(username, contactMsn, false);
		MessageAssert.assertEquals(message, comparisonString);
	}

}
