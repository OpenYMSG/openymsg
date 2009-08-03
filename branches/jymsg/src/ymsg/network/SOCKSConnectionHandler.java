package ymsg.network;

import java.util.Properties;

public class SOCKSConnectionHandler extends DirectConnectionHandler implements NetworkConstants
{	private String socksHost;				// Socks service host
	private int socksPort;					// Socks service port
		
	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// Reads the SOCKS setting from Java properties.
	// -----------------------------------------------------------------
	public SOCKSConnectionHandler() throws IllegalArgumentException
	{	socksHost = System.getProperty(SOCKS_HOST,"");
		socksPort = Integer.parseInt(System.getProperty(SOCKS_PORT,"-1"));
		if(socksHost.length()<=0 || socksPort<=0)
			throw new IllegalArgumentException("Bad SOCKS proxy properties: "+
				socksHost+":"+socksPort);
		System.getProperties().put(SOCKS_SET,"true");
	}
	
	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// Sets specific SOCKS server/port.  Note: these settings will be
	// global to all Socket's across the JVM.
	// -----------------------------------------------------------------
	public SOCKSConnectionHandler(String h,int p)
	{	socksHost=h;  socksPort=p;
		Properties pr = System.getProperties();
		pr.put(SOCKS_HOST,socksHost);
		pr.put(SOCKS_PORT,socksPort+"");
		pr.put(SOCKS_SET,"true");
	}

	public String toString()
	{	return "SOCKS connection: "+socksHost+":"+socksPort;
	}
}
