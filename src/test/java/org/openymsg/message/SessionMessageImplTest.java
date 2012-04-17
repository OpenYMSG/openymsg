package org.openymsg.message;

import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.openymsg.Contact;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.Executor;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.execute.write.Message;
import org.openymsg.network.ServiceType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionMessageImplTest {
	private String username = "testuser";
	private Contact contact = new Contact("testbuddy", YahooProtocol.YAHOO);
	private Executor executor;
	private SessionMessageCallback callback;
	private SessionMessageImpl session;

	@BeforeMethod
	public void beforeMethod() {
		executor = Mockito.mock(Executor.class);
		callback = Mockito.mock(SessionMessageCallback.class);
		session = new SessionMessageImpl(executor, username, callback);
	}

	@AfterMethod
	public void afterMethod() {
		Mockito.verifyNoMoreInteractions(callback);
		Mockito.verify(executor).register(Mockito.eq(ServiceType.MESSAGE_ACK), (SinglePacketResponse) Mockito.any());
		Mockito.verify(executor).register(Mockito.eq(ServiceType.MESSAGE), (SinglePacketResponse) Mockito.any());
		Mockito.verify(executor).register(Mockito.eq(ServiceType.NOTIFY), (SinglePacketResponse) Mockito.any());
		Mockito.verifyNoMoreInteractions(executor);
	}

	@Test
	public void testSendMessage() {
		String message = "dfgfdgdfgdfgfdg";
		session.sendMessage(contact, message);
		Mockito.verify(executor).execute(argThat(new SendMessage(username, contact, message, "0"), "messageId"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Contact cannot be null")
	public void testSendMessageNoContact() {
		String message = "dfgfdgdfgdfgfdg";
		session.sendMessage(null, message);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Message cannot be null")
	public void testSendMessageNoMessage() {
		session.sendMessage(contact, null);
	}

	@Test
	public void testSendBuzz() {
		session.sendBuzz(contact);
		Mockito.verify(executor).execute(
				argThat(new SendMessage(username, contact, SessionMessageImpl.BUZZ, "0"), "messageId"));
	}

	@Test
	public void testTypingStarted() {
		session.sendTypingNotification(contact, true);
		Mockito.verify(executor).execute(argThat((new TypingNotificationMessage(username, contact, true))));
	}

	@Test
	public void testTypingDone() {
		session.sendTypingNotification(contact, false);
		Mockito.verify(executor).execute(argThat(new TypingNotificationMessage(username, contact, false)));
	}

	@Test
	public void testMessageReceiveNoMessageId() {
		String message = "hello back";
		session.receivedMessage(contact, message, null);
		Mockito.verify(callback).receivedMessage(contact, message);
	}

	@Test
	public void testMessageReceiveMessageId() {
		String message = "hello back";
		String messageId = "id";
		session.receivedMessage(contact, message, messageId);
		Mockito.verify(callback).receivedMessage(contact, message);
		Mockito.verify(executor).execute(argThat(new MessageAckMessage(username, contact, messageId)));
	}

	@Test
	public void testMessageReceiveOffline() {
		String message = "hello back";
		long timestampInMillis = System.currentTimeMillis();
		session.receivedOfflineMessage(contact, message, timestampInMillis);
		Mockito.verify(callback).receivedOfflineMessage(contact, message, timestampInMillis);
	}

	@Test
	public void testMessageReceiveOfflineNoTime() {
		String message = "hello back";
		session.receivedOfflineMessage(contact, message, 0);
		Mockito.verify(callback).receivedMessage(contact, message);
	}

	@Test
	public void testMessageReceiveTypingBoth() {
		session.receivedTypingNotification(contact, true);
		session.receivedTypingNotification(contact, false);
		Mockito.verify(callback).receivedTypingNotification(contact, true);
		Mockito.verify(callback).receivedTypingNotification(contact, false);
	}

	@Test
	public void testMessageReceiveBuzz() {
		session.receivedBuzz(contact, null);
		session.receivedBuzz(contact, "id");
		Mockito.verify(callback, Mockito.times(2)).receivedBuzz(contact);
	}

	private Message argThat(Message message, String... excludeFields) {
		return (Message) Mockito.argThat(new ReflectionEquals(message, excludeFields));
	}

}
