package org.openymsg.contact;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.contact.roster.SessionRosterImpl;
import org.openymsg.contact.status.ContactStatusChangeCallback;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

public class ContactAddAckResponseTest {
	@Mock
	ContactStatusChangeCallback statusCallback;
	@Mock
	SessionRosterImpl roster;
	@Mock
	SessionGroupImpl group;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() {
		String test = "Magic:YMSG Version:16 Length:61 Service:ADD_BUDDY Status:SERVER_ACK SessionId:0x59e41a  [1] [testuser] [7] [testbuddy] [65] [groupName] [66] [0] [223] [1] [241] [0]";
		YMSG9Packet packet = PacketReader.readString(test);
		ContactAddAckResponse response = new ContactAddAckResponse(roster, group, statusCallback);
		response.execute(packet);
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		verify(roster).receivedContactAddAck(contact);
		ContactGroupImpl contactGroup = new ContactGroupImpl("groupName");
		contactGroup.add(contact);
		verify(group).possibleAddGroup(contactGroup);
		verify(statusCallback).publishPending(contact);
	}
}
