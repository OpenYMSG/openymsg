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
	LOGON(0x1),
	LOGOFF(0x2),
	ISAWAY(0x3),
	ISBACK(0x4),
	IDLE(0x5),
	MESSAGE(0x6),
	IDACT(0x7),
	IDDEACT(0x8),
	MAILSTAT(0x9),
	USERSTAT(0xa),
	NEWMAIL(0xb),
	CHATINVITE(0xc),
	CALENDAR(0xd),
	NEWPERSONMAIL(0xe),
	CONTACTNEW(0xf),
	ADDIDENT(0x10),
	ADDIGNORE(0x11),
	PING(0x12),
	GOTGROUPRENAME(0x13),
	SYSMESSAGE(0x14),
	SKINNAME(0x15),
	PASSTHROUGH2(0x16),
	CONFINVITE(0x18),
	CONFLOGON(0x19),
	CONFDECLINE(0x1a),
	CONFLOGOFF(0x1b),
	CONFADDINVITE(0x1c),
	CONFMSG(0x1d),
	CHATLOGON(0x1e),
	CHATLOGOFF(0x1f),
	CHATPM(0x20),
	GAMELOGON(0x28),
	GAMELOGOFF(0x29),
	GAMEMSG(0x2a),
	FILETRANSFER(0x46),
	VOICECHAT(0x4a),
	NOTIFY(0x4b),
	VERIFY(0x4c),
	P2PFILEXFER(0x4d),
	PEERTOPEER(0x4f),
	WEBCAM(0x50),
	AUTHRESP(0x54),
	LIST(0x55),
	AUTH(0x57),
	FRIENDADD(0x83),
	FRIENDREMOVE(0x84),
	CONTACTIGNORE(0x85),
	CONTACTREJECT(0x86),
	GROUPRENAME(0x89),
	CHATCONNECT(0x96),
	CHATGOTO(0x97),
	CHATJOIN(0x98),
	CHATLEAVE(0x99),
	CHATEXIT(0x9b),
	CHATADDINVITE(0x9d),
	CHATDISCONNECT(0xa0),
	CHATPING(0xa1),
	CHATMSG(0xa8),
	GAME_INVITE(0xb7),
	STEALTH_PERM(0xb9),
	STEALTH_SESSION(0xba),
	AVATAR(0xbc),
	PICTURE_CHECKSUM(0xbd),
	PICTURE(0xbe),
	PICTURE_UPDATE(0xc1),
	PICTURE_UPLOAD(0xc2),
	YAB_UPDATE(0xc4),
	Y6_VISIBLE_TOGGLE(0xc5), 
	Y6_STATUS_UPDATE(0xc6),
	PICTURE_STATUS(0xc7),
	VERIFY_ID_EXISTS(0xc8),
	AUDIBLE(0xd0),
	Y7_PHOTO_SHARING(0xd2),
	Y7_CONTACT_DETAILS(0xd3),
	Y7_CHAT_SESSION(0xd4),	
	Y7_AUTHORIZATION(0xd6),
	Y7_FILETRANSFER(0xdc),
	Y7_FILETRANSFERINFO(0xdd),
	Y7_FILETRANSFERACCEPT(0xde),
	Y7_MINGLE(0xe1), 
	UNKNOWN001(0xe4),
	Y7_CHANGE_GROUP(0xe7), 
	UNKNOWN002(0xef),
	YAHOO_SERVICE_WEBLOGIN(0x0226),
	YAHOO_SERVICE_SMS_MSG(0x02ea),
	UNKNOWN003(0x3330),
	UNKNOWN005(0x6c6f),
	
	// Home made service numbers, used in event dispatch only
	X_ERROR(0xf00),
	X_OFFLINE(0xf01),
	X_EXCEPTION(0xf02),
	X_BUZZ(0xf03),
	X_CHATUPDATE(0xf04);

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

		throw new IllegalArgumentException("No such ServiceType value '"
				+ value + "' (which is '" + Integer.toHexString(value)
				+ "' in hex).");
	}
}
