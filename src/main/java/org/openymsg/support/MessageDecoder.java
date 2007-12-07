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

import java.util.Stack;

/**
 * This message decoder class is designed to work along side the main jYMSG9
 * classes, accepting raw message strings from Yahoo, and translating them into
 * something a bit more Java-friendly.
 * 
 * Currently the decoder can translate messages into plain text only (formatting
 * stripped).
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class MessageDecoder {
	protected char[] msg; // Incoming message as chars

	protected StringBuffer out; // Decode to this buffer

	protected int pos; // Char position in incoming msg

	protected Stack<MessageElement> stack; // Element stack

	protected static final char ESC = 0x1b;

	protected static final String ESC_SEQ = "\u001b[";

	/**
	 * CONSTRUCTOR
	 */
	public MessageDecoder() {
		out = new StringBuffer();
		stack = new Stack<MessageElement>();
	}

	/**
	 * Decode message
	 */
	public synchronized MessageElement decode(String m) {
		msg = m.toCharArray();
		out.setLength(0);
		stack.removeAllElements();
		pos = 0;

		MessageElement section = new MessageElement(MessageElement.ROOT);
		stack.push(section);

		while (pos < msg.length) {
			if (startsWith("<font "))
				handleFont();
			else if (startsWith("<fade "))
				handleFade();
			else if (startsWith("<alt "))
				handleAlt();
			else if (startsWith(ESC_SEQ))
				handleEscape();
			else if (startsWith("</font>"))
				handleFontExit();
			else if (startsWith("</fade>"))
				handleFadeExit();
			else if (startsWith("</alt>"))
				handleAltExit();
			else if (startsWith("<"))
				handleOtherTag();
			else
				out.append(msg[pos]);
			pos++;
		}
		addText();
		return section;
	}

	public String decodeToText(String m) {
		MessageElement me = decode(m);
		return me.toText();
	}

	/**
	 * Handler for each type of data
	 */
	protected void handleFont() {
		addText();
		pos += 6; // Skip over '<font '
		int end = nextNonLiteral('>');
		String s = new String(msg, pos, end - pos);
		add(new MessageElement(MessageElement.FONT, s));
		pos = end;
	}

	protected void handleFade() {
		addText();
		pos += 6; // Skip over '<FADE '
		int end = nextNonLiteral('>');
		String s = new String(msg, pos, end - pos);
		add(new MessageElement(MessageElement.FADE, s));
		pos = nextNonLiteral('>');
	}

	protected void handleAlt() {
		addText();
		pos += 5; // Skip over '<ALT '
		int end = nextNonLiteral('>');
		String s = new String(msg, pos, end - pos);
		add(new MessageElement(MessageElement.ALT, s));
		pos = nextNonLiteral('>');
	}

	protected void handleEscape() {
		addText();
		pos += 2; // Skip over ESC [

		MessageElement section = null;
		char c = msg[pos];

		if (c >= '1' && c <= '4') // Bold, italic, colour, underline
		{
			// Add a new section to stack
			switch (c) {
			case '1':
				section = new MessageElement(MessageElement.BOLD);
				break;
			case '2':
				section = new MessageElement(MessageElement.ITALIC);
				break;
			case '3':
				section = new MessageElement(MessageElement.COLOUR_INDEX, ""
						+ msg[pos + 1]);
				break;
			case '4':
				section = new MessageElement(MessageElement.UNDERLINE);
				break;
			}
			add(section);
		} else if (c == 'x') // End: bold, italic, underline
		{
			pos++;
			// Up stack to find matching opening section
			int ty = MessageElement.ROOT;
			switch (msg[pos]) {
			case '1':
				ty = MessageElement.BOLD;
				break;
			case '2':
				ty = MessageElement.ITALIC;
				break;
			case '4':
				ty = MessageElement.UNDERLINE;
				break;
			}
			if (ty > MessageElement.ROOT)
				remove(ty);
		} else if (c == '#') // Absolute colour #rrggbb
		{
			pos++;
			String s = new String(msg, pos, 6);
			section = new MessageElement(MessageElement.COLOUR_ABS, s);
			add(section);
		}
		pos = nextNonLiteral('m');
	}

	protected void handleFontExit() {
		addText();
		pos += 6;
		remove(MessageElement.FONT);
	}

	protected void handleFadeExit() {
		addText();
		pos += 6;
		remove(MessageElement.FADE);
	}

	protected void handleAltExit() {
		addText();
		pos += 5;
		remove(MessageElement.ALT);
	}

	protected void handleOtherTag() {
		addText();
		pos += 1;
		int end = nextNonLiteral('>');
		String s = new String(msg, pos, end - pos).toLowerCase();
		// Is this a colour name? ie. <red> <blue> ...etc...
		int i1 = MessageElement.whichColourName(s);
		if (i1 >= 0) {
			MessageElement section = new MessageElement(
					MessageElement.COLOUR_NAME, i1);
			add(section);
			pos = end;
		}
		// Is this b i u or /b /i /u ?
		else if (s.equals("b")) {
			add(new MessageElement(MessageElement.BOLD));
			pos++;
		} else if (s.equals("/b")) {
			remove(MessageElement.BOLD);
			pos += 2;
		} else if (s.equals("i")) {
			add(new MessageElement(MessageElement.ITALIC));
			pos++;
		} else if (s.equals("/i")) {
			remove(MessageElement.ITALIC);
			pos += 2;
		} else if (s.equals("u")) {
			add(new MessageElement(MessageElement.UNDERLINE));
			pos++;
		} else if (s.equals("/u")) {
			remove(MessageElement.UNDERLINE);
			pos += 2;
		}
		// Is this a /colour name? ie. </red> </blue> ...etc...
		else if (s.length() > 1 && s.charAt(0) == '/') {
			int i2 = MessageElement.whichColourName(s.substring(1));
			if (i2 >= 0)
				remove(MessageElement.COLOUR_NAME, i2);
			pos = end;
		}
		// <#rrggbb> ?
		else if (s.startsWith("#")) {
			add(new MessageElement(MessageElement.COLOUR_ABS, s.substring(1)));
			pos += 7;
		} else {
			// If we fail to identify the tag, put it out as text. We've
			// already swallowed the opening '<', so add that to the start
			// of the new out buffer and let the parser pick up from the
			// next character. (Unswallow last character!)
			out.append('<');
			pos--;
		}
	}

	/**
	 * Stack ops
	 */
	private void add(MessageElement s) {
		stack.peek().addChild(s); // Add to current element
		stack.push(s); // Become current element
	}

	private void remove(int type) {
		if (stack.size() > 1) {
			MessageElement me = stack.pop();
			while (me.type != type && stack.size() > 1) {
				me = stack.pop();
			}
		}
	}

	private void remove(int type, int col) {
		if (stack.size() > 1) {
			MessageElement me = stack.pop();
			while ((me.type != type || !me.colourEquals(col))
					&& stack.size() > 1) {
				me = stack.pop();
			}
		}
	}

	private void addText() {
		if (out.length() > 0) {
			MessageElement me = stack.peek();
			MessageElement me2 = new MessageElement(MessageElement.TEXT, out
					.toString());
			me.addChild(me2);
			out = new StringBuffer();
		}
	}

	/**
	 * Low level util methods
	 */
	private boolean startsWith(String s) {
		for (int i = 0; i < s.length(); i++) {
			// Exit if we run out of msg half way through a successful match
			if (pos + i >= msg.length)
				return false;
			// Otherwise, check next char in pattern
			char c = msg[pos + i];
			if (c >= 'A' && c <= 'Z')
				c = Character.toLowerCase(c);
			if (c != s.charAt(i))
				return false;
		}
		return true;
	}

	private int nextNonLiteral(char t) {
		boolean literal = false;
		int p = pos;
		while (p < msg.length && (msg[p] != t || literal)) {
			if (msg[p] == '\"')
				literal = !literal;
			p++;
		}
		return p;
	}
}
