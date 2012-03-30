package org.openymsg.message;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a NOTIFY packet. Could be used for all sorts of purposes, but mainly games and typing notifications. Only
 * typing is supported by this API. The mode determines the type of notification, "TYPING" or "GAME"; msg holds the game
 * name (or a single space if typing). *
 * @param friend
 * @param yid id
 * @param on true start typing, false stop typing
 * @param msg
 * @param mode
 */
public class TypingNotificationMessage implements Message {
	private static final String NOTIFY_TYPING = "TYPING";
	private String username;
	private Contact contact;
	private boolean on;

	public TypingNotificationMessage(String username, Contact contact, boolean on) {
		this.username = username;
		this.contact = contact;
		this.on = on;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		final PacketBodyBuffer body = new PacketBodyBuffer();
		// Added 1 for is typing, not sure we need the "4"
		// TODO - need all of the fields
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
