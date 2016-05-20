package org.openymsg.connection.write;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ScheduledMessageSenderTest {
	@Mock
	private PacketWriter writer;
	@Mock
	private Message message;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void afterMethod() {
		verifyNoMoreInteractions(writer);
		verifyNoMoreInteractions(message);
	}

	@Test
	public void testExecute() {
		ScheduledMessageSender sender = new ScheduledMessageSender(writer, message);
		sender.execute();
		verify(writer).execute(message);
	}

	@Test()
	public void testNoExecutor() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Writer cannot be null");
		new ScheduledMessageSender(null, message);
	}

	@Test()
	public void testNoMessage() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Message cannot be null");
		new ScheduledMessageSender(writer, null);
	}

	@Test
	public void testException() {
		ScheduledMessageSender sender = new ScheduledMessageSender(writer, message);
		sender.failure(new Exception("Test failure"));
	}
}
