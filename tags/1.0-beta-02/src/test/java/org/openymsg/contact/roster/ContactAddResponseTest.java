package org.openymsg.contact.roster;

import org.mockito.Mockito;
import org.openymsg.Name;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ContactAddResponseTest {

	@Test
	public void testAcceptYahoo() {
		String test = "Magic:YMSG Version:16 Length:38 Service:Y7_BUDDY_AUTHORIZATION Status:SERVER_ACK SessionId:0x59e41a  [4] [testbuddy] [5] [testuser] [13] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		// String username = "testuser";
		// SessionRosterImpl session = Mockito.mock(SessionRosterImpl.class);
		SessionRosterCallback callback = Mockito.mock(SessionRosterCallback.class);
		ContactAddResponse response = new ContactAddResponse(callback);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Mockito.verify(callback).receivedContactAddAccepted(contact);
	}

	@Test
	public void testInviteYahoo() {
		String test = "Magic:YMSG Version:16 Length:53 Service:Y7_BUDDY_AUTHORIZATION Status:SOMETHING3 SessionId:0x428f66  [4] [testbuddy] [5] [testuser] [216] [First] [254] [Last]";
		YMSG9Packet packet = PacketReader.readString(test);
		// String username = "testuser";
		// SessionRosterImpl session = Mockito.mock(SessionRosterImpl.class);
		SessionRosterCallback callback = Mockito.mock(SessionRosterCallback.class);
		ContactAddResponse response = new ContactAddResponse(callback);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Name name = new Name("First", "Last");
		Mockito.verify(callback).receivedContactAddRequest(contact, name, null);
	}

	@Test
	public void testDeclineYahoo() {
		String test = "Magic:YMSG Version:16 Length:54 Service:Y7_BUDDY_AUTHORIZATION Status:SERVER_ACK SessionId:0x5fcd19  [4] [testbuddy] [5] [testuser] [13] [2] [14] [declinedYou]";
		YMSG9Packet packet = PacketReader.readString(test);
		// String username = "testuser";
		// SessionRosterImpl session = Mockito.mock(SessionRosterImpl.class);
		SessionRosterCallback callback = Mockito.mock(SessionRosterCallback.class);
		ContactAddResponse response = new ContactAddResponse(callback);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "declinedYou";
		Mockito.verify(callback).receivedContactAddDeclined(contact, message);
	}

	@Test
	public void testInviteMsn() {
		Assert.fail("not implemented");
	}

	@Test
	public void testAcceptMsn() {
		Assert.fail("not implemented");
	}

	@Test
	public void testDeclineMsn() {
		Assert.fail("not implemented");
	}

}
