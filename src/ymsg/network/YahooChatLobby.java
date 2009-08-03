package ymsg.network;

import java.util.*;

// *********************************************************************
// Represents a single chat lobby.  Yahoo chatrooms consist of one or
// more numbered lobbies inside each public/private room.  The name of
// room and the number of the lobby (separated by a colon) form the
// 'network name' of the lobby - used by Yahoo to identify uniquely a
// given chat 'space' on its systems.  Each lobby has a count of users,
// a count of voice chat users, and a count of webcam users.  See also
// YahooChatRoom and YahooChatCategory.
//
// [*] = These counts are populated by the methods which load category
// and room structures in YahooChatCategory - they are not updated after
// they are initially created.  (FIX!)
// *********************************************************************
public class YahooChatLobby
{	protected int count;			// Lobby number
	protected int userCount;		// Number of users [*]
	protected int voiceCount;		// Number of users on voice chat [*]
	protected int webcamCount;		// Number of users with webcam [*]
	protected YahooChatRoom parent;	// Parent room

	private String netName=null;	// Name used in network protocol

	private Hashtable users;		// Key=id (string)  Value=YahooChatUser

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// -----------------------------------------------------------------
	YahooChatLobby(int cnt,int usr,int vce,int web)
	{	count=cnt;  userCount=usr;  voiceCount=vce;  webcamCount=web;
		users = new Hashtable();
	}

	void setParent(YahooChatRoom ycr)
	{	parent=ycr;  netName=parent.getName()+":"+count;
	}

	// -----------------------------------------------------------------
	// User methods, package accessibility
	// -----------------------------------------------------------------
	void addUser(YahooChatUser ycu) { users.put(ycu.getId(),ycu); }
	void removeUser(YahooChatUser ycu) { users.remove(ycu.getId()); }
	void clearUsers() { users.clear(); }
	boolean exists(YahooChatUser ycu) {	return users.containsKey(ycu.getId()); }
	YahooChatUser getUser(String id) { return (YahooChatUser)users.get(id); }

	// -----------------------------------------------------------------
	// Public accessors
	// -----------------------------------------------------------------
	public int getLobbyNumber() { return count; }
	public int getUserCount() { return userCount; }
	public int getVoiceCount() { return voiceCount; }
	public int getWebcamCount() { return webcamCount; }
	public String getNetworkName() { return netName; }
	public YahooChatRoom getParent() { return parent; }
	public Vector getMembers()
	{	Vector v = new Vector(userCount);
		for(Enumeration e=users.elements();e.hasMoreElements();)
			v.addElement(e.nextElement());
		return v;
	}

	public String toString()
	{	return "count="+count+" users="+userCount+" voices="+voiceCount+
			" webcams="+webcamCount+" netname="+netName;
	}
}
