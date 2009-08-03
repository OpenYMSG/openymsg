package ymsg.network.event;

import ymsg.network.YahooChatUser;
import ymsg.network.YahooChatLobby;

// *********************************************************************
// Note: this class is designed to hold more than one chat user.  However
// in real life Yahoo never actually seems to send details of more than
// one user in a single packet (except the for initial packet containing
// the list of users in the room when you first join - but this event
// isn't used for that) ... However, the packet does contain a user count
// - so to play it safe this class uses an array.
//
// 						From	ChatUser	ChatUsers	Lobby	Message	Emote
// chatLogonReceived	y		y			y			y		n		n
// chatLogoffReceived	y		y			y			y		n		n
// chatMessageReceived	y		y			y			y		y		y
// chatConnectionBroken n		n			n			n		n		n
// *********************************************************************
public class SessionChatEvent extends SessionEvent
{	protected YahooChatUser[] users;
	protected YahooChatLobby lobby;
	protected boolean emote;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	// -----Chat user joined/left
	public SessionChatEvent(Object o,int sz,YahooChatLobby ycl)
	{	super(o);
		users = new YahooChatUser[sz];  lobby=ycl;
	}

	// -----Message received
	public SessionChatEvent(Object o,YahooChatUser ycu,String m,String em,YahooChatLobby ycl)
	{	this(o,1,ycl);  setChatUser(0,ycu);
		message=m;  emote=(em!=null && em.equals("2"));
	}

	public void setChatUser(int i,YahooChatUser ycu) { users[i]=ycu; }

	// -----------------------------------------------------------------
	// Accessors
	// -----------------------------------------------------------------
	// -----User joined room
	public YahooChatUser getChatUser() { return users[0]; }
	public YahooChatUser[] getChatUsers() { return users; }
	public YahooChatLobby getLobby() { return lobby; }
	public String getFrom() { return users[0].getId(); }
	public boolean isEmote() { return emote; }

	public String toString()
	{	return super.toString()+" size:"+users.length+" chatuser:"+users[0].getId()+
			" lobby:"+lobby.getNetworkName();
	}
}
