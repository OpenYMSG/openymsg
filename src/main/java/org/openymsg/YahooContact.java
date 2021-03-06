package org.openymsg;

/**
 * Identity of a contact. This consists of two parts: id and protocol. Most of the time the protocol is
 * YahooProtocol.YAHOO. Comparable to enable easier testing.
 * @author neilhart
 */
public class YahooContact implements Comparable<YahooContact> {
	/** unique name within a specific protocol */
	private final String name;
	/** system the id is on. Mostly YahooProtocol.YAHOO */
	private final YahooProtocol protocol;

	/**
	 * Create a new Contact
	 * @param id id of Contact
	 * @param protocol protocol of Contact
	 */
	public YahooContact(String id, YahooProtocol protocol) {
		if (id == null) {
			throw new IllegalArgumentException("id cannot be null");
		}
		if (protocol == null) {
			throw new IllegalArgumentException("protocol cannot be null");
		}
		this.name = id;
		this.protocol = protocol;
	}

	/**
	 * Quick, and dirty, creator of Contact. Protocol is set to YahooProtocol.YAHOO
	 * @param id contact id
	 */
	@Deprecated
	public YahooContact(String id) {
		this(id, YahooProtocol.YAHOO);
	}

	/**
	 * Get the protocol for the Contact
	 * @return protocol
	 */
	public YahooProtocol getProtocol() {
		return this.protocol;
	}

	/**
	 * Get the name of the Contact
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "Contact [name=" + name + ", protocol=" + protocol + "]";
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof YahooContact))
			return false;
		YahooContact other = (YahooContact) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (protocol != other.protocol)
			return false;
		return true;
	}

	@Override
	public int compareTo(YahooContact other) {
		if (this == other)
			return 0;
		if (other == null)
			return 1;
		else if (name.equals(other.name))
			return protocol.compareTo(other.protocol);
		else
			return name.compareTo(other.name);
	}
}
