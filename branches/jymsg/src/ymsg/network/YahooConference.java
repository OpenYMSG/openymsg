package ymsg.network;

import java.util.Vector;

// *********************************************************************
// As conference packets can be received in an inconvenient order, this
// class carries a lot of code to compensate.  Conference packets can
// actually arrive both before and (probably) after the formal lifetime
// of the conference (from invite received/accepted to logoff).
//
// Packets which arrive before an invite are buffered.  When an invite
// arrives the packets are fetched and the buffer null'd (which is
// then used as a flag to determine whether an invite has arrived
// or not).  By using this method, the API user will *ALWAYS* get an
// invite before any other packets.
//
// The closed flag marks a closed conference.  Packets arriving after
// this time should be ignored.
//
// The users list should not contain any of our user's own identities.
// This is why they are screened out by the addUser/addUsers methods.
// *********************************************************************
public class YahooConference
{	protected Vector users;					// YahooUser's in this conference
	protected String room;					// Room name
	private boolean closed;					// Conference has been exited?
	private Vector packetBuffer;			// Buffer packets before invite
	private Session parent;					// Parent session object
	private YahooIdentity identity;			// Yahoo identity for this conf.
	private UserStore userStore;			// Canonical user list

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// Note: the first constructor is used when *we* create a conference,
	// the second is used when we are invited to someone else's conference.
	// When *we* create a conference, there is no need to buffer packets
	// prior to an invite.
	// -----------------------------------------------------------------
	YahooConference(UserStore us,YahooIdentity yid,String r,Session ss,boolean b)
	{	userStore=us;  identity=yid;  users = new Vector();  parent=ss;
		room=r;  closed=false;  
		if(b) packetBuffer = new Vector();
			else  packetBuffer=null;
	}
	
	YahooConference(UserStore us,YahooIdentity yid,String r,Session ss)
	{	this(us,yid,r,ss,true);
	}

	// -----------------------------------------------------------------
	// The closed flag is set when this conference is exited.  All
	// further packets from this conference should be ignored.
	// -----------------------------------------------------------------
	void closeConference() { closed=true; }

	
	// -----------------------------------------------------------------
	// Public accessors
	// -----------------------------------------------------------------
	public String getName() { return room; }
	public boolean isClosed() { return closed; }
	public Vector getMembers() { return (Vector)users.clone(); }
	public YahooIdentity getIdentity() { return identity; }

	public String toString()
	{	return "name="+room+" users="+users.size()+" id="+identity.getId()+" closed?="+closed;
	}

	// -----------------------------------------------------------------
	// The packetBuffer object is created when the conference is created
	// and set to null when the conference invite actually arrives.
	// -----------------------------------------------------------------
	// -----Have we been invited yet?
	boolean isInvited() { return (packetBuffer==null); }
	// -----We're received an invite, change status and return buffer
	Vector inviteReceived()
	{	Vector v=packetBuffer;  packetBuffer=null;
		return v;
	}
	// -----Add a packet to the buffer
	void addPacket(YMSG9Packet ev)
	{	if(packetBuffer==null)  throw new IllegalStateException("Cannot buffer packets, invite already received");
		packetBuffer.addElement(ev);
	}

	// -----------------------------------------------------------------
	// Add to and get user list
	// -----------------------------------------------------------------
	Vector getUsers() { return users; }
	synchronized void addUsers(String[] u)
	{	for(int i=0;i<u.length;i++)  addUser(u[i]);
	}
	synchronized void addUser(String u)
	{	if(!exists(u) && !id(u))  
		{	users.addElement(userStore.getOrCreate(u));
		}
	}
	synchronized void removeUser(String u)
	{	for(int i=0;i<users.size();i++)
		{	if(_get(i).getId().equals(u))
			{	users.removeElementAt(i);  return;
			}
		}
	}

	private YahooUser _get(int i) { return (YahooUser)users.elementAt(i); }

	// -----------------------------------------------------------------
	// Does a user exist in Vector (uses .equals() on user id)
	// -----------------------------------------------------------------
	private boolean exists(String s)
	{	for(int i=0;i<users.size();i++)
			if(_get(i).getId().equals(s))  return true;
		return false;
	}

	// -----------------------------------------------------------------
	// Is this one of the user's identities?
	// -----------------------------------------------------------------
	private boolean id(String s)
	{	YahooIdentity[] identities = parent.getIdentities();
		for(int i=0;i<identities.length;i++)
			if(identities[i].getId().equals(s))  return true;
		return false;
	}
}
