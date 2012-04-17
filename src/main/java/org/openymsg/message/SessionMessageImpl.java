package org.openymsg.message;

import org.openymsg.Contact;
import org.openymsg.execute.Executor;
import org.openymsg.execute.read.NoOpResponse;
import org.openymsg.network.ServiceType;

public class SessionMessageImpl implements SessionMessage {
	// Buzz string
	public final static String BUZZ = "<ding>";
	private static final String blankMessageNumber = "0000000000000000";
	private Executor executor;
	private String username;
	// TODO - random messageNumber start
	private long messageNumber = System.currentTimeMillis();
	private SessionMessageCallback callback;

	/**
	 * Creates the manager for sending and receiving messages and typing notification
	 * @param executor executor
	 * @param username user name
	 * @param callback callback for notification of message and typing
	 * @throws IllegalArgumentException if executor, user name, or callback is null
	 */
	public SessionMessageImpl(Executor executor, String username, SessionMessageCallback callback)
			throws IllegalArgumentException {
		if (executor == null) {
			throw new IllegalArgumentException("Executor cannot be null");
		}
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}

		this.executor = executor;
		this.username = username;
		this.callback = callback;
		this.executor.register(ServiceType.MESSAGE_ACK, new NoOpResponse());
		this.executor.register(ServiceType.MESSAGE, new MessageResponse(this));
		this.executor.register(ServiceType.NOTIFY, new TypingNotificationResponse(this));
	}

	/**
	 * Send a buzz message
	 * @param to Recipient of the buzz.
	 * @throws IllegalArgumentException if contact is null
	 */
	public void sendBuzz(Contact to) throws IllegalArgumentException {
		sendMessage(to, BUZZ);
	}

	/**
	 * Send a chat message.
	 * @param to Yahoo ID of the user to transmit the message.
	 * @param message The message to transmit.
	 * @throws IllegalArgumentException if contact or message is null
	 */
	// TODO - handle message ask
	// TODO - current contact?
	public void sendMessage(Contact contact, String message) throws IllegalArgumentException {

		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null");
		}

		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}

		String messageId = buildMessageNumber();

		this.executor.execute(new SendMessage(username, contact, message, messageId));
	}

	@Override
	public void sendTypingNotification(Contact contact, boolean isTyping) {
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null");
		}

		this.executor.execute(new TypingNotificationMessage(username, contact, isTyping));
	}

	protected String buildMessageNumber() {
		String messageNumber = "" + this.messageNumber++;
		messageNumber = blankMessageNumber.substring(0, blankMessageNumber.length() - messageNumber.length())
				+ messageNumber;
		return messageNumber;
	}

	// TODO - send ack if ignored?
	public void receivedMessage(Contact contact, String message, String messageId) {
		if (messageId != null) {
			this.executor.execute(new MessageAckMessage(username, contact, messageId));
		}
		this.callback.receivedMessage(contact, message);
	}

	// TODO - ack buzz?
	public void receivedBuzz(Contact contact, String id) {
		this.callback.receivedBuzz(contact);
	}

	public void receivedOfflineMessage(Contact contact, String message, long timestampInMillis) {
		if (timestampInMillis == 0) {
			this.callback.receivedMessage(contact, message);
		} else {
			this.callback.receivedOfflineMessage(contact, message, timestampInMillis);
		}
	}

	public void receivedTypingNotification(Contact contact, boolean isTyping) {
		this.callback.receivedTypingNotification(contact, isTyping);
	}

}
