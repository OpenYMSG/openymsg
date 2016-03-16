package org.openymsg.message;

import java.io.IOException;

import org.openymsg.YahooContact;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Message for sending a typing notification. In the future, gaming notifications are similar and my be handled.
 * @author neilhart
 */
public class TypingNotificationMessage implements Message {
	/** type for typing */
	private static final String NOTIFY_TYPING = "TYPING";
	/** username */
	private String username;
	/** who the notification is sent to */
	private YahooContact contact;
	/** typing or done */
	private boolean on;

	/**
	 * build a typing notification message
	 * @param username user's name
	 * @param contact destination for the notification
	 * @param on true if typing, false if done
	 */
	public TypingNotificationMessage(String username, YahooContact contact, boolean on) {
		this.username = username;
		this.contact = contact;
		this.on = on;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		final PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("49", NOTIFY_TYPING);
		body.addElement("1", this.username);
		body.addElement("14", " "); // msg);
		if (on) {
			body.addElement("13", "1");
		} else {
			body.addElement("13", "0");
		}
		body.addElement("5", contact.getName());
		if (!contact.getProtocol().isYahoo()) {
			body.addElement("241", contact.getProtocol().getValue());
		}
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.NOTIFY;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.NOTIFY;
	}

}
