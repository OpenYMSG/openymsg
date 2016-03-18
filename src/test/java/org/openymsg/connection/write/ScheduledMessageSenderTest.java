package org.openymsg.connection.write;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openymsg.connection.YahooConnection;

public class ScheduledMessageSenderTest {
	private YahooConnection executor;
	private Message message;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void beforeMethod() {
		executor = mock(YahooConnection.class);
		message = mock(Message.class);
	}

	@After
	public void afterMethod() {
		verifyNoMoreInteractions(executor);
		verifyNoMoreInteractions(message);
	}

	@Test
	public void testExecute() {
		ScheduledMessageSender sender = new ScheduledMessageSender(executor, message);
		sender.execute();
		verify(executor).execute(message);
	}

	@Test()
	public void testNoExecutor() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Executor cannot be null");
		new ScheduledMessageSender(null, message);
	}

	@Test()
	public void testNoMessage() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Message cannot be null");
		new ScheduledMessageSender(executor, null);
	}

	@Test
	public void testException() {
		ScheduledMessageSender sender = new ScheduledMessageSender(executor, message);
		sender.failure(new Exception("Test failure"));
	}
}
