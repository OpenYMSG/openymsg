package org.openymsg;

/**
 * Identity of a contact. This consists of two parts: id and protocol. Most of the time the protocol is
 * YahooProtocol.YAHOO.
 * 
 * @author neilhart
 */
public class Contact {
	/** unique name within a specific protocol */
	private String id;
	/** system the id is on. Mostly YahooProtocol.YAHOO */
	private YahooProtocol protocol;

	/**
	 * Create a new Contact
	 * @param id id of Contact
	 * @param protocol protocol of Contact
	 */
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

	/**
	 * Quick, and dirty, creator of Contact. Protocol is set to YahooProtocol.YAHOO
	 * @param id contact id
	 */
	@Deprecated
	public Contact(String id) {
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
	 * Get the id of the Contact
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Fix the protocol for a contact.  Should not be used.
	 * This changes the hash
	 * @param protocol new protocol
	 */
	@Deprecated
	public void setProtocol(YahooProtocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", protocol=" + protocol + "]";
	}

	/**
	 * Hash of the Contact using the id and protocol.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	/**
	 * Comparison of Contacts using id and protocol.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Contact other = (Contact) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		if (protocol != other.protocol) return false;
		return true;
	}
}
