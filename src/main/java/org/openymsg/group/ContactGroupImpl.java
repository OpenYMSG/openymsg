package org.openymsg.group;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.util.CollectionUtils;

public class ContactGroupImpl implements ContactGroup {
	private String name;
	boolean active;
	private Set<Contact> contacts = Collections.synchronizedSet(new HashSet<Contact>());

	public ContactGroupImpl(String name) {
		this.name = name;
	}

	public void add(Contact contact) {
		this.contacts.add(contact);
	}
	
	@Override
	public Set<Contact> getContacts() {
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
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ContactGroupImpl other = (ContactGroupImpl) obj;
		if (name == null) {
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		return true;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

}
