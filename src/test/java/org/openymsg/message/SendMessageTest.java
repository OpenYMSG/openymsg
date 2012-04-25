package org.openymsg.message;

import java.io.IOException;

import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.testing.MessageAssert;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SendMessageTest {
	private String username = "testuser";
	private YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
	private YahooContact contactMsn = new YahooContact("testbuddy@live.com", YahooProtocol.MSN);
	// TODO not a contact message
	private String offline = "Magic:YMSG Version:16 Length:133 Service:MESSAGE Status:WEBLOGIN SessionId:0x5e64fc  [1] [testuser] [5] [testbuddy@live.com] [241] [2] [97] [1] [63] [;0] [64] [0] [206] [0] [14] [an offline message] [429] [0000000070C35E1E] [450] [0]";

	@Test
	public void testSimple() throws IOException {
		String test = "Magic:YMSG Version:16 Length:108 Service:MESSAGE Status:OFFLINE SessionId:0x45130f  [1] [testuser] [5] [testbuddy] [97] [1] [63] [;0] [64] [0] [206] [0] [14] [dfgfdgdfgdfgfdg] [429] [0000000308B2279D] [450] [0]";
		String messageId = "0000000308B2279D";
		String message = "dfgfdgdfgdfgfdg";
		SendMessage outgoing = new SendMessage(username, contact, message, messageId);
		MessageAssert.assertEquals(outgoing, test);
	}

	@Test
	public void testProtocol() throws IOException {
		String test = "Magic:YMSG Version:16 Length:120 Service:MESSAGE Status:OFFLINE  SessionId:0x45130f  [1] [testuser] [5] [testbuddy@live.com] [241] [2] [97] [1] [63] [;0] [64] [0] [206] [0] [14] [dfgfdgdfgdfgfdg] [429] [0000000308B2279D] [450] [0]";
		String messageId = "0000000308B2279D";
		String message = "dfgfdgdfgdfgfdg";
		SendMessage outgoing = new SendMessage(username, contactMsn, message, messageId);
		MessageAssert.assertEquals(outgoing, test);
	}

	@Test(enabled = false)
	public void testOfflineProtocol() throws IOException {
		Assert.fail("Not completed");
	}

}
