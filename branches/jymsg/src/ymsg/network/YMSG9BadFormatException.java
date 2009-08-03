package ymsg.network;

import java.lang.reflect.Method;

public class YMSG9BadFormatException extends java.lang.RuntimeException
{	private static Method initCauseMethod;		// Exception chaining
	private Throwable throwable;				// Chained object
	
	// -----------------------------------------------------------------
	// STATIC CONSTRUCTOR
	// -----------------------------------------------------------------
	static
	{	// -----Use reflection to find the initCause method, to remain backward
		// -----compatable with pre SDK1.4 runtimes which don't carry it.
		try
		{	Class[] params = { Throwable.class };
			initCauseMethod = new YMSG9BadFormatException().getClass().getMethod("initCause",params);
		}catch(NoSuchMethodException e) { initCauseMethod=null; }
	}

	private YMSG9BadFormatException() {}

	// -----------------------------------------------------------------
	// CONSTRUCTORS - regular constructors.
	// -----------------------------------------------------------------
	YMSG9BadFormatException(String m,boolean b) { super("Bad parse of "+m+" packet"); }
	YMSG9BadFormatException(String m) { super(m); }
	// -----------------------------------------------------------------
	// CONSTRUCTOR - supporting exception chaining.
	// -----------------------------------------------------------------
	YMSG9BadFormatException(String m,boolean b,Throwable ex)
	{	this(m,b);
		// -----Record local copy of exception, for non-SDK1.4 runtimes.
		throwable = ex;
		// -----If >= SDK1.4, this won't be null
		if(initCauseMethod!=null)
		{	try
			{	Throwable[] params = { throwable };
				initCauseMethod.invoke(this,params);
			}catch(Exception e) {}
		}
	}
	
	public Throwable getCausingThrowable() { return throwable; }
}
