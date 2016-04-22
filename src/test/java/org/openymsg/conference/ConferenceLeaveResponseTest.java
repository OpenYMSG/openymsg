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
import org.openymsg.conference.response.ConferenceLeaveResponse;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

public class ConferenceLeaveResponseTest {
	String username = "testuser";
	@Mock
	private SessionConferenceCallback callback;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testLogoffMultipleMembersYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:118 Service:CONFLOGOFF Status:SERVER_ACK SessionId:0x56cf6d  [1] [testuser] [302] [3] [3] [testuser] [3] [testbuddy] [303] [3] [56] [testbuddy2] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceLeaveResponse response = new ConferenceLeaveResponse(callback);
		response.execute(packet);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact leaver = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		// Verify
		Mockito.verify(callback).receivedConferenceLeft(conference, leaver);
	}

	@Test
	public void test() throws IOException {
		String test = "Magic:YMSG Version:16 Length:161 Service:CONFLOGOFF Status:SERVER_ACK SessionId:0x58fe2f  [1] [testuser] [302] [3] [3] [testuser] [3] [testbuddy] [3] [testbuddy2] [3] [testbuddy3] [3] [testbuddy4] [303] [3] [56] [testbuddy5] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceLeaveResponse response = new ConferenceLeaveResponse(callback);
		response.execute(packet);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact leaver = new YahooContact("testbuddy5", YahooProtocol.YAHOO);
		// Verify
		Mockito.verify(callback).receivedConferenceLeft(conference, leaver);
	}
}
