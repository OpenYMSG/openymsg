package org.openymsg.contact;

import java.util.HashSet;
import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.Status;
import org.openymsg.YahooProtocol;

public class ContactImpl implements Contact {
	private Set<String> groupIds = new HashSet<String>();
	private YahooProtocol protocol;
	private final String id;
	private boolean ignored;
	private Status status;
	private Object customStatusMessage;
	private Object customStatus;

	public ContactImpl(String id, String groupId, YahooProtocol protocol) {
		this(id, protocol);
		this.addGroupId(groupId);
	}

	public ContactImpl(String id, YahooProtocol protocol) {
		this.id = id;
		this.protocol = protocol;
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

	public Set<String> getGroupIds() {
		return groupIds;
	}

	public void addGroupId(String groupId) {
		this.groupIds.add(groupId);
	}

	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public void setStatus(Status status) {
		this.status = status;
        if (this.status != Status.CUSTOM) {
            this.customStatusMessage = null;
            this.customStatus = null;
        }
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ContactImpl other = (ContactImpl) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		if (protocol != other.protocol) return false;
		return true;
	}

	@Override
	public String toString() {
		return "ContactImpl [groupIds=" + groupIds + ", protocol=" + protocol + ", id=" + id + ", ignored=" + ignored
				+ ", status=" + status + ", customStatusMessage=" + customStatusMessage + ", customStatus="
				+ customStatus + "]";
	}
}
