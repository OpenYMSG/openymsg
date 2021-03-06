package org.openymsg.connection.write;

import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

import java.io.IOException;

/**
 * Message used by the connection writer to build a Yahoo packet.
 * @author neilhart
 */
public interface Message {
	/**
	 * build the message body
	 */
	PacketBodyBuffer getBody() throws IOException;

	/**
	 * service type
	 * @return service type
	 */
	ServiceType getServiceType();

	/**
	 * message status
	 * @return message status
	 */
	MessageStatus getMessageStatus();
}
