package org.openymsg.contact.status;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactStatus;

/**
 * Handles several kinds of status messages. Status15 - single messages for
 * Yahoo 9 users logging in and out/invisible for MSN users Y6 Status Update for
 * Yahoo 9 users changing status
 * 
 * @author neilhart
 */
public class ContactStatusUserService implements SessionStatus {
	private final ContactStatusState state;

	public ContactStatusUserService(ContactStatusState state) {
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
