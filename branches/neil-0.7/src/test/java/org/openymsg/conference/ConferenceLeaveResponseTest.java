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

public class ConferenceLeaveResponseTest {
	String username = "testuser";
	private SessionConferenceImpl session;

	@BeforeMethod
	public void beforeMethod() {
		session = Mockito.mock(SessionConferenceImpl.class);
	}

	@Test
	public void testLogoffMultipleMembersYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:118 Service:CONFLOGOFF Status:SERVER_ACK SessionId:0x56cf6d  [1] [testuser] [302] [3] [3] [testuser] [3] [testbuddy] [303] [3] [56] [testbuddy2] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceLeaveResponse response = new ConferenceLeaveResponse(session);
		response.execute(packet);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact leaver = new YahooContact("testbuddy2", YahooProtocol.YAHOO);

		// Verify
		Mockito.verify(session).receivedConferenceLeft(conference, leaver);
	}

	@Test
	public void test() throws IOException {
		String test = "Magic:YMSG Version:16 Length:161 Service:CONFLOGOFF Status:SERVER_ACK SessionId:0x58fe2f  [1] [testuser] [302] [3] [3] [testuser] [3] [testbuddy] [3] [testbuddy2] [3] [testbuddy3] [3] [testbuddy4] [303] [3] [56] [testbuddy5] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceLeaveResponse response = new ConferenceLeaveResponse(session);
		response.execute(packet);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact leaver = new YahooContact("testbuddy5", YahooProtocol.YAHOO);

		// Verify
		Mockito.verify(session).receivedConferenceLeft(conference, leaver);
	}
}
