package org.openymsg.session;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class ListResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(ListResponse.class);

	@Override
	public void execute(YMSG9Packet packet) {
		checkLegacy(packet);
		String firstName = packet.getValue("216");
		String lastName = packet.getValue("254");
		log.debug("got name: " + firstName + "/" + lastName);
	}

	private void checkLegacy(YMSG9Packet packet) {
		String listOfGroups = packet.getValue("87");
		if (listOfGroups != null) {
			log.warn("Getting list of groups when we shouldn't");
		}
		String lisOfIgnoredUsers = packet.getValue("88");
		if (lisOfIgnoredUsers != null) {
			log.warn("Getting list of ignored users when we shouldn't");
		}
		// Identities list (alternative yahoo ids we can use!)
		// TODO - handle Identities
		String listOfIdentities = packet.getValue("89");
		if (listOfIdentities != null) {
			StringTokenizer st = new StringTokenizer(listOfIdentities, ",");
			// identities.clear();
			boolean foundOne = false;
			while (st.hasMoreTokens()) {
				final String id = st.nextToken().toLowerCase();
				if (foundOne) {
					log.info("Getting more than one identity: " + id);
				}
				foundOne = true;
//				identities.put(id, new YahooIdentity(id));
			}
		}
	}

}
