package org.openymsg.conference;

import java.util.Set;

import org.openymsg.Conference;
import org.openymsg.ConferenceStatus;

/**
 * Conference Services for Yahoo. 
 * Not sure if the conferenceId can contain non-alphanumeric characters.
 * Also unsure of Unicode characters for any of the String field.
 * Not sure about invited ids for MSN and other non-default Yahoo protocols
 * @author neilhart
 *
 */
public interface SessionConference {
	/**
	 * Create a conference and invite a set of yahoo ids.  The yahoo ids do not need to be in the your list of contacts.
	 * Either the conferenceId or the message will show as the conference name for the invited users.  
	 * @param conferenceId unique name for the conference.  Sometimes this shows as the conference name.
	 * @param invitedIds set of yahoo ids.  
	 * @param message invite message.  Sometimes this shows as the conference name.
	 * @return new Conference
	 */
	Conference createConference(String conferenceId, Set<String> invitedIds, String message) ;

	/**
	 * Send a message to the conference.
	 * @param conference conference to send the message
	 * @param message message to send to the conference
	 * @throws IllegalArgumentException if conference doesn't exist
	 */
	void sendConferenceMessage(Conference conference, String message) throws IllegalArgumentException;

	/**
	 * Leave a conference.  One of the members may re-invite you to the conference. 
	 * @param conference conference leave
	 * @throws IllegalArgumentException if conference doesn't exist
	 */
	void leaveConference(Conference conference) throws IllegalArgumentException;

	/**
	 * Accept a conference invite that was sent to you.
	 * @param conference conference in the invite
	 * @throws IllegalArgumentException if conference doesn't exist
	 */
	void acceptConferenceInvite(Conference conference) throws IllegalArgumentException;

	/**
	 * Decline a conference invite that was sent to you with a message
	 * @param conference conference in the invite
	 * @param message decline reason sent to the inviter, not the conference.  May be null
	 * @throws IllegalArgumentException if conference doesn't exist
	 */
	void declineConferenceInvite(Conference conference, String message) throws IllegalArgumentException;

	/**
	 * Invite another yahoo id to the conference with a messages.  Invited id does not need to be a contact.
	 * @param conference conference in the invite
	 * @param invitedId yahoo id
	 * @param message invite message.  May be null
	 * @throws IllegalArgumentException if conference doesn't exist
	 */
	void extendConference(Conference conference, String invitedId, String message) throws IllegalArgumentException;
	
	/**
	 * Get the Conference with the matching id.  Null if id is not matched.
	 * @param conferenceId conference id to find
	 * @return matching Conference, null if not found.
	 */
	Conference getConference(String conferenceId);
	
	/**
	 * get the ConferenceStatus with the matching id.  Null if id is not matched.
	 * @param conferenceId conference id to find
	 * @return matching ConferenceStatus, null if not found.
	 */
	ConferenceStatus getConferenceStatus(String conferenceId);
}
