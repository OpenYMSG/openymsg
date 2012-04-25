package org.openymsg.execute.write;

import org.mockito.Mockito;
import org.openymsg.execute.Executor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ScheduledMessageSenderTest {
	private Executor executor;
	private Message message;

	@BeforeMethod
	public void beforeMethod() {
		executor = Mockito.mock(Executor.class);
		message = Mockito.mock(Message.class);
	}

	@AfterMethod
	public void afterMethod() {
		Mockito.verifyNoMoreInteractions(executor);
		Mockito.verifyNoMoreInteractions(message);
	}

	@Test
	public void testExecute() {
		ScheduledMessageSender sender = new ScheduledMessageSender(executor, message);
		sender.execute();

		Mockito.verify(executor).execute(message);
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
