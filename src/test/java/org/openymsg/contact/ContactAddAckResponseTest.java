package org.openymsg.contact;

import org.mockito.Mockito;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.ContactAddAckResponse;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.SessionStatusImpl;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.annotations.Test;

public class ContactAddAckResponseTest {

	@Test
	public void test() {
		String test = "Magic:YMSG Version:16 Length:61 Service:ADD_BUDDY Status:SERVER_ACK SessionId:0x59e41a  [1] [testuser] [7] [testbuddy] [65] [groupName] [66] [0] [223] [1] [241] [0]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionRosterImpl roster = Mockito.mock(SessionRosterImpl.class);
		SessionGroupImpl group = Mockito.mock(SessionGroupImpl.class);
		SessionStatusImpl status = Mockito.mock(SessionStatusImpl.class);
		ContactAddAckResponse response = new ContactAddAckResponse(roster, group, status);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Mockito.verify(roster).possibleAddContact(contact);
		ContactGroupImpl contactGroup = new ContactGroupImpl("groupName");
		contactGroup.add(contact);
		Mockito.verify(group).possibleAddGroup(contactGroup);
		Mockito.verify(status).publishPending(contact);
	}

}
