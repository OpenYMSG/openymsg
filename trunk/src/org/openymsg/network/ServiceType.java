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

/**
 * Enumeration of all ServiceType values, as found in the YMSG packets.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public enum ServiceType {
	LOGON(0x01), LOGOFF(0x02), ISAWAY(0x03), ISBACK(0x04), IDLE(0x05), MESSAGE(
			0x06), IDACT(0x07), IDDEACT(0x08), MAILSTAT(0x09), USERSTAT(0x0a), NEWMAIL(
			0x0b), CHATINVITE(0x0c), CALENDAR(0x0d), NEWPERSONMAIL(0x0e), CONTACTNEW(
			0x0f), ADDIDENT(0x10), ADDIGNORE(0x11), PING(0x12), GROUPRENAME(
			0x13), SYSMESSAGE(0x14), PASSTHROUGH2(0x16), CONFINVITE(0x18), CONFLOGON(
			0x19), CONFDECLINE(0x1a), CONFLOGOFF(0x1b), CONFADDINVITE(0x1c), CONFMSG(
			0x1d),
	/*
	 * CHATLOGON (0x1e), CHATLOGOFF (0x1f),
	 */
	CHATPM(0x20), GAMELOGON(0x28), GAMELOGOFF(0x29), GAMEMSG(0x2a), FILETRANSFER(
			0x46), VOICECHAT(0x4a), NOTIFY(0x4b), P2PFILEXFER(0x4d), PEERTOPEER(
			0x4f), AUTHRESP(0x54), LIST(0x55), AUTH(0x57), FRIENDADD(0x83), FRIENDREMOVE(
			0x84), CONTACTIGNORE(0x85), CONTACTREJECT(0x86), CHATCONNECT(0x96), CHATGOTO(
			0x97), // ?
	CHATLOGON(0x98), CHATLEAVE(0x99), // ?
	CHATLOGOFF(0x9b), CHATDISCONNECT(0xa0), CHATPING(0xa1), // ?
	CHATMSG(0xa8), UNKNOWN001(228), UNKNOWN002(239), UNKNOWN003(13104),
	// Home made service numbers, used in event dispatch only
	X_ERROR(0xf00), X_OFFLINE(0xf01), X_EXCEPTION(0xf02), X_BUZZ(0xf03), X_CHATUPDATE(
			0xf04);

	// service jYMSG9 libyahoo2
	// ------------------------------------------------
	// 0x0f CONTACTNEW NEWCONTACT
	// 0x83 FRIENDADD ADDBUDDY
	// 0x84 FRIENDREMOVE REMBUDDY
	// 0x85 CONTACTIGNORE IGNORECONTACT
	// 0x86 CONTACTREJECT REJECTCONTACT
	// 0x96 CHATCONNECT CHATONLINE
	// 0x98 CHATLOGON CHATJOIN
	// 0x9b CHATLOGOFF CHATEXIT
	// 0xa0 CHATDISCONNECT CHATLOGOFF
	// 0xa8 CHATMSG COMMENT

	private final int value;

	ServiceType(final int value) {
		this.value = value;
	}

	/**
	 * Returns the integer value for this ServiceType.
	 * 
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns the enum-value that matches the integer representation. Throws an
	 * IllegalArgumentException if no such enum value exists.
	 * 
	 * @param value
	 *            Integer value representing a ServiceType
	 * @return Returns the ServiceType associated with the integer value.
	 */
	public static ServiceType getServiceType(int value) {
		final ServiceType[] all = ServiceType.values();
		for (int i = 0; i < all.length; i++) {
			if (all[i].getValue() == value) {
				return all[i];
			}
		}

		throw new IllegalArgumentException("No such ServiceType (" + value
				+ ").");
	}
}
