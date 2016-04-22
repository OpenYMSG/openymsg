package org.openymsg.context.session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.contact.status.ContactStatusChangeCallback;
import org.openymsg.context.SessionContextImpl;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

public class PagerLogoffResponseTest {
	@Mock
	private SessionContextImpl sessionContext;
	@Mock
	private ContactStatusChangeCallback statusCallback;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() {
		String test = "Magic:YMSG Version:16 Length:15 Service:LOGOFF Status:SERVER_ACK SessionId:0x45130f  [7] [testuser]";
		YMSG9Packet packet = PacketReader.readString(test);
		String username = "testuser";
		PagerLogoffResponse response = new PagerLogoffResponse(username, sessionContext, statusCallback);
		response.execute(packet);
	}
}
