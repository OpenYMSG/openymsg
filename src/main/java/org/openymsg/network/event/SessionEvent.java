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
package org.openymsg.network.event;

import java.util.Date;

/**
 * This class is the parent of all event classes in this package.
 * 
 * To From Message Timestamp contactRejectionReceived y y y n
 * contactRequestReceived y y y y messageReceived y y y n buzzReceived y y y n
 * offlineMessageReceived y y y y listReceived n n n n logoffReceived n n n n
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public interface SessionEvent {

	String getTo();

	String getFrom();

	String getMessage();

	Date getTimestamp();

	long getStatus();

}
