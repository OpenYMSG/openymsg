package org.openymsg;

public class Contact {
	private YahooProtocol protocol;
	private final String id;

	public Contact(String id, YahooProtocol protocol) {
		if (id == null) {
			throw new IllegalArgumentException("id cannot be null");
		}
		if (protocol == null) {
			throw new IllegalArgumentException("protocol cannot be null");
		}
		this.id = id;
		this.protocol = protocol;
	}

	@Deprecated
	public Contact(String string) {
		this(string, YahooProtocol.YAHOO);
	}

	public YahooProtocol getProtocol() {
		return this.protocol;
	}

	public String getId() {
		return this.id;
	}

	public void setProtocol(YahooProtocol protocol) {
		this.protocol = protocol;
	}
}
