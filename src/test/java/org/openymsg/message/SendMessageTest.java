package org.openymsg.message;

import java.io.IOException;

import org.openymsg.Contact;
import org.openymsg.MessageAssert;
import org.openymsg.YahooProtocol;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SendMessageTest {
	private String username = "testuser";
	private Contact contact = new Contact("testbuddy", YahooProtocol.YAHOO);
	private Contact contactMsn = new Contact("testbuddy@live.com", YahooProtocol.MSN);

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
		String test = "Magic:YMSG Version:16 Length:120 Service:MESSAGE Status:OFFLINE SessionId:0x45130f  [1] [testuser] [5] [testbuddy@live.com] [241] [2] [97] [1] [63] [;0] [64] [0] [206] [0] [14] [dfgfdgdfgdfgfdg] [429] [0000000308B2279D] [450] [0]";
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
