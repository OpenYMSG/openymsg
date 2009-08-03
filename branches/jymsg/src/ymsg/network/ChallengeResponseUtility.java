package ymsg.network;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class ChallengeResponseUtility
{	private final static String Y64 = 	"ABCDEFGHIJKLMNOPQRSTUVWXYZ"+
										"abcdefghijklmnopqrstuvwxyz"+
										"0123456789._";

	protected static MessageDigest md5Obj;

	// -----------------------------------------------------------------
	// Create a singleton md5 object for all our hashing needs (well, most!)
	// -----------------------------------------------------------------
	static
	{	try
		{	md5Obj=MessageDigest.getInstance("MD5");
		}catch(NoSuchAlgorithmException e) { e.printStackTrace(); }
	}

	// -----------------------------------------------------------------
	// Yahoo uses its own custom variation on Base64 encoding (although a
	// little birdy tells me this routine actually comes from the Apple Mac?)
	//
	// For those not familiar with Base64 etc, all this does is treat an
	// array of bytes as a bit stream, sectioning the stream up into six
	// bit slices, which can be represented by the 64 characters in the
	// 'table' Y64 above.  In this fashion raw binary data can be expressed
	// as valid 7 bit printable ASCII - although the size of the data will
	// expand by 25% - three bytes (24 bits) taking up four ASCII characters.
	// Now obviously the bit stream will terminate mid way throught an ASCII
	// character if the input array size isn't evenly divisible by 3.  To
	// flag this, either one or two dashes are appended to the output.  A
	// single dash if we're two over, and two dashes if we're only one over.
	// (No dashes are appended if the input size evenly divides by 3.)
	// -----------------------------------------------------------------
	static String yahoo64(byte[] buffer)
	{	int limit = buffer.length-(buffer.length%3);
		int pos=0;
		String out="";
		int[] buff = new int[buffer.length];

		for(int i=0;i<buffer.length;i++)  buff[i]=(int)buffer[i] & 0xff;

		for(int i=0;i<limit;i+=3)
		{	// -----Top 6 bits of first byte
			out=out+Y64.charAt( buff[i]>>2 );
			// -----Bottom 2 bits of first byte append to top 4 bits of second
			out=out+Y64.charAt( ((buff[i]<<4) & 0x30) | (buff[i+1]>>4) );
			// -----Bottom 4 bits of second byte appended to top 2 bits of third
			out=out+Y64.charAt( ((buff[i+1]<<2) & 0x3c) | (buff[i+2]>>6) );
			// -----Bottom six bits of third byte
			out=out+Y64.charAt( buff[i+2] & 0x3f );
		}

		// -----Do we still have a remaining 1 or 2 bytes left?
		int i=limit;
		switch(buff.length-i)
		{	case 1 :
				// -----Top 6 bits of first byte
				out=out+Y64.charAt( buff[i]>>2 );
				// -----Bottom 2 bits of first byte
				out=out+Y64.charAt( ((buff[i]<<4) & 0x30) );
				out=out+"--";  break;
			case 2 :
				// -----Top 6 bits of first byte
				out=out+Y64.charAt( buff[i]>>2 );
				// -----Bottom 2 bits of first byte append to top 4 bits of second
				out=out+Y64.charAt( ((buff[i]<<4) & 0x30) | (buff[i+1]>>4) );
				// -----Bottom 4 bits of second byte
				out=out+Y64.charAt( ((buff[i+1]<<2) & 0x3c) );
				out=out+"-";  break;
		}

		return out;
	}

	// -----------------------------------------------------------------
	// Return the MD5 or a string and byte array (note: md5Singleton()
	// is easier on the object heap, but is NOT thread safe.  It's ideal
	// for doing lots of hashing inside a tight loop - but remember to
	// mutex lock 'md5Obj' before using it!)
	// -----------------------------------------------------------------
	static byte[] md5(String s) throws NoSuchAlgorithmException
	{	return md5(s.getBytes());
	}
	static byte[] md5(byte[] buff) throws NoSuchAlgorithmException
	{	return MessageDigest.getInstance("MD5").digest(buff);
	}
	static byte[] md5Singleton(byte[] buff) throws NoSuchAlgorithmException
	{	md5Obj.reset();  return md5Obj.digest(buff);
	}
	// -----------------------------------------------------------------
	// Return the MD5Crypt of a string and salt
	// -----------------------------------------------------------------
	static byte[] md5Crypt(String k,String s)
	{	return UnixMD5Crypt.crypt(k,s).getBytes();
	}
}
