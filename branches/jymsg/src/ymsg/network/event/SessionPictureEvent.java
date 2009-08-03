package ymsg.network.event;

// *********************************************************************
// *********************************************************************
public class SessionPictureEvent
    extends SessionEvent
{	
    protected byte[] data;
    
    // -----------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------
    public SessionPictureEvent(Object o,String t,String f,byte[] data)
    {	super(o,t,f);
        this.data = data;
    }
    
    public byte[] getPictureData()
    { return data; }
    
    public String toString()
    {	return super.toString()+" data.len:"+data.length;
    }
}
