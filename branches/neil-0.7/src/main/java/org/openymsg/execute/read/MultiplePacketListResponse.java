package org.openymsg.execute.read;

import java.util.List;

import org.openymsg.execute.MultiplePacketResponse;
import org.openymsg.network.YMSG9Packet;

public abstract class MultiplePacketListResponse implements MultiplePacketResponse{
	protected List<YMSG9Packet> packets = null;

	@Override
	public void execute(List<YMSG9Packet> packets) {
		this.packets = packets;
		this.execute();
	}
	
	abstract protected void execute();

}
