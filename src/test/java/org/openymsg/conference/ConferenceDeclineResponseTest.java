package org.openymsg.conference;

import java.io.IOException;

import org.mockito.Mockito;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConferenceDeclineResponseTest {
	String username = "testuser";
	private SessionConferenceImpl session;

	@BeforeMethod
	public void beforeMethod() {
		session = Mockito.mock(SessionConferenceImpl.class);
	}

	@Test
	public void testYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:100 Service:CONFDECLINE Status:SERVER_ACK SessionId:0x58fe2f  [1] [testuser] [14] [Nothankyou.] [54] [testbuddy] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [97] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceDeclineResponse response = new ConferenceDeclineResponse(session);
		response.execute(packet);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact decliner = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "Nothankyou.";

		// Verify
		Mockito.verify(session).receivedConferenceDecline(conference, decliner, message);
	}

}
