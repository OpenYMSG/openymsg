package org.openymsg.conference;

import org.openymsg.conference.response.ConferenceAcceptResponse;
import org.openymsg.conference.response.ConferenceDeclineResponse;
import org.openymsg.conference.response.ConferenceExtendResponse;
import org.openymsg.conference.response.ConferenceInviteResponse;
import org.openymsg.conference.response.ConferenceLeaveResponse;
import org.openymsg.conference.response.ConferenceMessageResponse;
import org.openymsg.connection.YahooConnection;
import org.openymsg.network.ServiceType;

public class ConferenceServiceBuilder {
	private String username;
	private YahooConnection connection;
	private final ConferenceServiceState state = new ConferenceServiceState();
	private SessionConferenceCallback callback;

	public ConferenceUserService build() {
		ConferenceSocketService socketService = createSocketService();
		this.initializeRegistry(socketService);
		return new ConferenceUserService(username, connection, state);
	}

	private ConferenceSocketService createSocketService() {
		return new ConferenceSocketService(callback, state);
	}

	protected void initializeRegistry(ConferenceSocketService socketService) {
		connection.register(ServiceType.CONFMSG, new ConferenceMessageResponse(socketService));
		connection.register(ServiceType.CONFINVITE, new ConferenceInviteResponse(socketService));
		connection.register(ServiceType.CONFADDINVITE, new ConferenceExtendResponse(socketService));
		connection.register(ServiceType.CONFDECLINE, new ConferenceDeclineResponse(socketService));
		connection.register(ServiceType.CONFLOGON, new ConferenceAcceptResponse(socketService));
		connection.register(ServiceType.CONFLOGOFF, new ConferenceLeaveResponse(socketService));
	}

	public ConferenceServiceBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	public ConferenceServiceBuilder setConnection(YahooConnection connection) {
		this.connection = connection;
		return this;
	}

	public ConferenceServiceBuilder setCallback(SessionConferenceCallback callback) {
		this.callback = callback;
		return this;
	}

}
