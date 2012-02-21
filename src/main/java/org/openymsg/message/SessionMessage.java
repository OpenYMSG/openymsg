package org.openymsg.message;

import java.io.IOException;

import org.openymsg.Contact;

//TODO - send message to non-contacts, including MSN
public interface SessionMessage {
	/**
	 * Send a chat message.
	 * 
	 * @param to Yahoo ID of the user to transmit the message.
	 * @param message The message to transmit.
	 * @throws IllegalStateException
	 */
	void sendMessage(Contact contact, String message) throws IllegalStateException;

	/**
	 * Send a buzz message
	 * 
	 * @param to Recipient of the buzz.
	 * @throws IllegalStateException
	 */
	void sendBuzz(Contact contact) throws IllegalStateException;

	void sendTypingNotification(Contact contact, boolean isTyping) throws IOException;
	
}
