package org.openymsg.conference;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.mockito.Mockito;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConferenceAcceptResponseTest {
	String username = "testuser";
	private SessionConferenceImpl session;

	@BeforeMethod
	public void beforeMethod() {
		session = Mockito.mock(SessionConferenceImpl.class);
	}

	@Test
	public void testYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:70 Service:CONFLOGON Status:SERVER_ACK SessionId:0x56cf6d  [1] [testuser] [53] [testbuddy] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceAcceptResponse response = new ConferenceAcceptResponse(session);
		response.execute(packet);

		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact accepter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Set<YahooContact> members = new HashSet<YahooContact>();
		members.add(accepter);

		// Verify
		Mockito.verify(session).receivedConferenceAccept(conference, accepter);
	}

}
