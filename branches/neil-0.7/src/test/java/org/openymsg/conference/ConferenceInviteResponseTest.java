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

public class ConferenceInviteResponseTest {
	String username = "testuser";
	private SessionConferenceImpl session;

	@BeforeMethod
	public void beforeMethod() {
		session = Mockito.mock(SessionConferenceImpl.class);
	}

	@Test
	public void testSingleInviteYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:208 Service:CONFINVITE Status:SERVER_ACK SessionId:0x58fe2f  [1] [testuser] [13] [0] [50] [testbuddy] [52] [testuser] [57] [testbuddy-8iVmHcCkflGJpBXpjBbzCw--] [58] [Invitingtestuser] [97] [1] [233] [grFkPBIo9ofg.TLbOmEWP2ceP3iZMlbEY-] [234] [testbuddy-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceInviteResponse response = new ConferenceInviteResponse(session);
		response.execute(packet);
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "Invitingtestuser";
		Set<YahooContact> invited = new HashSet<YahooContact>();
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		invited.add(me);
		Set<YahooContact> members = new HashSet<YahooContact>();
		members.add(inviter);

		// Verify
		Mockito.verify(session).receivedConferenceInvite(conference, inviter, invited, members, message);
	}

	@Test
	public void testSingleInviteAckYahoo() throws IOException {
		String test = "Magic:YMSG Version:16 Length:208 Service:CONFINVITE Status:SERVER_ACK SessionId:0x58fe2f  [1] [testuser] [13] [0] [50] [testuser] [52] [testbuddy] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [58] [Invitingtestbuddy] [97] [1] [233] [grFkPBIo9ofg.TLbOmEWP2ceP3iZMlbEY-] [234] [testuser-8iVmHcCkflGJpBXpjBbzCw--]";
		YMSG9Packet packet = PacketReader.readString(test);
		ConferenceInviteResponse response = new ConferenceInviteResponse(session);
		response.execute(packet);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		Set<YahooContact> invited = new HashSet<YahooContact>();
		YahooContact buddy = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		invited.add(buddy);
		Set<YahooContact> members = new HashSet<YahooContact>();
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		members.add(me);
		String message = "Invitingtestbuddy";

		// Verify
		Mockito.verify(session).receivedConferenceInviteAck(conference, me, invited, members, message);
	}

}
