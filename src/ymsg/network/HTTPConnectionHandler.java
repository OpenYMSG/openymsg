package ymsg.network;

import java.util.*;
import java.io.*;
import java.net.Socket;

public class HTTPConnectionHandler extends ConnectionHandler
implements ServiceConstants, NetworkConstants
{	private ThreadGroup threadGroup;		// Thread container
	private Session session;				// Associated session object
	private String proxyHost;				// HTTP proxy host name
	private int proxyPort;					// HTTP proxy post
	private long lastFetch;					// Time of last packet fetch
	private Vector packets;					// Incoming packet queue
	private boolean connected=false;		// Sending/receiving data?
	private boolean quitFlag=false;			// Exit thread
	private String cookie=null;				// HTTP cookie field
	private long identifier=0;				// Some kind of id, from LOGON incoming
	private Notifier notifierThread;		// Send IDLE packets on timeout

	/* These are now in NetworkConstants (implemented by ConnectionHandler)
	private final static String END = "\n";
	private final static String PROXY_HOST = "proxyHost";
	private final static String PROXY_PORT = "proxyPort";
	private final static String PROXY_SET = "proxySet";
	private final static String PROXY_NON = "http.nonProxySet";
	*/
	/* These are now determined by properties and Util.class
	private final static String HOST = "http.pager.yahoo.com";
	private final static String HTTP_POST = "POST http://"+HOST+"/notify HTTP/1.0"+END;
	private final static String HTTP_AGENT = "User-Agent: "+USER_AGENT+END;
	private final static String HTTP_HOST = "Host: "+HOST+END;
	*/
	private final static long IDLE_TIMEOUT = 30*1000;

	private String headerHttpPost;			// HTTP top line
	private String headerAgent;				// Headers
	private String headerHost;				// ,,
	private String headerProxyAuth;			// ,,


	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// Attempts to read the HTTP proxy settings from property settings
	// -----------------------------------------------------------------
	public HTTPConnectionHandler() throws IllegalArgumentException
	{	_init();
		proxyHost = Util.httpProxyHost();
		proxyPort = Util.httpProxyPort();
		if(proxyHost==null || proxyPort<=0)
			throw new IllegalArgumentException("Bad HTTP proxy properties");
	}

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// Sets the HTTP proxy properties to a specific host and port, with
	// exception list of hosts to not proxy.
	// -----------------------------------------------------------------
	public HTTPConnectionHandler(String ph,int pp,String ex)
	{	_init();
		proxyHost=ph;  proxyPort=pp;
		// -----Names are prefixed with "http." for 1.3 and after
		Properties p = System.getProperties();
		p.put(PROXY_HOST_OLD,proxyHost);	p.put(PROXY_HOST,proxyHost);
		p.put(PROXY_PORT_OLD,proxyPort+"");	p.put(PROXY_PORT,proxyPort+"");
		p.put(PROXY_SET,"true");
		// -----Only supported in 1.3 and after (?)
		if(ex!=null)  p.put(PROXY_NON,ex);  else  p.remove(PROXY_NON);
	}

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// Sets the HTTP proxy properties to a specific host and port, with
	// exception list of hosts to not proxy.
	// -----------------------------------------------------------------
	public HTTPConnectionHandler(String ph,int pp,Vector ex)
	{	this(ph,pp,(String)null);
		// -----Expand list to item|item|... , then store
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<ex.size();i++)
		{	sb.append( (String)ex.elementAt(i) );
			if(i+1<ex.size()) sb.append("|");
		}
		System.getProperties().put(PROXY_NON,sb.toString());
	}

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// Sets the HTTP proxy properties to a specific host and port
	// -----------------------------------------------------------------
	public HTTPConnectionHandler(String ph,int pp)
	{	this(ph,pp,(String)null);
	}

	private void _init()
	{	packets = new Vector(5);  connected=false;
		// -----HTTP headers etc.
		String host = Util.httpHost();
		headerHttpPost = "POST http://"+host+"/notify HTTP/1.0"+END;
		headerAgent = "User-Agent: "+USER_AGENT+END;
		headerHost = "Host: "+host+END;
		headerProxyAuth = "Proxy-Authorization: "+Util.httpProxyAuth()+END;
	}

	// -----------------------------------------------------------------
	// High level method for setting proxy authorization, for users
	// behind firewalls which require credentials to access a HTTP proxy.
	// -----------------------------------------------------------------
	public static void setProxyAuthorizationProperty(String method,String username,String password)
	throws UnsupportedOperationException
	{	if(method.equalsIgnoreCase("basic"))
		{	String a = username+":"+password;
			String s = "Basic "+Util.base64(a.getBytes());
			System.setProperty(PropertyConstants.HTTP_PROXY_AUTH,s);
		}
		else
		{	throw new UnsupportedOperationException("Method "+method+" unsupported.");
		}
	}

	// -----------------------------------------------------------------
	// **ConnectionHandler methods start
	// -----------------------------------------------------------------

	// -----------------------------------------------------------------
	// Session calls this when a connection handler is installed
	// -----------------------------------------------------------------
	void install(Session ss,ThreadGroup tg)
	{	session=ss;  threadGroup=tg;
	}

	// -----------------------------------------------------------------
	// Do nothing more than make a note that we are on/off line
	// -----------------------------------------------------------------
	void open()
	{	connected=true;
		synchronized(this)				// In case two threads call open()
		{	if(notifierThread==null)
				notifierThread = new Notifier(threadGroup,"HTTP Notifier");
		}
	}
	void close()
	{	connected=false;
		synchronized(this)				// In case two threads call close()
		{	if(notifierThread!=null)
			{	notifierThread.quitFlag=true;  notifierThread=null;
			}
		}
	}

	// -----------------------------------------------------------------
	// The only time Yahoo can actually send us any packets is when we
	// send it some.  Yahoo encodes its packets in a POST body - the
	// format is the same binary representation used for direct connections
	// (with one or two extra codes).
	//
	// After posting a packet, the connection will receive a HTTP response
	// who's payload consists of four bytes followed by zero or more
	// packets.  The first byte of the four is a count of packets encoded
	// in the following body.
	//
	// Each incoming packet is transfered to a queue, where receivePacket()
	// takes them off - thus preserving the effect that input and output
	// packets are being received independently, as with other connection
	// handlers.  As readPacket() can throw an exception, these are caught
	// and transfered onto the queue too, then rethrown by receivePacket() .
	// -----------------------------------------------------------------
	synchronized void sendPacket(PacketBodyBuffer body,int service,long status,long sessionID)
	throws IOException,IllegalStateException
	{	if(!connected)  throw new IllegalStateException("Not logged in");
		
		if( filterOutput(body,service) )  return;
		byte[] b = body.getBuffer();

		Socket soc = new Socket(proxyHost,proxyPort);
		PushbackInputStream pbis = new PushbackInputStream(soc.getInputStream());
		DataOutputStream dos = new DataOutputStream(soc.getOutputStream());

		// -----HTTP header
		dos.writeBytes(headerHttpPost);
		dos.writeBytes("Content-length: "+(b.length+YMSG9_HEADER_SIZE)+END);
		dos.writeBytes(headerAgent);
		dos.writeBytes(headerHost);
		if(headerProxyAuth!=null)  dos.writeBytes(headerProxyAuth);
		if(cookie!=null)  dos.writeBytes("Cookie: "+cookie+END);
		dos.writeBytes(END);
		// -----YMSG9 header
		dos.write(MAGIC,0,4);
		dos.write(VERSION_HTTP,0,4);
		dos.writeShort(b.length & 0xffff);
		dos.writeShort(service & 0xffff);
		dos.writeInt((int)(status & 0xffffffff));
		dos.writeInt((int)(sessionID & 0xffffffff));
		// -----YMSG9 body
		dos.write(b,0,b.length);
		dos.flush();

		// -----HTTP response header
		String s = readLine(pbis);
		if(s==null || s.indexOf(" 200 ")<0)  return;	// Not "HTTP/1.0 200 OK"
		while(s!=null && s.trim().length()>0)  			// Read past header
			s=readLine(pbis);
		// -----Payload count
		byte[] code = new byte[4];
		pbis.read(code,0,4);							// Packet count (Little-Endian?)
		int count=code[0];
		// -----Payload body
		YMSG9InputStream yip = new YMSG9InputStream(pbis);
		YMSG9Packet pkt;
		try
		{	for(int i=0;i<count;i++)
			{	pkt=yip.readPacket();
				if(!filterInput(pkt))  push(pkt);
			}
		}catch(Exception e) { push(e); }

		if(Util.debugMode)  System.out.println("Size:"+packets.size());

		soc.close();

		// -----Reset idle timeout
		lastFetch=System.currentTimeMillis();
	}

	// -----Read one line of text, terminating in usual \r \n combinations
	private String readLine(PushbackInputStream pbis) throws IOException
	{	int c = pbis.read();
		String s="";
		while(c!='\n' && c!='\r') { s=s+(char)c;  c=pbis.read(); }
		// -----Check next character
		int c2 = pbis.read();
		if( (c=='\n' && c2!='\r') || (c=='\r' && c2!='\n') )  pbis.unread(c2);
		return s;
	}

	// -----------------------------------------------------------------
	// This method blocks until there is a packet to deliver, during
	// which time it releases its object lock.
	// -----------------------------------------------------------------
	YMSG9Packet receivePacket() throws IOException
	{	if(!connected)  throw new IllegalStateException("Not logged in");
		while(true)
		{	synchronized(this)
			{	if(packets.size()>0)
				{	Object o = pull();
					if(o instanceof IOException)  throw (IOException)o;
						else  return (YMSG9Packet)o;
				}
			}
			try { Thread.sleep(100); } catch(InterruptedException e) {}
		}
	}

	// -----------------------------------------------------------------
	// ** ConnectionHandler methods end
	// -----------------------------------------------------------------

	// -----------------------------------------------------------------
	// Sometimes packets need to be altered, either going in or out.
	// Typically this is about reading or adding extra HTTP specific
	// content which Yahoo uses.  These methods are called to perform
	// the necessary manipulations.
	//
	// Returns true if this packet should be surpressed.
	// -----------------------------------------------------------------
	private boolean filterOutput(PacketBodyBuffer body,int service)
	{	switch(service)
		{	case SERVICE_ISBACK :
			case SERVICE_LOGOFF :
				// Do not send ISBACK or LOGOFF
				return true;
			default :
				break;
		}
		if(identifier>0)  body.addElement("24",identifier+"");
		return false;
	}

	private boolean filterInput(YMSG9Packet pkt)
	{	switch(pkt.service)
		{	case SERVICE_LIST :
				// Remember cookie and send it in subsequent packets
				String[] cookieArr = extractCookies(pkt);
				cookie = cookieArr[COOKIE_Y]+"; "+cookieArr[COOKIE_T];
				break;
			case SERVICE_LOGON :
				// Remember the 24 tag and send it in subsequent packets
				try { identifier = Long.parseLong(pkt.getValue("24")); }
					catch(NumberFormatException e) { identifier=0; }
				break;
			case SERVICE_MESSAGE :
				// When sending a message we often get a 0x06 packet back, empty
				// or containing the status tag (66) of friend we messaged.
				if(pkt.getValue("14")==null)
				{	if(pkt.getValue("10")!=null)  pkt.service=SERVICE_ISBACK;
					else if(pkt.body.length==0)  return true;
				}
				break;
		}
		return false;
	}

	// -----------------------------------------------------------------
	// Packet queue methods
	// -----------------------------------------------------------------
	private synchronized Object pull()
	{	if(packets.size()<=0)  return null;
		Object p = packets.elementAt(0);
		packets.removeElementAt(0);
		return p;
	}

	private synchronized void push(Object p)
	{	packets.addElement(p);
	}


	public String toString()
	{	return "HTTP connection: "+proxyHost+":"+proxyPort;
	}

	// *****************************************************************
	// This thread fires off a IDLE packet every thirty seconds.  This
	// is because the only way the server can deliver us any incoming
	// packets is on the input stream of a HTTP connection we have made
	// ourselves.
	// *****************************************************************
	class Notifier extends Thread
	{	boolean quitFlag=false;

		Notifier(ThreadGroup tg,String nm)
		{	super(tg,nm);
			lastFetch = System.currentTimeMillis();
			this.start();
		}

		public void run()
		{	while(!quitFlag)
			{	try { Thread.sleep(1000); } catch(InterruptedException e) {}
				long t = System.currentTimeMillis();
				if(	!quitFlag && connected &&  (t-lastFetch > IDLE_TIMEOUT) &&
					session.getSessionStatus()==StatusConstants.MESSAGING )
				{	try { session.transmitIdle(); } catch(IOException e) {}
				}
			}
		}
	}
}
