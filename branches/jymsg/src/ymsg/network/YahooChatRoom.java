package ymsg.network;

import java.util.Vector;

// *********************************************************************
// Represents a single chat room, either public (Yahoo owned) or private
// (user owned).  Each room is divided into multiple independent chat
// spaces, known as 'lobbies'.  Rooms are placed into a hierarchy structure
// of named categories.  See also YahooChatLobby and YahooChatCategory.
// *********************************************************************
public class YahooChatRoom
{	protected String name,rawName;			// Name of room
	protected String topic;					// Topic of room
	protected long id;						// Id code
	protected boolean access;				// True=public, false=private
	protected Vector lobbies;				// YahooChatLobby objects

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// -----------------------------------------------------------------
	YahooChatRoom(long i,String nm,String tp,boolean ac)
	{	id=i;  rawName=nm;  name=Util.entityDecode(rawName);  topic=tp;  access=ac;
		lobbies = new Vector();
	}

	void addLobby(YahooChatLobby l)
	{	l.setParent(this);
		lobbies.addElement(l);
	}

	String getRawName() { return rawName; }

	// -----Accessors
	public String getName() { return name; }
	public long getId() { return id; }
	public boolean isPublic() { return access; }
	public Vector getLobbies() { return lobbies; }
	public int size() { return lobbies.size(); }

	public String toString() { return "name="+name+" id="+id+
	" public?="+access+" lobbies="+lobbies.size(); }
}
