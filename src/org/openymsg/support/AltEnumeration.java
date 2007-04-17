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
package org.openymsg.support;

import java.awt.Color;
import java.util.Enumeration;

/**
 * Creates an enumeration object which produces a sequence of Color objects
 * based upon a ALT.
 * 
 * hasMoreElements() will always return true.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
class AltEnumeration implements Enumeration<Color> {
	private int index; // Current step

	private Color[] colours; // Colours in alt transition

	/**
	 * CONSTRUCTOR
	 */
	AltEnumeration(Color[] c) {
		colours = c;
		index = 0;
	}

	/**
	 * Enumeration methods
	 */
	public boolean hasMoreElements() {
		return true;
	}

	public Color nextElement() {
		index++;
		return colours[(index - 1) % colours.length];
	}
}
