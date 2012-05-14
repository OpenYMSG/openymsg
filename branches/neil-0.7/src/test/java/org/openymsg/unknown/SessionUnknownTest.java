package org.openymsg.unknown;

import org.mockito.Mockito;
import org.openymsg.connection.read.PacketReader;
import org.openymsg.network.ServiceType;
import org.testng.annotations.Test;

public class SessionUnknownTest {

	@Test
	public void test() {
		PacketReader reader = Mockito.mock(PacketReader.class);
		SessionUnknown session = new SessionUnknown(reader);
		Mockito.verify(reader).register(ServiceType.UNKNOWN002, new Unknown002Response());

	}
}
