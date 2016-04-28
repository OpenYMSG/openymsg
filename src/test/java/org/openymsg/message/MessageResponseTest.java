package org.openymsg.message;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.YahooConnection;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

public class MessageResponseTest {
	private String username = "testuser";
	@Mock
	private SessionMessageCallback callback;
	@Mock
	private YahooConnection executor;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSimple() {
		String test = "Magic:YMSG Version:16 Length:206 Service:MESSAGE Status:SERVER_ACK SessionId:0x5fcd19  [4] [testbuddy] [5] [testuser] [14] [sending message] [15] [1335299986] [63] [;0] [64] [0] [97] [1] [206] [2] [252] [GHXzmtBef5EjXVljxP9GLk4WZBJnlQ==] [429] [000000004172116E] [450] [0] [455] [GHXzmtBef5EjXVljxP9GLk4WZBJnlQ==]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionMessageImpl session = new SessionMessageImpl(executor, username, callback);
		MessageResponse response = new MessageResponse(session);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "sending message";
		verify(callback).receivedMessage(contact, message);
	}

	@Test
	public void testProcotol() {
		String test = "Magic:YMSG Version:16 Length:158 Service:MESSAGE Status:SERVER_ACK SessionId:0x45130f  [4] [testbuddy@live.com] [5] [testuser] [14] [hello back] [15] [1332786061] [241] [2] [252] [Ihq8NfPGNq9SBqI9cXsa+Wh2T5/Izw==] [455] [Ihq8NfPGNq9SBqI9cXsa+Wh2T5/Izw==]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionMessageImpl session = new SessionMessageImpl(executor, username, callback);
		MessageResponse response = new MessageResponse(session);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy@live.com", YahooProtocol.MSN);
		String message = "hello back";
		verify(callback).receivedMessage(contact, message);
	}

	@Test
	public void testOfflineMultipleFromSameContact() {
		String test = "Magic:YMSG Version:16 Length:523 Service:MESSAGE Status:OFFLINE5 SessionId:0x58fe2f  [31] [6] [32] [6] [4] [testbuddy] [5] [testuser] [14] [sending number one] [15] [1334803157] [97] [1] [252] [o7XmlqokuTHEHuuc4dw5D0sv9oWQ2Q==] [455] [o7XmlqokuTHEHuuc4dw5D0sv9oWQ2Q==] [31] [6] [32] [6] [4] [testbuddy] [5] [testuser] [14] [sending number two] [15] [1334803164] [97] [1] [252] [m5XeFPwB53agkDuGgw7fnwADyVdjBQ==] [455] [m5XeFPwB53agkDuGgw7fnwADyVdjBQ==] [31] [6] [32] [6] [4] [testbuddy] [5] [testuser] [14] [and number three] [15] [1334803171] [97] [1] [252] [RQ2dWeIwbyLaHXjEuOeDX2EW+KRGoQ==] [455] [RQ2dWeIwbyLaHXjEuOeDX2EW+KRGoQ==]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionMessageImpl session = new SessionMessageImpl(executor, username, callback);
		MessageResponse response = new MessageResponse(session);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "sending number one";
		verify(callback).receivedOfflineMessage(contact, message, 1334803157L * 1000);
		message = "sending number two";
		verify(callback).receivedOfflineMessage(contact, message, 1334803164L * 1000);
		message = "and number three";
		verify(callback).receivedOfflineMessage(contact, message, 1334803171L * 1000);
	}
}
