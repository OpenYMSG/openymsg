package org.openymsg.message;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

public class TypingNotificationResponseTest {
	private YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
	private SessionMessageImpl session;
	private TypingNotificationResponse response;

	@Before
	public void beforeMethod() {
		session = mock(SessionMessageImpl.class);
		response = new TypingNotificationResponse(session);
	}

	@Test
	public void testYahooTyping() {
		String test =
				"Magic:YMSG Version:16 Length:56 Service:NOTIFY Status:SERVER_ACK SessionId:0x45130f  [4] [testbuddy] [5] [testuser] [13] [1] [14] [ ] [49] [TYPING]";
		YMSG9Packet packet = PacketReader.readString(test);
		response.execute(packet);
		verify(session).receivedTypingNotification(contact, true);
	}

	@Test
	public void testYahooDone() {
		String test =
				"Magic:YMSG Version:16 Length:56 Service:NOTIFY Status:SERVER_ACK SessionId:0x45130f  [4] [testbuddy] [5] [testuser] [13] [0] [14] [ ] [49] [TYPING]";
		YMSG9Packet packet = PacketReader.readString(test);
		response.execute(packet);
		verify(session).receivedTypingNotification(contact, false);
	}
}
