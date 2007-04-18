/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openymsg.network;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jdom.JDOMException;
import org.openymsg.network.chatroom.ChatroomManager;
import org.openymsg.network.chatroom.YahooChatLobby;
import org.openymsg.network.chatroom.YahooChatUser;
import org.openymsg.network.event.SessionChatEvent;
import org.openymsg.network.event.SessionConferenceEvent;
import org.openymsg.network.event.SessionErrorEvent;
import org.openymsg.network.event.SessionEvent;
import org.openymsg.network.event.SessionExceptionEvent;
import org.openymsg.network.event.SessionFileTransferEvent;
import org.openymsg.network.event.SessionFriendEvent;
import org.openymsg.network.event.SessionGroupEvent;
import org.openymsg.network.event.SessionListener;
import org.openymsg.network.event.SessionNewMailEvent;
import org.openymsg.network.event.SessionNotifyEvent;

/**
 * Written by FISH, Feb 2003 , Copyright FISH 2003 - 2007
 * 
 * This class represents the main entry point into the YMSG9 API. A Session
 * represents one IM connection.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class Session implements StatusConstants {
	/** Primary Yahoo ID: the real account id. */
	private YahooIdentity primaryID;

	/** Login Yahoo ID: we logged in under this. */
	private YahooIdentity loginID;

	/** Map of alternative identities that can be used by this user. */
	private final Map<String, YahooIdentity> identities = new HashMap<String, YahooIdentity>();

	/** Yahoo user password. */
	private String password;

	private String cookieY, cookieT, cookieC;

	/** IMvironment (decor, etc.) */
	private String imvironment;

	/** Yahoo status (presence) */
	private Status status;

	/** Message for custom status. */
	private String customStatusMessage;

	/** Available/Back=f, away=t */
	private boolean customStatusBusy;

	/** Yahoo user's groups */
	private final Set<YahooGroup> groups = new HashSet<YahooGroup>();

	/** Creating conference room names. */
	private int conferenceCount;

	/** Canonical (we hope) set of YahooUsers. */
	UserStore userStore;

	/** Status of session (see StatusConstants) */
	private volatile SessionState sessionStatus;

	/** Holds Yahoo's session id */
	volatile long sessionId = 0;

	public volatile ConnectionHandler network;

	private static final ScheduledExecutorService SCHEDULED_PINGER_SERVICE = new ScheduledThreadPoolExecutor(0);

	private ScheduledFuture<?> pingerFuture;

	private InputThread ipThread;

	private final Hashtable<String, TypingNotifier> typingNotifiers = new Hashtable<String, TypingNotifier>();

	private EventDispatcher eventDispatchQueue;

	private YahooException loginException = null;

	/** For split packets in multiple parts */
	private YMSG9Packet cachePacket;

	private final ChatroomManager chatroomManager;

	/** Current conferences, hashed on room */
	private final Hashtable<String, YahooConference> conferences = new Hashtable<String, YahooConference>();

	private SessionState chatSessionStatus;

	private volatile YahooChatLobby currentLobby = null;

	private YahooIdentity chatID;

	/**
	 * Creates a new Session based on a ConnectionHandler as configured in the
	 * current System properties.
	 * 
	 * @throws IOException
	 * @throws JDOMException
	 * @throws MalformedURLException
	 * @throws NumberFormatException
	 */
	public Session() throws NumberFormatException {
		this(null);
	}

	/**
	 * Creates a new Session based on the provided ConnectionHandler. If the
	 * attribute is 'null', a ConnectionHandler as configured in the current
	 * System properties will be used.
	 * 
	 * @param connectionHandler
	 *            The ConnectionHandler that backs the connection to the Yahoo
	 *            Network.
	 * @throws IOException
	 * @throws JDOMException
	 * @throws MalformedURLException
	 * @throws NumberFormatException
	 */
	public Session(ConnectionHandler connectionHandler)
			throws NumberFormatException {
		if (connectionHandler != null) {
			network = connectionHandler;
		} else {
			Properties p = System.getProperties();
			if (p.containsKey(NetworkConstants.SOCKS_HOST)) {
				network = new SOCKSConnectionHandler();
			} else if (p.containsKey(NetworkConstants.PROXY_HOST)
					|| p.containsKey(NetworkConstants.PROXY_HOST_OLD)) {
				network = new HTTPConnectionHandler();
			} else {
				network = new DirectConnectionHandler();
			}
		}

		chatroomManager = new ChatroomManager(null, null);
		eventDispatchQueue = new EventDispatcher();
		eventDispatchQueue.start();
		status = Status.AVAILABLE;
		sessionId = 0;
		sessionStatus = SessionState.UNSTARTED;
		userStore = new UserStore();

		network.install(this);

	}

	/**
	 * Adds a session listener to the collection of listeners to which events
	 * are dispatched.
	 * 
	 * @param sessionListener
	 *            SessionListener to be added.
	 */
	public void addSessionListener(SessionListener sessionListener) {
		if (sessionListener == null) {
			throw new IllegalArgumentException(
					"Argument 'sessionListener' cannot be null.");
		}
		eventDispatchQueue.addSessionListener(sessionListener);
	}

	/**
	 * Removes the listener from the collection of listeners to which events are
	 * dispatched.
	 * 
	 * @param sessionListener
	 *            The SessionListener to be removed
	 */
	public void removeSessionListener(SessionListener sessionListener) {
		if (sessionListener == null) {
			throw new IllegalArgumentException(
					"Argument 'sessionListener' cannot be null.");
		}
		eventDispatchQueue.removeSessionListener(sessionListener);
	}

	/**
	 * Returns the handler used to send/receive messages from the network
	 */
	public ConnectionHandler getConnectionHandler() {
		return network;
	}

	/**
	 * Call this to connect to the Yahoo server and do all the initial
	 * handshaking and accepting of data
	 * 
	 * @param username
	 *            Yahoo id
	 * @param password
	 *            password
	 */
	public void login(String username, String password)
			throws IllegalStateException, IOException, AccountLockedException,
			LoginRefusedException {
		if (username == null || username.length() == 0) {
			sessionStatus = SessionState.FAILED;
			throw new IllegalArgumentException(
					"Argument 'username' cannot be null or an empty String.");
		}

		if (password == null || password.length() == 0) {
			sessionStatus = SessionState.FAILED;
			throw new IllegalArgumentException(
					"Argument 'password' cannot be null or an empty String.");
		}

		// Check the session status first
		synchronized (this) {
			if (sessionStatus != SessionState.UNSTARTED) {
				throw new IllegalStateException("Session should be unstarted");
			}
			sessionStatus = SessionState.CONNECTING;
		}

		// Yahoo ID's are apparently always lower case
		username = username.toLowerCase();

		// Reset session and init some variables
		resetData();
		loginID = new YahooIdentity(username);
		primaryID = null;
		this.password = password;
		sessionId = 0;
		imvironment = "0";
		try {
			// Create the socket and threads (ipThread, sessionPingRunnable and
			// maybe eventDispatchQueue)
			openSession();

			// Begin login process
			transmitAuth();

			// Wait until connection or timeout
			long timeout = System.currentTimeMillis()
					+ Util.loginTimeout(NetworkConstants.LOGIN_TIMEOUT);
			while (sessionStatus != SessionState.LOGGED_ON
					&& sessionStatus != SessionState.FAILED && !past(timeout)) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// ignore
				}
			}

			if (past(timeout)) {
				sessionStatus = SessionState.FAILED;
				throw new InterruptedIOException("Login timed out");
			}

			if (sessionStatus == SessionState.FAILED) {
				throw (LoginRefusedException) loginException;
			}
		} finally {
			// Logon failure? When network input finishes, all supporting
			// threads are stopped.
			if (sessionStatus != SessionState.LOGGED_ON)
				closeSession();
		}
	}

	/**
	 * Logs off the current session.
	 * 
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public synchronized void logout() throws IllegalStateException, IOException {
		checkStatus();
		sessionStatus = SessionState.UNSTARTED;
		transmitLogoff();
		network.close();
	}

	/**
	 * Reset a failed session, so the session object can be used again (for all
	 * those who like to minimise the number of discarded objects for the GC to
	 * clean up ;-)
	 */
	public synchronized void reset() throws IllegalStateException {
		if (sessionStatus != SessionState.FAILED
				&& sessionStatus != SessionState.UNSTARTED)
			throw new IllegalStateException("Session currently active");
		sessionStatus = SessionState.UNSTARTED;
		chatSessionStatus = SessionState.UNSTARTED;
		resetData(); // Just to be on the safe side
	}

	/**
	 * Send a chat message.
	 * 
	 * @param to
	 *            Yahoo ID of the user to transmit the message.
	 * @param message
	 *            The message to transmit.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void sendMessage(String to, String message)
			throws IllegalStateException, IOException {
		sendMessage(to, message, loginID);
	}

	/**
	 * Send a chat message
	 * 
	 * @param to
	 *            Yahoo ID of the user to transmit the message.
	 * @param message
	 *            The message to transmit.
	 * @param yid
	 *            Yahoo Identity used to transmit the message from.
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws IllegalIdentityException
	 */
	public void sendMessage(String to, String message, YahooIdentity yid)
			throws IllegalStateException, IOException, IllegalIdentityException {
		checkStatus();

		if (!identities.containsKey(yid.getId())) {
			throw new IllegalIdentityException("The YahooIdentity '"
					+ yid.getId()
					+ "'is not a valid identity for this session.");
		}
		transmitMessage(to, yid, message);
	}

	/**
	 * Send a buzz message
	 * 
	 * @param to
	 *            Recipient of the buzz.
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void sendBuzz(String to) throws IllegalStateException, IOException {
		sendMessage(to, NetworkConstants.BUZZ);
	}

	/**
	 * Send a buzz message
	 * 
	 * @param to
	 *            Recipient of the buzz.
	 * @param yid
	 *            Yahoo Identity used to send the buzz.
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws IllegalIdentityException
	 */
	public void sendBuzz(String to, YahooIdentity yid)
			throws IllegalStateException, IOException, IllegalIdentityException {
		sendMessage(to, NetworkConstants.BUZZ, yid);
	}

	/**
	 * Get the status of the session, ie: unstarted, authenticating, etc. Check
	 * this after login() to find out if you've connected to Yahoo okay.
	 * 
	 * @return Current Status of this session object.
	 */
	public SessionState getSessionStatus() {
		return sessionStatus;
	}

	/**
	 * Get the Yahoo status, ie: available, invisible, busy, not at desk, etc.
	 * 
	 * @return Current presence status of the user.
	 */
	public synchronized Status getStatus() {
		return status;
	}

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc.
	 * Legit values are in the StatusConstants interface. If you want to login
	 * as invisible, set this to Status.INVISIBLE before you call login() Note:
	 * setter is overloaded, the second version is intended for use when setting
	 * custom status messages. The boolean is true if available and false if
	 * away.
	 * 
	 * @param status
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public synchronized void setStatus(Status status)
			throws IllegalArgumentException, IOException {
		if (sessionStatus == SessionState.UNSTARTED
				&& !(status == Status.AVAILABLE || status == Status.INVISIBLE)) {
			throw new IllegalArgumentException(
					"Unstarted sessions can be available or invisible only");
		}

		if (status == Status.CUSTOM) {
			throw new IllegalArgumentException(
					"Cannot set custom state without message");
		}
		this.status = status;
		customStatusMessage = null;

		if (sessionStatus == SessionState.LOGGED_ON) {
			_doStatus();
		}
	}

	/**
	 * Sets the Yahoo status, ie: available, invisible, busy, not at desk, etc.
	 * Legit values are in the StatusConstants interface. If you want to login
	 * as invisible, set this to Status.INVISIBLE before you call login() Note:
	 * setter is overloaded, the second version is intended for use when setting
	 * custom status messages. The boolean is true if available and false if
	 * away.
	 * 
	 * @param message
	 * @param b
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public synchronized void setStatus(String message, boolean b)
			throws IllegalArgumentException, IOException {
		if (sessionStatus == SessionState.UNSTARTED) {
			throw new IllegalArgumentException(
					"Unstarted sessions can be available or invisible only");
		}

		if (message == null) {
			throw new IllegalArgumentException(
					"Cannot set custom state with null message");
		}

		status = Status.CUSTOM;
		customStatusMessage = message;
		customStatusBusy = b;
		_doStatus();
	}

	private void _doStatus() throws IllegalStateException, IOException {
		if (status == Status.AVAILABLE)
			transmitIsBack();
		else if (status == Status.CUSTOM)
			transmitIsAway(customStatusMessage, customStatusBusy);
		else
			transmitIsAway();
	}

	public String getCustomStatusMessage() {
		return customStatusMessage;
	}

	public boolean isCustomBusy() {
		return customStatusBusy;
	}

	/**
	 * Ask server to return refreshed stats for this session. Server will send
	 * back a USERSTAT and truncated NEWMAIL packet.
	 */
	public void refreshStats() throws IllegalStateException, IOException {
		checkStatus();
		transmitUserStat();
	}

	/**
	 * Checks if the provided ID matches one of the identities of the user of
	 * this session.
	 * 
	 * @param yahooIdentity
	 *            The identity to check for.
	 * @return ''true'' if the ID matches one of the IDs belonging to this user.
	 */
	public boolean isValidYahooID(String yahooIdentity) {
		return identities.containsKey(yahooIdentity);
	}

	/**
	 * Return the primary Yahoo Identity of this session.
	 * 
	 * @return This users primary identity.
	 */
	public YahooIdentity getPrimaryIdentity() {
		return primaryID;
	}

	/**
	 * Returns the Yahoo Identity that was used to login to the network.
	 * 
	 * @return YahooIdentity used to login with.
	 */
	public YahooIdentity getLoginIdentity() {
		return loginID;
	}

	/**
	 * Activate or deactivate a particular Yahoo Identity.
	 * 
	 * @param yid
	 * @param activate
	 * @throws IllegalStateException
	 * @throws IllegalIdentityException
	 * @throws IOException
	 */
	public void activateIdentity(YahooIdentity yid, boolean activate)
			throws IllegalStateException, IllegalIdentityException, IOException {
		checkStatus();

		if (!identities.containsKey(yid.getId())) {
			throw new IllegalIdentityException("The YahooIdentity '"
					+ yid.getId()
					+ "'is not a valid identity for this session.");
		}

		// Make an exception of the primary identity
		if (yid.equals(primaryID)) {
			throw new IllegalIdentityException(
					"Primary identity cannot be de/activated");
		}
		// Send message
		if (activate)
			transmitIdActivate(yid.getId());
		else
			transmitIdDeactivate(yid.getId());
	}

	/**
	 * Add/remove source AWT text component to use for TYPING packets sent to
	 * the specified user, from a given identity. Each notifier is tied to both
	 * target and source. Note: this method has now been changed so it no longer
	 * needs an AWT component. 'com' can be null, with the API user employing
	 * keyTyped() to manually send key strokes.
	 * 
	 * There's serious bug in Yahoo which means that even if you send a typing
	 * notify packet with an alternate identity in it, Yahoo always delivers a
	 * packet with the primary id. (Security/privacy bug?) For this reason these
	 * methods do not *yet* support id's - however, as you'll see, the code is
	 * all ready for an extra 'syid' parameter.
	 * 
	 * @param user
	 * @param com
	 */
	public void addTypingNotification(String user, Component com) {
		/* if(syid==null) syid=loginID */
		final String syid = primaryID.getId();
		final String key = "user" + "\n" + syid;
		synchronized (typingNotifiers) {
			if (typingNotifiers.containsKey(key))
				return; // Aleady registered
			typingNotifiers.put(key, new TypingNotifier(com, user, syid));
		}
	}

	public void removeTypingNotification(String user) {
		/* if(syid==null) syid=loginID */
		final String syid = primaryID.getId();
		final String key = "user" + "\n" + syid;
		synchronized (typingNotifiers) {
			final TypingNotifier tn = typingNotifiers.get(key);
			if (tn == null)
				return;
			tn.quit = true;
			tn.interrupt();
			typingNotifiers.remove(key);
		}
	}

	public void keyTyped(String user) {
		/* if(syid==null) yid=loginID */
		final String syid = primaryID.getId();
		final String key = "user" + "\n" + syid;
		final TypingNotifier tn = typingNotifiers.get(key);
		if (tn != null)
			tn.keyTyped();
	}

	/**
	 * Return lists for friends tree menu
	 */
	public Set<YahooGroup> getGroups() {
		return groups;
	}

	public Hashtable<String, YahooUser> getUsers() {
		return new Hashtable<String, YahooUser>(userStore.getUsers());
	}

	public YahooUser getUser(String id) {
		return userStore.get(id);
	}

	/**
	 * General accessors
	 */
	public String getImvironment() {
		return imvironment;
	}

	public String[] getCookies() {
		String[] arr = new String[3];
		arr[NetworkConstants.COOKIE_Y] = cookieY;
		arr[NetworkConstants.COOKIE_T] = cookieT;
		arr[NetworkConstants.COOKIE_C] = cookieC;
		return arr;
	}

	/**
	 * Conference code
	 */
	public YahooConference createConference(String[] users, String msg)
			throws IllegalStateException, IOException, IllegalIdentityException {
		for (int i = 0; i < users.length; i++) {
			if (primaryID.getId().equals(users[i])
					|| loginID.getId().equals(users[i])
					|| identities.containsKey(users[i])) {
				throw new IllegalIdentityException(
						users[i]
								+ " is an identity of this session and cannot be used here");
			}
		}
		return createConference(users, msg, loginID);
	}

	public YahooConference createConference(String[] users, String msg,
			YahooIdentity yid) throws IllegalStateException, IOException,
			IllegalIdentityException {
		checkStatus();

		if (!identities.containsKey(yid.getId())) {
			throw new IllegalIdentityException("The YahooIdentity '"
					+ yid.getId()
					+ "'is not a valid identity for this session.");
		}

		for (int i = 0; i < users.length; i++) {
			if (primaryID.getId().equals(users[i])
					|| loginID.getId().equals(users[i])
					|| identities.containsKey(users[i])) {
				throw new IllegalIdentityException(
						users[i]
								+ " is an identity of this session and cannot be used here");
			}
		}

		String r = getConferenceName(yid.getId());
		transmitConfInvite(users, yid.getId(), r, msg);
		return getConference(r);
	}

	public void acceptConferenceInvite(SessionConferenceEvent ev)
			throws IllegalStateException, IOException,
			NoSuchConferenceException {
		YahooConference room = ev.getRoom();
		checkStatus();
		transmitConfLogon(room.getName(), room.getIdentity().getId());
	}

	public void declineConferenceInvite(SessionConferenceEvent ev, String msg)
			throws IllegalStateException, IOException,
			NoSuchConferenceException {
		YahooConference room = ev.getRoom();
		checkStatus();
		transmitConfDecline(room.getName(), room.getIdentity().getId(), msg);
	}

	public void extendConference(YahooConference room, YahooIdentity user,
			String msg) throws IllegalStateException, IOException,
			NoSuchConferenceException, IllegalIdentityException {
		checkStatus();

		final String id = user.getId();
		if (primaryID.getId().equals(id) || loginID.getId().equals(id)
				|| identities.containsKey(id)) {
			throw new IllegalIdentityException(id
					+ " is an identity of this session and cannot be used here");
		}
		transmitConfAddInvite(user, room.getName(), room.getIdentity().getId(),
				msg);
	}

	public void sendConferenceMessage(YahooConference room, String msg)
			throws IllegalStateException, IOException,
			NoSuchConferenceException {
		checkStatus();
		transmitConfMsg(room.getName(), room.getIdentity().getId(), msg);
	}

	public void leaveConference(YahooConference room)
			throws IllegalStateException, IOException,
			NoSuchConferenceException {
		checkStatus();
		transmitConfLogoff(room.getName(), room.getIdentity().getId());
	}

	/**
	 * Friends code
	 */
	public void addFriend(String friend, String group)
			throws IllegalStateException, IOException {
		checkStatus();
		transmitFriendAdd(friend, group);
	}

	public void removeFriend(String friend, String group)
			throws IllegalStateException, IOException {
		checkStatus();
		transmitFriendRemove(friend, group);
	}

	public void renameGroup(String oldName, String newName)
			throws IllegalStateException, IOException {
		checkStatus();
		transmitGroupRename(oldName, newName);
	}

	public void rejectContact(SessionEvent se, String msg)
			throws IllegalArgumentException, IllegalStateException, IOException {
		if (se.getFrom() == null || se.getTo() == null)
			throw new IllegalArgumentException(
					"Missing to or from field in event object.");
		checkStatus();
		transmitContactReject(se.getFrom(), se.getTo(), msg);
	}

	public void ignoreContact(String friend, boolean ignore)
			throws IllegalStateException, IOException {
		checkStatus();
		transmitContactIgnore(friend, ignore);
	}

	public void refreshFriends() throws IllegalStateException, IOException {
		checkStatus();
		transmitList();
	}

	/**
	 * File transfer 'save as' saves to a particular directory and filename,
	 * 'save to' uses the file's own name and saves it to a particular
	 * directory. Note: the 'to' method gets its filename from different
	 * sources. Initially the URL filename (minus the path), however the header
	 * Content-Disposition will override this. The 'as' method always uses its
	 * own specified filename. If both _path_ and _filename_ are not null then
	 * the saveFT() method assumes 'to'... but if _path_ is null, saveFT()
	 * assumes 'as' and _filename_ is the entire path+filename.
	 */
	public void sendFileTransfer(String user, File file, String msg)
			throws IllegalStateException, FileTransferFailedException,
			IOException {
		checkStatus();
		transmitFileTransfer(user, msg, file);
	}

	public void saveFileTransferAs(SessionFileTransferEvent ev, String filename)
			throws FileTransferFailedException, IOException {
		saveFT(ev, null, filename);
	}

	public void saveFileTransferTo(SessionFileTransferEvent ev, String dir)
			throws FileTransferFailedException, IOException {
		// Yahoo encodes the filename into the URL, but allow for
		// Content-Disposition header override.
		if (!dir.endsWith(File.separator))
			dir = dir + File.separator;
		saveFT(ev, dir, ev.getFilename());
	}

	private void saveFT(SessionFileTransferEvent ev, String path,
			String filename) throws FileTransferFailedException, IOException {
		int len;
		byte[] buff = new byte[4096];

		// HTTP request
		HttpURLConnection uConn = (HttpURLConnection) (ev.getLocation()
				.openConnection());
		Util.initURLConnection(uConn);
		uConn.setRequestProperty("User-Agent", NetworkConstants.USER_AGENT);
		// uConn.setRequestProperty("Host",ftHost);
		uConn.setRequestProperty("Cookie", cookieY + "; " + cookieT);
		uConn.connect();

		// Response header
		if (uConn.getResponseCode() != 200)
			throw new FileTransferFailedException("Server HTTP error code: "
					+ uConn.getResponseCode());
		String rp = uConn.getHeaderField("Content-Disposition");
		if (path != null && rp != null) {
			int i = rp.indexOf("filename=");
			if (i >= 0)
				filename = rp.substring(i + 9);
			// Strip quotes if necessary
			if (filename.charAt(0) == '\"')
				filename = filename.substring(1, filename.length() - 1);
		}

		// Response body
		if (path != null)
			filename = path + filename;
		InputStream is = uConn.getInputStream();
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filename));
		try {
			do {
				len = is.read(buff);
				if (len > 0)
					bos.write(buff, 0, len);
			} while (len >= 0);
			bos.flush();
		} finally {
			bos.close();
			is.close();
		}
		uConn.disconnect();
	}

	/**
	 * Logs the current default identity of this session into the provided
	 * lobby.
	 * 
	 * @param lobby
	 *            Lobby to login to.
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws LoginRefusedException
	 */
	public void chatLogin(YahooChatLobby lobby) throws IllegalStateException,
			IOException, LoginRefusedException {
		chatLogin(lobby, loginID);
	}

	/**
	 * Logs the provided yahoo identity into the provided lobby.
	 * 
	 * @param lobby
	 *            Lobby to login to.
	 * @param yahooId
	 *            Yahoo Identity that should login to the lobby.
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws LoginRefusedException
	 * @throws IllegalIdentityException
	 */
	public void chatLogin(YahooChatLobby lobby, YahooIdentity yahooId)
			throws IllegalStateException, IOException, LoginRefusedException,
			IllegalIdentityException {
		checkStatus();

		if (!identities.containsKey(yahooId.getId())) {
			throw new IllegalIdentityException("The YahooIdentity '"
					+ yahooId.getId()
					+ "'is not a valid identity for this session.");
		}

		synchronized (this) {
			if (chatSessionStatus != SessionState.UNSTARTED
					&& chatSessionStatus != SessionState.LOGGED_ON) {
				throw new IllegalStateException(
						"Chat session should be unstarted or messaging. You can't login to two chatrooms at the same time. Wait for one login to complete before connecting to the next one.");
			}
			chatSessionStatus = SessionState.CONNECTING;
		}

		final long timeout = System.currentTimeMillis()
				+ Util.loginTimeout(NetworkConstants.LOGIN_TIMEOUT);
		chatID = yahooId;

		try {
			transmitChatConnect(chatID.getId());
			while (chatSessionStatus != SessionState.CONNECTED
					&& !past(timeout)) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			if (past(timeout)) {
				throw new InterruptedIOException("Chat connect timed out");
			}

			// Transmit 'login' packet and wait for acknowledgement
			transmitChatJoin(lobby.getNetworkName(), lobby.getParentRoomId());
			while (chatSessionStatus == SessionState.CONNECTED
					&& !past(timeout)) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// ignore
				}
			}

			if (past(timeout)) {
				throw new InterruptedIOException("Chat login timed out");
			}

			if (chatSessionStatus == SessionState.FAILED) {
				throw (LoginRefusedException) loginException;
			}

			// Successful?
			if (chatSessionStatus == SessionState.LOGGED_ON) {
				currentLobby = lobby;
			} else {
				currentLobby = null;
			}
		} finally {
			if (chatSessionStatus != SessionState.LOGGED_ON) {
				chatSessionStatus = SessionState.FAILED;
				chatID = null;
			}
		}
	}

	public synchronized void chatLogout() throws IllegalStateException,
			IOException {
		checkStatus();
		checkChatStatus();
		transmitChatDisconnect(currentLobby.getNetworkName());
		currentLobby = null;
	}

	public synchronized void extendChat(String to, String msg)
			throws IllegalStateException, IOException {
		checkStatus();
		checkChatStatus();
		final String netName = currentLobby.getNetworkName();
		final long roomId = currentLobby.getParentRoomId();
		transmitChatInvite(netName, roomId, to, msg);
	}

	public void sendChatMessage(String msg) throws IllegalStateException,
			IOException {
		checkStatus();
		checkChatStatus();
		transmitChatMsg(currentLobby.getNetworkName(), msg, false);
	}

	public void sendChatEmote(String emote) throws IllegalStateException,
			IOException {
		checkStatus();
		checkChatStatus();
		transmitChatMsg(currentLobby.getNetworkName(), emote, true);
	}

	public YahooChatLobby getCurrentChatLobby() {
		return currentLobby;
	}

	public SessionState getChatSessionStatus() {
		return chatSessionStatus;
	}

	public void resetChat() throws IllegalStateException {
		if (chatSessionStatus != SessionState.FAILED
				&& chatSessionStatus != SessionState.UNSTARTED)
			throw new IllegalStateException("Chat session currently active");
		chatSessionStatus = SessionState.UNSTARTED;
	}

	/**
	 * Transmit an AUTH packet, as a way of introduction to the server. As we do
	 * not know our primary ID yet, both 0 and 1 use loginID .
	 */
	protected void transmitAuth() throws IOException {
		if (sessionStatus != SessionState.CONNECTING) {
			throw new IllegalStateException(
					"Cannot transmit an AUTH packet if you're not completely unconnected to the Yahoo Network. Current state: "
							+ sessionStatus);
		}

		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("0", loginID.getId()); // FIX: only req. for
		// HTTPConnectionHandler ?
		body.addElement("1", loginID.getId());
		sendPacket(body, ServiceType.AUTH); // 0x57
	}

	/**
	 * Transmit an AUTHRESP packet, the second part of our login process. As we
	 * do not know our primary ID yet, both 0 and 1 use loginID . Note: message
	 * also contains our initial status. plp - plain response (not MD5Crypt'd)
	 * crp - crypted response (MD5Crypt'd)
	 */
	protected void transmitAuthResp(String plp, String crp) throws IOException {
		if (sessionStatus != SessionState.CONNECTED) {
			throw new IllegalStateException(
					"Cannot transmit an AUTHRESP packet if you're not completely unconnected to the Yahoo Network. Current state: "
							+ sessionStatus);
		}
		sessionStatus = SessionState.CONNECTED;

		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("0", loginID.getId());
		body.addElement("6", plp);
		body.addElement("96", crp);
		body.addElement("135", "6,0,0,1710"); // Needed for v12(?)
		body.addElement("2", "1");
		body.addElement("1", loginID.getId());
		sendPacket(body, ServiceType.AUTHRESP, status); // 0x54
	}

	/**
	 * Transmit a CHATCONNECT packet. We send one of these as the first packet
	 * to the chat server - by way of introduction. The server responds with its
	 * own 0x96 packet back at us, and then we can logon.
	 */
	protected void transmitChatConnect(String yid) throws IOException {
		chatSessionStatus = SessionState.CONNECTING; // Set status
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("109", primaryID.getId());
		body.addElement("1", yid);
		body.addElement("6", "abcde"); // FIX: what is this?
		sendPacket(body, ServiceType.CHATCONNECT); // 0x96
	}

	/**
	 * Sends a packet requesting chat room creation
	 */
	protected void transmitChatCreate(long catId, String rname, String topic,
			boolean pub) throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", chatID.getId());
		body.addElement("104", rname); // Room name
		body.addElement("105", topic);
		body.addElement("126", "0"); // Fix: what is this?
		body.addElement("128", catId + "");
		body.addElement("129", catId + "");
		body.addElement("62", "2"); // Fix: what is this?
		sendPacket(body, ServiceType.getServiceType(0xa9));
	}

	/**
	 * Transmit a CHATDISCONNECT packet.
	 */
	protected void transmitChatDisconnect(String room) throws IOException {
		chatSessionStatus = SessionState.UNSTARTED;
		currentLobby = null;
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("104", room);
		body.addElement("109", chatID.getId());
		sendPacket(body, ServiceType.CHATDISCONNECT); // 0xa0
	}

	/**
	 * Transmit a CHATADDINVITE packet. We send one of these to invite a fellow
	 * Yahoo user to a chat room.
	 */
	protected void transmitChatInvite(String netname, long id, String to,
			String msg) throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", chatID.getId());
		body.addElement("104", netname); // Room name
		body.addElement("117", msg); // Invite text
		body.addElement("118", to); // Target
		body.addElement("129", id + ""); // Room id
		sendPacket(body, ServiceType.getServiceType(0x9d)); // 0x9d
	}

	/**
	 * Transmit a CHATJOINpacket. We send one of these after the CHATCONNECT
	 * packet, as the second phase of chat login. Note: netname uses network
	 * name of "room:lobby".
	 */
	protected void transmitChatJoin(String netname, long roomId)
			throws IOException {
		if (sessionStatus != SessionState.CONNECTED) {
			throw new IllegalStateException(
					"Logging on is only possible right after successfully connecting to the network.");
		}

		if (netname == null || netname.length() < 3 || !netname.contains(":")) {
			throw new IllegalStateException(
					"Argument 'netname' cannot be null and must include network name, containing a room and lobby name seperated by a colon (':') character.");
		}

		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", chatID.getId());
		body.addElement("104", netname);
		body.addElement("129", Long.toString(roomId));
		body.addElement("62", "2"); // FIX: what is this?
		sendPacket(body, ServiceType.CHATJOIN); // 0x98
	}

	/**
	 * Transmit a CHATMSG packet. The contents of this message will be forwarded
	 * to other users of the chatroom, BUT NOT TO US! Note: 'netname' is the
	 * network name of "room:lobby".
	 */
	protected void transmitChatMsg(String netname, String msg, boolean emote)
			throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", chatID.getId());
		body.addElement("104", netname);
		body.addElement("117", msg);
		if (emote)
			body.addElement("124", "2"); // 1=Regular, 2=Emote
		else
			body.addElement("124", "1");
		if (Util.isUtf8(msg))
			body.addElement("97", "1");
		sendPacket(body, ServiceType.CHATMSG); // 0xa8
	}

	/**
	 * Transmit a CHATPM packet. Person message packets. FIX: DOES THIS WORK ???
	 */
	protected void transmitChatPM(String to, String msg) throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("5", to);
		body.addElement("14", msg);
		sendPacket(body, ServiceType.CHATPM); // 0x020
	}

	/**
	 * Transmit a CHATPING packet.
	 */
	protected void transmitChatPing() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		sendPacket(body, ServiceType.CHATPING); // 0xa1
	}

	/**
	 * Transmit an CONFADDINVITE packet. We send one of these when we wish to
	 * invite more users to our conference.
	 */
	protected void transmitConfAddInvite(YahooIdentity user, String room,
			String yid, String msg) throws IOException,
			NoSuchConferenceException {
		// Check this conference actually exists (throws exception if not)
		getConference(room);
		// Send new invite packet to Yahoo
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", yid);
		body.addElement("51", user.getId());
		body.addElement("57", room);
		Vector<YahooUser> users = getConference(room).getUsers();
		for (int i = 0; i < users.size(); i++)
			body.addElement("52", (users.elementAt(i)).getId());
		for (int i = 0; i < users.size(); i++)
			body.addElement("53", (users.elementAt(i)).getId());
		body.addElement("58", msg);
		body.addElement("13", "0"); // FIX : what's this for?
		sendPacket(body, ServiceType.CONFADDINVITE); // 0x1c
	}

	/**
	 * Transmit an CONFDECLINE packet. We send one of these when we decline an
	 * offer to join a conference.
	 */
	protected void transmitConfDecline(String room, String yid, String msg)
			throws IOException, NoSuchConferenceException {
		// Flag this conference as now dead
		YahooConference yc = getConference(room);
		yc.closeConference();
		Vector<YahooUser> users = yc.getUsers();
		// Send decline packet to Yahoo
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", yid);
		for (int i = 0; i < users.size(); i++)
			body.addElement("3", (users.elementAt(i)).getId());
		body.addElement("57", room);
		body.addElement("14", msg);
		sendPacket(body, ServiceType.CONFDECLINE); // 0x1a
	}

	/**
	 * Transmit an CONFINVITE packet. This is sent when we want to create a new
	 * conference, with the specified users and with a given welcome message.
	 */
	protected void transmitConfInvite(String[] users, String yid, String room,
			String msg) throws IOException {
		// Create a new conference object
		conferences.put(room, new YahooConference(userStore, identities
				.get(yid), room, this, false));
		// Send request to Yahoo
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", yid);
		body.addElement("50", primaryID.getId());
		for (int i = 0; i < users.length; i++)
			body.addElement("52", users[i]);
		body.addElement("57", room);
		body.addElement("58", msg);
		body.addElement("13", "0"); // FIX: what's this for?
		sendPacket(body, ServiceType.CONFINVITE); // 0x18
	}

	/**
	 * Transmit an CONFLOGOFF packet. We send one of these when we wish to leave
	 * a conference.
	 */
	protected void transmitConfLogoff(String room, String yid)
			throws IOException, NoSuchConferenceException {
		// Flag this conference as now dead
		YahooConference yc = getConference(room);
		yc.closeConference();
		Vector<YahooUser> users = yc.getUsers();
		// Send decline packet to Yahoo
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", yid);
		for (int i = 0; i < users.size(); i++)
			body.addElement("3", (users.elementAt(i)).getId());
		body.addElement("57", room);
		sendPacket(body, ServiceType.CONFLOGOFF); // 0x1b
	}

	/**
	 * Transmit an CONFLOGON packet. Send this when we want to accept an offer
	 * to join a conference.
	 */
	protected void transmitConfLogon(String room, String yid)
			throws IOException, NoSuchConferenceException {
		// Get a list of users for this conference
		Vector<YahooUser> users = getConference(room).getUsers();
		// Send accept packet to Yahoo
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", yid);
		for (int i = 0; i < users.size(); i++)
			body.addElement("3", (users.elementAt(i)).getId());
		body.addElement("57", room);
		sendPacket(body, ServiceType.CONFLOGON); // 0x19
	}

	/**
	 * Transmit an CONFMSG packet. We send one of these when we wish to send a
	 * message to a conference.
	 */
	protected void transmitConfMsg(String room, String yid, String msg)
			throws IOException, NoSuchConferenceException {
		// Get a list of users for this conference
		Vector<YahooUser> users = getConference(room).getUsers();
		// Send message packet to yahoo
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", yid);
		for (int i = 0; i < users.size(); i++)
			body.addElement("53", (users.elementAt(i)).getId());
		body.addElement("57", room);
		body.addElement("14", msg);
		if (Util.isUtf8(msg))
			body.addElement("97", "1");
		sendPacket(body, ServiceType.CONFMSG); // 0x1d
	}

	/**
	 * Transmit an CONTACTIGNORE packet. We would do this in response to an
	 * ADDFRIEND packet arriving. (???) FIX: when does this get sent?
	 */
	protected void transmitContactIgnore(String friend, boolean ignore)
			throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", primaryID.getId()); // FIX: effective id?
		body.addElement("7", friend);
		if (ignore)
			body.addElement("13", "1"); // Bug: 1/2 not 0/1 ???
		else
			body.addElement("13", "2");
		sendPacket(body, ServiceType.CONTACTIGNORE); // 0x85
	}

	/**
	 * Transmit a CONTACTREJECT packet. We would do this when we wish to
	 * overrule an attempt to add us as a friend (when we get a ADDFRIEND
	 * packet!)
	 */
	protected void transmitContactReject(String friend, String yid, String msg)
			throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", yid);
		body.addElement("7", friend);
		body.addElement("14", msg);
		sendPacket(body, ServiceType.CONTACTREJECT); // 0x86
	}

	/**
	 * Transmit a FILETRANSFER packet, to send a binary file to a friend.
	 */
	protected void transmitFileTransfer(String to, String message, File file)
			throws FileTransferFailedException, IOException {
		if (file == null) {
			throw new IllegalArgumentException(
					"Argument 'file' cannot be null.");
		}

		if (!file.isFile()) {
			throw new IllegalArgumentException(
					"The provided file object does not denote a normal file (but possibly a directory).");
		}

		if (file.length() == 0L) {
			throw new FileTransferFailedException("File transfer: empty file");
		}

		final String cookie = cookieY + "; " + cookieT;

		final byte[] marker = { '2', '9', (byte) 0xc0, (byte) 0x80 };

		// Create a Yahoo packet into 'packet'
		final PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("0", primaryID.getId());
		body.addElement("5", to);
		body.addElement("28", Long.toString(file.length()));
		body.addElement("27", file.getName());
		body.addElement("14", message);
		byte[] packet = body.getBuffer();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.write(NetworkConstants.MAGIC, 0, 4);
		dos.write(NetworkConstants.VERSION, 0, 4);
		dos.writeShort((packet.length + 4) & 0xFFFF);
		dos.writeShort(ServiceType.FILETRANSFER.getValue() & 0xFFFF);
		dos.writeInt((int) (status.getValue() & 0xFFFFFFFF));
		dos.writeInt((int) (sessionId & 0xFFFFFFFF));
		dos.write(packet, 0, packet.length);
		dos.write(marker, 0, 4); // Extra 4 bytes : marker before file data
		// (?)

		packet = baos.toByteArray();

		// Send to Yahoo using POST
		String ftHost = Util.fileTransferHost();
		String ftURL = "http://" + ftHost + NetworkConstants.FILE_TF_PORTPATH;
		HttpURLConnection uConn = (HttpURLConnection) (new URL(ftURL)
				.openConnection());
		uConn.setRequestMethod("POST");
		uConn.setDoOutput(true); // POST, not GET
		Util.initURLConnection(uConn);
		uConn.setRequestProperty("Content-Length", Long.toString(file.length()
				+ packet.length));
		uConn.setRequestProperty("User-Agent", NetworkConstants.USER_AGENT);
		// uConn.setRequestProperty("Host",ftHost);
		uConn.setRequestProperty("Cookie", cookie);
		uConn.connect();

		final BufferedOutputStream bos = new BufferedOutputStream(uConn
				.getOutputStream());
		try {
			bos.write(packet);
			bos.write(Util.getBytesFromFile(file));
			bos.flush();
		} finally {
			bos.close();
		}

		final int ret = uConn.getResponseCode();
		uConn.disconnect();

		if (ret != 200) {
			throw new FileTransferFailedException("Server rejected upload");
		}
	}

	/**
	 * Transmit a FRIENDADD packet. If all goes well we'll get a FRIENDADD
	 * packet back with the details of the friend to confirm the transation
	 * (usually preceeded by a CONTACTNEW packet with well detailed info).
	 * friend - Yahoo id of friend to add group - Group to add it to
	 */
	protected void transmitFriendAdd(String friend, String group)
			throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", primaryID.getId()); // ???: effective id?
		body.addElement("7", friend);
		body.addElement("65", group);
		sendPacket(body, ServiceType.FRIENDADD); // 0x83
	}

	/**
	 * Transmit a FRIENDREMOVE packet. We should get a FRIENDREMOVE packet back
	 * (usually preceeded by a CONTACTNEW packet). friend - Yahoo id of friend
	 * to remove group - Group to remove it from
	 */
	protected void transmitFriendRemove(String friend, String group)
			throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", primaryID.getId()); // ???: effective id?
		body.addElement("7", friend);
		body.addElement("65", group);
		sendPacket(body, ServiceType.FRIENDREMOVE); // 0x84
	}

	/**
	 * Transmit a GOTGROUPRENAME packet, to change the name of one of our friends
	 * groups.
	 */
	/*
	 * TODO: Currently, this behavior is as it was in jYMSG. Protocol
	 * specification would suggest that not 0x13 (GOTGROUPRENAME) but 0x89
	 * (GROUPRENAME) should be used for this operation. Find out and make
	 * sure.
	 */ 
	protected void transmitGroupRename(String oldName, String newName)
			throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", primaryID.getId()); // ???: effective id?
		body.addElement("65", oldName);
		body.addElement("67", newName);
		sendPacket(body, ServiceType.GOTGROUPRENAME); // 0x13
	}

	/**
	 * Transmit a IDACT packet.
	 */
	protected void transmitIdActivate(String id) throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("3", id);
		sendPacket(body, ServiceType.IDACT); // 0x07
	}

	/**
	 * Transmit a IDDEACT packet.
	 */
	protected void transmitIdDeactivate(String id) throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("3", id);
		sendPacket(body, ServiceType.IDDEACT); // 0x08
	}

	/**
	 * Transmit an IDLE packet. Used by the HTTP proxy connection to provide a
	 * mechanism were by incoming packets can be delivered. An idle packet is
	 * sent every thirty seconds (as part of a HTTP POST) and the server
	 * responds with all the packets accumulated since last contact.
	 */
	protected void transmitIdle() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", loginID.getId()); // FIX: Should this be primary?
		body.addElement("0", primaryID.getId());
		sendPacket(body, ServiceType.IDLE); // 0x05
	}

	/**
	 * Transmit a regular ISAWAY packet. To return, try transmiting an ISBACK
	 * packet!
	 */
	protected void transmitIsAway() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("10", status + "");
		sendPacket(body, ServiceType.ISAWAY, status); // 0x03
	}

	/**
	 * Sent out a custom ISAWAY message.
	 * 
	 * @param msg
	 *            custom message.
	 * @param isAway
	 *            ''true'' to set state to 'away', ''false'' to set state to
	 *            'back'.
	 * @throws IOException
	 */
	protected void transmitIsAway(String msg, boolean isAway)
			throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		status = Status.CUSTOM;
		body.addElement("10", Long.toString(status.getValue()));
		body.addElement("19", msg);
		if (isAway) {
			body.addElement("47", "1"); // 1=away
		} else {
			body.addElement("47", "0"); // 0=back
		}
		sendPacket(body, ServiceType.ISAWAY, status); // 0x03
	}

	/**
	 * Transmit an ISBACK packet, contains no body, just the Yahoo status. We
	 * should send this to return from an ISAWAY, or after we have confirmed a
	 * sucessful LOGON - it sets our initial status (visibility) to the outside
	 * world. Typical initial values for 'status' are AVAILABLE and INVISIBLE.
	 */
	protected void transmitIsBack() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("10", status + "");
		sendPacket(body, ServiceType.ISBACK, status); // 0x04
	}

	/**
	 * Transmit a LIST packet.
	 */
	protected void transmitList() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", primaryID.getId());
		sendPacket(body, ServiceType.LIST); // 0x55
	}

	/**
	 * Transmit a LOGOFF packet, which should exit us from Yahoo IM.
	 */
	protected void transmitLogoff() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("0", loginID.getId()); // Is this only in for HTTP?
		sendPacket(body, ServiceType.LOGOFF); // 0x02
		ipThread.stopMe();
		network.close();
	}

	/**
	 * Transmit a MESSAGE packet.
	 * 
	 * @param to
	 *            he Yahoo ID of the user to send the message to
	 * @param yid
	 *            Yahoo identity used to send the message from
	 * @param msg
	 *            the text of the message
	 */
	protected void transmitMessage(String to, YahooIdentity yid, String msg)
			throws IOException {
		if (to == null || to.length() == 0) {
			throw new IllegalArgumentException(
					"Argument 'to' cannot be null or an empty String.");
		}

		if (yid == null) {
			throw new IllegalArgumentException("Argument 'yid' cannot be null.");
		}

		// Send packet
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("0", primaryID.getId()); // From (primary ID)
		body.addElement("1", yid.getId()); // From (effective ID)
		body.addElement("5", to); // To
		body.addElement("14", msg); // Message
		// Extension for YMSG9
		if (Util.isUtf8(msg))
			body.addElement("97", "1");
		body.addElement("63", ";" + imvironment); // Not supported here!
		body.addElement("64", "0");
		sendPacket(body, ServiceType.MESSAGE, Status.OFFLINE); // 0x06

		// If we have a typing notifier, inform it the typing has ended
		TypingNotifier tn = typingNotifiers.get(to);
		if (tn != null)
			tn.stopTyping();
	}

	/**
	 * Transmit a NOTIFY packet. Could be used for all sorts of purposes, but
	 * mainly games and typing notifications. Only typing is supported by this
	 * API. The mode determines the type of notification, "TYPING" or "GAME";
	 * msg holds the game name (or a single space if typing).
	 */
	protected void transmitNotify(String friend, String yid, boolean on,
			String msg, String mode) throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("4", yid);
		body.addElement("5", friend);
		body.addElement("14", msg);
		if (on)
			body.addElement("13", "1");
		else
			body.addElement("13", "0");
		body.addElement("49", mode);
		sendPacket(body, ServiceType.NOTIFY, Status.TYPING); // 0x4b
	}

	/**
	 * Transmit a PING packet always and a CHATPING packet, if the user is
	 * logged into a lobby.
	 */
	protected void transmitPings() throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		sendPacket(body, ServiceType.PING); // 0x12

		if (currentLobby != null) {
			transmitChatPing();
		}
	}

	/**
	 * Transmit a USERSTAT packet. Purpose? Unknown :-) It would seem that
	 * transmiting this packet results in a USERSTAT (0x0a) packet followed by a
	 * NEWMAIL (0x0b) packet with just the 9 field (new mail count) set being
	 * sent by the server.
	 */
	protected void transmitUserStat() throws IOException // 0x0a
	{
		PacketBodyBuffer body = new PacketBodyBuffer();
		sendPacket(body, ServiceType.USERSTAT);
	}

	/**
	 * Transmit a STEALTHSESSION packet. This is used to set our on/offline
	 * status for the current session only on a user-by-user basis. (???)
	 */
	protected void transmitStealthSession(int stat, int flag, String yid,
			String target) throws IOException {
		PacketBodyBuffer body = new PacketBodyBuffer();
		body.addElement("1", yid); // My id
		body.addElement("31", stat + ""); // Status to change to
		body.addElement("13", flag + ""); // What is this for?
		body.addElement("7", target); // Friend who is target
		sendPacket(body, ServiceType.getServiceType(0xba));
	}

	/**
	 * Process an incoming ADDIGNORE packet. We get one of these when we
	 * ignore/unignore someone, although their purpose is unknown as Yahoo
	 * follows up with a CONTACTIGNORE packet. The only disting- uising feature
	 * is the latter is always sent, wereas this packet is only sent if there's
	 * an actual change in ignore status. The packet's payload appears to always
	 * be empty.
	 */
	protected void receiveAddIgnore(YMSG9Packet pkt) // 0x11
	{
		// Not implementation (yet!)
	}

	/**
	 * Process an incoming AUTH packet (in response to the AUTH packet we
	 * transmitted to the server). Format: "1" <loginID> "94" <challenge string
	 * (24 chars)> Note: for YMSG10 Yahoo sneakily changed the
	 * challenge/response method dependant upon a switch in field '13'. If this
	 * field is 0 use v9, if 1 then use v10.
	 */
	protected void receiveAuth(YMSG9Packet pkt) // 0x57
			throws IOException {
		if (sessionStatus != SessionState.CONNECTING) {
			throw new IllegalStateException(
					"Received a response to an AUTH packet, outside the normal login flow. Current state: "
							+ sessionStatus);
		}
		String v10 = pkt.getValue("13"); // '0'=v9, '1'=v10
		String[] s;
		try {
			if (v10 != null && v10.equals("1"))
				s = ChallengeResponseV10.getStrings(loginID.getId(), password,
						pkt.getValue("94"));
			else
				s = ChallengeResponseV9.getStrings(loginID.getId(), password,
						pkt.getValue("94"));
		} catch (NoSuchAlgorithmException e) {
			throw new YMSG9BadFormatException("auth", pkt, e);
		} catch (IOException e) {
			throw new YMSG9BadFormatException("auth", pkt, e);
		}
		sessionStatus = SessionState.CONNECTED;
		transmitAuthResp(s[0], s[1]);
	}

	/**
	 * Process an incoming AUTHRESP packet. If we get one of these it means the
	 * logon process has failed. Set the session state to be failed, and flag
	 * the end of login. Note: we don't throw exceptions on the input thread,
	 * but instead we pass them to the thread which called login()
	 */
	protected void receiveAuthResp(YMSG9Packet pkt) // 0x54
	{
		try {
			if (pkt.exists("66")) {
				final long l = Long.parseLong(pkt.getValue("66"));
				switch (AuthenticationState.getStatus(l)) {
				// Account locked out?
				case LOCKED:
					URL u;
					try {
						u = new URL(pkt.getValue("20"));
					} catch (Exception e) {
						u = null;
					}
					loginException = new AccountLockedException("User "
							+ loginID + " has been locked out", u);
					break;

				// Bad login (password?)
				case BAD:
					loginException = new LoginRefusedException("User "
							+ loginID + " refused login",
							AuthenticationState.BAD);
					break;

				// unkown account?
				case BADUSERNAME:
					loginException = new LoginRefusedException("User "
							+ loginID + " unknown",
							AuthenticationState.BADUSERNAME);
					break;
				}
			}
		}
		// FIX: Add exception chaining to all YahooException's
		catch (NumberFormatException e) {
			loginException = new LoginRefusedException(
					"Number format exception" + e.toString());
		} finally {
			// When this method exits, the ipThread loop calling it will
			// terminate.
			ipThread.stopMe();
			// Notify login() calling thread of failure
			sessionStatus = SessionState.FAILED;
		}
	}

	/**
	 * Process an incoming CHATCONNECT packet. We get one of these in reply to
	 * sending a CHATCONNECT packet to Yahoo. It marks the end of the first
	 * stage of the chat login handshake. chatLogin() waits for this packet
	 * before proceeding to the next stage.
	 */
	protected void receiveChatConnect(YMSG9Packet pkt) // 0x96
	{
		if (chatSessionStatus != SessionState.CONNECTING) {
			throw new IllegalStateException(
					"Received a 'CHATCONNECT' packet outside of the logon process. Current state: "
							+ chatSessionStatus);
		}
		chatSessionStatus = SessionState.CONNECTED;
	}

	/**
	 * Process an incoming CHATDISCONNECT packet. We get one of these to confirm
	 * we have logged off.
	 */
	protected void receiveChatDisconnect(YMSG9Packet pkt) // 0xa0
	{
		// This should already have been set by transmitChatDisconnect(),
		// if it isn't set then we have been booted out without actually
		// asking to leave. This usually happens when Yahoo times us out
		// of a room after a long period of inactivity.
		if (chatSessionStatus != SessionState.UNSTARTED) {
			eventDispatchQueue.append(new SessionEvent(this),
					ServiceType.CHATDISCONNECT);
		}
		chatSessionStatus = SessionState.UNSTARTED;
	}

	/**
	 * Process an incoming CHATEXIT packet. We get one of these when someone
	 * leaves the chatroom - including ourselves, received just prior to our
	 * CHATDISCONNECT confirmation packet (see above). Note: on rare occassions
	 * this packet has been received for a user we previously had not been
	 * informed about.
	 */
	protected void receiveChatExit(YMSG9Packet pkt) // 0x9b
	{
		try {
			String netname = pkt.getValue("104"); // room:lobby
			String id = pkt.getValue("109"); // Yahoo id
			YahooChatLobby ycl = chatroomManager.getLobby(netname);
			if (ycl == null)
				throw new NoSuchChatroomException("Chatroom/lobby " + netname
						+ " not found.");
			// Remove user from room (very very occassionally the user does
			// not exist as a known member of this lobby, so ycu==null!!)
			YahooChatUser ycu = ycl.getUser(id);
			if (ycu != null)
				ycl.removeUser(ycu);
			else
				ycu = createChatUser(pkt, 0); // Create for benefit of event!
			// Create and fire event FIX: should cope with multiple users!
			SessionChatEvent se = new SessionChatEvent(this, 1, ycl);
			se.setChatUser(0, ycu);
			eventDispatchQueue.append(se, ServiceType.CHATEXIT);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("chat logoff", pkt, e);
		}
	}

	/**
	 * Process an incoming CHATJOIN packet. We get one of these: (a) as a way
	 * of finishing the login handshaking process, containing room details (we
	 * already know) and a list of current members. (b) when the login process
	 * fails (room full?), containing only a 114 field (set to '-35'?) - see
	 * error handling code elsewhere (c) as a stripped down version when a new
	 * user enters the room (including ourselves!) (d) as a stripped down
	 * version when an existing member updates their details. Sometimes a login
	 * packet is broken over several packets. The clue here appears to be that
	 * the first (and subsequent?) packets have a status of 5, while the final
	 * packet has a status of 1.
	 */
	protected void receiveChatJoin(YMSG9Packet pkt) // 0x98
	{
		boolean joining = false;
		try {
			// Is this an error packet, sent to us via processError() ?
			if (pkt.exists("114")) {
				loginException = new LoginRefusedException("User " + chatID
						+ " refused chat login");
				joining = true;
				chatSessionStatus = SessionState.FAILED;
				return; // ...to finally block
			}

			// Returns null if more packets to come
			pkt = compoundChatLoginPacket(pkt);
			if (pkt == null)
				return;

			// As we need to load a room to get at its lobby data so we
			// can login, the next line *should* never fail... however :-)
			String netname = pkt.getValue("104"); // room:lobby
			YahooChatLobby ycl = chatroomManager.getLobby(netname);
			if (ycl == null)
				throw new NoSuchChatroomException("Chatroom/lobby " + netname
						+ " not found.");

			// Note: Yahoo sometimes lies about the '108' count of users!
			// Reduce count until to see how many users there *actually* are!
			int cnt = Integer.parseInt(pkt.getValue("108"));
			while (cnt > 0 && pkt.getNthValue("109", cnt - 1) == null)
				cnt--;

			// Is this an update packet, for an existing member?
			YahooChatUser ycu = ycl.getUser(pkt.getValue("109"));
			if (cnt == 1 && ycu != null) {
				// Count is one and user exists - UPDATE
				final int attributes = Integer.parseInt(pkt.getValue("113"));
				final String alias = pkt.getValue("141"); // optional
				final int age = Integer.parseInt(pkt.getValue("110"));
				final String location = pkt.getValue("142"); // optional

				ycu.setAttributes(attributes);
				ycu.setAlias(alias);
				ycu.setAge(age);
				ycu.setLocation(location);

				SessionChatEvent se = new SessionChatEvent(this, 1, ycl);
				se.setChatUser(0, ycu);
				eventDispatchQueue.append(se, ServiceType.X_CHATUPDATE);
				return; // ...to finally block
			}
			// Full sized packet, when joining room?
			joining = pkt.exists("61");
			// If we are joining, clear the array of users (just to be sure!)
			if (joining)
				ycl.clearUsers();

			// When sent in muliple parts the login packet usually
			// contains a high degree of duplicates. Remove using hash.
			Hashtable<String, YahooChatUser> ht = new Hashtable<String, YahooChatUser>();
			for (int i = 0; i < cnt; i++) {
				// Note: automatically creates YahooUser if necessary
				ycu = createChatUser(pkt, i);
				ht.put(ycu.getId(), ycu);
			}
			// Create event, add users
			SessionChatEvent se = new SessionChatEvent(this, cnt, ycl);
			int i = 0;
			for (Enumeration<YahooChatUser> en = ht.elements(); en
					.hasMoreElements();) {
				ycu = en.nextElement();
				// Does this user exist already? (This should always be
				// no, as update packets should always have only one member
				// who already exists - thus caught by the 'if' block above!
				if (!ycl.exists(ycu))
					ycl.addUser(ycu); // Add to lobby
				se.setChatUser(i++, ycu); // Add to event
			}

			// We don't send an event if we get the larger 'logging in'
			// type packet as the chat user list is brand new. We only send
			// events when someone joins and we need to signal an update.
			if (!joining) {
				// Did we actually accrue any *new* users in previous loop?
				if (se.getChatUsers().length > 0)
					eventDispatchQueue.append(se, ServiceType.CHATJOIN);
			} else {
				chatSessionStatus = SessionState.LOGGED_ON;
			}
			return; // ...to finally block
		} catch (Exception e) {
			e.printStackTrace();
			throw new YMSG9BadFormatException("chat login", pkt, e);
		} finally {
			// FIX: Not thread safe if multiple chatroom supported!
			if (joining && chatSessionStatus != SessionState.FAILED)
				chatSessionStatus = SessionState.LOGGED_ON;
		}
	}

	/**
	 * Process an incoming CHATMSG packet. We get one of these when a chatroom
	 * user sends a message to the room. (Note: very very occassionally we
	 * *might* get a message from a user we have not been told about yet! I've
	 * not seen any chat message packets like this, but it *does* happen with
	 * other chat packets, so I have to assume it can happen to chat message
	 * packets too!) Note: status checked because we may have timed out on chat
	 * login
	 */
	protected void receiveChatMsg(YMSG9Packet pkt) // 0xa8
	{
		if (chatSessionStatus != SessionState.LOGGED_ON) {
			throw new IllegalStateException("Time out on chat login.");
		}

		try {
			String netname = pkt.getValue("104"); // room:lobby
			YahooChatLobby ycl = chatroomManager.getLobby(netname);
			if (ycl == null)
				throw new NoSuchChatroomException("Chatroom/lobby " + netname
						+ " not found.");
			// Create and fire event
			YahooChatUser ycu = ycl.getUser(pkt.getValue("109"));
			if (ycu == null)
				ycu = createChatUser(pkt, 0);
			SessionChatEvent se = new SessionChatEvent(this, ycu, // from
					pkt.getValue("117"), // message
					pkt.getValue("124"), // 1=Regular, 2=Emote
					ycl // room:lobby
			);
			eventDispatchQueue.append(se, ServiceType.CHATMSG);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("chat message", pkt, e);
		}
	}

	/**
	 * Process an incoming CHATPM packet. We get one of these when someone
	 * 'personal messages' us from within a chatroom. Why this packet is used,
	 * not a regular MESSAGE packet is unknown. It seems that the web-based chat
	 * clients, at least, prefer this packet type. Note: status checked because
	 * we may have timed out on chat login
	 */
	protected void receiveChatPM(YMSG9Packet pkt) // 0x20
	{
		if (chatSessionStatus != SessionState.LOGGED_ON) {
			throw new IllegalStateException("Time out on chat login.");
		}

		try {
			SessionEvent se = new SessionEvent(this, pkt.getValue("5"), // to
					pkt.getValue("4"), // from
					pkt.getValue("14") // message
			);
			eventDispatchQueue.append(se, ServiceType.MESSAGE);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("chat PM", pkt, e);
		}
	}

	/**
	 * Process an incoming CONFDECLINE packet. We get one of these when someone
	 * we previously invited to a conference, declines our invite.
	 */
	protected void receiveConfDecline(YMSG9Packet pkt) // 0x1a
	{
		try {
			YahooConference yc = getOrCreateConference(pkt);
			// Create event
			SessionConferenceEvent se = new SessionConferenceEvent(this, pkt
					.getValue("1"), // to (effective id)
					pkt.getValue("54"), // from
					pkt.getValue("14"), // message (topic)
					yc, // room
					null // users array
			);
			// Remove the user
			yc.removeUser(se.getFrom());
			// Fire invite event
			if (!yc.isClosed()) // Should never be closed!
				eventDispatchQueue.append(se, ServiceType.CONFDECLINE);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("conference decline", pkt, e);
		}
	}

	/**
	 * Process an incoming CONFLOGOFF packet. We get one of these when someone
	 * leaves the conference. Note: in *very* extreme circum- stances, this may
	 * arrive before the invite packet.
	 */
	protected void receiveConfLogoff(YMSG9Packet pkt) // 0x1b
	{
		// If we have not received an invite yet, buffer packets
		YahooConference yc = getOrCreateConference(pkt);
		synchronized (yc) {
			if (!yc.isInvited()) {
				yc.addPacket(pkt);
				return;
			}
		}
		// Otherwise, handle the packet
		try {
			SessionConferenceEvent se = new SessionConferenceEvent(this, pkt
					.getValue("1"), // to (effective id)
					pkt.getValue("56"), // from
					null, // message
					yc // room
			);
			// Remove the user
			yc.removeUser(se.getFrom());
			// Fire invite event
			if (!yc.isClosed()) // Should never be closed fir invite!
				eventDispatchQueue.append(se, ServiceType.CONFLOGOFF);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("conference logoff", pkt, e);
		}
	}

	/**
	 * Process an incoming CONFLOGON packet. We get one of these when someone
	 * joins a conference we have been invited to (even if we ourselves have yet
	 * to accept/decline). Note: in extreme circum- stances, this may arrive
	 * before the invite packet.
	 */
	protected void receiveConfLogon(YMSG9Packet pkt) // 0x19
	{
		// If we have not received an invite yet, buffer packets
		YahooConference yc = getOrCreateConference(pkt);
		synchronized (yc) {
			if (!yc.isInvited()) {
				yc.addPacket(pkt);
				return;
			}
		}
		// Otherwise, handle the packet
		try {
			SessionConferenceEvent se = new SessionConferenceEvent(this, pkt
					.getValue("1"), // to (effective id)
					pkt.getValue("53"), // from (accepting user)
					null, // message
					yc // room
			);
			// Add user (probably already on list, but just to be sure!)
			yc.addUser(se.getFrom());
			// Fire event
			if (!yc.isClosed())
				eventDispatchQueue.append(se, ServiceType.CONFLOGON);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("conference logon", pkt, e);
		}
	}

	/**
	 * Process an incoming CONFMSG packet. We get one of these when someone in a
	 * conference we are part of sends a message. Note: in extreme circumstances
	 * this may arrive before the invite packet.
	 */
	protected void receiveConfMsg(YMSG9Packet pkt) // 0x1d
	{
		// If we have not received an invite yet, buffer packets
		YahooConference yc = getOrCreateConference(pkt);
		synchronized (yc) {
			if (!yc.isInvited()) {
				yc.addPacket(pkt);
				return;
			}
		}
		// Otherwise, handle the packet
		try {
			SessionConferenceEvent se = new SessionConferenceEvent(this, pkt
					.getValue("1"), // to (effective id)
					pkt.getValue("3"), // from
					pkt.getValue("14"), // message
					yc // room
			);
			// Fire event
			if (!yc.isClosed())
				eventDispatchQueue.append(se, ServiceType.CONFMSG);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("conference mesg", pkt, e);
		}
	}

	/**
	 * Process an incoming CONTACTIGNORE packet. We get one of these to confirm
	 * an outgoing CONTACTIGNORE - an ADDIGNORE packet may preceed this, but
	 * only if the ignore status has genuinely changed state.
	 */
	protected void receiveContactIgnore(YMSG9Packet pkt) // 0x85
	{
		try {
			String n = pkt.getValue("0");
			boolean ig = pkt.getValue("13").charAt(0) == '1';
			int st = Integer.parseInt(pkt.getValue("66"));
			if (st == 0) {
				// Update ignore status, and fire friend changed event
				YahooUser yu = userStore.getOrCreate(n);
				yu.setIgnored(ig);
				// Fire event
				SessionFriendEvent se = new SessionFriendEvent(this, 1);
				se.setUser(0, yu);
				eventDispatchQueue.append(se, ServiceType.ISAWAY);
			} else {
				// Error
				String m = "Contact ignore error: ";
				switch (st) {
				case 2:
					m = m + "Already on ignore list";
					break;
				case 3:
					m = m + "Not currently ignored";
					break;
				case 12:
					m = m + "Cannot ignore friend";
					break;
				default:
					m = m + "Unknown error";
					break;
				}
				errorMessage(pkt, m);
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("contact ignore", pkt, e);
		}
	}

	/**
	 * Process an incoming CONTACTNEW packet. We get one of these: (1) when
	 * someone has added us to their friends list, giving us the chance to
	 * refuse them; (2) when we add or remove a friend (see FRIENDADD/REMOVE
	 * outgoing) as confirmation prior to the FRIENDADD/REMOVE packet being
	 * echoed back to us - if the friend is online status info may be inc- luded
	 * (supposedly for multiple friends, although given the circum- stances
	 * probably always contains only one!); (3) when someone refuses a contact
	 * request (add friend) from us.
	 */
	protected void receiveContactNew(YMSG9Packet pkt) // 0x0f
	{
		try {
			if (pkt.length <= 0) // Empty packet is received after
			{
				return; // we transmit FRIENDADD/REMOVE
			} else if (pkt.exists("7")) // Ditto, except friend is online
			{
				updateFriendsStatus(pkt);
				return;
			} else if (pkt.status == 0x07) // Conact refused
			{
				SessionEvent se = new SessionEvent(this, null, // to
						pkt.getValue("3"), // from
						pkt.getValue("14") // message
				);
				eventDispatchQueue.append(se, ServiceType.CONTACTREJECT);
			} else
			// Contact request
			{
				final String to = pkt.getValue("1");
				final String from = pkt.getValue("3");
				final String message = pkt.getValue("14");
				final String timestamp = pkt.getValue("15");

				final SessionEvent se;
				if (timestamp == null || timestamp.length() == 0) {
					se = new SessionEvent(this, to, from, message);
				} else {
					final long timestampInMillis = 1000 * Long
							.parseLong(timestamp);
					se = new SessionEvent(this, to, from, message,
							timestampInMillis);
				}
				se.setStatus(pkt.status); // status!=0 means offline message
				eventDispatchQueue.append(se, ServiceType.CONTACTNEW);
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("contact request", pkt, e);
		}
	}

	/**
	 * Process an incoming FILETRANSFER packet. This packet can be received
	 * under two circumstances: after a successful FT upload, in which case it
	 * contains a text message with the download URL, or because someone has
	 * sent us a file. Note: TF packets do not contain the file data itself, but
	 * rather a link to a tmp area on Yahoo's webservers which holds the file.
	 */
	protected void receiveFileTransfer(YMSG9Packet pkt) // 0x46
	{
		try {
			final String to = pkt.getValue("5");
			final String from = pkt.getValue("4");
			final String message = pkt.getValue("14");

			if (!pkt.exists("38")) {
				// Acknowledge upload
				final SessionEvent se = new SessionEvent(this, to, from,
						message);
				eventDispatchQueue.append(se, ServiceType.MESSAGE);
			} else {
				// Receive file transfer
				final String expires = pkt.getValue("38");
				final String url = pkt.getValue("20");

				final SessionFileTransferEvent se = new SessionFileTransferEvent(
						this, to, from, message, Long.valueOf(expires), url);
				eventDispatchQueue.append(se, ServiceType.FILETRANSFER);
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("file transfer", pkt, e);
		}
	}

	/**
	 * Process an incoming FRIENDADD packet. We get one of these after we've
	 * sent a FRIENDADD packet, as confirmation. It contains basic details of
	 * our new friend, although it seems a bit redundant as Yahoo sents a
	 * CONTACTNEW with these details before this packet.
	 */
	protected void receiveFriendAdd(YMSG9Packet pkt) // 0x83
	{
		try {
			// We probably got a CONTACTNEW before we got this packet, so
			// check if the user hasn't already been created in users hash
			// (if not, create!) then add to groups structure.
			final String username = pkt.getValue("7");
			// String s = pkt.getValue("66"),
			final String groupname = pkt.getValue("65");
			final YahooUser yu = userStore.getOrCreate(username);
			insertFriend(yu, groupname);
			// Fire event : 7=friend, 66=status, 65=group name
			final SessionFriendEvent se = new SessionFriendEvent(this, yu,
					groupname);
			eventDispatchQueue.append(se, ServiceType.FRIENDADD);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("friend added", pkt, e);
		}
	}

	/**
	 * Process an incoming FRIENDADD packet. We get one of these after we've
	 * sent a FRIENDREMOVE packet, as confirmation. It contains basic details of
	 * the friend we've deleted.
	 */
	protected void receiveFriendRemove(YMSG9Packet pkt) // 0x84
	{
		try {
			String n = pkt.getValue("7"), g = pkt.getValue("65");
			YahooUser yu = userStore.get(n);
			if (yu == null) {
				return;
			}
			deleteFriend(yu, g);
			// Fire event : 7=friend, 66=status, 65=group name
			SessionFriendEvent se = new SessionFriendEvent(this, yu, g);
			eventDispatchQueue.append(se, ServiceType.FRIENDREMOVE);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("friend removed", pkt, e);
		}
	}

	/**
	 * Process and incoming GOTGROUPRENAME packet.
	 */
	protected void receiveGroupRename(YMSG9Packet pkt) // 0x13
	{
		try {
			String oldName = pkt.getValue("67");
			String newName = pkt.getValue("65");
			if (oldName == null || newName == null)
				return;
			SessionGroupEvent se = new SessionGroupEvent(this, oldName, newName);
			eventDispatchQueue.append(se, ServiceType.GOTGROUPRENAME);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("group rename", pkt, e);
		}
	}

	/**
	 * Process an incoming IDACT packet.
	 */
	protected void receiveIdAct(YMSG9Packet pkt) // 0x07
	{
		// FIX: do something here!
	}

	/**
	 * Process an incoming IDDEACT packet.
	 */
	protected void receiveIdDeact(YMSG9Packet pkt) // 0x08
	{
		// Fix: do something here!
	}

	/**
	 * Process an incoming ISAWAY packet. See ISBACK below.
	 */
	protected void receiveIsAway(YMSG9Packet pkt) // 0x03
	{
		// If this an update to a friend?
		if (pkt.exists("7")) {
			updateFriendsStatus(pkt);
		}
	}

	/**
	 * Process an incoming ISBACK packet. God alone knows what I'm supposed to
	 * do with this when the payload is empty!!
	 */
	protected void receiveIsBack(YMSG9Packet pkt) // 0x04
	{
		if (pkt.exists("7")) {
			updateFriendsStatus(pkt);
		}
	}

	/**
	 * Process and incoming LIST packet. We'll typically get one of these when
	 * our logon is sucessful. (It should arrive before the LOGON packet.) Note:
	 * this packet can arrive in several parts. Taking a look at other
	 * third-party YMSG implemenations it would seem that the absence of cookies
	 * is the trait marking an incomplete packet.
	 */
	protected void receiveList(YMSG9Packet pkt) // 0x55
	{
		// These fields will be concatenated, others will be appended
		String[] concatFields = { "87", "88", "89" };
		// Either cache or merge with cached packet
		if (cachePacket == null) {
			cachePacket = pkt;
		} else {
			cachePacket.merge(pkt, concatFields);
		}

		// Complete: this is the final packet
		if (pkt.exists("59")) {
			_receiveList(cachePacket);
		}
	}

	/**
	 * Handles a completely constructed LIST packet. LIST packets can come in
	 * several parts. These are being merged into one by
	 * {@link #receiveList(YMSG9Packet)}. The final, merged packet is then used
	 * to call this method.
	 * 
	 * @param pkt
	 *            A 'complete' LIST packet - the result of merging all
	 *            fragmented LIST packets sent by the Yahoo Network.
	 */
	private void _receiveList(YMSG9Packet pkt) // 0x55
	{
		/*
		 * Friends list, each group is encoded as the group name (ie: "Friends")
		 * followed by a colon, followed by a comma separated list of friend
		 * ids, followed by a single \n (0x0a).
		 */
		try {
			final String grps = pkt.getValue("87"); // Value for key "87"
			if (grps != null) {
				final StringTokenizer st1 = new StringTokenizer(grps, "\n");

				// Extract each group.
				while (st1.hasMoreTokens()) {
					final String s1 = st1.nextToken();
					// Store group name and decoded friends list
					final YahooGroup group = new YahooGroup(s1.substring(0, s1
							.indexOf(":")));
					final StringTokenizer st2 = new StringTokenizer(s1
							.substring(s1.indexOf(":") + 1), ",");

					// extract each user.
					while (st2.hasMoreTokens()) {
						final String k = st2.nextToken();
						final YahooUser yu = userStore.getOrCreate(k);
						group.addUser(yu);
						yu.adjustGroupCount(+1);
					}
					groups.add(group);
				}
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("friends list in list", pkt, e);
		}

		// Ignored list (people we don't want to hear from!)
		try {
			String s = pkt.getValue("88"); // Value for key "88"
			if (s != null) {
				// Comma separated list (?)
				StringTokenizer st = new StringTokenizer(s, ",");
				while (st.hasMoreTokens()) {
					s = st.nextToken();
					YahooUser yu = userStore.getOrCreate(s);
					yu.setIgnored(true);
				}
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("ignored list in list", pkt, e);
		}

		// Identities list (alternative yahoo ids we can use!)
		try {
			String s = pkt.getValue("89"); // Value for key "89"
			if (s != null) {
				// Comma separated list (?)
				StringTokenizer st = new StringTokenizer(s, ",");
				identities.clear();
				while (st.hasMoreTokens()) {
					final String id = st.nextToken();
					identities.put(id, new YahooIdentity(id));
				}
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("identities list in list", pkt, e);
		}

		// Stealth blocked list (people we don't want to see us!)
		try {
			String s = pkt.getValue("185"); // Value for key "185"
			if (s != null) {
				// Comma separated list (?)
				StringTokenizer st = new StringTokenizer(s, ",");
				while (st.hasMoreTokens()) {
					s = st.nextToken();
					YahooUser yu = userStore.getOrCreate(s);
					yu.setStealthBlocked(true);
				}
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("ignored list in list", pkt, e);
		}

		// Yahoo gives us three cookies, Y, T and C
		try {
			String[] ck = ConnectionHandler.extractCookies(pkt);
			cookieY = ck[NetworkConstants.COOKIE_Y]; // Y=<cookie>
			cookieT = ck[NetworkConstants.COOKIE_T]; // T=<cookie>
			cookieC = ck[NetworkConstants.COOKIE_C]; // C=<cookie>
		} catch (Exception e) {
			throw new YMSG9BadFormatException("cookies in list", pkt, e);
		}

		// Primary identity: the *real* Yahoo ID for this account.
		// Only present if logging in under non-primary identity(?)
		try {
			if (pkt.exists("3")) {
				primaryID = new YahooIdentity(pkt.getValue("3").trim());
			} else {
				primaryID = loginID;
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("primary identity in list", pkt, e);
		}

		// Set the primary and login flags on the relevant YahooIdentity objects
		primaryID.setPrimaryIdentity(true);
		loginID.setLoginIdentity(true);

		// If this was sent outside the login process, send an event
		// FIX: Leidson Campos (PlanetaMessenger.org) informs me that with
		// the condition left in he doesn't get this even. Presumably because
		// Yahoo are now sending these packets before login over??? This
		// needs investigation.
		/* if(loginOver) */

		eventDispatchQueue.append(new SessionEvent(this), ServiceType.LIST);
	}

	/**
	 * Process an incoming LOGOFF packet. If we get one of these it means Yahoo
	 * wants to throw us off the system. When logging in using the same Yahoo ID
	 * using a second client, I noticed the Yahoo server sent one of these (just
	 * a header, no contents) and closed the socket.
	 */
	protected void receiveLogoff(YMSG9Packet pkt) // 0x02
	{
		// Is this packet about us, or one of our online friends?
		if (!pkt.exists("7")) // About us
		{
			// Note: when this method returns, the input thread loop
			// which called it exits.
			sessionStatus = SessionState.UNSTARTED;
			ipThread.stopMe();
		} else
		// About friends
		{
			// Process optional section, friends going offline
			try {
				updateFriendsStatus(pkt);
			} catch (Exception e) {
				throw new YMSG9BadFormatException("online friends in logoff", pkt, e);
			}
		}
	}

	/**
	 * Process an incoming LOGON packet. If we get one of these it means the
	 * logon process has been successful. If the user has friends already
	 * online, an extra section of varying length is appended, starting with a
	 * count, and then detailing each friend in turn.
	 * 
	 * @throws IOException
	 */
	protected void receiveLogon(YMSG9Packet pkt) throws IOException // 0x01
	{
		if (sessionStatus != SessionState.CONNECTED) {
			throw new IllegalStateException(
					"I received a LOGON packet outside of the logging in proces. Don't know how to interpret this.");
		}
		try {
			// Is this packet about us, or one of our online friends?
			if (pkt.exists("7")) {
				// Process optional section, friends currently online
				try {
					updateFriendsStatus(pkt);
				} catch (Exception e) {
					throw new YMSG9BadFormatException(
							"online friends in logon", pkt, e);
				}
			}
			// Still logging in?
		} finally {
			if (sessionStatus != SessionState.LOGGED_ON) {
				// set inital presence state.
				if (status == Status.AVAILABLE) {
					transmitIsBack();
				} else {
					transmitIsAway();
				}

				sessionStatus = SessionState.LOGGED_ON;
			}
		}
	}

	/**
	 * Process an incoming MESSAGE packet. Message can be either online or
	 * offline, the latter having a datestamp of when they were sent.
	 */
	protected void receiveMessage(YMSG9Packet pkt) // 0x06
	{
		try {
			if (!pkt.exists("14")) {
				// Contains no message?
				return;
			} else if (pkt.status == Status.NOTINOFFICE.getValue()) {
				// Sent while we were offline
				int i = 0;
				// Read each message, until null
				while (pkt.getNthValue("31", i) != null) {
					final SessionEvent se;

					final String to = pkt.getNthValue("5", i);
					final String from = pkt.getNthValue("4", i);
					final String message = pkt.getNthValue("14", i);
					final String timestamp = pkt.getNthValue("15", i);

					if (timestamp == null || timestamp.length() == 0) {
						se = new SessionEvent(this, to, from, message);
					} else {
						final long timestampInMillis = 1000 * Long
								.parseLong(timestamp);
						se = new SessionEvent(this, to, from, message,
								timestampInMillis);
					}
					se.setStatus(pkt.status); // status!=0 means offline
					// message

					eventDispatchQueue.append(se, ServiceType.X_OFFLINE);
					i++;
				}
			} else {
				// Sent while we are online
				final String to = pkt.getValue("5");
				final String from = pkt.getValue("4");
				final String message = pkt.getValue("14");

				final SessionEvent se = new SessionEvent(this, to, from,
						message);
				if (se.getMessage().equalsIgnoreCase(NetworkConstants.BUZZ)) {
					eventDispatchQueue.append(se, ServiceType.X_BUZZ);
				} else {
					eventDispatchQueue.append(se, ServiceType.MESSAGE);
				}
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("message", pkt, e);
		}
	}

	/**
	 * Process an incoming NEWMAIL packet, informing us of how many unread Yahoo
	 * mail messages we have.
	 */
	protected void receiveNewMail(YMSG9Packet pkt) // 0x0b
	{
		try {
			SessionNewMailEvent se;
			if (!pkt.exists("43")) // Count only
			{
				se = new SessionNewMailEvent(this, pkt.getValue("9") // new
				// mail
				// count
				);
			} else
			// Mail message
			{
				se = new SessionNewMailEvent(this, pkt.getValue("43"), // from
						pkt.getValue("42"), // email address
						pkt.getValue("18") // subject
				);
			}
			eventDispatchQueue.append(se, ServiceType.NEWMAIL);
		} catch (Exception e) {
			throw new YMSG9BadFormatException("new mail", pkt, e);
		}
	}

	/**
	 * Process an incoming NOTIFY packet. "Typing" for example. (Why these
	 * things needs to be sent is beyond me!)
	 */
	protected void receiveNotify(YMSG9Packet pkt) // 0x4b
	{
		try {
			if (pkt.status == 0x01) // FIX: documentation says this should be
			// Status.TYPING (0x16)
			{
				SessionNotifyEvent se = new SessionNotifyEvent(this, pkt
						.getValue("5"), // to
						pkt.getValue("4"), // from
						pkt.getValue("14"), // message (game)
						pkt.getValue("49"), // type (typing/game)
						pkt.getValue("13") // mode (on/off)
				);
				se.setStatus(pkt.status);
				eventDispatchQueue.append(se, ServiceType.NOTIFY);
			}
		} catch (Exception e) {
			throw new YMSG9BadFormatException("notify", pkt, e);
		}
	}

	/**
	 * Process and incoming PING packet. When logging in under v10, this packet
	 * is sent appended to a LOGON. It contains only two fields, 143 and 144.
	 * Purpose as yet unknown.
	 */
	protected void receivePing(YMSG9Packet pkt) // 0x12
	{
	}

	/**
	 * Process and incoming USERSTAT packet.
	 */
	protected void receiveUserStat(YMSG9Packet pkt) // 0x0a
	{
		status = Status.getStatus(pkt.status);
	}

	/**
	 * Process an error CHATLOGIN packet. The only time these seem to be sent is
	 * when we fail to connect to a chat room - perhaps because it is full (?)
	 */
	protected void erroneousChatLogin(YMSG9Packet pkt) // 0x98
	{
		chatSessionStatus = SessionState.FAILED;
	}

	/**
	 * Note: the term 'packet' here refers to a YMSG message, not a TCP packet
	 * (although in almost all cases the two will be synonymous). This is to
	 * avoid confusion with a 'YMSG message' - the actual discussion packet.
	 * 
	 * service - the Yahoo service number status - the Yahoo status number (not
	 * sessionStatus above!) body - the payload of the packet
	 * 
	 * Note: it is assumed that the ConnectionHandler has been open()'d
	 */
	protected void sendPacket(PacketBodyBuffer body, ServiceType service,
			Status status) throws IOException {
		network.sendPacket(body, service, status.getValue(), sessionId);
	}

	protected void sendPacket(PacketBodyBuffer body, ServiceType service)
			throws IOException {
		sendPacket(body, service, Status.AVAILABLE);
	}

	/**
	 * Convenience method - have we passed a given time? (More readable)
	 */
	private boolean past(long time) {
		return (System.currentTimeMillis() > time);
	}

	/**
	 * Start threads
	 */
	private void openSession() throws IOException {
		// Open the socket, create input and output streams
		network.open();
		// Create a thread to handle input from network
		ipThread = new InputThread(this);
		ipThread.start();
		// Add a Runnable to periodically send ping packets for our connection
		pingerFuture = SCHEDULED_PINGER_SERVICE.scheduleAtFixedRate(new SessionPinger(this),
				NetworkConstants.PING_TIMEOUT_IN_SECS, NetworkConstants.PING_TIMEOUT_IN_SECS,
				TimeUnit.SECONDS);
	}

	/**
	 * If the network isn't closed already, close it.
	 */
	void closeSession() throws IOException {
		// Close the input thread (unless ipThread itself is calling us)
		if (ipThread != null && Thread.currentThread() != ipThread) {
			ipThread.stopMe();
			ipThread.interrupt();
			ipThread = null;
		}
		
		// Remove our pinger Runnable from scheduler
		if (pingerFuture != null) {
			pingerFuture.cancel(false);
			pingerFuture = null;
		}
		
		eventDispatchQueue.kill();
		// If the network is open, close it
		network.close();
	}

	/**
	 * Are we logged into Yahoo?
	 */
	private void checkStatus() throws IllegalStateException {
		if (sessionStatus != SessionState.LOGGED_ON) {
			throw new IllegalStateException("Not logged in");
		}
	}

	private void checkChatStatus() throws IllegalStateException {
		if (chatSessionStatus != SessionState.LOGGED_ON)
			throw new IllegalStateException("Not logged in to a chatroom");
	}

	/**
	 * Preform a clean up of all data fields to 'reset' instance
	 */
	private void resetData() {
		primaryID = null;
		loginID = null;
		password = null;
		cookieY = null;
		cookieT = null;
		cookieC = null;
		imvironment = null;
		customStatusMessage = null;
		customStatusBusy = false;
		conferences.clear();
		groups.clear();
		identities.clear();

		synchronized (typingNotifiers) {
			for (TypingNotifier notifier : typingNotifiers.values()) {
				notifier.quit = true;
				notifier.interrupt();
			}
			typingNotifiers.clear();
		}

		loginException = null;
	}

	/**
	 * A key 16 was received, send an error message event
	 */
	void errorMessage(YMSG9Packet pkt, String m) {
		if (m == null)
			m = pkt.getValue("16");
		SessionErrorEvent se = new SessionErrorEvent(this, m, pkt.service);
		if (pkt.exists("114"))
			se.setCode(Integer.parseInt(pkt.getValue("114").trim()));
		eventDispatchQueue.append(se, ServiceType.X_ERROR);
	}

	/**
	 * Handy method: alert application to exception via event
	 */
	void sendExceptionEvent(Exception e, String msg) {
		SessionExceptionEvent se = new SessionExceptionEvent(Session.this, msg,
				e);
		eventDispatchQueue.append(se, ServiceType.X_EXCEPTION);
	}

	/**
	 * Chat logins sometimes use multiple packets. The clue is that incomplete
	 * packets carry a status of 5, and the final packet carries a status of 1.
	 * This method compounds incoming 0x98 packets and returns null until the
	 * last ('1') packet is delivered, when it returns the compounded packet.
	 * 
	 * @return null if the current packet being processed was not the final
	 *         packet making up this login, otherwise the compounded packet will
	 *         be returned.
	 */
	private YMSG9Packet compoundChatLoginPacket(YMSG9Packet pkt) {
		if (pkt.status != 5 && pkt.status != 1) {
			throw new IllegalArgumentException("Status must be either 1 or 5.");
		}

		// Incomplete
		if (pkt.status == 5) {
			if (cachePacket == null) {
				cachePacket = pkt;
			} else {
				cachePacket.append(pkt);
			}
			return null;
		}

		// This is the last one making up the complete packet. Append and
		// return.
		if (cachePacket != null) {
			cachePacket.append(pkt);
			pkt = cachePacket;
			cachePacket = null;
		}
		return pkt;
	}

	/**
	 * LOGON packets can contain multiple friend status sections, ISAWAY and
	 * ISBACK packets contain only one. Update the YahooUser details and fire
	 * event.
	 */
	private void updateFriendsStatus(YMSG9Packet pkt) {
		// Online friends count, however count may be missing if == 1
		// (Note: only LOGON packets have multiple friends)
		String s = pkt.getValue("8");
		if (s == null && pkt.getValue("7") != null)
			s = "1";
		// If LOGOFF packet, the packet's user status is wrong (available)
		boolean logoff = (pkt.service == ServiceType.LOGOFF);
		// Process online friends data
		if (s != null) {
			int cnt = Integer.parseInt(s);
			SessionFriendEvent se = new SessionFriendEvent(this, cnt);
			// Process each friend
			for (int i = 0; i < cnt; i++) {
				// Update user (do not create new user, as client may
				// still have reference to old)
				YahooUser yu = userStore.get(pkt.getNthValue("7", i));
				// When we add a friend, we get a status update before
				// getting a confirmation FRIENDADD packet (crazy!)
				if (yu == null) {
					String n = pkt.getNthValue("7", i);
					yu = userStore.getOrCreate(n);
				}

				// 7=friend 10=status 17=chat 13=pager (old version)
				// 7=friend 10=status 13=chat&pager (new version May 2005)
				final long longStatus = Long
						.parseLong(pkt.getNthValue("10", i));
				final String friend = pkt.getNthValue("7", i);
				final Status status = logoff ? Status.OFFLINE : Status
						.getStatus(longStatus);
				if (pkt.exists("17")) {
					final boolean onChat = pkt.getNthValue("17", i).equals("1");
					final boolean onPager = pkt.getNthValue("13", i)
							.equals("1");
					yu.update(friend, status, onChat, onPager);
				} else {
					final String visibility = pkt.getNthValue("13", i);
					yu.update(friend, status, visibility);
				}

				// Custom message? 19=Custom status 47=Custom message
				if (pkt.getNthValue("19", i) != null
						&& pkt.getNthValue("47", i) != null) {
					yu.setCustom(pkt.getNthValue("19", i), pkt.getNthValue(
							"47", i));
				}
				// 137=Idle time (seconds) 138=Clear idle time
				s = pkt.getNthValue("138", i);
				if (s != null)
					yu.setIdleTime("-1");
				s = pkt.getNthValue("137", i);
				if (s != null)
					yu.setIdleTime(s);
				// 60=SMS
				// 197=Avatars
				// 192=Friends icon (checksum)
				// ...
				// Add to event object
				se.setUser(i, yu);
			}
			// Fire event
			eventDispatchQueue.append(se, ServiceType.ISAWAY);
		}
	}

	/**
	 * Inserts the given user into the desired group, if not already present.
	 * Creates the group if not present.
	 */
	private void insertFriend(YahooUser yahooUser, String groupName) {
		YahooGroup addToThis = null;
		for (YahooGroup group : groups) {
			if (group.getName().equalsIgnoreCase(groupName)) {
				addToThis = group;
				break;
			}
		}

		if (addToThis == null) {
			addToThis = new YahooGroup(groupName);
			groups.add(addToThis);
		}

		// Add user if needs be
		if (!addToThis.contains(yahooUser)) {
			addToThis.addUser(yahooUser);
			yahooUser.adjustGroupCount(+1);
		}
	}

	/**
	 * Deletes a friend from the desired group, if present. Removes the group
	 * too if now empty.
	 */
	private void deleteFriend(YahooUser user, String groupName) {
		for (YahooGroup group : groups) {
			if (group.getName().equalsIgnoreCase(groupName)) {
				group.removeUser(user);
				user.adjustGroupCount(-1);
				// If the groups is empty, remove it too
				if (group.isEmpty()) {
					groups.remove(group);
				}
				break;
			}
		}
	}

	/**
	 * Create chat user from a chat packet. Note: a YahooUser is create if
	 * necessary.
	 */
	private YahooChatUser createChatUser(YMSG9Packet pkt, int i) {
		pkt.generateQuickSetAccessors("109");
		final YahooUser yu = userStore.getOrCreate(pkt.getNthValue("109", i));

		final int attributes = Integer.parseInt(pkt.getValueFromNthSetQA("113",
				i));
		final String alias = pkt.getValueFromNthSetQA("141", i); // optional
		final int age = Integer.parseInt(pkt.getValueFromNthSetQA("110", i));
		final String location = pkt.getValueFromNthSetQA("142", i); // optional

		return new YahooChatUser(yu, attributes, alias, age, location);
	}

	/**
	 * Create a unique conference name
	 */
	private String getConferenceName(String yid) {
		return yid + "-" + conferenceCount++;
	}

	private YahooConference getConference(String room)
			throws NoSuchConferenceException {
		YahooConference yc = conferences.get(room);
		if (yc == null)
			throw new NoSuchConferenceException("Conference " + room
					+ " not found.");
		return yc;
	}

	YahooConference getOrCreateConference(YMSG9Packet pkt) {
		String room = pkt.getValue("57");
		YahooIdentity yid = identities.get(pkt.getValue("1"));
		YahooConference yc = conferences.get(room);
		if (yc == null) {
			yc = new YahooConference(userStore, yid, room, this);
			conferences.put(room, yc);
		}
		return yc;
	}

	/**
	 * Convenience method to add a event to the eventdispatcher (fire the event
	 * to all listeners).
	 * 
	 * @param event
	 *            The SessionEvent to be dispatched.
	 * @param type
	 *            Type of the event to be dispatched.
	 */
	void fire(SessionEvent event, ServiceType type) {
		if (event == null) {
			throw new IllegalArgumentException(
					"Argument 'event' cannot be null.");
		}

		if (type == null) {
			throw new IllegalArgumentException(
					"Argument 'type' cannot be null.");
		}

		eventDispatchQueue.append(event, type);
	}

	/**
	 * Thread for sending typing start/stop packets There are two key components
	 * here: The first is the keyPressed() method which timestamps the last
	 * keypress from its source AWT component and sends typing start packets
	 * when needed. The second is a thread which wakes infrequently to check if
	 * the last timestamp is older than the timeout, and sends a typing end
	 * packet if so.
	 */
	private class TypingNotifier extends java.awt.event.KeyAdapter implements
			Runnable {
		public volatile boolean quit = false; // Exit run in J2 compliant way

		private volatile long lastKey; // Timestamp of last keypress

		private volatile int timeout = 1000 * 30; // 30 second timeout

		private volatile boolean typing = false; // Current typing mode

		// (t=yes)

		private Thread thread; // Timeout monitoring thread

		private volatile Component typeSource; // Source of typing events

		// private int listenerCnt=0; // Count how often we've been added
		private volatile String target; // Yahoo id of target

		private volatile String identity; // Yahoo id of sender

		public TypingNotifier(Component com, String to, String from) {
			typeSource = com;
			target = to;
			identity = from;
			if (typeSource != null)
				typeSource.addKeyListener(this);
			thread = new Thread(this, "Typing Notification: " + from + "->"
					+ to);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}

		// KeyListener method
		@Override
		public void keyTyped(KeyEvent ev) {
			keyTyped();
		}

		// Process a typed key
		void keyTyped() {
			// Just incase we get an event before the constructor finished
			// or (more likely) we are actually connected to the network
			if (!thread.isAlive() || sessionStatus != SessionState.LOGGED_ON)
				return;
			// Store time of key press and sent message is needed
			lastKey = System.currentTimeMillis();
			if (!typing) {
				try {
					transmitNotify(target, identity, true, " ", NOTIFY_TYPING);
				} catch (IOException e) // IO problem? Event then exit thread
				{
					sendExceptionEvent(e, "Source: TypingNotifier");
					quit = true;
				}
			}
			typing = true;
		}

		// Thread to detect typing off (timeout)
		public void run() {
			try {
				while (!quit) {
					// Wake every second and check if typing ended
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// ignore
					}
					// Currently typing, and timed out? (And logged on?)
					if (sessionStatus == SessionState.LOGGED_ON && typing
							&& System.currentTimeMillis() - lastKey > timeout) {
						try {
							transmitNotify(target, identity, false, " ",
									NOTIFY_TYPING);
						} catch (IOException e) // IO problem? Event then exit
						// thread
						{
							sendExceptionEvent(e, "Source: TypingNotifier");
							quit = true;
						}
						typing = false;
					}
				}
			} finally {
				if (typeSource != null)
					typeSource.removeKeyListener(this);
			}
		}

		public void interrupt() {
			thread.interrupt();
		}

		public void stopTyping() {
			if (typing) {
				try {
					transmitNotify(target, identity, false, " ", NOTIFY_TYPING);
				} catch (IOException e) // IO problem? Event then exit thread
				{
					sendExceptionEvent(e, "Source: TypingNotifier");
					quit = true;
				}
				typing = false;
			}
		}
	}
}
