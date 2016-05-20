package org.openymsg.message;

import org.openymsg.YahooContact;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.read.NoOpResponse;
import org.openymsg.connection.read.ReaderRegistry;
import org.openymsg.connection.write.Message;
import org.openymsg.connection.write.PacketWriter;
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
	protected final PacketWriter writer;
	/** user name */
	protected final String username;
	// TODO - random messageNumber start
	/** initial message number */
	private long messageNumber = System.currentTimeMillis();
	/** callback for incoming messages and notifications */
	private final SessionMessageCallback callback;

	/**
	 * Creates the manager for sending and receiving messages and typing notification
	 * @param connection connection to yahoo
	 * @param username user name
	 * @param callback callback for notification of message and typing
	 * @throws IllegalArgumentException if executor, user name, or callback is null
	 */
	public SessionMessageImpl(YahooConnection connection, String username, SessionMessageCallback callback)
			throws IllegalArgumentException {
		if (connection == null) {
			throw new IllegalArgumentException("connection cannot be null");
		}
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}
		this.writer = connection.getPacketWriter();
		this.username = username;
		this.callback = callback;
		ReaderRegistry registry = connection.getReaderRegistry();
		registry.register(ServiceType.MESSAGE_ACK, new NoOpResponse());
		registry.register(ServiceType.MESSAGE, new MessageResponse(this));
		registry.register(ServiceType.NOTIFY, new TypingNotificationResponse(this));
	}

	@Override
	public void sendBuzz(YahooContact to) throws IllegalArgumentException {
		sendMessage(to, BUZZ);
	}

	// TODO - handle message ask
	// TODO - current contact?
	@Override
	public void sendMessage(YahooContact contact, String message) throws IllegalArgumentException {
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null");
		}
		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
		String messageId = buildMessageNumber();
		this.writer.execute(createSendMessageRequest(contact, message, messageId));
	}

	protected Message createSendMessageRequest(YahooContact contact, String message, String messageId) {
		return new SendMessage(username, contact, message, messageId);
	}

	@Override
	public void sendTypingNotification(YahooContact contact, boolean isTyping) {
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null");
		}
		this.writer.execute(new TypingNotificationMessage(username, contact, isTyping));
	}

	// TODO - send ack if ignored?
	public void receivedMessage(YahooContact contact, String message, String messageId) {
		if (messageId != null) {
			this.writer.execute(new MessageAckMessage(username, contact, messageId));
		}
		this.callback.receivedMessage(contact, message);
	}

	// TODO - send ack if ignored?
	public void receivedBuzz(YahooContact contact, String messageId) {
		if (messageId != null) {
			this.writer.execute(new MessageAckMessage(username, contact, messageId));
		}
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
		messageNumber =
				blankMessageNumber.substring(0, blankMessageNumber.length() - messageNumber.length()) + messageNumber;
		return messageNumber;
	}
}
