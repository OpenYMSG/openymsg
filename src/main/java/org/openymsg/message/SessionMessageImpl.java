package org.openymsg.message;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.Contact;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.Executor;

public class SessionMessageImpl implements SessionMessage {
    // Buzz string
    public final static String BUZZ = "<ding>";
	private static final Log log = LogFactory.getLog(SessionMessageImpl.class);
	private Executor executor;
	private String username;
	//TODO - random messageNumber start
    private long messageNumber = System.currentTimeMillis();

	public SessionMessageImpl(Executor executor, String username) {
		this.executor = executor;
		this.username = username;
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
    //TODO - handle message ask
    public void sendMessage(Contact contact, String message) throws IllegalStateException {
    	//TODO - check status
//        checkStatus();

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

    protected String getType(Contact contact) {
        if (contact == null || contact.getProtocol() == null) {
            return YahooProtocol.YAHOO.getStringValue();
        }
        return contact.getProtocol().getStringValue();
    }

    protected String buildMessageNumber() {
    	//TODO - better start point
        String blankMessageNumber = "0000000000000000";
        String messageNumber = "" + this.messageNumber++;
        messageNumber = blankMessageNumber.substring(0, blankMessageNumber.length() - messageNumber.length())
                + messageNumber;
        return messageNumber;
    }

}
