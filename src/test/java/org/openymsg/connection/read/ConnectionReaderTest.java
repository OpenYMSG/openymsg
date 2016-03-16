package org.openymsg.connection.read;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.YMSG9Packet;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConnectionReaderTest {
	private ConnectionHandler connection;
	private ReaderRegistryImpl registry;

	@BeforeMethod
	public void beforeMethod() {
		connection = mock(ConnectionHandler.class);
		registry = mock(ReaderRegistryImpl.class);
	}

	@AfterMethod
	public void afterMethod() {
		verifyNoMoreInteractions(connection);
		verifyNoMoreInteractions(registry);
	}

	@Test
	public void testNoPacket() {
		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.execute();

		verify(connection).receivePacket();
		verifyZeroInteractions(registry);
	}

	@Test(timeOut = 500)
	public void testSinglePacket() {
		YMSG9Packet packet = new YMSG9Packet();
		when(connection.receivePacket()).thenReturn(packet).thenReturn(null);

		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.execute();

		verify(connection, times(2)).receivePacket();
		verify(registry).received(packet);
	}

	@Test(timeOut = 500)
	public void testMultiplePacket() {
		YMSG9Packet packet1 = new YMSG9Packet();
		YMSG9Packet packet2 = new YMSG9Packet();
		YMSG9Packet packet3 = new YMSG9Packet();
		when(connection.receivePacket()).thenReturn(packet1, packet2, packet3).thenReturn(null);

		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.execute();

		verify(connection, times(4)).receivePacket();
		verify(registry).received(packet1);
		verify(registry).received(packet2);
		verify(registry).received(packet3);
	}

	@Test
	public void testFinished() {
		YMSG9Packet packet = new YMSG9Packet();
		when(connection.receivePacket()).thenReturn(packet).thenReturn(null);

		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.finished();
		try {
			reader.execute();
		}
		catch (Exception e) {
			// fine throwing exception
		}

		verifyZeroInteractions(connection);
		verifyZeroInteractions(registry);
	}

	@Test
	public void testException() {
		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.failure(new Exception("Test failure"));
	}

}
