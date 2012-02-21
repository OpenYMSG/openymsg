package org.openymsg;

import java.util.Set;

/**
 * Conference information. A confernce has a unique id.
 * 
 * @author neilhart
 */
// TODO invited lists
public interface Conference {

	/**
	 * Unique id
	 * 
	 * @return id
	 */
	String getId();

	/**
	 * Set of contacts that are members of the conference. This returns an unmodifiable, copy of the internal set so the
	 * internal is thread-safe
	 * 
	 * @return members of the conference
	 */
	Set<Contact> getMembers();

}
