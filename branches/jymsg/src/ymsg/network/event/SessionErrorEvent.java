package ymsg.network.event;

// *********************************************************************
//						Message		Service			Code
// errorPacketReceived	y			y (or null)		y (or -1)
// *********************************************************************
public class SessionErrorEvent extends SessionEvent
{	protected int service,code=-1;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	public SessionErrorEvent(Object o,String m,int sv)
	{	super(o);  message=m;  service=sv;
	}

	public void setCode(int c) { code=c; }

	public int getService() { return service; }
	public int getCode() { return code; }

	public String toString()
	{	return "Error: message=\""+message+"\" service=0x"+Integer.toHexString(service);
	}
}
