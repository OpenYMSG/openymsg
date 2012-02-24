package org.openymsg.message;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a MESSAGE packet.
 */
public class SendMessage implements Message {
	private String username;
	private Contact contact;
	private String message;
	private String messageId;

	public SendMessage(String username, Contact contact, String message, String messageId) {
		this.username = username;
		this.contact = contact;
		this.message = message;
		this.messageId = messageId;
	}

	/**
	 * Doesn't support Doodling on whiteboard, whatever that is.
	 * @return environment
	 */
	private String getImvironment() {
		String imvironment = ";0"; // TODO - Get environment from contact
		return imvironment;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", username);
		body.addElement("5", contact.getId());
		if (!contact.getProtocol().isYahoo()) body.addElement("241", contact.getProtocol().getStringValue()); // TODO - placement
		if (this.isUtf8(message)) body.addElement("97", "1");
		body.addElement("63", getImvironment());
		body.addElement("64", "0"); // Extension for YMSG9
		body.addElement("206", "0"); /* 0 = no picture, 2 = picture, maybe 1 = avatar? */ //TODO never send to federated
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
			if (s.charAt(i) > 0x7f) return true;
		}
		return false;
	}

}
