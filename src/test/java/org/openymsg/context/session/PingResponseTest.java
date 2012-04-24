package org.openymsg.context.session;

import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.annotations.Test;

public class PingResponseTest {

	@Test
	public void test() {
		String test = "Magic:YMSG Version:16 Length:18 Service:PING Status:SERVER_ACK SessionId:0x45130f  [143] [60] [144] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		PingResponse response = new PingResponse();
		response.execute(packet);
	}
}
