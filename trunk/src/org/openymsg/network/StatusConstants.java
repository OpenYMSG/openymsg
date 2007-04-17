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
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public interface StatusConstants {
	public static final String STATUS_AVAILABLE_STR = "Available";

	public static final String STATUS_BRB_STR = "Be right back";

	public static final String STATUS_BUSY_STR = "Busy";

	public static final String STATUS_NOTATHOME_STR = "Not at home";

	public static final String STATUS_NOTATDESK_STR = "Not at desk";

	public static final String STATUS_NOTINOFFICE_STR = "Not in office";

	public static final String STATUS_ONPHONE_STR = "On the phone";

	public static final String STATUS_ONVACATION_STR = "On vacation";

	public static final String STATUS_OUTTOLUNCH_STR = "Out to lunch";

	public static final String STATUS_STEPPEDOUT_STR = "Stepped out";

	public static final String STATUS_INVISIBLE_STR = "Invisible";

	public static final String STATUS_CUSTOM_STR = "<custom>";

	public static final String STATUS_IDLE_STR = "Zzz";

	public static final int STEALTH_DEFAULT = 0;

	public static final int STEALTH_ONLINE = 1;

	public static final int STEALTH_OFFLINE = 2;

	public static final String NOTIFY_TYPING = "TYPING";

	public static final String NOTIFY_GAME = "GAME";
}
