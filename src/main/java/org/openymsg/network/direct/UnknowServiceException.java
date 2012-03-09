package org.openymsg.network.direct;

import org.openymsg.network.YMSG9Packet;

/**
 * @author Giancarlo Frison - Nimbuzz B.V. <giancarlo@nimbuzz.com>
 */
public class UnknowServiceException extends RuntimeException {
	private static final long serialVersionUID = -606709859886555442L;
	private YMSG9Packet packet;

	/**
	 * @param p
	 */
	public UnknowServiceException(YMSG9Packet p) {
		super();
		packet = p;
	}

	public YMSG9Packet getPacket() {
		return packet;
	}
}
