package org.openymsg.message;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.Executor;

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
	}

	/**
	 * Send a buzz message
	 * 
	 * @param to
	 *            Recipient of the buzz.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void sendBuzz(Contact to) throws IllegalStateException {
		sendMessage(to, BUZZ);
	}

	/**
	 * Send a chat message.
	 * 
	 * @param to
	 *            Yahoo ID of the user to transmit the message.
	 * @param message
	 *            The message to transmit.
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

		String contactType = this.getType(contact);

		String messageId = buildMessageNumber();

		this.executor.execute(new SendMessage(username, contact.getId(), contactType, message, messageId));
	}
	
    /**
     * notify to friend the typing start or end action message parameters Version: 16 Service: Notify (75) Status:
     * Notify (22)
     * 
     * 49: TYPING 1: userId 14: <empty> 13: 1 or 0 5: sendingToId
     * 
     * @param friend
     *            user whose sending message
     * @param isTyping
     *            true if start typing, false if typing end up
     * @throws IOException
     */
	@Override
    public void sendTypingNotification(Contact contact, boolean isTyping) throws IOException {
    	//TODO type for msn users
//        String type = this.getType(friend);
		this.executor.execute(new TypingNotificationMessage(username, contact, isTyping));
//        transmitNotify(friend, primaryID.getId(), isTyping, " ", NOTIFY_TYPING, type);
    }

	protected String getType(Contact contact) {
		if (contact == null || contact.getProtocol() == null) {
			return YahooProtocol.YAHOO.getStringValue();
		}
		return contact.getProtocol().getStringValue();
	}

	protected String buildMessageNumber() {
		// TODO - better start point
		String blankMessageNumber = "0000000000000000";
		String messageNumber = "" + this.messageNumber++;
		messageNumber = blankMessageNumber.substring(0, blankMessageNumber.length() - messageNumber.length())
				+ messageNumber;
		return messageNumber;
	}

	public void receivedMessage(String contactId, String message, String messageId) {
		this.executor.execute(new MessageAckMessage(username, contactId, messageId));
		this.callback.receivedMessage(contactId, message);
	}

	public void receivedBuzz(String contactId, String id) {
		this.callback.receivedBuzz(contactId);
	}

	public void receivedOfflineMessage(String from, String message, long timestampInMillis) {
		if (timestampInMillis == 0) {
			this.callback.receivedMessage(from, message);
		}
		else {
			this.callback.receivedOfflineMessage(from, message, timestampInMillis);
		}
	}

}
