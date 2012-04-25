package org.openymsg.execute.write;

import java.io.IOException;

import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

/**
 * @author neilhart
 */
public interface Message {
	PacketBodyBuffer getBody() throws IOException;

	ServiceType getServiceType();

	MessageStatus getMessageStatus();

}
