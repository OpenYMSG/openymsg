package org.openymsg.connection.write;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.openymsg.connection.YahooConnection;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ScheduledMessageSenderTest {
	private YahooConnection executor;
	private Message message;

	@BeforeMethod
	public void beforeMethod() {
		executor = mock(YahooConnection.class);
		message = mock(Message.class);
	}

	@AfterMethod
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

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Executor cannot be null")
	public void testNoExecutor() {
		new ScheduledMessageSender(null, message);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Message cannot be null")
	public void testNoMessage() {
		new ScheduledMessageSender(executor, null);
	}

	@Test
	public void testException() {
		ScheduledMessageSender sender = new ScheduledMessageSender(executor, message);
		sender.failure(new Exception("Test failure"));
	}

}
