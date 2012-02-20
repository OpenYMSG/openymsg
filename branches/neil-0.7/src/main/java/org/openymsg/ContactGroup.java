package org.openymsg;

import java.util.Set;


public interface ContactGroup {

	void add(Contact yu);

	Set<Contact> getContacts();

	String getName();

}
