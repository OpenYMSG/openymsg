package org.openymsg.unknown;

import org.openymsg.connection.YahooConnection;
import org.openymsg.network.ServiceType;

/**
 * Handle Unknown message types. All are no ops.
 * @author neilhart
 */
public class SessionUnknown {
	public SessionUnknown(YahooConnection connection) {
		connection.getReaderRegistry().register(ServiceType.UNKNOWN002, new Unknown002Response());
	}
}
