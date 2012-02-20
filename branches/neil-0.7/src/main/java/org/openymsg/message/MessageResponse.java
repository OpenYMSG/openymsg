package org.openymsg.message;

import java.util.LinkedList;

import org.openymsg.Status;
import org.openymsg.execute.read.SinglePacketResponseAbstract;

/**
 * Process an incoming MESSAGE packet. Message can be either online or offline, the latter having a datestamp of when
 * they were sent.
 */
public class MessageResponse extends SinglePacketResponseAbstract {
	private SessionMessageImpl sesionMessage;
	private String username;
	private LinkedList<String> previousIds = new LinkedList<String>();

	@Override
	protected void execute() {
		try {
			if (!this.packet.exists("14")) {
				// Contains no message?
				return;
			}

			if (this.packet.status == Status.NOTINOFFICE.getValue()) {
				// Sent while we were offline
				int i = 0;
				// Read each message, until null
				while (this.packet.getNthValue("31", i) != null) {
					extractOfflineMessage(i);
					i++;
				}
			}
			else {
				this.extractMessage();
			}
		}
		catch (Exception e) {
			// throw new YMSG9BadFormatException("message", this.packet, e);
		}

	}

	private void extractMessage() {
		// Sent while we are online
		final String to = this.packet.getValue("5");
		final String from = this.packet.getValue("4");
		final String message = this.packet.getValue("14");
		final String id = this.packet.getValue("429");

		if (id == null || !previousIds.contains(from + id)) {
			// TODO - handle message
			// final SessionEvent se = new SessionEvent(this, to, from, message);
			// if (se.getMessage().equalsIgnoreCase(NetworkConstants.BUZZ)) {
			// // eventDispatchQueue.append(se, ServiceType.X_BUZZ);
			// }
			// else {
			// // eventDispatchQueue.append(se, ServiceType.MESSAGE);
			// }
		}

		if (id != null) {
			previousIds.addLast(from + id);
			if (previousIds.size() > 10) previousIds.removeFirst();

		}

	}

	private void extractOfflineMessage(int i) {

		final String to = this.packet.getNthValue("5", i);
		final String from = this.packet.getNthValue("4", i);
		final String message = this.packet.getNthValue("14", i);
		final String timestamp = this.packet.getNthValue("15", i);

		if (timestamp == null || timestamp.length() == 0) {
			// se = new SessionEvent(this, to, from, message);
		}
		else {
			final long timestampInMillis = 1000 * Long.parseLong(timestamp);
			// se = new SessionEvent(this, to, from, message, timestampInMillis);
		}
		// se.setStatus(this.packet.status); // status!=0 means offline
		// message

		// eventDispatchQueue.append(se, ServiceType.X_OFFLINE);
	}

}
