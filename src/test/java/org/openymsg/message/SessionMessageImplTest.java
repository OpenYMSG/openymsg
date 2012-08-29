package org.openymsg.message;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.openymsg.testing.MessageAssert.argThatMessage;

import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.network.ServiceType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionMessageImplTest {
	private String username = "testuser";
	private YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
	private YahooConnection executor;
	private SessionMessageCallback callback;
	private SessionMessageImpl session;

	@BeforeMethod
	public void beforeMethod() {
		executor = mock(YahooConnection.class);
		callback = mock(SessionMessageCallback.class);
		session = new SessionMessageImpl(executor, username, callback);
	}

	@AfterMethod
	public void afterMethod() {
		verifyNoMoreInteractions(callback);
		verify(executor).register(eq(ServiceType.MESSAGE_ACK), (SinglePacketResponse) any());
		verify(executor).register(eq(ServiceType.MESSAGE), (SinglePacketResponse) any());
		verify(executor).register(eq(ServiceType.NOTIFY), (SinglePacketResponse) any());
		verifyNoMoreInteractions(executor);
	}

	@Test
	public void testSendMessage() {
		String message = "dfgfdgdfgdfgfdg";
		session.sendMessage(contact, message);
		verify(executor).execute(argThatMessage(new SendMessage(username, contact, message, "0"), "messageId"));
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
		verify(executor).execute(
				argThatMessage(new SendMessage(username, contact, SessionMessageImpl.BUZZ, "0"), "messageId"));
	}

	@Test
	public void testTypingStarted() {
		session.sendTypingNotification(contact, true);
		verify(executor).execute(argThatMessage((new TypingNotificationMessage(username, contact, true))));
	}

	@Test
	public void testTypingDone() {
		session.sendTypingNotification(contact, false);
		verify(executor).execute(argThatMessage(new TypingNotificationMessage(username, contact, false)));
	}

	@Test
	public void testMessageReceiveNoMessageId() {
		String message = "hello back";
		session.receivedMessage(contact, message, null);
		verify(callback).receivedMessage(contact, message);
	}

	@Test
	public void testMessageReceiveMessageId() {
		String message = "hello back";
		String messageId = "id";
		session.receivedMessage(contact, message, messageId);
		verify(callback).receivedMessage(contact, message);
		verify(executor).execute(argThatMessage(new MessageAckMessage(username, contact, messageId)));
	}

	@Test
	public void testMessageReceiveOffline() {
		String message = "hello back";
		long timestampInMillis = System.currentTimeMillis();
		session.receivedOfflineMessage(contact, message, timestampInMillis);
		verify(callback).receivedOfflineMessage(contact, message, timestampInMillis);
	}

	@Test
	public void testMessageReceiveOfflineNoTime() {
		String message = "hello back";
		session.receivedOfflineMessage(contact, message, 0);
		verify(callback).receivedMessage(contact, message);
	}

	@Test
	public void testMessageReceiveTypingBoth() {
		session.receivedTypingNotification(contact, true);
		session.receivedTypingNotification(contact, false);
		verify(callback).receivedTypingNotification(contact, true);
		verify(callback).receivedTypingNotification(contact, false);
	}

	@Test
	public void testMessageReceiveBuzz() {
		session.receivedBuzz(contact, null);
		session.receivedBuzz(contact, "id");
		verify(callback, times(2)).receivedBuzz(contact);
	}

}
