package org.openymsg.contact;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.group.ContactGroupImpl;
import org.openymsg.group.MoveContactToGroupMessage;
import org.testng.annotations.Test;

public class MoveContactToGroupMessageTest {
	
	@Test
	public void testSimple() throws IOException {
		String test = "Magic:YMSG Version:16 Length:100 Service:Y7_CHANGE_GROUP Status:DEFAULT SessionId:0x41cee0  [1] [testuser] [302] [240] [300] [240] [7] [testbuddy] [224] [fromgroup] [264] [togroup] [301] [240] [303] [240]";
		String username = "testuser";
		Contact contact = new Contact("testbuddy");
		ContactGroup from = new ContactGroupImpl("fromgroup");
		ContactGroup to = new ContactGroupImpl("togroup");
		MoveContactToGroupMessage message = new MoveContactToGroupMessage(username, contact, from, to);
		System.err.println(message.getBody());
	}
}
