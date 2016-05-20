package org.openymsg.connection.read;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.YMSG9Packet;

public class ConnectionReaderJobTest {
	@Mock
	private ConnectionHandler connection;
	@Mock
	private ConnectionReaderReceiver receiver;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void afterMethod() {
		verifyNoMoreInteractions(connection);
		verifyNoMoreInteractions(receiver);
	}

	@Test
	public void testNoPacket() {
		ConnectionReaderJob reader = new ConnectionReaderJob(connection, receiver);
		reader.execute();
		verify(connection).receivePacket();
		verifyZeroInteractions(receiver);
	}

	@Test(timeout = 500)
	public void testSinglePacket() {
		YMSG9Packet packet = new YMSG9Packet();
		when(connection.receivePacket()).thenReturn(packet).thenReturn(null);
		ConnectionReaderJob reader = new ConnectionReaderJob(connection, receiver);
		reader.execute();
		verify(connection, times(2)).receivePacket();
		verify(receiver).received(packet);
	}

	@Test(timeout = 500)
	public void testMultiplePacket() {
		YMSG9Packet packet1 = new YMSG9Packet();
		YMSG9Packet packet2 = new YMSG9Packet();
		YMSG9Packet packet3 = new YMSG9Packet();
		when(connection.receivePacket()).thenReturn(packet1, packet2, packet3).thenReturn(null);
		ConnectionReaderJob reader = new ConnectionReaderJob(connection, receiver);
		reader.execute();
		verify(connection, times(4)).receivePacket();
		verify(receiver).received(packet1);
		verify(receiver).received(packet2);
		verify(receiver).received(packet3);
	}

	@Test
	public void testFinished() {
		YMSG9Packet packet = new YMSG9Packet();
		when(connection.receivePacket()).thenReturn(packet).thenReturn(null);
		ConnectionReaderJob reader = new ConnectionReaderJob(connection, receiver);
		reader.finished();
		try {
			reader.execute();
		} catch (Exception e) {
			// fine throwing exception
		}
		verifyZeroInteractions(connection);
		verifyZeroInteractions(receiver);
	}

	@Test
	public void testException() {
		ConnectionReaderJob reader = new ConnectionReaderJob(connection, receiver);
		reader.failure(new Exception("Test failure"));
	}
}
