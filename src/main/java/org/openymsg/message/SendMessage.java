package org.openymsg.message;

import java.io.IOException;

import org.openymsg.YahooContact;
import org.openymsg.connection.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a MESSAGE packet. * Doesn't support Doodling on whiteboard, whatever
 * that is. imvironment is ":0"
 */
public class SendMessage implements Message {
	private final String username;
	private final YahooContact contact;
	private final String message;
	private final String messageId;

	public SendMessage(String username, YahooContact contact, String message, String messageId) {
		this.username = username;
		this.contact = contact;
		this.message = message;
		this.messageId = messageId;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", username);
		body.addElement("5", contact.getName());
		if (!contact.getProtocol().isYahoo()) {
			body.addElement("241", contact.getProtocol().getStringValue());
		}
		body.addElement("97", "1");
		body.addElement("63", ";0");
		body.addElement("64", "0");
		body.addElement("206",
				"0"); /* 0 = no picture, 2 = picture, maybe 1 = avatar? */
		body.addElement("14", message);
		body.addElement("429", messageId);
		body.addElement("450", "0");
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.MESSAGE;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.OFFLINE;
	}

	/**
	 * Is Utf-8 text
	 */
	public final boolean isUtf8(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) > 0x7f)
				return true;
		}
		return false;
	}
}
