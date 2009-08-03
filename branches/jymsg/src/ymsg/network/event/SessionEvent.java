package ymsg.network.event;

import java.util.Date;

// *********************************************************************
// This class is the parent of all event classes in this package.
//
//							To		From	Message	Timestamp
// contactRejectionReceived	y		y		y		n
// contactRequestReceived	y		y		y		y
// messageReceived			y		y		y		n
// buzzReceived				y		y		y		n
// offlineMessageReceived	y		y		y		y
// listReceived				n		n		n		n
// logoffReceived			n		n		n		n
// *********************************************************************
public class SessionEvent extends java.util.EventObject
{	protected String to=null,from=null,message=null;
	protected Date timestamp;
	protected long status=0;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	public SessionEvent(Object o)
	{	super(o);
	}

	public SessionEvent(Object o,String t,String f)
	{	this(o);  to=t;  from=f;
	}

	public SessionEvent(Object o,String t,String f,String m)	// Online message
	{	this(o,t,f);  message=m;
	}

	public SessionEvent(Object o,String t,String f,String m,String dt) // Offline message
	{	this(o,t,f,m);
		try { timestamp = new Date(Long.parseLong(dt)*1000); }
			catch(NumberFormatException e) { timestamp = null; }
	}


	// -----------------------------------------------------------------
	// Accessors
	// -----------------------------------------------------------------
	public String getTo() { return to; }
	public String getFrom() { return from; }
	public String getMessage() { return message; }
	public Date getTimestamp() { return timestamp; }

	public long getStatus() { return status; }
	public void setStatus(long s) { status=s; }

	public String toString()
	{	return "to:"+to+" from:"+from+" message:"+message+
			" timestamp:"+timestamp;
	}
}
