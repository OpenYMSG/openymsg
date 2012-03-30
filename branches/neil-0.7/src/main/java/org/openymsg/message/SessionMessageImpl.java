package org.openymsg.message;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.execute.Executor;
import org.openymsg.execute.read.NoOpResponse;
import org.openymsg.network.ServiceType;

public class SessionMessageImpl implements SessionMessage {
	// Buzz string
	public final static String BUZZ = "<ding>";
	private Executor executor;
	private String username;
	// TODO - random messageNumber start
	private long messageNumber = System.currentTimeMillis();
	private SessionMessageCallback callback;

	public SessionMessageImpl(Executor executor, String username, SessionMessageCallback callback) {
		this.executor = executor;
		this.username = username;
		this.callback = callback;
		this.executor.register(ServiceType.MESSAGE_ACK, new NoOpResponse());
		this.executor.register(ServiceType.MESSAGE, new MessageResponse(this));
	}

	/**
	 * Send a buzz message
	 * @param to Recipient of the buzz.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void sendBuzz(Contact to) throws IllegalStateException {
		sendMessage(to, BUZZ);
	}

	/**
	 * Send a chat message.
	 * @param to Yahoo ID of the user to transmit the message.
	 * @param message The message to transmit.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	// TODO - handle message ask
	public void sendMessage(Contact contact, String message) throws IllegalStateException {
		// TODO - check status
		// checkStatus();

		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null");
		}

		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}

		String messageId = buildMessageNumber();

		this.executor.execute(new SendMessage(username, contact, message, messageId));
	}

	/**
	 * notify to friend the typing start or end action message parameters Version: 16 Service: Notify (75) Status:
	 * Notify (22) 49: TYPING 1: userId 14: <empty> 13: 1 or 0 5: sendingToId
	 * @param friend user whose sending message
	 * @param isTyping true if start typing, false if typing end up
	 * @throws IOException
	 */
	@Override
	public void sendTypingNotification(Contact contact, boolean isTyping) throws IOException {
		this.executor.execute(new TypingNotificationMessage(username, contact, isTyping));
	}

	protected String buildMessageNumber() {
		// TODO - better start point
		String blankMessageNumber = "0000000000000000";
		String messageNumber = "" + this.messageNumber++;
		messageNumber = blankMessageNumber.substring(0, blankMessageNumber.length() - messageNumber.length())
				+ messageNumber;
		return messageNumber;
	}

	public void receivedMessage(Contact contact, String message, String messageId) {
		this.executor.execute(new MessageAckMessage(username, contact, messageId));
		this.callback.receivedMessage(contact, message);
	}

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

}
