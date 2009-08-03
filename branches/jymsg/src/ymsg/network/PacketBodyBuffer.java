package ymsg.network;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

// *********************************************************************
// This handy class hides most of the pain of building the Yahoo message
// body.  Each body consists of key/value pairs (or sometimes just keys)
// with each field separated by the sequence 0xc080
//
// Note: this class is NOT thread safe (although, to be honest, building 
// a single message body from more than one independent thread is surely 
// asking for trouble?!??)
// *********************************************************************
class PacketBodyBuffer
{	protected ByteArrayOutputStream baos;
	private final static int[] SEPARATOR = { 0xc0, 0x80 };	// Yahoo separator

	// -----------------------------------------------------------------
	// CONSTRUCTOR
	// -----------------------------------------------------------------
	PacketBodyBuffer()
	{	baos = new ByteArrayOutputStream(1024);		// 1K initial size
	}
	
	// -----------------------------------------------------------------
	// Add a string to the buffer, and terminate with separator.
	// Note: this method is NOT thread safe.
	// -----------------------------------------------------------------
	void addString(String s)
	{	//for(int i=0;i<s.length();i++)  baos.write(s.charAt(i));
		try
		{	baos.write(s.getBytes("UTF-8"));
		}catch(Exception e) { e.printStackTrace(); }
		baos.write(SEPARATOR[0]);  baos.write(SEPARATOR[1]);
	}
	
	// -----------------------------------------------------------------
	// Add key/value pair to buffer.
	// Note: this method is NOT thread safe.
	// -----------------------------------------------------------------
	void addElement(String key,String value)
	{	addString(key);  addString(value);
	}
	
	// -----------------------------------------------------------------
	// Return buffer as byte array.
	// Note: this method is NOT thread safe.
	// -----------------------------------------------------------------
	synchronized byte[] getBuffer() { return baos.toByteArray(); }
	
	// -----------------------------------------------------------------
	// Reset (clear) buffer
	// -----------------------------------------------------------------
	void reset() { baos.reset(); }
}
