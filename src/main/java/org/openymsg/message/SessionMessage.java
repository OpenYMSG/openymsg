package org.openymsg.message;

import org.openymsg.YahooContact;

/**
 * Session services for sending messages and typing notifications.
 * @author neilhart
 */
// TODO - send message to non-contacts, including MSN
// non-contact, opens a Y7 Chat Session, notification message(web login)
public interface SessionMessage {
	/**
	 * Send a chat message.
	 * @param contact Yahoo ID of the user to transmit the message.
	 * @param message The message to transmit.
	 * @throws IllegalArgumentException if contact or message is null
	 */
	// TODO handle offline
	void sendMessage(YahooContact contact, String message) throws IllegalArgumentException;

	/**
	 * Send a buzz message
	 * @param contact Recipient of the buzz.
	 * @throws IllegalArgumentException if contact is null
	 */
	// TODO handle offline
	void sendBuzz(YahooContact contact) throws IllegalArgumentException;

	/**
	 * Send notification of typing
	 * @param contact to buddy
	 * @param isTyping typing or done
	 * @throws IllegalArgumentException if contact is null
	 */
	void sendTypingNotification(YahooContact contact, boolean isTyping) throws IllegalArgumentException;

}
