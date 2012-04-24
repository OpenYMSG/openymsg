package org.openymsg.conference;

import java.io.IOException;

import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.testing.MessageAssert;
import org.testng.annotations.Test;

public class LeaveConferenceMessageTest {

	@Test
	public void test() throws IOException {
		String test = "Magic:YMSG Version:16 Length:129 Service:CONFLOGOFF Status:DEFAULT SessionId:0x56cf6d  [1] [testuser] [57] [testuser-8iVmHcCkflGJpBXpjBbzCw--] [3] [testbuddy] [3] [testbuddy2] [3] [testbuddy3] [3] [testbuddy4] [3] [testbuddy5]";
		String username = "testuser";
		YahooContact member1 = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact member2 = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		YahooContact invited1 = new YahooContact("testbuddy3", YahooProtocol.YAHOO);
		YahooContact invited2 = new YahooContact("testbuddy4", YahooProtocol.YAHOO);
		YahooContact invited3 = new YahooContact("testbuddy5", YahooProtocol.YAHOO);
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		ConferenceMembershipImpl membership = new ConferenceMembershipImpl();
		membership.addMember(member1);
		membership.addMember(member2);
		membership.addInvited(invited1);
		membership.addInvited(invited2);
		membership.addInvited(invited3);
		LeaveConferenceMessage outgoing = new LeaveConferenceMessage(username, conference, membership);
		MessageAssert.assertEquals(outgoing, test);
	}
}
