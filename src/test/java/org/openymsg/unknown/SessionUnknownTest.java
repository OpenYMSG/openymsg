package org.openymsg.unknown;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.openymsg.connection.read.PacketReader;
import org.openymsg.network.ServiceType;

public class SessionUnknownTest {
	@Test
	public void test() {
		PacketReader reader = mock(PacketReader.class);
		@SuppressWarnings("unused")
		SessionUnknown session = new SessionUnknown(reader);
		verify(reader).register(ServiceType.UNKNOWN002, new Unknown002Response());
	}
}
