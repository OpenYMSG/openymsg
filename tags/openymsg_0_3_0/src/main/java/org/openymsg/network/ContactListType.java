package org.openymsg.network;

/**
 * Distinct types of sets of contacts.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public enum ContactListType {

	/**
	 * People on the normal contact list of the user.
	 */
	Friends,

	/**
	 * People that are not allowed to see the user.
	 */
	StealthBlocked,

	/**
	 * People that the user does not want to hear from.
	 */
	Ignored,

	/**
	 * People that the user had not accepted add request.
	 */
	Pending,
}
