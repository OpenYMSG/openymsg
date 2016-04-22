package org.openymsg.conference;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.conference.response.ConferenceMessageResponse;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

public class ConferenceMessageResponseTest {
	String username = "testuser";
	@Mock
	private SessionConferenceCallback callback;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() throws IOException {
		String test = "Magic:YMSG Version:16 Length:111 Service:CONFMSG Status:SERVER_ACK SessionId:0x56cf6d  [1] [testuser] [3] [testbuddy] [14] [myMessage] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [97] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceMessageResponse response = new ConferenceMessageResponse(callback);
		response.execute(packet);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact sender = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "myMessage";
		// Verify
		Mockito.verify(callback).receivedConferenceMessage(conference, sender, message);
	}
}
