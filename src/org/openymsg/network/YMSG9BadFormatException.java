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

import java.lang.reflect.Method;

/**
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class YMSG9BadFormatException extends RuntimeException {
	private static final long serialVersionUID = 6813710810393070454L;

	private static Method initCauseMethod; // Exception chaining

	private Throwable throwable; // Chained object

	/**
	 * STATIC CONSTRUCTOR
	 */
	static {
		// Use reflection to find the initCause method, to remain backward
		// compatable with pre SDK1.4 runtimes which don't carry it.
		try {
			Class<?>[] params = { Throwable.class };
			initCauseMethod = YMSG9BadFormatException.class.getMethod(
					"initCause", params);
		} catch (NoSuchMethodException e) {
			initCauseMethod = null;
		}
	}

	private YMSG9BadFormatException() {
	}

	public YMSG9BadFormatException(String m, boolean b) {
		super("Bad parse of " + m + " packet");
	}

	public YMSG9BadFormatException(String m) {
		super(m);
	}

	public YMSG9BadFormatException(String m, boolean b, Throwable ex) {
		this(m, b);
		// Record local copy of exception, for non-SDK1.4 runtimes.
		throwable = ex;
		// If >= SDK1.4, this won't be null
		if (initCauseMethod != null) {
			try {
				Object[] params = { throwable };
				initCauseMethod.invoke(this, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Throwable getCausingThrowable() {
		return throwable;
	}
}
