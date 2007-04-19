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
import java.awt.Font;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * A message element structure which can be manipulated to form a structure than
 * can ultimately be expressed as a Yahoo message.
 * 
 * FIX: Yahoo appears to be in the process of changing over its escape based
 * encoding scheme to something a little like SGML tags. So far examples of the
 * new scheme have only been seen on chatrooms. At some point this class should
 * be modified to allow the switching between 'old' and 'new' encoding styles...
 * 
 * PURPOSE OLD NEW +--------------------+-----------------+-----------------+ |
 * Bold | [esc]1m | <b> | | Italic | [esc]2m | <i> | | Colour (enum) | [esc]3?m |
 * <col_name> | | Colour (abs) | [esc]3#rrggbbm | <#rrggbb> | | Font | | <font
 * ... > | | Fade | | <fade ... > | | Alt | | <alt ... > |
 * +--------------------+-----------------+-----------------+
 * 
 * Note: Yahoo's colour escape codes do not work as containers - that is to say
 * they do not have a close 'code' which undoes their effect. For as long as
 * Yahoo uses escape codes rather than proper SGML-style containers (<abc>...</abc>)
 * this code offers overloaded methods to 'fake' containers via a stack of
 * colours, so that 'pseudo' end codes can be added by simply repeating the
 * colour code above the 'closing' element in the stack.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class MutableMessageElement extends MessageElement implements
		java.util.List<MessageElement> {
	private static IllegalStateException wrongType; // Accessors may throw this

	private static String esc;

	private static String boldOn, boldOff; // Bold esc sequences

	private static String italicOn, italicOff; // Italic esc sequences

	private static String underlineOn, underlineOff; // Underline esc

	// sequences

	protected int idxColour;

	protected String namedColour;

	private boolean container = true; // Element can hold children?

	/**
	 * CONSTRUCTOR - accessable only from static factory methods
	 */
	private MutableMessageElement(int t) {
		super(null, t);
	}

	private MutableMessageElement(int t, boolean c) {
		this(t);
		container = c;
	}

	/*
	 * private void _initOld() { boldOn = esc+"1m"; boldOff=esc+"x1m";
	 * italicOn=esc+"2m"; italicOff=esc+"x2m"; underlineOn=esc+"4m";
	 * underlineOff=esc+"x4m"; } private void _initNew() { boldOn="<b>";
	 * boldOff="</b>"; italicOn="<i>"; italicOff="</i>"; underlineOn="<u>";
	 * underlineOff="</u>"; }
	 */

	// FIXME: needed?
	boolean isContainer() {
		return container;
	}

	/**
	 * STATIC CONSTRUCTOR
	 */
	static {
		wrongType = new IllegalStateException(
				"Incorrect element type for operation");

		esc = MessageDecoder.ESC_SEQ;
		boldOn = esc + "1m";
		boldOff = esc + "x1m";
		italicOn = esc + "2m";
		italicOff = esc + "x2m";
		underlineOn = esc + "4m";
		underlineOff = esc + "x4m";
	}

	/**
	 * Static factory type methods to create new types. Many types are
	 * containers, but some are not (either because they are leaves by their
	 * very nature, like TEXT, or the Yahoo protocol doesn't appear to support a
	 * closing tag for them.)
	 */
	public static MutableMessageElement createRoot() {
		return new MutableMessageElement(ROOT);
	}

	public static MutableMessageElement createText(String text) {
		MutableMessageElement mme = new MutableMessageElement(TEXT, false);
		mme.setText(text);
		return mme;
	}

	public static MutableMessageElement createBold() {
		return new MutableMessageElement(BOLD);
	}

	public static MutableMessageElement createItalic() {
		return new MutableMessageElement(ITALIC);
	}

	public static MutableMessageElement createIndexedColour(int idx) {
		MutableMessageElement mme = new MutableMessageElement(COLOUR_INDEX,
				false);
		mme.setIndexedColour(idx);
		return mme;
	}

	public static MutableMessageElement createNamedColour(String n) {
		MutableMessageElement mme = new MutableMessageElement(COLOUR_NAME,
				false);
		mme.setNamedColour(n);
		return mme;
	}

	public static MutableMessageElement createAbsoluteColour(Color c) {
		MutableMessageElement mme = new MutableMessageElement(COLOUR_ABS, false);
		mme.setAbsoluteColour(c);
		return mme;
	}

	public static MutableMessageElement createUnderline() {
		return new MutableMessageElement(UNDERLINE);
	}

	/*
	 * public static MutableMessageElement createFont(String face,int size) {
	 * MutableMessageElement mme = new MutableMessageElement(FONT);
	 * mme.setFont(face,size); return mme; }
	 */

	public static MutableMessageElement createFont(Font f) {
		MutableMessageElement mme = new MutableMessageElement(FONT);
		mme.setFont(f);
		return mme;
	}

	public static MutableMessageElement createFade(Color[] c) {
		MutableMessageElement mme = new MutableMessageElement(FADE);
		mme.setTransition(c);
		return mme;
	}

	public static MutableMessageElement createAlt(Color[] c) {
		MutableMessageElement mme = new MutableMessageElement(ALT);
		mme.setTransition(c);
		return mme;
	}

	/**
	 * Accessors
	 */
	public int getType() {
		return type;
	}

	public String getText() throws IllegalStateException {
		_checkType(TEXT);
		return text;
	}

	public void setText(String t) throws IllegalStateException {
		_checkType(TEXT);
		text = t;
	}

	public int getIndexedColour() throws IllegalStateException {
		_checkType(COLOUR_INDEX);
		return idxColour;
	}

	public void setIndexedColour(int i) throws IllegalStateException {
		_checkType(COLOUR_INDEX);
		idxColour = i;
		colour = COLOUR_OBJECTS[i];
	}

	public String getNamedColour() throws IllegalStateException {
		_checkType(COLOUR_NAME);
		return namedColour;
	}

	public void setNamedColour(String n) throws IllegalStateException {
		_checkType(COLOUR_NAME);
		namedColour = n;
		colour = COLOUR_OBJECTS[MessageElement.whichColourName(n)];
	}

	public Color getAbsoluteColour() throws IllegalStateException {
		_checkType(COLOUR_ABS);
		return colour;
	}

	public void setAbsoluteColour(Color c) throws IllegalStateException {
		_checkType(COLOUR_ABS);
		colour = c;
	}

	public Font getFont() throws IllegalStateException {
		_checkType(FONT);
		return new Font(fontFace, Font.PLAIN, fontSize);
	}

	public void setFont(Font f) throws IllegalStateException {
		_checkType(FONT);
		fontFace = f.getFamily();
		fontSize = f.getSize();
	}

	/*
	 * public void setFont(String f,int sz) throws IllegalStateException {
	 * _checkType(FONT); fontFace=f; fontSize=sz; }
	 */

	public Color[] getTransition() throws IllegalStateException {
		if (type != FADE && type != ALT)
			throw wrongType;
		return transition;
	}

	public void setTransition(Color[] c) throws IllegalStateException {
		if (type != FADE && type != ALT)
			throw wrongType;
		transition = c;
	}

	private void _checkType(int t) throws IllegalStateException {
		if (type != t)
			throw wrongType;
	}

	/**
	 * List interface methods all defer to the child array. (I wish there was an
	 * easier way to do this - making MessageElement subclass one of the
	 * Collection classes would work, but then MessageElement would be mutable,
	 * which I'd like to avoid.)
	 */
	public void add(int index, MessageElement element) {
		children.add(index, element);
	}

	public boolean add(MessageElement o) {
		return children.add(o);
	}

	public boolean addAll(Collection<? extends MessageElement> c) {
		return children.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends MessageElement> c) {
		return children.addAll(index, c);
	}

	public void clear() {
		children.clear();
	}

	public boolean contains(Object o) {
		return children.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return children.containsAll(c);
	}

	@Override
	public boolean equals(Object o) {
		return children.equals(o);
	}

	public MessageElement get(int index) {
		return children.get(index);
	}

	@Override
	public int hashCode() {
		return children.hashCode();
	}

	public int indexOf(Object o) {
		return children.indexOf(o);
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public Iterator<MessageElement> iterator() {
		return children.iterator();
	}

	public int lastIndexOf(Object o) {
		return children.lastIndexOf(o);
	}

	public ListIterator<MessageElement> listIterator() {
		return children.listIterator();
	}

	public ListIterator<MessageElement> listIterator(int index) {
		return children.listIterator(index);
	}

	public MessageElement remove(int index) {
		return children.remove(index);
	}

	public boolean remove(Object o) {
		return children.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return children.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return children.retainAll(c);
	}

	public MessageElement set(int index, MessageElement element) {
		return children.set(index, element);
	}

	public int size() {
		return children.size();
	}

	public List<MessageElement> subList(int fromIndex, int toIndex) {
		return children.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return children.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return children.toArray(a);
	}

	/**
	 * Translate to Yahoo instant messenger format
	 */
	public String toYahooIM() {
		return toYahooIM(false);
	}

	public String toYahooIM(boolean fc) {
		StringBuffer sb = new StringBuffer();
		Stack<String> colStack = fc ? new Stack<String>() : null;
		toYahooIM(sb, colStack);
		return sb.toString();
	}

	private void toYahooIM(StringBuffer sb, Stack<String> colStack) {
		switch (type) {
		case NULL:
			break;
		case TEXT:
			sb.append(text);
			break;
		case BOLD:
			sb.append(boldOn);
			break;
		case ITALIC:
			sb.append(italicOn);
			break;
		case COLOUR_INDEX:
			sb.append(_colIdx1(idxColour, colStack));
			break;
		case COLOUR_ABS:
			break; // Not supported by IM ?
		case COLOUR_NAME:
			break; // Not supported by IM ?
		case UNDERLINE:
			sb.append(underlineOn);
			break;
		case FONT:
			sb.append("<font face=\"" + fontFace + "\" size=\"" + fontSize
					+ "\">");
			break;
		case FADE:
			break; // Not supported by IM ?
		case ALT:
			break; // Not supported by IM ?
		}
		for (int i = 0; i < children.size(); i++) {
			MutableMessageElement el = (MutableMessageElement) children.get(i);
			el.toYahooIM(sb, colStack);
		}
		switch (type) {
		case NULL:
			break;
		case BOLD:
			sb.append(boldOff);
			break;
		case ITALIC:
			sb.append(italicOff);
			break;
		case COLOUR_INDEX:
			if (colStack != null)
				sb.append(_colPop(colStack));
			break;
		case COLOUR_ABS:
			break; // Not supported by IM ?
		case COLOUR_NAME:
			break; // Not supported by IM ?
		case UNDERLINE:
			sb.append(underlineOff);
			break;
		case FONT:
			sb.append("</font>");
			break;
		case FADE:
			break; // Not supported by IM ?
		case ALT:
			break; // Not supported by IM ?
		}
	}

	/**
	 * Translate to Yahoo chat format
	 */
	public String toYahooChat() {
		return toYahooChat(false);
	}

	public String toYahooChat(boolean fc) {
		StringBuffer sb = new StringBuffer();
		Stack<String> colStack = fc ? new Stack<String>() : null;
		toYahooChat(sb, colStack);
		return sb.toString();
	}

	private void toYahooChat(StringBuffer sb, Stack<String> colStack) {
		switch (type) {
		case NULL:
			break;
		case TEXT:
			sb.append(text);
			break;
		case BOLD:
			sb.append(boldOn);
			break;
		case ITALIC:
			sb.append(italicOn);
			break;
		case COLOUR_INDEX:
			sb.append(_colIdx1(idxColour, colStack));
			break;
		case COLOUR_ABS:
			sb.append(_colAbs1(colour, colStack));
			break;
		case COLOUR_NAME:
			sb.append("<" + namedColour + ">");
			break;
		case UNDERLINE:
			sb.append(underlineOn);
			break;
		case FONT:
			sb.append("<font face=\"" + fontFace + "\" size=\"" + fontSize
					+ "\">");
			break;
		case FADE:
			sb.append("<fade " + _toTransition(transition) + ">");
			break;
		case ALT:
			sb.append("<alt " + _toTransition(transition) + ">");
			break;
		}
		for (int i = 0; i < children.size(); i++) {
			MutableMessageElement el = (MutableMessageElement) children.get(i);
			el.toYahooChat(sb, colStack);
		}
		switch (type) {
		case NULL:
			break;
		case BOLD:
			sb.append(boldOff);
			break;
		case ITALIC:
			sb.append(italicOff);
			break;
		case COLOUR_INDEX:
			if (colStack != null)
				sb.append(_colPop(colStack));
			break;
		case COLOUR_ABS:
			if (colStack != null)
				sb.append(_colPop(colStack));
			break;
		case COLOUR_NAME:
			sb.append("</" + namedColour + ">");
			break;
		case UNDERLINE:
			sb.append(underlineOff);
			break;
		case FONT:
			sb.append("</font>");
			break;
		case FADE:
			sb.append("</fade>");
			break;
		case ALT:
			sb.append("</alt>");
			break;
		}
	}

	private String _colIdx1(int _idxColour, Stack<String> colStack) {
		String tmp = esc + "3" + (char) ('0' + _idxColour) + "m";
		if (colStack != null)
			colStack.push(tmp);
		return tmp;
	}

	private String _colAbs1(Color _colour, Stack<String> colStack) {
		String tmp = esc + "3#" + _to6HexDigits(_colour) + "m";
		if (colStack != null)
			colStack.push(tmp);
		return tmp;
	}

	private String _colPop(Stack<String> colStack) {
		colStack.pop(); // Pop ourselves from stack
		if (!colStack.empty())
			return colStack.peek();
		return "";
	}

	/**
	 * Utility methods
	 */
	private String _to6HexDigits(Color c) {
		StringBuffer sb = new StringBuffer(Integer.toString(
				(c.getRGB() & 0xffffff), 16));
		while (sb.length() < 6)
			sb.insert(0, '0');
		return new String(sb);
	}

	private String _toTransition(Color[] c) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < c.length; i++) {
			if (i != 0)
				sb.append(',');
			sb.append('#').append(_to6HexDigits(c[i]));
		}
		return new String(sb);
	}
}
