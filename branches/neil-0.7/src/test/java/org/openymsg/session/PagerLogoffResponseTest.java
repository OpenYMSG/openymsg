package org.openymsg.session;

import org.mockito.Mockito;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.annotations.Test;

public class PagerLogoffResponseTest {

	@Test
	public void test() {
		String test = "Magic:YMSG Version:16 Length:15 Service:LOGOFF Status:SERVER_ACK SessionId:0x45130f  [7] [testuser]";
		YMSG9Packet packet = PacketReader.readString(test);
		String username = "testuser";
		SessionSessionImpl session = Mockito.mock(SessionSessionImpl.class);
		PagerLogoffResponse response = new PagerLogoffResponse(username, session);
		response.execute(packet);
	}
}
