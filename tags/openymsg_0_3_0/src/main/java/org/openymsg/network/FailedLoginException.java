package org.openymsg.network;

public class FailedLoginException extends YahooException {
	private static final long serialVersionUID = 7869503808546162033L;

	public FailedLoginException(String message, Throwable cause) {
		super(message, cause);
	}

}
