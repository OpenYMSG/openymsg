package org.openymsg.unknown;

import org.openymsg.execute.PacketReader;
import org.openymsg.network.ServiceType;

/**
 * Handle Unknown message types.  All are no ops.
 * @author neilhart
 *
 */
public class SessionUnknown {
	private PacketReader reader;

	public SessionUnknown(PacketReader reader) {
		this.reader = reader;
		this.reader.register(ServiceType.UNKNOWN002, new Unknown002Response());
	}

}
