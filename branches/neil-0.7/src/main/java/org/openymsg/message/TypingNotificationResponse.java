package org.openymsg.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

/**
 * Receiver for typing notifications. Game notifications are not handled. A notification will be passed to the
 * SessionMessageImpl.
 * @author neilhart
 */
public class TypingNotificationResponse implements SinglePacketResponse {
	/** logger */
	private static final Log log = LogFactory.getLog(TypingNotificationResponse.class);
	/** Typing notification type */
	public static final String NOTIFY_TYPING = "TYPING";
	/** Gaming notification type */
	public static final String NOTIFY_GAME = "GAME";
	/** session to pass on notification too */
	private SessionMessageImpl session;

	/**
	 * Create an instance with the session
	 * @param session session
	 */
	public TypingNotificationResponse(SessionMessageImpl session) {
		this.session = session;
	}

	/**
	 * handle the incoming packet.
	 * @param packet incoming packet
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		// FIX: documentation says this should be Status.TYPING (0x16)
		// if (packet.status == 0x01) {
		@SuppressWarnings("unused")
		String to = packet.getValue("4");
		String from = packet.getValue("5");
		// TODO protocol
		String message = packet.getValue("14"); // message (game)
		String type = packet.getValue("49"); // type (typing/game)
		String mode = packet.getValue("13"); // mode (on/off)

		boolean isTyping = false;
		if ("0".equals(mode)) {
			isTyping = false;
		} else if ("1".equals(mode)) {
			isTyping = true;
		} else {
			log.warn("mode not found: " + mode);
		}

		YahooContact contact = new YahooContact(from);
		if (NOTIFY_TYPING.equalsIgnoreCase(type)) {
			this.session.receivedTypingNotification(contact, isTyping);
		} else if (NOTIFY_GAME.equalsIgnoreCase(type)) {
			log.debug("Not handling game notify with: " + message);
		} else {
			log.warn("Not handling notify with: " + type);
		}
	}

}
