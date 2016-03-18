package org.openymsg.conference;

import org.openymsg.YahooContact;

import java.util.Set;

/**
 * Information on the contacts that have participated in a Conference. Contacts should only be in one list.
 * @author neilhart
 */
public interface ConferenceMembership {
	/**
	 * Set of contacts that are members of the conference.
	 * @return contacts that are members of the conference
	 */
	Set<YahooContact> getMembers();

	/**
	 * Set of contacts that are invited to the conference.
	 * @return contacts that are invited
	 */
	Set<YahooContact> getInvited();

	/**
	 * Set of contacts that have declined an invite or left the conference.
	 * @return contacts that have left or declined
	 */
	Set<YahooContact> getDeclineOrLeft();
}
