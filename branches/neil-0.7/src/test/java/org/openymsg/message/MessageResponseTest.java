package org.openymsg.message;

import org.mockito.Mockito;
import org.openymsg.Contact;
import org.openymsg.PacketReader;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.Executor;
import org.openymsg.network.YMSG9Packet;
import org.testng.annotations.Test;

public class MessageResponseTest {
	private String username = "testuser";

	@Test(enabled = false)
	public void testSimple() {
	}

	@Test
	public void testProcotol() {
		String test = "Magic:YMSG Version:16 Length:158 Service:MESSAGE Status:SERVER_ACK SessionId:0x45130f  [4] [testbuddy@live.com] [5] [testuser] [14] [hello back] [15] [1332786061] [241] [2] [252] [Ihq8NfPGNq9SBqI9cXsa+Wh2T5/Izw==] [455] [Ihq8NfPGNq9SBqI9cXsa+Wh2T5/Izw==]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionMessageCallback callback = Mockito.mock(SessionMessageCallback.class);
		Executor executor = Mockito.mock(Executor.class);
		SessionMessageImpl session = new SessionMessageImpl(executor, username, callback);
		MessageResponse response = new MessageResponse(session);
		response.execute(packet);
		Contact contact = new Contact("testbuddy@live.com", YahooProtocol.MSN);
		String message = "hello back";
		Mockito.verify(callback).receivedMessage(contact, message);
	}
}
