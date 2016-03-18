/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol. Copyright (C) 2007 G. der Kinderen,
 * Nimbuzz.com This program is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openymsg.legacy.network.event;

/**
 * MailCount From EmailA. Subject Message newMailReceived y (gt 0) n n(?) n n
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionNewMailEvent extends SessionEvent {
	protected int mail;
	protected String subject, address;

	// CONSTRUCTORS
	public SessionNewMailEvent(Object o, String ml) {
		super(o);
		mail = Integer.parseInt(ml);
	}

	public SessionNewMailEvent(Object o, String fr, String em, String sb) {
		super(o, null, fr, null);
		mail = 0;
		address = em;
		subject = sb;
	}

	// Accessors
	public int getMailCount() {
		return mail;
	}

	public String getSubject() {
		return subject;
	}

	public String getEmailAddress() {
		return address;
	}

	public boolean isWholeMail() {
		return (mail == 0);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString()).append(" mail:").append(mail).append(" addr:")
				.append(address).append(" subject:").append(subject);
		return sb.toString();
	}
}
