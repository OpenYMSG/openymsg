package org.openymsg.contact;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.annotations.Test;

public class ContactRemoveAckResponseTest {

	@Test
	public void test() {
		String test = "Magic:YMSG Version:16 Length:53 Service:REMOVE_BUDDY Status:SERVER_ACK SessionId:0x59e41a  [1] [testuser] [7] [testbuddy] [65] [groupName] [66] [0] [241] [0]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionRosterImpl roster = mock(SessionRosterImpl.class);
		SessionGroupImpl group = mock(SessionGroupImpl.class);
		ContactRemoveAckResponse response = new ContactRemoveAckResponse(roster, group);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		verify(roster).possibleRemoveContact(contact);
		ContactGroupImpl contactGroup = new ContactGroupImpl("groupName");
		contactGroup.add(contact);
		verify(group).possibleRemoveGroup(contactGroup);
	}

}
