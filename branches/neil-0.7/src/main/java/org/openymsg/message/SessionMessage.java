package org.openymsg.message;

import org.openymsg.Contact;

public interface SessionMessage {
	/**
	 * Send a chat message.
	 * 
	 * @param to
	 *            Yahoo ID of the user to transmit the message.
	 * @param message
	 *            The message to transmit.
	 * @throws IllegalStateException
	 */
	void sendMessage(Contact contact, String message) throws IllegalStateException;

	/**
	 * Send a buzz message
	 * 
	 * @param to
	 *            Recipient of the buzz.
	 * @throws IllegalStateException
	 */
	public void sendBuzz(Contact to) throws IllegalStateException;

}
