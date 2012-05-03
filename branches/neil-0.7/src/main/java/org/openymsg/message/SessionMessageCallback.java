package org.openymsg.message;

import org.openymsg.YahooContact;

/**
 * Receiving messages and typing status from Yahoo. The contact may not be one of the user's contact
 * @author neilhart
 */
public interface SessionMessageCallback {
	/**
	 * received a message
	 * @param from contact who sent the message
	 * @param message message
	 */
	void receivedMessage(YahooContact from, String message);

	/**
	 * received a buzz
	 * @param from contact who sent the message
	 */
	void receivedBuzz(YahooContact from);

	/**
	 * received an off-line message.
	 * @param from contact who sent the message
	 * @param message message
	 * @param timestampInMillis original time stamp of the message
	 */
	void receivedOfflineMessage(YahooContact from, String message, long timestampInMillis);

	/**
	 * received a typing notification
	 * @param from contact who started/stop typing
	 * @param isTyping true if typing, false if done
	 */
	void receivedTypingNotification(YahooContact from, boolean isTyping);
}
