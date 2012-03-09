package org.openymsg.contact.status;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.openymsg.Contact;
import org.openymsg.PacketReader;
import org.openymsg.Status;
import org.openymsg.network.YMSG9Packet;
import org.testng.annotations.Test;

public class ListOfStatusesResponseTest {

	@Test
	public void testSimple() {
		String TEST = "Magic:YMSG Version:16 Length:187 Service:STATUS_15 Status:DEFAULT SessionId:0x479a43  [241] [0] [302] [315] [300] [315] [7] [testuser] [10] [0] [13] [1] [192] [1142663985] [198] [0] [213] [2] [244] [12582847] [300] [444] [440] [0] [301] [444] [550] [ERGV732G4XICQINSM7YBVG3AEQ] [301] [315] [303] [315]";
		YMSG9Packet packet = PacketReader.readString(TEST);
		SessionStatusImpl sessionStatus = Mockito.mock(SessionStatusImpl.class);
		ListOfStatusesResponse response = new ListOfStatusesResponse(sessionStatus);
		List<YMSG9Packet> packets = new ArrayList<YMSG9Packet>();
		packets.add(packet);
		response.execute(packets);
		ContactStatusImpl status = new ContactStatusImpl();
		status.update(Status.AVAILABLE, false, true);
		Mockito.verify(sessionStatus).addStatus(new Contact("testuser"), status);
	}
}
