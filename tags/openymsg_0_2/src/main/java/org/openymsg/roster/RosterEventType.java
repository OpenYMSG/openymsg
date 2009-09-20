package org.openymsg.roster;

/**
 * Types of roster events.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 */
public enum RosterEventType {
	/**
	 * A user has been added to the roster.
	 */
	add,

	/**
	 * A user has been deleted from the roster.
	 */
	remove,

	/**
	 * A user on the roster has been updated.
	 */
	update
}
