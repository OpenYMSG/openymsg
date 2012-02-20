package org.openymsg.auth;

import java.io.IOException;

import org.openymsg.execute.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.NetworkConstants;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * Transmit an AUTHRESP packet, the second part of our login process. As we do not know our primary ID yet, both 0 and 1
 * use loginID . Note: message also contains our initial status. cookieY - plain response (not MD5Crypt'd) cookieT - crypted
 * response (MD5Crypt'd)
 */
public class LoginCompleteMessage implements Message {
	private String username;
	private String cookieY;
	private String cookieT;
	private String base64;
	private String cookieB;

	public LoginCompleteMessage(String username, String cookieY, String cookieT, String base64, String cookieB) {
		this.username = username;
		this.cookieY = cookieY;
		this.cookieT = cookieT;
		this.base64 = base64;
		this.cookieB = cookieB;
	}

	@Override
	public PacketBodyBuffer getBody() throws IOException {
		// if (sessionStatus != SessionState.CONNECTED) {
		// throw new IllegalStateException(
		// "Cannot transmit an AUTHRESP packet if you're not completely connected to the Yahoo Network. Current state: "
		// + sessionStatus);
		// }
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", username);
		body.addElement("0", username);
		body.addElement("277", cookieY);
		body.addElement("278", cookieT);
		body.addElement("307", base64);
		body.addElement("244", NetworkConstants.CLIENT_VERSION_ID);
		body.addElement("2", username);
		body.addElement("2", "1");
		//TODO missing
		if (cookieB != null) {
			body.addElement("59", cookieB);
		}
		body.addElement("98", NetworkConstants.ROOM_LIST_LOCALE_US);
		body.addElement("135", NetworkConstants.CLIENT_VERSION);
		return body;
	}

	@Override
	public ServiceType getServiceType() {
		return ServiceType.AUTHRESP;
	}

	@Override
	public MessageStatus getMessageStatus() {
		return MessageStatus.WEBLOGIN;
	}

	@Override
	public void messageProcessed() {
		// TODO Auto-generated method stub
		
	}

}
