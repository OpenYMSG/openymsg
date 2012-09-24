package org.openymsg.contact.roster;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.openymsg.Name;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ContactAddAuthorizationResponseTest {
	private SessionRosterImpl session;

	@BeforeMethod
	public void beforeMethod() {
		session = mock(SessionRosterImpl.class);
	}

	@Test
	public void testAcceptYahoo() {
		String test = "Magic:YMSG Version:16 Length:38 Service:Y7_BUDDY_AUTHORIZATION Status:SERVER_ACK SessionId:0x59e41a  [4] [testbuddy] [5] [testuser] [13] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		ContactAddAuthorizationResponse response = new ContactAddAuthorizationResponse(session);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		verify(session).receivedContactAddAccepted(contact);
	}

	@Test
	public void testInviteYahoo() {
		String test = "Magic:YMSG Version:16 Length:53 Service:Y7_BUDDY_AUTHORIZATION Status:SOMETHING3 SessionId:0x428f66  [4] [testbuddy] [5] [testuser] [216] [First] [254] [Last]";
		YMSG9Packet packet = PacketReader.readString(test);
		ContactAddAuthorizationResponse response = new ContactAddAuthorizationResponse(session);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Name name = new Name("First", "Last");
		String id = "testuser";
		verify(session).receivedContactAddRequest(id, contact, name, null);
	}

	@Test
	public void testDeclineYahoo() {
		String test = "Magic:YMSG Version:16 Length:54 Service:Y7_BUDDY_AUTHORIZATION Status:SERVER_ACK SessionId:0x5fcd19  [4] [testbuddy] [5] [testuser] [13] [2] [14] [declinedYou]";
		YMSG9Packet packet = PacketReader.readString(test);
		ContactAddAuthorizationResponse response = new ContactAddAuthorizationResponse(session);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "declinedYou";
		verify(session).receivedContactAddDeclined(contact, message);
	}

	@Test
	public void testInviteMsn() {
		Assert.fail("not implemented");
	}

	@Test
	public void testAcceptMsn() {
		String test = "Magic:YMSG Version:16 Length:38 Service:Y7_BUDDY_AUTHORIZATION Status:SERVER_ACK SessionId:0x59e41a  [4] [testbuddy] [5] [testuser] [13] [1] [241] [2]";
		YMSG9Packet packet = PacketReader.readString(test);
		ContactAddAuthorizationResponse response = new ContactAddAuthorizationResponse(session);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.MSN);
		verify(session).receivedContactAddAccepted(contact);
	}

	@Test
	public void testDeclineMsn() {
		String test = "Magic:YMSG Version:16 Length:54 Service:Y7_BUDDY_AUTHORIZATION Status:SERVER_ACK SessionId:0x5fcd19  [4] [testbuddy] [5] [testuser] [13] [2] [241] [2] [14] [declinedYou]";
		YMSG9Packet packet = PacketReader.readString(test);
		ContactAddAuthorizationResponse response = new ContactAddAuthorizationResponse(session);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.MSN);
		String message = "declinedYou";
		verify(session).receivedContactAddDeclined(contact, message);
	}

}
