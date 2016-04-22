package org.openymsg.contact.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactStatus;
import org.openymsg.connection.YahooConnection;

/**
 * Handles several kinds of status messages. Status15 - single messages for
 * Yahoo 9 users logging in and out/invisible for MSN users Y6 Status Update for
 * Yahoo 9 users changing status
 * 
 * @author neilhart
 */
public class ContactStatusUserService implements SessionStatus {
	/** logger */
	private static final Log log = LogFactory.getLog(ContactStatusUserService.class);
	private YahooConnection executor;
	private final ContactStatusState state;

	public ContactStatusUserService(YahooConnection executor, ContactStatusState state) {
		this.executor = executor;
		// this(executor, new
		// SessionStatusCallbackHandlerBuilder().setCallback(callback));
		this.state = state;
	}

	// public ContactStatusUserService(YahooConnection executor,
	// SessionStatusCallbackHandlerBuilder builder) {
	// this.executor = executor;
	// this.callback = builder.setStatuses(statuses).build();
	// initialize();
	// }
	//
	@Override
	public YahooContactStatus getStatus(YahooContact contact) {
		return state.getStatus(contact);
	}

}
