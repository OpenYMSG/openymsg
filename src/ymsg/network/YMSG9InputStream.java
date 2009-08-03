package ymsg.network;

import java.util.Vector;
import java.io.*;

// *********************************************************************
// A YMSG9 packet has a 20 byte fixed format header.  The first four
// bytes are the magic code "YMSG".  The next four contain the protocol
// version 0x09000000.  The next two are the size in bytes of the body.
// Following this a two byte service id (which effectively determines the
// message/body type).  Then a four byte status code, usually zero on
// outgoing packets.  Finally a four byte session code, which should
// always parrot in outgoing packets the last incoming packet (it can
// switch mid-session, apparently!)
//
// The body is a set of ASCII strings terninated by the byte sequence
// 0xc080 .  Some of these strings are 'instructions' written as numbers,
// which appear to dictate the type and/or meaning of the data which
// follows in the next section.  In a way they are like key/value pairs,
// except that not all keys appear to require values.  (Given the user-
// unfriendly nature of these codes, and their apparent inconsistent
// usage from one message type to another, little reverse engineered
// information exists as to their actual meaning.  Those implementing
// the YMSG9 protocol tend to just copy them verbatim.)
// *********************************************************************
public class YMSG9InputStream extends BufferedInputStream
{	DebugInputStream dbis = null;

	public YMSG9InputStream(InputStream in)
	{	super(in);
		if(in instanceof DebugInputStream)  dbis=(DebugInputStream)in;
	}

	// -----------------------------------------------------------------
	// Read a complete packet, including headers.  Returns null upon EOF,
	// and throws IOException when confused.
	// -----------------------------------------------------------------
	public YMSG9Packet readPacket() throws IOException
	{	YMSG9Packet p = new YMSG9Packet();

		if(dbis!=null)  dbis.debugReset();

		byte[] header = new byte[20];
		if(readBuffer(header)<=0)  return null;
		// -----Somewhat ineligant way to extract the header data
		p.magic = ""+(char)header[0]+(char)header[1]+(char)header[2]+(char)header[3];
		p.version = u2i(header[5]);
		p.length = (u2i(header[8])<<8)+(u2i(header[9]));
		p.service = (u2i(header[10])<<8)+(u2i(header[11]));
		p.status = (u2i(header[12])<<24)+(u2i(header[13])<<16)+(u2i(header[14])<<8)+(u2i(header[15]));
		p.sessionId = (u2i(header[16])<<24)+(u2i(header[17])<<16)+(u2i(header[18])<<8)+(u2i(header[19]));
		// -----Check the header
		if(!p.magic.equals("YMSG"))  throw new IOException("Bad YMSG9 header");

		// -----Read the body
		Vector v = new Vector();
		StringBuffer sb = new StringBuffer();
		byte[] body = new byte[p.length];
		if(readBuffer(body)<0)  return null;

		if(dbis!=null)  dbis.debugDump();

		// -----Now extract strings in the body
		int start=0;
		for(int i=0;i<body.length-1;i++)
		{	if(u2i(body[i])==0xc0 && u2i(body[i+1])==0x80)
			{	// -----Create a UTF-8 string and add to array
				String s = new String(body,start,i-start,"UTF-8");
				v.addElement(s);
				if(dbis!=null)  System.out.println(">>"+s);
				// -----Skip over second byte in separator, reset string start
				i++;  start=i+1;
			}
		}
		p.body = new String[v.size()];
		for(int i=0;i<v.size();i++)  p.body[i]=(String)v.elementAt(i);

		return p;
	}

	private int u2i(byte b) { return (int)b & 0xff; }

	// -----------------------------------------------------------------
	// Reads an entire buffer of data, allowing for blocking.
	// Returns bytes read (should be == to buffer size) or negative
	// bytes read if 'EOF' encountered.
	// -----------------------------------------------------------------
	private int readBuffer(byte[] buff) throws IOException
	{	int p=0,r=0;
		while(p<buff.length)
		{	r = super.read(buff,p,buff.length-p);
			if(r<0)  return (p+1)*-1;  else  p+=r;
		}
		return p;
	}
}
