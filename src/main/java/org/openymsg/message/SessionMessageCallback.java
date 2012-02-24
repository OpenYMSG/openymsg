package org.openymsg.message;

import org.openymsg.Contact;


/**
 * Receiving messages and typing status from Yahoo.  The contactId may not be a contact but a non-contact Yahoo Id.
 * 
 * @author neilhart
 *
 */
public interface SessionMessageCallback {
	void receivedMessage(Contact from, String message);
	void receivedBuzz(Contact from);
	void receivedOfflineMessage(Contact from, String message, long timestampInMillis);
	void receivedTypingNotification(Contact from, boolean isTyping);
}
