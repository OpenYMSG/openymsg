package org.openymsg.contact.group;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContactGroupImpl implements YahooContactGroup {
	private String name;
	boolean active;
	private Set<YahooContact> contacts = Collections.synchronizedSet(new HashSet<YahooContact>());

	public ContactGroupImpl(String name) {
		this.name = name;
	}

	public void add(YahooContact contact) {
		this.contacts.add(contact);
	}

	@Override
	public Set<YahooContact> getContacts() {
		return CollectionUtils.protectedSet(this.contacts);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactGroupImpl other = (ContactGroupImpl) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}
}
