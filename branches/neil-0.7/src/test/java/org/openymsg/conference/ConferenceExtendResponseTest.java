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

public class ConferenceExtendResponseTest {
	String username = "testuser";
	private SessionConferenceImpl session;

	@BeforeMethod
	public void beforeMethod() {
		session = Mockito.mock(SessionConferenceImpl.class);
	}

	/**
	 * testuser receives a notice that testbuddy has invited testbuddy2 to a conference that testuser is already in
	 * @throws IOException
	 */
	// TODO this was an announcement
	@Test
	public void testYahooSingleExistingSingleInvite() throws IOException {
		String test = "Magic:YMSG Version:16 Length:88 Service:CONFADDINVITE Status:SOMETHING11 SessionId:0x58fe2f  [1] [testuser] [50] [testbuddy] [51] [testbuddy2] [57] [testbuddy-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceExtendResponse response = new ConferenceExtendResponse(session);
		response.execute(packet);
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact invited = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited);

		// Verify
		Mockito.verify(session).receivedConferenceExtend(conference, inviter, invitedContacts);
	}

	/**
	 * testuser receives a notice that testbuddy has invited testbuddy3, testbuddy4, testbuddy5 to a conference that
	 * testuser is already in
	 * @throws IOException
	 */
	@Test
	// TODO this was an announcement
	public void testYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:106 Service:CONFADDINVITE Status:SOMETHING11 SessionId:0x56cf6d  [1] [testuser] [50] [testbuddy] [51] [testbuddy3,testbuddy4,testbuddy5] [57] [testbuddy-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceExtendResponse response = new ConferenceExtendResponse(session);
		response.execute(packet);
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact invited1 = new YahooContact("testbuddy3", YahooProtocol.YAHOO);
		YahooContact invited2 = new YahooContact("testbuddy4", YahooProtocol.YAHOO);
		YahooContact invited3 = new YahooContact("testbuddy5", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited1);
		invitedContacts.add(invited2);
		invitedContacts.add(invited3);

		// Verify
		Mockito.verify(session).receivedConferenceExtend(conference, inviter, invitedContacts);
	}
}
