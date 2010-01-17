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

import java.util.ArrayList;
import java.util.List;

/**
 * A message element represents a low level segment of a decoded message. The
 * sections form a hierarchy, with zero or more sections nested inside a given
 * section.
 * 
 * Thanks to John Morris, who provided examples of some useful upgrades and
 * optimisations to the Swing Document code.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class MessageElement {
	public final static int NULL = -2; // No meaning

	public final static int ROOT = -1; // Root section

	public final static int TEXT = 0; // Text data

	public final static int BOLD = 1; // Bold container

	public final static int ITALIC = 2; // Italic container

	public final static int COLOUR_INDEX = 3; // Colour index 0-9 container

	public final static int UNDERLINE = 4; // Underline container

	public final static int FONT = 5; // Font container

	public final static int FADE = 6; // Fade container

	public final static int ALT = 7; // Alt container

	public final static int COLOUR_ABS = 8; // Colour absolute #rrggbb container

	public final static int COLOUR_NAME = 9; // Named colour <red> <blue>

	// etc.

	protected int type; // Type of section (see above)

	protected List<MessageElement> children; // Contained sections

	protected String text;

	protected int colour;

	static final String[] COLOUR_INDEXES = { "black", "blue", "cyan", "pink",
			"green", "gray", "purple", "orange", "red", "brown", "yellow" };

	/**
	 * CONSTRUCTORS
	 */
	protected MessageElement(int t) {
		type = t;
		children = new ArrayList<MessageElement>();
	}

	protected MessageElement(int t, String body) {
		this(t);
		switch (t) {
		case TEXT:
			text = body;
			break;
		default:
			// ignore all non-plain text stuff
		}
	}

	protected MessageElement(int t, int num) {
		this(t);
		switch (t) {
		case COLOUR_NAME:
			colour = num;
			break;
		}
	}

	/**
	 * Utility methods
	 */
	static int whichColourName(String n) {
		for (int i = 0; i < COLOUR_INDEXES.length; i++) {
			if (n.equals(COLOUR_INDEXES[i]))
				return i;
		}
		return -1;
	}

	boolean colourEquals(int i) {
		return (colour == i);
	}

	/**
	 * Add a child to this section
	 */
	void addChild(MessageElement s) {
		children.add(s);
	}


	/**
	 * Translate to plain text
	 */
	public String toText() {
		final StringBuffer sb = new StringBuffer();
		toText(sb);
		return sb.toString();
	}

	private void toText(StringBuffer sb) {
		if (type == TEXT)
			sb.append(text);

		for (MessageElement sc : children) {
			sc.toText(sb);
		}
	}
}
