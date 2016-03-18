package org.openymsg.context.session;

import org.junit.Test;
import org.mockito.Mockito;
import org.openymsg.contact.SessionContactImpl;
import org.openymsg.context.SessionContextImpl;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

public class PagerLogoffResponseTest {
	@Test
	public void test() {
		String test =
				"Magic:YMSG Version:16 Length:15 Service:LOGOFF Status:SERVER_ACK SessionId:0x45130f  [7] [testuser]";
		YMSG9Packet packet = PacketReader.readString(test);
		String username = "testuser";
		SessionContextImpl sessionContext = Mockito.mock(SessionContextImpl.class);
		SessionContactImpl sessionContact = Mockito.mock(SessionContactImpl.class);
		PagerLogoffResponse response = new PagerLogoffResponse(username, sessionContext, sessionContact);
		response.execute(packet);
	}
}
