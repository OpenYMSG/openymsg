package org.openymsg.unknown;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.read.ReaderRegistry;
import org.openymsg.connection.write.PacketWriter;
import org.openymsg.network.ServiceType;

public class SessionUnknownTest {
	@Mock
	YahooConnection connection;
	@Mock
	private PacketWriter writer;
	@Mock
	private ReaderRegistry registry;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(connection.getPacketWriter()).thenReturn(writer);
		when(connection.getReaderRegistry()).thenReturn(registry);
	}

	@Test
	public void test() {
		@SuppressWarnings("unused")
		SessionUnknown session = new SessionUnknown(connection);
		verify(registry).register(ServiceType.UNKNOWN002, new Unknown002Response());
	}
}
