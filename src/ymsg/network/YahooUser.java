package ymsg.network;

import java.util.Hashtable;
import java.util.Enumeration;

// *********************************************************************
// This class represents a single canonical represenation of a user on
// Yahoo.  For each user there is only ever one single instance of this
// class - as updated details arrive, an existing object is sought for
// and updated.  This is to prevent code holding onto YahooUser references
// from pointing to stale data.
//
// The groupCount integer is used to service the isFriend() method.  When
// added to a group it is incremented, when removed it is decremented.  
// When zero, it means this user is not part of the client's friends list.
//
// Note: this API cannot guarantee the accuracy of details held on users
// who's contact with you has expired.  So... if you leave a chatroom
// in which 'fred' was a member, 'fred's object will continue to be in
// the hashtable - BUT his status will almost certainly not be updated
// any longer (unless, of course, you have a link with him via some other
// route - like if he is on your friends list too!)
// *********************************************************************
public class YahooUser
{	protected String id;							// Yahoo id
	protected long status;							// Status (away, etc)
	protected boolean onChat,onPager,ignored;		// Flags
	protected String customStatusMessage;			// Custom away
	protected boolean customStatusBusy;				// Ditto
	private int groupCount=0;						// Friends group count
    private byte[] image;
    protected long versionId;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// Do not called these manually.  UserStore is a cache of canonical 
	// YahooUser objects - use the getOrCreate() methods in that class so
	// the user objects are correctly added to that cache.
	// -----------------------------------------------------------------
	YahooUser(String i,long st,boolean ch,boolean pg)
	{	update(i,st,ch,pg);
	}

	YahooUser(String i,String st,String ch,String pg)
	{	update(i,st,ch,pg);
	}

	YahooUser(String i)
	{	this(i,StatusConstants.STATUS_OFFLINE,false,false);
	}


	// -----------------------------------------------------------------
	// Accessors
	// -----------------------------------------------------------------
	void setIgnored(boolean i)
	{	ignored=i;
	}
	void setCustom(String m,String a)
	{	customStatusMessage=m;  customStatusBusy=a.charAt(0)=='1';
	}
    void setCustomStatusMessage(String m)
    {
        customStatusMessage=m;
    }

	void adjustGroupCount(int inc) { groupCount+=inc; }

	// -----------------------------------------------------------------
	// Public accessors
	// -----------------------------------------------------------------
	public String getId() { return id; }
	public long getStatus() { return status; }
    public long getVersionId() { return versionId; }
	public boolean isOnChat() { return onChat; }
	public boolean isOnPager() { return onPager; }
	public boolean isLoggedIn() { return (onChat||onPager); }
	public boolean isIgnored() { return ignored; }
	public String getCustomStatusMessage() { return customStatusMessage; }
	public boolean isCustomBusy() { return customStatusBusy; }
	public boolean isFriend() { return (groupCount>0); }

	public String toString()
	{	return "id="+id+" status=0x"+Long.toHexString(status)+
			" chat?="+onChat+" pager?="+onPager+" ignored?="+ignored+
			" custMesg="+customStatusMessage+" custBusy?="+customStatusBusy+
			" friend?="+isFriend();
	}

	// -----------------------------------------------------------------
	// Update this object's details
	// -----------------------------------------------------------------
	// -----This is the new version, where 13=combined pager/chat
	void update(String i,String st,String vs)
	{	int visibility=0;
		visibility = (vs==null) ? 0 : Integer.parseInt(vs);
		update( i , Long.parseLong(st) , (visibility&2)>0 , (visibility&1)>0 );
	}
	// -----This is the old version, when 13=pager and 17=chat
	void update(String i,String st,String ch,String pg)
	{	update( i , Long.parseLong(st) , 
			(ch!=null && ch.charAt(0)=='1') , (pg!=null && pg.charAt(0)=='1') );
	}
	void update(String i,long st,boolean ch,boolean pg)
	{	id=i;  status=st;  onChat=ch;  onPager=pg;
		if(status != StatusConstants.STATUS_CUSTOM)
		{	customStatusMessage=null;  customStatusBusy=false;
		}
	}

    /**
     * @return the image
     */
    public byte[] getImage()
    {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(byte[] image)
    {
        this.image = image;
    }
}
