package org.openymsg.conference;

import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.util.CollectionUtils;

public class ConferenceMembershipImpl implements ConferenceMembership {
	/** set of Contacts that are currently members */
	private Set<Contact> members;
	private Set<Contact> invited;
	private Set<Contact> declinedOrLeft;

	public ConferenceMembershipImpl(Set<Contact> members, Set<Contact> invited) {
		this.members = members;
		this.invited = invited;
	}

	@Override
	public Set<Contact> getMembers() {
		return CollectionUtils.unmodifiableSet(this.members);
	}

	@Override
	public Set<Contact> getInvited() {
		return CollectionUtils.unmodifiableSet(this.invited);
	}

	@Override
	public Set<Contact> getDeclineOrLeft() {
		return CollectionUtils.unmodifiableSet(this.declinedOrLeft);
	}

}
