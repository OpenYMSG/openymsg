package org.openymsg.contact.roster;

import static org.testng.Assert.fail;

import java.io.IOException;

import org.openymsg.Name;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.testing.MessageAssert;
import org.testng.annotations.Test;

public class ContactAddMessageTest {
	String AddMultiBuddyToAnotherGroupOut = "Magic:YMSG Version:16 Length:124 Service:ADD_BUDDY Status:DEFAULT SessionId:0x59e41a  [14] [] [65] [ABC] [97] [1] [1] [testuser] [302] [319] [300] [319] [7] [testbuddy1] [301] [319] [300] [319] [7] [testbuddy2] [301] [319] [303] [319]";

	/**
	 * addBuddyAndGroupOut
	 * @throws IOException
	 */
	@Test
	public void testFull() throws IOException {
		String test = "Magic:YMSG Version:16 Length:134 Service:ADD_BUDDY Status:DEFAULT SessionId:0x59e41a  [14] [Hereisamessage] [65] [groupName] [97] [1] [216] [First] [254] [Last] [1] [testuser] [302] [319] [300] [319] [7] [testbuddy] [301] [319] [303] [319]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContactGroup group = new ContactGroupImpl("groupName");
		String addMessage = "Hereisamessage";
		Name name = new Name("First", "Last");
		ContactAddMessage message = new ContactAddMessage(username, contact, group, addMessage, name);
		MessageAssert.assertEquals(message, test);
	}

	@Test
	public void testMSN() throws IOException {
		String test = "Magic:YMSG Version:16 Length:134 Service:ADD_BUDDY Status:DEFAULT SessionId:0x59e41a  [14] [Hereisamessage] [65] [groupName] [97] [1] [216] [First] [254] [Last] [1] [testuser] [302] [319] [300] [319] [7] [testbuddy] [241] [1] [301] [319] [303] [319]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.MSN);
		YahooContactGroup group = new ContactGroupImpl("groupName");
		String addMessage = "Hereisamessage";
		Name name = new Name("First", "Last");
		ContactAddMessage message = new ContactAddMessage(username, contact, group, addMessage, name);
		MessageAssert.assertEquals(message, test);
	}

	@Test
	public void testNoName() {
		fail("not implemented");
	}

	@Test
	// TODO fails because message is empty.
	public void testNoMessage() throws IOException {
		String test = "Magic:YMSG Version:16 Length:115 Service:ADD_BUDDY Status:DEFAULT SessionId:0x5fcd19  [14] [] [65] [groupName] [97] [1] [216] [First] [254] [Last] [1] [testuser] [302] [319] [300] [319] [7] [testbuddy] [301] [319] [303] [319]";
		String username = "testuser";
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContactGroup group = new ContactGroupImpl("groupName");
		Name name = new Name("First", "Last");
		ContactAddMessage message = new ContactAddMessage(username, contact, group, null, name);
		MessageAssert.assertEquals(message, test);
	}

}
