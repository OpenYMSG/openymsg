package ymsg.network.event;

// *********************************************************************
// This class is used to pass exceptions from the input thread to the
// main application.
//
//							Message		Exception
// inputExceptionThrown		y			y
// *********************************************************************
public class SessionExceptionEvent extends SessionEvent
{	protected Exception exception;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	public SessionExceptionEvent(Object o,String m,Exception e)
	{	super(o);  message=m;  exception=e;
	}

	public Exception getException() { return exception; }

	public String toString()
	{	return "Exception: message=\""+message+"\" type="+exception.toString();
	}

}
