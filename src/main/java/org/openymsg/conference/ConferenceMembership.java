package org.openymsg.conference;

import java.util.Set;

import org.openymsg.Contact;

public interface ConferenceMembership {
	/**
	 * Set of contacts that are members of the conference. This returns an unmodifiable.
	 * @return members of the conference
	 */
	Set<Contact> getMembers();

	Set<Contact> getInvited();

	Set<Contact> getDeclineOrLeft();
}
