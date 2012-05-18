package org.openymsg.connection.read;

import org.mockito.Mockito;
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
		connection = Mockito.mock(ConnectionHandler.class);
		registry = Mockito.mock(ReaderRegistryImpl.class);
	}

	@AfterMethod
	public void afterMethod() {
		Mockito.verifyNoMoreInteractions(connection);
		Mockito.verifyNoMoreInteractions(registry);
	}

	@Test
	public void testNoPacket() {
		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.execute();

		Mockito.verify(connection).receivePacket();
		Mockito.verifyZeroInteractions(registry);
	}

	@Test(timeOut = 500)
	public void testSinglePacket() {
		YMSG9Packet packet = new YMSG9Packet();
		Mockito.when(connection.receivePacket()).thenReturn(packet).thenReturn(null);

		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.execute();

		Mockito.verify(connection, Mockito.times(2)).receivePacket();
		Mockito.verify(registry).received(packet);
	}

	@Test(timeOut = 500)
	public void testMultiplePacket() {
		YMSG9Packet packet1 = new YMSG9Packet();
		YMSG9Packet packet2 = new YMSG9Packet();
		YMSG9Packet packet3 = new YMSG9Packet();
		Mockito.when(connection.receivePacket()).thenReturn(packet1, packet2, packet3).thenReturn(null);

		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.execute();

		Mockito.verify(connection, Mockito.times(4)).receivePacket();
		Mockito.verify(registry).received(packet1);
		Mockito.verify(registry).received(packet2);
		Mockito.verify(registry).received(packet3);
	}

	@Test
	public void testFinished() {
		YMSG9Packet packet = new YMSG9Packet();
		Mockito.when(connection.receivePacket()).thenReturn(packet).thenReturn(null);

		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.finished();
		try {
			reader.execute();
		}
		catch (Exception e) {
			// fine throwing exception
		}

		Mockito.verifyZeroInteractions(connection);
		Mockito.verifyZeroInteractions(registry);
	}

	@Test
	public void testException() {
		ConnectionReader reader = new ConnectionReader(connection, registry);
		reader.failure(new Exception("Test failure"));
	}

}
