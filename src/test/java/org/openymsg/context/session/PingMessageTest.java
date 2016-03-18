package org.openymsg.context.session;

import org.junit.Test;
import org.openymsg.testing.MessageAssert;

import java.io.IOException;

public class PingMessageTest extends PingMessage {
	@Test
	public void test() throws IOException {
		String test = "Magic:YMSG Version:16 Length:0 Service:PING Status:DEFAULT SessionId:0x45130f";
		PingMessage message = new PingMessage();
		MessageAssert.assertEquals(message, test);
	}
}
