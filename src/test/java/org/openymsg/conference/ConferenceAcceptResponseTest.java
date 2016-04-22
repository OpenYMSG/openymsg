package org.openymsg.conference;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.conference.response.ConferenceAcceptResponse;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

public class ConferenceAcceptResponseTest {
	String username = "testuser";
	@Mock
	private SessionConferenceCallback callback;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:70 Service:CONFLOGON Status:SERVER_ACK SessionId:0x56cf6d  [1] [testuser] [53] [testbuddy] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceAcceptResponse response = new ConferenceAcceptResponse(callback);
		response.execute(packet);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact accepter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Set<YahooContact> members = new HashSet<YahooContact>();
		members.add(accepter);
		// Verify
		Mockito.verify(callback).receivedConferenceAccept(conference, accepter);
	}
}
