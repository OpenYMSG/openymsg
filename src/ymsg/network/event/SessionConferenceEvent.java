package ymsg.network.event;

import ymsg.network.YahooConference;
import ymsg.network.YahooUser;

// *********************************************************************
//								From	To		Message	Room	Users	User
// conferenceInviteReceived		y		y		y(top.)	y		y		n
// conferenceLogonReceived		y		y		n		y		n		y
// conferenceLogoffReceived		y		y		n		y		n		y
// conferenceMessageReceived	y		y		y		y		n		y
// *********************************************************************
public class SessionConferenceEvent extends SessionEvent
{	private YahooConference room;
	private YahooUser[] users;

	public SessionConferenceEvent(Object o,String t,String f,String m,YahooConference r)
	{	super(o,t,f,m);  room=r;
	}
	public SessionConferenceEvent(Object o,String t,String f,String m,YahooConference r,YahooUser[] u)
	{	this(o,t,f,m,r);  users=u;
	}

	public YahooConference getRoom() { return room; }
	public YahooUser[] getUsers() { return users; }
	public YahooUser getUser() { return users[0]; }
	public String getTopic() { return getMessage(); }

	public String toString()
	{	if(users!=null)
			return super.toString()+" room:"+room+" users(size):"+users.length;
		else
			return super.toString()+" room:"+room;
	}
}
