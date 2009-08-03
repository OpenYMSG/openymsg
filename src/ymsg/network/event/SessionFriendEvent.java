package ymsg.network.event;

import ymsg.network.YahooUser;

// *********************************************************************
//							From	Friend		Friends		Group
// friendsUpdateReceived	y		y			y			n
// friendAddedReceived		y		y			y			y
// friendRemovedReceived	y		y			y			y
// *********************************************************************
public class SessionFriendEvent extends SessionEvent
{	protected YahooUser[] list;
	protected String group;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	public SessionFriendEvent(Object o,int sz)  // Friends list update
	{	super(o);
		list = new YahooUser[sz];  group=null;
	}

	public SessionFriendEvent(Object o,YahooUser yu,String gp)  // Friend added
	{	this(o,1);
		setUser(0,yu);  group=gp;
	}

	// -----------------------------------------------------------------
	// Accessors
	// -----------------------------------------------------------------
	// -----Friends update
	public void setUser(int i,YahooUser yu) { list[i]=yu; }
	public YahooUser[] getFriends() { return list; }
	// -----Friend added
	public YahooUser getFriend() { return list[0]; }
	public String getGroup() { return group; }
	public String getFrom() { return list[0].getId(); }

	public String toString()
	{	if(list.length>1)
			return super.toString()+" list(size):"+list.length;
		else
			return super.toString()+" friends are "+list.length+" group:"+group;
	}
}
