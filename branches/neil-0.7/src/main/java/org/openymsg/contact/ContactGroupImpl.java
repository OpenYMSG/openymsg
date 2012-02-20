package org.openymsg.contact;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;

public class ContactGroupImpl implements ContactGroup {
	private String name;
	private Set<Contact> contacts = new HashSet<Contact>();

	public ContactGroupImpl(String name) {
		this.name = name;
	}

	@Override
	public void add(Contact contact) {
		this.contacts.add(contact);
	}
	
	@Override
	public Set<Contact> getContacts() {
		return Collections.unmodifiableSet(this.contacts);
	}

	@Override
	public String getName() {
		return this.name;
	}

}
