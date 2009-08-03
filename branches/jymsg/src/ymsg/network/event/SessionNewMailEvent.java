package ymsg.network.event;


// *********************************************************************
//						MailCount	From	EmailA.	Subject	Message
// newMailReceived		y (gt 0)	n		n(?)	n		n
// -- ditto --	(v2)	y (eq 0)	y		y		y		y
// *********************************************************************
public class SessionNewMailEvent extends SessionEvent
{	protected int mail;
	protected String subject,address;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	public SessionNewMailEvent(Object o,String ml)
	{	super(o);
		mail = Integer.parseInt(ml);
	}

	public SessionNewMailEvent(Object o,String fr,String em,String sb)
	{	super(o,null,fr,null);
		mail=0;  address=em;  subject=sb;
	}

	// -----------------------------------------------------------------
	// Accessors
	// -----------------------------------------------------------------
	public int getMailCount() { return mail; }
	public String getSubject() { return subject; }
	public String getEmailAddress() { return address; }
	public boolean isWholeMail() { return (mail==0); }

	public String toString()
	{	return super.toString()+" mail:"+mail+" addr:"+address+" subject:"+subject;
	}
}
