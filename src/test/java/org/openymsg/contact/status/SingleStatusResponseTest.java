package org.openymsg.contact.status;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.YahooStatus;
import org.openymsg.contact.status.response.LogoffStatusResponse;
import org.openymsg.contact.status.response.SingleStatusResponse;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;

/**
 * <ul>
 * <li>STATUS_15 is for coming online.
 * <li>Y6_STATUS_UPDATE is for available/away and custom message.
 * <li>LOGOFF is for logoff or invisible
 * <ul>
 * 
 * @author nhart
 */
public class SingleStatusResponseTest {
	@Mock
	private SessionStatusCallback callback;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAvailableProtocol() {
		String test = "Magic:YMSG Version:16 Length:96 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [0] [13] [1] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		ContactStatusImpl status = getContactstatus(YahooStatus.AVAILABLE, false, true);
		verify(callback).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testAvailableFromInvisibleYahoo() {
		String statusTest = "Magic:YMSG Version:16 Length:307 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser] [10] [0] [13] [1] [97] [1] [192] [672912674] [198] [0] [213] [0] [244] [12582847] [283] [1] [317] [1] [300] [444] [440] [0] [301] [444] [550] [C2YM4G6G222Q3T5RNEHYB5K54Y] [301] [315] [303] [315] [302] [316] [300] [316] [135] [2.0.4] [258] [4eb73995-f313-4f4a-49a5-1bc4d7c3ee68] [310] [en-us] [301] [316] [303] [316]";
		YMSG9Packet packet = PacketReader.readString(statusTest);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		ContactStatusImpl status = getContactstatus(YahooStatus.AVAILABLE, false, true);
		verify(callback).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testAwayProtocol() {
		String test = "Magic:YMSG Version:16 Length:118 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [9] [13] [1] [47] [2] [138] [1] [187] [0] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		ContactStatusImpl status = getContactstatus(YahooStatus.AWAY, false, true);
		verify(callback).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testBusyProtocol() {
		String test = "Magic:YMSG Version:16 Length:118 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [2] [13] [1] [47] [1] [138] [1] [187] [0] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		ContactStatusImpl status = getContactstatus(YahooStatus.BUSY, false, true);
		verify(callback).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testInvisibleProtocol() {
		String test = "Magic:YMSG Version:16 Length:97 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [-1] [13] [0] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		ContactStatusImpl status = ContactStatusImpl.OFFLINE;
		verify(callback).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testSignInProtocol() {
		String test = "Magic:YMSG Version:16 Length:95 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [0] [13] [1] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		ContactStatusImpl status = getContactstatus(YahooStatus.AVAILABLE, false, true);
		YahooContact contact = new YahooContact("testuser@live.com", YahooProtocol.MSN);
		verify(callback).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testSignInYahoo() {
		String test = "Magic:YMSG Version:16 Length:193 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser] [10] [0] [13] [1] [192] [672912674] [198] [0] [213] [0] [244] [12582847] [283] [1] [317] [1] [300] [444] [440] [0] [301] [444] [550] [C2YM4G6G222Q3T5RNEHYB5K54Y] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		ContactStatusImpl status = getContactstatus(YahooStatus.AVAILABLE, false, true);
		verify(callback).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testSignOutAckProtocol() {
		String test = "Magic:YMSG Version:16 Length:96 Service:STATUS_15 Status:SERVER_ACK SessionId:0x45130f  [302] [315] [300] [315] [7] [testuser@live.com] [10] [-1] [13] [0] [241] [2] [244] [6] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		ContactStatusImpl status = ContactStatusImpl.OFFLINE;
		verify(callback).statusUpdate(new YahooContact("testuser@live.com", YahooProtocol.MSN), status);
	}

	@Test
	public void testSignOutLogoffYahoo() {
		String test = "Magic:YMSG Version:16 Length:15 Service:LOGOFF Status:SERVER_ACK SessionId:0x45130f  [7] [testuser]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse singleStatusResponse = new SingleStatusResponse(callback);
		LogoffStatusResponse response = new LogoffStatusResponse(singleStatusResponse);
		response.execute(packet);
		ContactStatusImpl status = ContactStatusImpl.OFFLINE;
		verify(callback).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testCustomAvailableProtocol() {
		String test = "Magic:YMSG Version:16 Length:63 Service:Y6_STATUS_UPDATE Status:SERVER_ACK SessionId:fffffffffe5a618f  [7] [testuser] [10] [99] [19] [Custom Message] [47] [0] [97] [1] [187] [0] [317] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		StatusMessage internalStatus = new CustomStatusMessage(YahooStatus.AVAILABLE, "Custom Message");
		ContactPresence presence = null;
		Long idleTime = -1L;
		ContactStatusImpl status = new ContactStatusImpl(internalStatus, presence, idleTime);
		verify(callback).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testCustomBusyProtocol() {
		String test = "Magic:YMSG Version:16 Length:63 Service:Y6_STATUS_UPDATE Status:SERVER_ACK SessionId:fffffffffe5a618f  [7] [testuser] [10] [99] [19] [Custom Message] [47] [1] [97] [1] [187] [0] [317] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		StatusMessage internalStatus = new CustomStatusMessage(YahooStatus.BUSY, "Custom Message");
		ContactPresence presence = null;
		Long idleTime = -1L;
		ContactStatusImpl status = new ContactStatusImpl(internalStatus, presence, idleTime);
		verify(callback).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testCustomIdleProtocol() {
		String test = "Magic:YMSG Version:16 Length:63 Service:Y6_STATUS_UPDATE Status:SERVER_ACK SessionId:fffffffffe5a618f  [7] [testuser] [10] [99] [19] [Custom Message] [47] [2] [97] [1] [187] [0] [317] [1]";
		YMSG9Packet packet = PacketReader.readString(test);
		SingleStatusResponse response = new SingleStatusResponse(callback);
		response.execute(packet);
		StatusMessage internalStatus = new CustomStatusMessage(YahooStatus.IDLE, "Custom Message");
		ContactPresence presence = null;
		Long idleTime = -1L;
		ContactStatusImpl status = new ContactStatusImpl(internalStatus, presence, idleTime);
		verify(callback).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	private ContactStatusImpl getContactstatus(YahooStatus yahooStatus, boolean onChat, boolean onPager) {
		if (yahooStatus.isCustom()) {
			throw new IllegalArgumentException("status cannot be custom");
		}
		NormalStatusMessage status = new NormalStatusMessage(yahooStatus);
		ContactPresence presence = new ContactPresence(onChat, onPager);
		long idleTime = -1L;
		return new ContactStatusImpl(status, presence, idleTime);
	}
}
