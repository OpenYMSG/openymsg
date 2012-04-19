package org.openymsg.contact.status;

import org.mockito.Mockito;
import org.openymsg.YahooContact;
import org.openymsg.YahooStatus;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.annotations.Test;

public class SingleStatusResponseTest {

	@Test
	public void testAvailableProtocol() {
		String test = "Magic:YMSG Version:16 Length:96 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [0] [13] [1] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.AVAILABLE, false, true);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test(enabled = false)
	// TODO - not sure this is needed.
	public void testInvisibleToAvailableProtocol() {
		String test = "Magic:YMSG Version:16 Length:143 Service:Y6_STATUS_UPDATE Status:SERVER_ACK SessionId:0x45130f  [7] [testuser] [10] [0] [97] [1] [302] [316] [300] [316] [135] [2.0.4] [258] [4eb73995-f313-4f4a-49a5-1bc4d7c3ee68] [310] [en-us] [301] [316] [303] [316] [317] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.AVAILABLE, false, true);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testAvailableFromInvisibleYahoo() {
		String test = "Magic:YMSG Version:16 Length:307 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser] [10] [0] [13] [1] [97] [1] [192] [672912674] [198] [0] [213] [0] [244] [12582847] [283] [1] [317] [1] [300] [444] [440] [0] [301] [444] [550] [C2YM4G6G222Q3T5RNEHYB5K54Y] [301] [315] [303] [315] [302] [316] [300] [316] [135] [2.0.4] [258] [4eb73995-f313-4f4a-49a5-1bc4d7c3ee68] [310] [en-us] [301] [316] [303] [316]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.AVAILABLE, false, true);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testAwayProtocol() {
		String test = "Magic:YMSG Version:16 Length:118 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [9] [13] [1] [47] [2] [138] [1] [187] [0] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.AWAY, false, true);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testBusyProtocol() {
		String test = "Magic:YMSG Version:16 Length:118 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [2] [13] [1] [47] [1] [138] [1] [187] [0] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.BUSY, false, true);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testInvisibleProtocol() {
		String test = "Magic:YMSG Version:16 Length:97 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [-1] [13] [0] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.OFFLINE, false, false);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	public void testSign(String test, ContactStatusImpl status, YahooContact contact) {
		// String test =
		// "Magic:YMSG Version:16 Length:95 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [0] [13] [1] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		// ContactStatusImpl status = new ContactStatusImpl();
		// status.update(Status.AVAILABLE, false, true);
		Mockito.verify(sessionStatus).statusUpdate(contact, status);
	}

	@Test
	public void testSignInProtocol() {
		String test = "Magic:YMSG Version:16 Length:95 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [0] [13] [1] [241] [2] [244] [6] [301] [315] [303] [315]";
		// YMSG9Packet packet = PacketReader.readString(test);
		// SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		// SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		// response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.AVAILABLE, false, true);
		YahooContact contact = new YahooContact("testuser@live.com", YahooProtocol.MSN);
		// Mockito.verify(sessionStatus).addStatus(new Contact("testuser@live.com", YahooProtocol.MSN), status);
		testSign(test, status, contact);
	}

	@Test
	public void testSignInYahoo() {
		String test = "Magic:YMSG Version:16 Length:193 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser] [10] [0] [13] [1] [192] [672912674] [198] [0] [213] [0] [244] [12582847] [283] [1] [317] [1] [300] [444] [440] [0] [301] [444] [550] [C2YM4G6G222Q3T5RNEHYB5K54Y] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.AVAILABLE, false, true);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testSignOutAckProtocol() {
		String test = "Magic:YMSG Version:16 Length:96 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [-1] [13] [0] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse response = new SingleStatusResponse(sessionStatus);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.OFFLINE, false, false);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testSignOutLogoffYahoo() {
		String test = "Magic:YMSG Version:16 Length:15 Service:LOGOFF Status:SERVER_ACK SessionId:0x45130f  [7] [testuser]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse singleStatusResponse = new SingleStatusResponse(sessionStatus);
		LogoffStatusResponse response = new LogoffStatusResponse(singleStatusResponse);
		response.execute(packet);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(YahooStatus.OFFLINE, false, false);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

}
