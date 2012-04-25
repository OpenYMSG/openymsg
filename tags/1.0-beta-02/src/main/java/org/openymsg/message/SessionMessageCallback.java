package org.openymsg.message;

import org.openymsg.YahooContact;


/**
 * Receiving messages and typing status from Yahoo.  The contactId may not be a contact but a non-contact Yahoo Id.
 * 
 * @author neilhart
 *
 */
public interface SessionMessageCallback {
	void receivedMessage(YahooContact from, String message);
	void receivedBuzz(YahooContact from);
	void receivedOfflineMessage(YahooContact from, String message, long timestampInMillis);
	void receivedTypingNotification(YahooContact from, boolean isTyping);
}
