package org.openymsg.message;

import org.openymsg.YahooContact;

//TODO - send message to non-contacts, including MSN
//non-contact, opens a Y7 Chat Session, notification message(web login)
public interface SessionMessage {
	/**
	 * Send a chat message.
	 * @param to Yahoo ID of the user to transmit the message.
	 * @param message The message to transmit.
	 * @throws IllegalStateException
	 */
	// TODO handle offline
	void sendMessage(YahooContact contact, String message) throws IllegalStateException;

	/**
	 * Send a buzz message
	 * @param to Recipient of the buzz.
	 * @throws IllegalStateException
	 */
	// TODO handle offline
	void sendBuzz(YahooContact contact) throws IllegalStateException;

	/**
	 * Send notification of typing
	 * @param contact to buddy
	 * @param isTyping typing or done
	 */
	void sendTypingNotification(YahooContact contact, boolean isTyping);

}
