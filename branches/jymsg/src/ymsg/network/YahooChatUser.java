package ymsg.network;

// *********************************************************************
// This class wraps a regular YahooUser to provide the extra information
// provided for each chat user.
//
// Note: if a YahooUser object for this user does not exist, one is
// automatically created and added to the static users hash in YahooUser.
// *********************************************************************
public class YahooChatUser
{	private YahooUser user;							// Regular Yahoo user object
	private int age,attributes;						// Age and flags
	private String alias,location;					// Alias(?) and location

	private final static int MALE_ATTR =	0x08000;
	private final static int FEMALE_ATTR =	0x10000;
	private final static int WEBCAM_ATTR =	0x00010;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	YahooChatUser(YahooUser yu,int at,String al,int ag,String loc)
	{	user=yu;  attributes=at;  age=ag;  alias=al;  location=loc;		
	}
	
	YahooChatUser(YahooUser yu,String at,String al,String ag,String loc)
	{	user=yu;  update(at,al,ag,loc);
	}
	
	/*private void _init(String i)
	{	// -----Does a YahooUser object of this id already exist?  If not, create!
		if(!YahooUser.contains(i))  new YahooUser(i,StatusConstants.STATUS_AVAILABLE,true,true);
		user = YahooUser.get(i);
	}*/
	
	void update(String at,String al,String ag,String loc)
	{	int a,b;
		try { a=Integer.parseInt(at); } catch(NumberFormatException e) { a=0; }
		try { b=Integer.parseInt(ag); } catch(NumberFormatException e) { b=0; }
		attributes=a;  age=b;  alias=al;  location=loc;
	}

	
	// -----------------------------------------------------------------
	// Accessors
	// -----------------------------------------------------------------
	public YahooUser getUser() { return user; }
	public String getId() { return user.getId(); }
	public int getAge() { return age; }
	public String getAlias() { return alias; }
	public String getLocation() { return location; }
	public int getAttributes() { return attributes; }  // REMOVE THIS AT SOME POINT
	public boolean isMale() { return ((attributes & MALE_ATTR)>0); }
	public boolean isFemale() { return ((attributes & FEMALE_ATTR)>0); }
	public boolean hasWebcam() { return ((attributes & WEBCAM_ATTR)>0); }

	public String toString() 
	{	return "user=["+user+"] age="+age+" attributes="+
			Integer.toHexString(attributes)+" alias="+alias+" location="+location;
	}
}
