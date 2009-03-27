package org.openymsg.network;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.openymsg.network.event.SessionConferenceEvent;
import org.openymsg.network.event.SessionEvent;
import org.openymsg.network.event.SessionFileTransferEvent;
import org.openymsg.network.event.SessionListener;
import org.openymsg.roster.Roster;

public interface Session<T extends Roster<U>, U extends YahooUser> extends StatusConstants, FriendManager{

	boolean isValidYahooID(String username);

	void addSessionListener(SessionListener sessionListener);

	YahooIdentity getLoginIdentity();

	void login(String username, String password) throws IllegalStateException, IOException, AccountLockedException, LoginRefusedException;

	SessionState getSessionStatus();

	void logout() throws IllegalStateException, IOException;

	T getRoster();

	void sendMessage(String otherusr, String chatmessage) throws IllegalStateException, IOException;

	void sendMessage(String otherusr, String chatmessage, YahooIdentity loginIdentity) throws IllegalStateException, IOException, IllegalIdentityException;

	void sendTypingNotification(String otherusr, boolean b) throws IOException;

	Status getStatus();

	void setStatus(Status status) throws IllegalArgumentException, IOException;

	void setStatus(String message, boolean showBusyIcon) throws IllegalArgumentException, IOException;

	String getCustomStatusMessage();

	void rejectContact(SessionEvent event, String string) throws IllegalArgumentException, IllegalStateException, IOException;

	void sendFileTransfer(String contactName, File file, String fileDescription) throws IllegalStateException, FileTransferFailedException, IOException;

	void saveFileTransferAs(SessionFileTransferEvent connectionInfo, String absolutePath) throws IllegalStateException, FileTransferFailedException, IOException;

	void declineConferenceInvite(SessionConferenceEvent ev, String string) throws IllegalStateException, IOException, NoSuchConferenceException;

	void removeSessionListener(SessionListener sessionListener);

	void sendBuzz(String to) throws IllegalStateException, IOException;
	
	void reset() throws IllegalStateException;
	
	void removeFriendFromGroup(String friendId, String groupId) throws IOException;
	
	/**
	 * Creates a new anonymous user. This user is said to be anonymous, because
	 * it is not on our roster. Note that users that are on our roster MUST be
	 * in at least one group. Anonymous users most likely represent users from
	 * conferences.
	 * 
	 * @param userId
	 *            The ID of the user.
	 */
	U createUser(String userId);
	
	/**
	 * Creates a new user in one particular group. Note that the fact that the
	 * user is in a group indicates that this user is on our roster.
	 * 
	 * @param userId
	 *            The ID of the user
	 * @param groupId
	 *            The ID of the group in which this user has been put.
	 */
	U createUser(final String userId, final String groupId);

	Set<SessionListener> getSessionListeners();

	void transmitKeepAlive() throws IOException;
}
