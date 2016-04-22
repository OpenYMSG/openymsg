package org.openymsg.contact;

import org.openymsg.connection.YahooConnection;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.ContactStatusChangeCallback;
import org.openymsg.contact.status.ContactStatusServiceBuilder;
import org.openymsg.contact.status.ContactStatusUserService;
import org.openymsg.network.ServiceType;

public class ContactServiceBuilder {
	protected YahooConnection connection;
	protected SessionContactCallback callback;
	private String username;
	private ContactStatusChangeCallback contactStatusChangeCallback;
	private SessionRosterImpl sessionRoster;
	private SessionGroupImpl sessionGroup;
	private ContactStatusUserService sessionStatus;

	public ContactUserService build() {
		sessionRoster = new SessionRosterImpl(connection, username, callback);
		sessionGroup = new SessionGroupImpl(connection, username);
		buildStatus();
		return new ContactUserService(sessionRoster, sessionGroup, sessionStatus);
	}

	protected void initialize() {
		connection.register(ServiceType.LIST_15,
				new ListOfContactsResponse(sessionRoster, sessionGroup, contactStatusChangeCallback));
		connection.register(ServiceType.REMOVE_BUDDY, new ContactRemoveAckResponse(sessionRoster, sessionGroup));
		connection.register(ServiceType.ADD_BUDDY,
				new ContactAddAckResponse(sessionRoster, sessionGroup, contactStatusChangeCallback));
	}

	protected void buildStatus() {
		ContactStatusServiceBuilder builder = getStatusBuilder();
		sessionStatus = builder.build();
		contactStatusChangeCallback = builder.getStatusCallback();
	}

	protected ContactStatusServiceBuilder getStatusBuilder() {
		return new ContactStatusServiceBuilder().setConnection(connection).setCallback(callback);
	}

	public ContactServiceBuilder setConnection(YahooConnection connection) {
		this.connection = connection;
		return this;
	}

	public ContactServiceBuilder setCallback(SessionContactCallback callback) {
		this.callback = callback;
		return this;
	}

	public ContactServiceBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	public ContactStatusChangeCallback getContactStatusChangeCallback() {
		return contactStatusChangeCallback;
	}

}
