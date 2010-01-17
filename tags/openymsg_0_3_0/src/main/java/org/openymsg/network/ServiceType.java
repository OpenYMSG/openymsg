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

import org.openymsg.support.Logger;

/**
 * Enumeration of all ServiceType values, as found in the YMSG packets.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public enum ServiceType {
    /**
     * receive 'addFriend' command by an other user
     */
    FRIENDADD(0x83), //131 - Add Buddy
    ADDIDENT(0x10),
    ADDIGNORE(0x11),
    AUDIBLE(0xd0),
    AUTH(0x57),
    AUTHRESP(0x54),
    AVATAR(0xbc),
    CALENDAR(0xd),
    CHATADDINVITE(0x9d),
    CHATCONNECT(0x96),
    CHATDISCONNECT(0xa0),
    CHATEXIT(0x9b),
    CHATGOTO(0x97),
    CHATINVITE(0xc),
    CHATJOIN(0x98),
    CHATLEAVE(0x99),
    CHATLOGOFF(0x1f),
    CHATLOGON(0x1e),
    CHATMSG(0xa8),
    CHATPING(0xa1),
    CHATPM(0x20),
    CONFADDINVITE(0x1c),
    CONFDECLINE(0x1a),
    CONFINVITE(0x18),
    CONFLOGOFF(0x1b),
    CONFLOGON(0x19),
    CONFMSG(0x1d),
    CONTACTIGNORE(0x85),
    CONTACTNEW(0xf),
    CONTACTREJECT(0x86),
    FILETRANSFER(0x46),
    FRIENDREMOVE(0x84), //132
    GAME_INVITE(0xb7),
    GAMELOGOFF(0x29),
    GAMELOGON(0x28),
    GAMEMSG(0x2a),
    GOTGROUPRENAME(0x13),
    GROUPRENAME(0x89),
    IDACT(0x7),
    IDDEACT(0x8),
    IDLE(0x5),
    ISAWAY(0x3),
    ISBACK(0x4),
    KEEPALIVE(0x8a),
    LIST(0x55),
    LIST_15(0xf1),
    LOGOFF(0x2),
    LOGON(0x1),
    MAILSTAT(0x9),
    MESSAGE(0x6),
    MESSAGE_ACK(0xfb),
    NEWMAIL(0xb),
    NEWPERSONMAIL(0xe),
    NOTIFY(0x4b),
    P2PFILEXFER(0x4d),
    PASSTHROUGH2(0x16),
    PEERTOPEER(0x4f),
    PICTURE(0xbe),
    PICTURE_CHECKSUM(0xbd),
    PICTURE_STATUS(0xc7),
    PICTURE_UPDATE(0xc1),
    PICTURE_UPLOAD(0xc2),
    PING(0x12),
    SKINNAME(0x15),
    STEALTH_PERM(0xb9),
    STEALTH_SESSION(0xba),
    SYSMESSAGE(0x14),
    UNKNOWN001(0xe4),
    UNKNOWN002(0xef),
    UNKNOWN003(0x3330),
    UNKNOWN005(0x6c6f), 
    USERSTAT(0xa),
    VERIFY(0x4c),
    VERIFY_ID_EXISTS(0xc8),
    VOICECHAT(0x4a),
    WEBCAM(0x50),
    X_BUZZ(0xf03),
    X_CHATUPDATE(0xf04),
    X_CHATCAPTCHA(0xf05),
    X_ERROR(0xf00),
    X_EXCEPTION(0xf02),
    X_OFFLINE(0xf01),
    Y6_STATUS_UPDATE(0xc6),
    STATUS_15(0xf0), //240
    Y6_VISIBLE_TOGGLE(0xc5), 
    Y7_AUTHORIZATION(0xd6), //214 - Y7 Buddy Authorization
    Y7_CHANGE_GROUP(0xe7), 
    Y7_CHAT_SESSION(0xd4),
    Y7_CONTACT_DETAILS(0xd3),
    Y7_FILETRANSFER(0xdc),
    Y7_FILETRANSFERACCEPT(0xde),
    Y7_FILETRANSFERINFO(0xdd),
    
    // Home made service numbers, used in event dispatch only
    Y7_MINGLE(0xe1),
    Y7_PHOTO_SHARING(0xd2),
    YAB_UPDATE(0xc4),
    YAHOO_SERVICE_SMS_MSG(0x02ea),
    YAHOO_SERVICE_WEBLOGIN(0x0226);

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

        Logger.getLogger(ServiceType.class).warn("No such ServiceType value '"
                + value + "' (which is '" + Integer.toHexString(value)
                + "' in hex).");
        return null;
    }

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
}
