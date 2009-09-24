/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openymsg.network;

import java.io.IOException;
import java.net.SocketException;
import java.util.TimerTask;
import org.openymsg.support.Logger;

/**
 * Runnable class that is responsible for sending keep-alive packets to the
 * Yahoo! network for each session instance that's currently logged on.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class SessionPinger extends TimerTask {

    private static final Logger log = Logger.getLogger(SessionPinger.class);
    int count = 0;

	/**
	 * The session on which behalf the keep-alive packet should be sent.
	 */
	final Session session;

	/**
	 * Creates a new instance which is linked to a particular session.
	 * 
	 * @param session
	 *            The session for which to send out a keep-alive packet.
	 */
	public SessionPinger(Session session) {
		if (session == null) {
			throw new IllegalArgumentException(
					"Argument 'session' cannot be null.");
		}
		this.session = session;
	}

	/**
	 * Tries to send the keep-alive packet, then exits.
	 */
	@Override
	public void run() {
		try {
			session.transmitKeepAlive();
			if (count++ % NetworkConstants.PING_TO_KEEPALIVE_RATIO == 0) {
				session.transmitPings();
			}
		} catch (IOException ex) {
			if (ex instanceof SocketException) {
				log.info("Logging out due to socket exception: " + session.getSessionID() + "/" + session.getLoginID());
				try {
					this.session.forceCloseSession();
				} catch (Exception e) {
                    log.error("Failed to close session: " + session.getSessionID() + "/" + session.getLoginID(), e);
				}
			}
			else 
			{
				log.error("Could not send keep-alive to: " + session.getSessionID() + "/" + session.getLoginID(), ex);
			}
		}
	}
}
