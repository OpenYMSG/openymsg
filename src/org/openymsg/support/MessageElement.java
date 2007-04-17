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
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

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

	protected Vector<MessageElement> children; // Contained sections

	protected int fontSize; // Attributes

	protected String fontFace, text; // Attributes

	protected Color[] transition; // Fade/alt colours

	protected Color colour;

	private MessageDecoderSettings settings;

	static final String[] COLOUR_INDEXES = { "black", "blue", "cyan", "pink",
			"green", "gray", "purple", "orange", "red", "brown", "yellow" };

	static final Color[] COLOUR_OBJECTS = { Color.black, Color.blue,
			Color.cyan, Color.pink, Color.green, Color.gray, Color.magenta,
			Color.orange, Color.red, Color.lightGray, // FIX: ltGray
			Color.yellow };

	/**
	 * CONSTRUCTORS
	 */
	protected MessageElement(MessageDecoderSettings set, int t) {
		settings = set;
		type = t;
		children = new Vector<MessageElement>();
	}

	protected MessageElement(MessageDecoderSettings set, int t, String body) {
		this(set, t);
		switch (t) {
		case TEXT:
			text = body;
			break;
		case FONT:
			fontFace = _attr(body, "face");
			String s = _attr(body, "size");
			if (s != null)
				try {
					fontSize = Integer.parseInt(s);
				} catch (NumberFormatException e) {
				}
			if (fontFace == null)
				type = NULL;
			// Modify if conflicts with settings
			if (settings != null) {
				if (settings.overMaxFontSize >= 0
						&& fontSize > settings.overMaxFontSize)
					fontSize = settings.overMaxFontSize;
				if (settings.overMinFontSize >= 0
						&& fontSize < settings.overMinFontSize)
					fontSize = settings.overMinFontSize;
				if (settings.overFontFace != null)
					fontFace = settings.overFontFace;
			}
			break;
		case FADE:
		case ALT:
			StringTokenizer st = new StringTokenizer(body, ",");
			transition = new Color[st.countTokens()];
			int i = 0;
			while (st.countTokens() > 0) {
				String a = st.nextToken();
				if (a.startsWith("#"))
					a = a.substring(1);

				try {
					transition[i++] = new Color(Integer.parseInt(a, 16));
				} catch (NumberFormatException e) {
					transition[i - 1] = Color.black;
				}
			}
			break;
		case COLOUR_INDEX:
			colour = COLOUR_OBJECTS[body.charAt(0) - '0'];
			// Modify if conflicts with settings
			if (settings != null && settings.overFg != null)
				colour = settings.overFg;
			break;
		case COLOUR_ABS:
			colour = new Color(Integer.parseInt(body, 16));
			// Modify if conflicts with settings
			if (settings != null && settings.overFg != null)
				colour = settings.overFg;
			break;
		}
	}

	protected MessageElement(MessageDecoderSettings def, int t, int num) {
		this(def, t);
		switch (t) {
		case COLOUR_NAME:
			colour = COLOUR_OBJECTS[num];
			// Modify if conflicts with settings
			if (settings != null && settings.overFg != null)
				colour = settings.overFg;
			break;
		}
	}

	/**
	 * Utility methods
	 */
	private String _attr(String haystack, String at) {
		at = at + "=\"";
		String lc = haystack.toLowerCase();

		int idx = lc.indexOf(at);
		if (idx >= 0) {
			haystack = haystack.substring(idx + at.length());
			idx = haystack.indexOf("\"");
			if (idx >= 0)
				haystack = haystack.substring(0, idx);
			return haystack;
		}
		return null;
	}

	static int whichColourName(String n) {
		for (int i = 0; i < COLOUR_INDEXES.length; i++) {
			if (n.equals(COLOUR_INDEXES[i]))
				return i;
		}
		return -1;
	}

	boolean colourEquals(int i) {
		return (colour == COLOUR_OBJECTS[i]);
	}

	int childTextSize() {
		int l = 0;
		for (int i = 0; i < children.size(); i++) {
			MessageElement e = children.elementAt(i);
			if (e.type == TEXT)
				l += e.text.length();
			else
				l += e.childTextSize();
		}
		return l;
	}

	/**
	 * Add a child to this section
	 */
	void addChild(MessageElement s) {
		children.addElement(s);
	}

	/**
	 * Translate to HTML
	 */
	public String toHTML() {
		StringBuffer sb = new StringBuffer();
		toHTML(sb);
		return sb.toString();
	}

	private void toHTML(StringBuffer sb) {
		switch (type) {
		case NULL:
			sb.append("<span>");
			break;
		case TEXT:
			sb.append(text);
			break;
		case BOLD:
			sb.append("<b>");
			break;
		case ITALIC:
			sb.append("<i>");
			break;
		case COLOUR_INDEX:
		case COLOUR_ABS:
		case COLOUR_NAME:
			sb.append("<font color=\"#").append("" + colour.getRGB()).append(
					"\">");
			break;
		case UNDERLINE:
			sb.append("<u>");
			break;
		case FONT:
			sb.append("<font face=\"" + fontFace + "\" size=\"" + fontSize
					+ "\">");
			break;
		case FADE:
			sb.append("<span>");
			break;
		case ALT:
			sb.append("<span>");
			break;
		}
		for (int i = 0; i < children.size(); i++) {
			MessageElement sc = children.elementAt(i);
			sc.toHTML(sb);
		}
		switch (type) {
		case NULL:
			sb.append("</span>");
			break;
		case BOLD:
			sb.append("</b>");
			break;
		case ITALIC:
			sb.append("</i>");
			break;
		case COLOUR_INDEX:
		case COLOUR_ABS:
		case COLOUR_NAME:
			sb.append("</font>");
			break;
		case UNDERLINE:
			sb.append("</u>");
			break;
		case FONT:
			sb.append("</font>");
			break;
		case FADE:
			sb.append("</span>");
			break;
		case ALT:
			sb.append("</span>");
			break;
		}
	}

	/**
	 * Translate to HTML
	 */
	public String toText() {
		StringBuffer sb = new StringBuffer();
		toText(sb);
		return sb.toString();
	}

	private void toText(StringBuffer sb) {
		if (type == TEXT)
			sb.append(text);
		for (int i = 0; i < children.size(); i++) {
			MessageElement sc = children.elementAt(i);
			sc.toText(sb);
		}
	}

	@Override
	public String toString() {
		switch (type) {
		case NULL:
			return "[Null]";
		case ROOT:
			return "[Root]";
		case TEXT:
			return "Text:" + text;
		case BOLD:
			return "<b>";
		case ITALIC:
			return "<i>";
		case COLOUR_INDEX:
			return "<col @" + colour + ">";
		case UNDERLINE:
			return "<u>";
		case FONT:
			return "<font " + fontFace + ":" + fontSize + ">";
		case FADE:
			return "<fade " + Arrays.toString(transition) + ">";
		case ALT:
			return "<alt " + Arrays.toString(transition) + ">";
		case COLOUR_ABS:
			return "<col #" + colour + ">";
		case COLOUR_NAME:
			return "<col name #" + colour + ">";
		default:
			return "?";
		}
	}
}
