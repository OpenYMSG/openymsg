package org.openymsg.context.session;

import java.io.IOException;

import org.openymsg.testing.MessageAssert;
import org.testng.annotations.Test;

public class KeepAliveMessageTest {

	@Test
	public void test() throws IOException {
		String test = "Magic:YMSG Version:16 Length:15 Service:KEEPALIVE Status:DEFAULT SessionId:0x45130f  [0] [testuser]";
		String username = "testuser";
		KeepAliveMessage message = new KeepAliveMessage(username);
		MessageAssert.assertEquals(message, test);
	}
}
