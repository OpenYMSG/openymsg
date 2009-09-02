package ymsg.network;

import java.net.Socket;
import java.net.SocketException;
import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectConnectionHandler extends ConnectionHandler
implements NetworkConstants
{	private String host;					// Yahoo IM host
	private int port;						// Yahoo IM port
	private boolean dontUseFallbacks=false;	// Don't use fallback port numbersac
	//private Session session;				// Associated session object
	// -----I/O
	private Socket socket;					// Network connection
	private YMSG9InputStream ips;			// For receiving messages
	private DataOutputStream ops;			// For sending messages

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	public DirectConnectionHandler(String h,int p)
	{	host=h;  port=p;  dontUseFallbacks=true;
	}
	public DirectConnectionHandler(int p)
	{	this(Util.directHost(),p);
	}
	public DirectConnectionHandler(boolean fl)
	{	this();  dontUseFallbacks=fl;
	}
	public DirectConnectionHandler()
	{	this( Util.directHost() , Util.directPort() );
		dontUseFallbacks=false;
	}

	public String getHost() { return host; }
	public int getPort() { return port; }

	// -----------------------------------------------------------------
	// **ConnectionHandler methods start
	// -----------------------------------------------------------------

	// -----------------------------------------------------------------
	// Session calls this when a connection handler is installed
	// -----------------------------------------------------------------
	void install(Session ss,ThreadGroup tg)
	{	//session=ss;
	}

    private void updateHost(String fromServer, boolean fallback)
    {
        try
        {
            URL url1 = new URL("http", PropertyConstants.DIRECT_HOST_DEFAULT, "/capacity");
            URL url2 = new URL("http", PropertyConstants.DIRECT_HOST_FALLBACK, "/capacity");

            InputStream in1 = url1.openStream();
            InputStream in2 = url2.openStream();

            Properties props = new Properties();
            props.load(in1);
            host = props.getProperty("CS_IP_ADDRESS", host);
            in1.close();
            in2.close();
//            Properties props = new Properties();
//            URL url = new URL("http", fromServer, "/capacity");
//            InputStream in = url.openStream();
//            props.load(in);
//            host = props.getProperty("CS_IP_ADDRESS", host);
//            in.close();
//
//            if(fallback)
//                updateHost(PropertyConstants.DIRECT_HOST_FALLBACK, false);
        }
        catch (Exception e) 
        {
            Logger.getLogger(
                DirectConnectionHandler.class.getName()).log(Level.SEVERE,
                "cannot connect to capacity server", e);

//            if(fallback)
//                updateHost(PropertyConstants.DIRECT_HOST_FALLBACK, false);
        }
    }

	// -----------------------------------------------------------------
	// Opens a socket to Yahoo IM, or throws an exception.  If fallback
	// ports are to be used, will attempt each port in turn - upon failure
	// will return the last exception (the one for the final port).
	// -----------------------------------------------------------------
	void open() throws SocketException,IOException
	{
        updateHost(PropertyConstants.DIRECT_HOST_DEFAULT, true);

        if(dontUseFallbacks)
		{	socket = new Socket(host,port);
		}
		else
		{	int[] fallbackPorts = Util.directPorts();
			int i=0;
			while(socket==null)
			{	try
				{	socket = new Socket(host,fallbackPorts[i]);
					port=fallbackPorts[i];
				}
				catch(SocketException e)
				{	socket=null;  i++;
					if(i>=fallbackPorts.length)  throw e;
				}
			}
		}

		if(Util.debugMode)
		{	ips = new YMSG9InputStream( new DebugInputStream(socket.getInputStream()) );
			ops = new DataOutputStream( new DebugOutputStream(socket.getOutputStream()) );
		}
		else
		{	ips = new YMSG9InputStream(socket.getInputStream());
			ops = new DataOutputStream(socket.getOutputStream());
		}
	}

	// -----------------------------------------------------------------
	// Break socket connection
	// -----------------------------------------------------------------
	void close() throws IOException
	{	if(socket!=null)  socket.close();
		socket=null;
	}

	// -----------------------------------------------------------------
	// Note: the term 'packet' here refers to a YMSG message, not a TCP packet
	// (although in almost all cases the two will be synonymous).  This is to
	// avoid confusion with a 'YMSG message' - the actual discussion packet.
	//
	// service - the Yahoo service number
	// status - the Yahoo status number (not sessionStatus!)
	// body - the payload of the packet
	//
	// Note: it is assumed that 'ops' will have been set by the time
	// this method is called.
	// -----------------------------------------------------------------
	protected void sendPacket(PacketBodyBuffer body,int service,long status,long sessionId)
	throws IOException
	{	byte[] b = body.getBuffer();
		// -----Because the buffer is held at class member level, this method
		// -----is not automatically thread safe.  Besides, we should be only
		// -----sending one message at a time!
//        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
//        DataOutputStream out = new DataOutputStream(b1);
//			out.write(MAGIC,0,4);						// Magic code 'YMSG'
//			out.write(VERSION,0,4);						// Version
//			out.writeShort(b.length & 0xFFFF);			// Body length (16 bit unsigned)
//			out.writeShort(service & 0xFFFF);			// Service ID (16 bit unsigned
//			out.writeInt((int)(status & 0xFFFFFFFF));	// Status (32 bit unsigned)
//			out.writeInt((int)(sessionId & 0xFFFFFFFF)); // Session id (32 bit unsigned)
//			// -----Then the body...
//			out.write(b,0,b.length);
//
//            System.out.println("sending:-----------------");
//            byte[] b2 = b1.toByteArray();
//            for (int i = 0; i < b2.length; i++)
//            {
//                System.out.print(String.format("%#04x ", b2[i]));
//            }
//            System.out.println("");
//            System.out.println("Sending : " + b1.toString());
//            System.out.println("-----------------");
//            new Exception().printStackTrace();
//

        synchronized(ops)
        {
//            ByteArrayOutputStream b1 = new ByteArrayOutputStream();
//            DataOutputStream ops = new DataOutputStream(b1);

            // -----20 byte header
			ops.write(MAGIC,0,4);						// Magic code 'YMSG'
			ops.write(VERSION,0,4);						// Version
			ops.writeShort(b.length & 0xFFFF);			// Body length (16 bit unsigned)
			ops.writeShort(service & 0xFFFF);			// Service ID (16 bit unsigned
			ops.writeInt((int)(status & 0xFFFFFFFF));	// Status (32 bit unsigned)
			ops.writeInt((int)(sessionId & 0xFFFFFFFF)); // Session id (32 bit unsigned)
			// -----Then the body...
			ops.write(b,0,b.length);
			// -----Now send the buffer
//            ops.flush();
//			System.out.println("message: " + new String(b));
//System.out.println("eeeeeeeeeeeee " + new String(b1.toByteArray()));
//            this.ops.write(b1.toByteArray());
            ops.flush();
		}
	}

	// -----------------------------------------------------------------
	// Return a Yahoo message
	// -----------------------------------------------------------------
	protected YMSG9Packet receivePacket() throws IOException
	{	return ips.readPacket();
	}

	// -----------------------------------------------------------------
	// ** ConnectionHandler methods end
	// -----------------------------------------------------------------


	public String toString()
	{	return "Direct connection: "+host+":"+port;
	}
}
