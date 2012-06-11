package org.openymsg.contact.status;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.YahooStatus;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.annotations.Test;

public class ListOfStatusesResponseTest {

	@Test
	public void testSimple() {
		String test = "Magic:YMSG Version:16 Length:187 Service:STATUS_15 Status:DEFAULT SessionId:0x479a43 [241] [0] [302] [315] [300] [315] [7] [testuser] [10] [0] [13] [1] [192] [1142663985] [198] [0] [213] [2] [244] [12582847] [300] [444] [440] [0] [301] [444] [550] [ERGV732G4XICQINSM7YBVG3AEQ] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse singleResponse = new SingleStatusResponse(sessionStatus);
		ListOfStatusesResponse response = new ListOfStatusesResponse(singleResponse);
		List<YMSG9Packet> packets = new ArrayList<YMSG9Packet>();
		packets.add(packet);
		response.execute(packets);
		ContactStatusImpl status = new ContactStatusImpl(YahooStatus.AVAILABLE, false, true);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}

	@Test
	public void testSingle() {
		String test = "Magic:YMSG Version:16 Length:192 Service:STATUS_15 Status:SERVER_ACK SessionId:664235ca  [302] [315] [300] [315] [7] [testuser] [10] [0] [13] [1] [192] [-1269201098] [198] [0] [213] [2] [244] [12582847] [283] [1] [317] [1] [300] [444] [440] [0] [301] [444] [550] [VSOZGHO5HDTW2F2PAQ7XKMMGQM] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		SingleStatusResponse singleResponse = new SingleStatusResponse(sessionStatus);
		ListOfStatusesResponse response = new ListOfStatusesResponse(singleResponse);
		List<YMSG9Packet> packets = new ArrayList<YMSG9Packet>();
		packets.add(packet);
		response.execute(packets);
		ContactStatusImpl status = new ContactStatusImpl(YahooStatus.AVAILABLE, false, true);
		Mockito.verify(sessionStatus).statusUpdate(new YahooContact("testuser", YahooProtocol.YAHOO), status);
	}
}