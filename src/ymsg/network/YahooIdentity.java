package ymsg.network;

// *********************************************************************
// Encapsulates a single identity that belongs to the current session.
// Yahoo enables users to have multiple personas via identities (aka
// profiles).  The 'primary' identity is the original Yahoo account,
// and all other identities are aliases thereof.
//
// Support for identities appears to have been tacked onto messenger
// after its initial release, being inconsistent and patchy in its
// implementation.  For example, while much of the protocol supports
// secondary identities (even logging into Yahoo messenger using a
// secondary identity is possible) other parts can only accept primary
// identities - for example sending a notify packet tagged with a
// secondary identity will result in the Yahoo server re-tagging the
// packet with the associated primary identity before it is delivered
// to its target.
//
// This 're-tagging' opens up potential security/privacy problems, as
// packets such as notify can betray the senders true identity.  They
// can also confuse the hell out of a Yahoo client (even the official
// client) if two IM windows are open to the same user under two or
// more of the identities, as all notify 'events' will be indicated in
// said users primary identity.
// *********************************************************************
public class YahooIdentity
{	protected String id;							// Yahoo id
	private boolean primary,login;					// Is primary or login id?
	private boolean active;

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// -----------------------------------------------------------------
	YahooIdentity(String i)
	{	id=i;  active=true;
	}

	// -----------------------------------------------------------------
	// Public accessors
	// -----------------------------------------------------------------
	public String getId() { return id; }
	
	public boolean isPrimaryIdentity() { return primary; }
	public boolean isLoginIdentity() { return login; }
	public boolean isActivated() { return active; }

	// -----------------------------------------------------------------
	// Package (default) setters
	// -----------------------------------------------------------------
	void setPrimaryIdentity(boolean b) { primary=b; }
	void setLoginIdentity(boolean b) { login=b; }
	void setActivated(boolean b) { active=b; }
	
	public String toString()
	{	return "id="+id+" primaryID="+primary+" loginID="+login+" activated="+active;
	}
}
