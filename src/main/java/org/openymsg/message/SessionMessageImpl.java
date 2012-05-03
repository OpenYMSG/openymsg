package org.openymsg.message;

import org.openymsg.YahooContact;
import org.openymsg.execute.Executor;
import org.openymsg.execute.read.NoOpResponse;
import org.openymsg.network.ServiceType;

/**
 * //TODO add top javadoc * Incoming messages and typing notifications will be forwarded onto the callback. This service
 * also handles creating the message number sent to yahoo and ack'ing incoming messages back to yahoo.
 * @author neilhart
 */
public class SessionMessageImpl implements SessionMessage {
	/** buzz messsage */
	public final static String BUZZ = "<ding>";
	/** blank message number format */
	private static final String blankMessageNumber = "0000000000000000";
	/** executor for the messages */
	private Executor executor;
	/** user name */
	private String username;
	// TODO - random messageNumber start
	/** initial message number */
	private long messageNumber = System.currentTimeMillis();
	/** callback for incoming messages and notifications */
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

	public void sendBuzz(YahooContact to) throws IllegalArgumentException {
		sendMessage(to, BUZZ);
	}

	// TODO - handle message ask
	// TODO - current contact?
	public void sendMessage(YahooContact contact, String message) throws IllegalArgumentException {
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
	public void sendTypingNotification(YahooContact contact, boolean isTyping) {
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null");
		}

		this.executor.execute(new TypingNotificationMessage(username, contact, isTyping));
	}

	// TODO - send ack if ignored?
	public void receivedMessage(YahooContact contact, String message, String messageId) {
		if (messageId != null) {
			this.executor.execute(new MessageAckMessage(username, contact, messageId));
		}

		this.callback.receivedMessage(contact, message);
	}

	// TODO - ack buzz?
	public void receivedBuzz(YahooContact contact, String id) {
		this.callback.receivedBuzz(contact);
	}

	public void receivedOfflineMessage(YahooContact contact, String message, long timestampInMillis) {
		if (timestampInMillis == 0) {
			this.callback.receivedMessage(contact, message);
		} else {
			this.callback.receivedOfflineMessage(contact, message, timestampInMillis);
		}
	}

	public void receivedTypingNotification(YahooContact contact, boolean isTyping) {
		this.callback.receivedTypingNotification(contact, isTyping);
	}

	protected String buildMessageNumber() {
		String messageNumber = "" + this.messageNumber++;
		messageNumber = blankMessageNumber.substring(0, blankMessageNumber.length() - messageNumber.length())
				+ messageNumber;
		return messageNumber;
	}

}
