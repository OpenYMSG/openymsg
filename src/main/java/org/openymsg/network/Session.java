package org.openymsg.network;

import java.io.IOException;

import org.openymsg.network.event.SessionEvent;
import org.openymsg.network.event.SessionListener;
import org.openymsg.roster.Roster;

public interface Session<T extends Roster<? extends YahooUser>> extends StatusConstants, FriendManager{

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
}
