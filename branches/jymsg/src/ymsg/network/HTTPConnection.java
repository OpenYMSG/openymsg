package ymsg.network;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

class HTTPConnection implements NetworkConstants
{	protected Socket socket;				// Network i/o socket
	protected PushbackInputStream pbis;		// Need pushback to decode \r\n or \n
	protected DataOutputStream dos;			// Output
	private DebugInputStream dbis=null;		// Debug
	protected String me;					// toString text
	private boolean eof=false;				// EOF (end of data) flag
	private String lineEnd="\r\n";			// Text line teminator


	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	HTTPConnection(String method,URL u,boolean unix) throws IOException
	{	String headerLine,proxyAuthContent;
		String host;
		int port;

		if(unix)  lineEnd="\n";

		// -----Attempt to get proxy host
		host=Util.httpProxyHost();
		// -----No proxy host, or URL should not be proxied?
		if(host==null || doNotProxy(u.getHost()))		// Direct connection
		{	// -----Set host/port/headerLine from URL assuming direct
			host = u.getHost();  port = u.getPort();
			if(port<=-1)  port=80;
			headerLine = method+" "+u.getFile()+" HTTP/1.1";
			proxyAuthContent = null;
		}
		else											// Proxy connection
		{	// -----Set host/port/headerLine assuming proxy
			port = Util.httpProxyPort();
			headerLine = method+" "+u.toString()+" HTTP/1.1";
			proxyAuthContent = Util.httpProxyAuth();
		}

		// -----toString
		me="HTTPConnection to:"+host+":"+port+" for:["+headerLine+"]";
		
		// -----Open I/O streams and send header, and possibly proxy auth
		socket = new Socket(host,port);  openStreams();
		this.println(headerLine);
		if(proxyAuthContent!=null)
			this.println("Proxy-Authorization: "+proxyAuthContent);
		//System.out.println(headerLine+" "+this.toString());
	}

	HTTPConnection(String method,URL u) throws IOException
	{	this(method,u,false);
	}

	private void openStreams() throws IOException
	{	if(Util.debugMode)
		{	dbis = new DebugInputStream(socket.getInputStream());
			pbis = new PushbackInputStream(dbis);
			dos = new DataOutputStream( new DebugOutputStream( socket.getOutputStream() ) );
		}
		else
		{	pbis = new PushbackInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		}
	}


	// -----------------------------------------------------------------
	// Input methods
	// -----------------------------------------------------------------
	String readLine() throws IOException
	{	String s="";
		// -----Read initial byte
		if(eof)  return null;
		int c = pbis.read();
		if(c==255 || c<0) { eof=true;  return null; }
		while(c!=255 && c>=0 && c!='\n' && c!='\r') { s=s+(char)c;  c=pbis.read(); }
		// -----Check next character
		if(c==255 || c<0)
		{	eof=true;
		}
		else
		{	int c2 = pbis.read();
			if( (c=='\n' && c2!='\r') || (c=='\r' && c2!='\n') )  pbis.unread(c2);
		}
		return s;
	}

	int read(byte[] b,int off,int len) throws IOException { return pbis.read(b,off,len); }

	int read(byte[] b) throws IOException { return read(b,0,b.length); }

	// -----------------------------------------------------------------
	// Output methods
	// -----------------------------------------------------------------
	void println(String s) throws IOException { dos.writeBytes(s+lineEnd); }
	void write(byte[] b,int off,int len) throws IOException { dos.write(b,off,len); }
	void write(byte[] b) throws IOException { dos.write(b); }
	void writeUShort(int i) throws IOException { dos.writeShort(i & 0xffff); }
	void writeUInt(long l) throws IOException { dos.writeInt((int)(l & 0xffffffff)); }
	void flush() throws IOException { dos.flush(); }

	void close() throws IOException
	{	if(socket!=null)  socket.close();
		sectionEnd();
	}

	// -----------------------------------------------------------------
	// Used to tell debug to dump its data
	// -----------------------------------------------------------------
	void sectionEnd()
	{	if(dbis!=null)  dbis.debugDump();
	}

	// -----------------------------------------------------------------
	// Returns true if this host is excempt from proxying
	// -----------------------------------------------------------------
	private boolean doNotProxy(String s)
	{	s=s.toLowerCase();
		String p = System.getProperty(PROXY_NON,"");
		StringTokenizer st = new StringTokenizer(p,"|");
		while(st.hasMoreTokens())
		{	p=st.nextToken();
			if(p.startsWith("*"))
			{	p=p.substring(1);
				if(s.endsWith(p.toLowerCase()))  return true;
			}
			else if(s.equalsIgnoreCase(p))  return true;
		}
		return false;
	}

	public String toString() { return me; }

	/*public static void main(String[] args)
	{	try
		{	new HTTPConnection
			(	args[0] ,
				new URL(args[1]) ,
				args[2]
			);
		}catch(Exception e) { e.printStackTrace(); }
	}*/
}
