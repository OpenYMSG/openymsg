package org.openymsg.message;


/**
 * Receiving messages and typing status from Yahoo.  The contactId may not be a contact but a non-contact Yahoo Id.
 * 
 * @author neilhart
 *
 */
public interface SessionMessageCallback {
	//TODO how to handle MSN users
	void receivedMessage(String contactId, String message);
	void receivedBuzz(String contactId);
	void receivedOfflineMessage(String contactId, String message, long timestampInMillis);
	void receivedTypingNotification(String contactId, boolean isTyping);
}
