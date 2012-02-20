package org.openymsg.auth;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.execute.read.SinglePacketResponseAbstract;

public class ListResponse extends SinglePacketResponseAbstract {
	private static final Log log = LogFactory.getLog(ListResponse.class);

	@Override
	public void execute() {
		String listOfGroups = this.packet.getValue("87");
		if (listOfGroups != null) {
			log.warn("Getting list of groups when we shouldn't");
		}
		String lisOfIgnoredUsers = this.packet.getValue("88");
		if (lisOfIgnoredUsers != null) {
			log.warn("Getting list of ignored users when we shouldn't");
		}
		// Identities list (alternative yahoo ids we can use!)
		// TODO - handle Identities
		String listOfIdentities = this.packet.getValue("89");
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
