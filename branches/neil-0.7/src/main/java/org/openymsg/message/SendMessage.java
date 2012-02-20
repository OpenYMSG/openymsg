package org.openymsg.message;

import java.io.IOException;

import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit a MESSAGE packet.
 * 
 * @param to
 *            he Yahoo ID of the user to send the message to
 * @param yid
 *            Yahoo identity used to send the message from
 * @param msg
 *            the text of the message
 */
public class SendMessage implements Message {
	private String username;
	private String contactId;
	private String contactType;
	private String message;
	private String messageId;
	private String imvironment = "0";

	public SendMessage(String username, String contactId, String contactType, String message, String messageId) {
		this.username = username;
		this.contactId = contactId;
		this.contactType = contactType;
		this.message = message;
		this.messageId = messageId;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
//        if (to == null || to.length() == 0) {
//            throw new IllegalArgumentException("Argument 'to' cannot be null or an empty String.");
//        }
//
//        if (yid == null) {
//            throw new IllegalArgumentException("Argument 'yid' cannot be null.");
//        }
//
//        String type = this.getType(to);
//
//        String messageNumberString = buildMessageNumber();
        // Send packet
        PacketBodyBuffer body = new PacketBodyBuffer();
        body.addElement("1", username); // From (effective ID)
        body.addElement("5", contactId); // To
        body.addElement("241", contactType);
        body.addElement("14", message); // Message
        // Extension for YMSG9
        if (this.isUtf8(message)) body.addElement("97", "1");
        body.addElement("63", ";" + imvironment ); // Not supported here!
        body.addElement("64", "0");
        body.addElement("206", "0");
        body.addElement("429", messageId);
        body.addElement("450", "0");
        return body;
//        sendPacket(body, ServiceType.MESSAGE, ); // 0x06
//        return messageNumberString;
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

	@Override
	public void messageProcessed() {
	}


}
