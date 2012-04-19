package org.openymsg.conference;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.util.CollectionUtils;

/**
 * Information on membership of a YahooConference. It contains the contact that were invited, established membership,
 * left or declined. This info comes from messages from Yahoo, not from user requests. So if a user invites a contact,
 * it would be in the invited list until Yahoo sends out the conference invite message.
 * <P>
 * This class also does checking of state. Methods return a boolean that indicates success if there were no problem. If
 * there was a problem, it is logged. Methods should not throw an exception if there is a problem.
 * @author neilhart
 */
public class ConferenceMembershipImpl implements ConferenceMembership {
	private static final Log log = LogFactory.getLog(ConferenceMembershipImpl.class);
	/** set of Contacts that are currently members */
	private Set<YahooContact> memberContacts = new HashSet<YahooContact>();
	/** set of Contacts that are invited, but not yet members */
	private Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
	/** set of Contacts that were members that left or declined an invitation */
	private Set<YahooContact> declinedOrLeftContacts = new HashSet<YahooContact>();

	/**
	 * Add a set of contacts to the invited list. Contacts are removed from the declinedOrLeft list if they is there.
	 * @param invited contacts that were invited
	 * @return true if no issues where found with the internal state
	 */
	public boolean addInvited(Set<YahooContact> invited) {
		boolean successful = true;
		for (YahooContact yahooContact : invited) {
			// TODO don't like this
			successful = addInvited(yahooContact) && successful;
		}
		return successful;
	}

	/**
	 * Add a contact to the invited list. Removed from the declinedOrLeft list if it is there.
	 * @param invited invited contact
	 * @return true if no issues where found with the internal state
	 */
	public boolean addInvited(YahooContact invited) {
		boolean successful = true;
		if (this.invitedContacts.contains(invited)) {
			successful = false;
			log.warn("Contact invited that was already invited: " + invited);
		}
		if (this.memberContacts.contains(invited)) {
			successful = false;
			log.warn("Contact invited that was already a member: " + invited);
		}
		this.invitedContacts.add(invited);
		removeFromDeclinedOrLeft(invited);
		return successful;
	}

	/**
	 * Add a set of contacts to the member list. Contacts are removed from the declinedOrLeft list if they is there.
	 * @param members contacts that are members
	 * @return true if no issues where found with the internal state
	 */
	public boolean addMember(Set<YahooContact> members) {
		boolean successful = true;
		for (YahooContact yahooContact : members) {
			// TODO don't like this
			successful = addMember(yahooContact) && successful;
		}
		return successful;
	}

	/**
	 * Add a contact to the member list. Removed from the declinedOrLeft list if it is there.
	 * @param member member contact
	 * @return true if no issues where found with the internal state
	 */
	public boolean addMember(YahooContact member) {
		boolean successful = true;
		if (memberContacts.contains(member)) {
			successful = false;
			log.warn("Adding a contact as a member that was already a member: " + member);
		}
		memberContacts.add(member);
		removeFromInvited(member);
		removeFromDeclinedOrLeft(member);
		return successful;
	}

	/**
	 * Add a contact to the declineOrLeft list. Removed from the invited list.
	 * @param decline declining contact
	 * @return true if no issues where found with the internal state
	 */
	public boolean addDecline(YahooContact decline) {
		boolean successful = true;
		log.debug("declined from invited: " + decline);
		boolean removed = this.invitedContacts.remove(decline);
		if (!removed) {
			successful = false;
			log.warn("Declined contact not invited: " + decline);
		}
		removed = this.memberContacts.remove(decline);
		if (removed) {
			successful = false;
			log.warn("Declined contact was a member: " + decline);
		}
		return successful;
	}

	/**
	 * Add a contact to the declineOrLeft list. Removed from the member list.
	 * @param left leaving contact
	 * @return true if no issues where found with the internal state
	 */
	public boolean addLeft(YahooContact left) {
		boolean successful = true;
		log.debug("left from member: " + left);
		boolean removed = this.memberContacts.remove(left);
		if (!removed) {
			successful = false;
			log.warn("Left contact not a member" + left);
		}
		removed = this.invitedContacts.remove(left);
		if (removed) {
			successful = false;
			log.warn("Left contact was invited: " + left);
			this.memberContacts.remove(left);
		}
		return successful;
	}

	@Override
	public Set<YahooContact> getMembers() {
		return CollectionUtils.unmodifiableSet(this.memberContacts);
	}

	@Override
	public Set<YahooContact> getInvited() {
		return CollectionUtils.unmodifiableSet(this.invitedContacts);
	}

	@Override
	public Set<YahooContact> getDeclineOrLeft() {
		return CollectionUtils.unmodifiableSet(this.declinedOrLeftContacts);
	}

	/**
	 * Remove from declined or left if the contact is there. It is OK if the contact is not in the collection.
	 * @param yahooContact
	 */
	private void removeFromDeclinedOrLeft(YahooContact yahooContact) {
		boolean removed = this.declinedOrLeftContacts.remove(yahooContact);
		if (removed) {
			log.debug("contact removed from declinedOrLeft: " + yahooContact);
		}
	}

	/**
	 * Remove from invited if the contact is there. It is OK if the contact is not in the collection.
	 * @param yahooContact
	 */
	private void removeFromInvited(YahooContact yahooContact) {
		boolean removed = this.invitedContacts.remove(yahooContact);
		if (removed) {
			log.debug("contact removed from invited: " + yahooContact);
		}
	}

}
