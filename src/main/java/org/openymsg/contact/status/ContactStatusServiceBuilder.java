package org.openymsg.contact.status;

import org.openymsg.connection.YahooConnection;
import org.openymsg.contact.status.response.ListOfStatusesResponse;
import org.openymsg.contact.status.response.SingleStatusResponse;
import org.openymsg.network.ServiceType;

public class ContactStatusServiceBuilder {
	private YahooConnection connection;
	private SessionStatusCallback callback;
	private final ContactStatusState state = new ContactStatusState();
	private ContactStatusChangeCallback statusCallback;

	public ContactStatusUserService build() {
		ContactStatusSocketService socketService = createSocketService();
		this.initializeRegistry(socketService);
		return new ContactStatusUserService(connection, state);
	}

	private ContactStatusSocketService createSocketService() {
		statusCallback = new ContactStatusSocketService(getCallback(), state);
		return (ContactStatusSocketService) statusCallback;
	}

	protected void initializeRegistry(ContactStatusSocketService socketService) {
		SingleStatusResponse singleStatusResponse = new SingleStatusResponse(socketService);
		connection.register(ServiceType.STATUS_15, new ListOfStatusesResponse(singleStatusResponse));
		connection.register(ServiceType.Y6_STATUS_UPDATE, singleStatusResponse);
	}

	public ContactStatusServiceBuilder setConnection(YahooConnection connection) {
		this.connection = connection;
		return this;
	}

	public ContactStatusServiceBuilder setCallback(SessionStatusCallback callback) {
		this.callback = callback;
		return this;
	}

	public ContactStatusChangeCallback getStatusCallback() {
		return statusCallback;
	}

	protected SessionStatusCallback getCallback() {
		return callback;
	}
}
