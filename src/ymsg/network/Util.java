package ymsg.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.StringTokenizer;

//import java.util.Vector;
//import java.util.StringTokenizer;

abstract class Util implements PropertyConstants
{	/*public static Vector separatedListToVector(String s,String sep)
	{	Vector v = new Vector();
		StringTokenizer st = new StringTokenizer(s,sep);
		while(st.hasMoreTokens())  v.addElement(st.nextToken());
		return v;
	}*/

	public static boolean debugMode;
	//public static String archiveFilename;

	static
	{	// -----Enable/disable global debugging information
		String p = System.getProperty("ymsg.debug","false");
		debugMode = (p.equalsIgnoreCase("true"));
		// -----Location of archive of 'unread' messages (see readArchiveFile())
		//archiveFilename = System.getProperty("ymsg.archive");
	}

	// -----------------------------------------------------------------
	// Package methods to wrap input and output streams to capture
	// debugging information.
	// -----------------------------------------------------------------
	static InputStream wrapIn(InputStream in)
	{	if(Util.debugMode) return new DebugInputStream(in);
			else return in;
	}
	static OutputStream wrapOut(OutputStream out)
	{	if(Util.debugMode) return new DebugOutputStream(out);
			else return out;
	}

	// -----------------------------------------------------------------
	// Properties
	// -----------------------------------------------------------------
	// Read ymsg.network.loginTimeout, or use default
	static int loginTimeout(int def)
	{	int loginTimeout = Integer.parseInt(System.getProperty("ymsg.network.loginTimeout",""+def)) *1000;
		if(loginTimeout<=0)  loginTimeout=Integer.MAX_VALUE;
		return loginTimeout;
	}
	// Read ymsg.network.directHost, or use default
	static String directHost()
	{	return System.getProperty( DIRECT_HOST , DIRECT_HOST_DEFAULT );
	}
	// Read ymsg.network.directHosts, or use default
	static int[] directPorts()
	{	String s = System.getProperty(DIRECT_PORTS);
		if(s==null)
		{	return DIRECT_PORTS_DEFAULT;
		}
		else
		{	StringTokenizer st = new StringTokenizer(s,",");
			int[] arr = new int[st.countTokens()];
			for(int i=0;i<arr.length;i++)
				arr[i]=Integer.parseInt(st.nextToken());
			return arr;
		}
	}
	// Read ymsg.network.directHosts[0], or use default
	static int directPort()
	{	return directPorts()[0];
	}
	// Read ymsg.network.httpHost, or use default
	static String httpHost()
	{	return System.getProperty( HTTP_HOST , HTTP_HOST_DEFAULT );
	}
	// Read ymsg.network.httpHost, or use default
	static String httpProxyAuth()
	{	return System.getProperty( HTTP_PROXY_AUTH );
	}
	// Read ymsg.network.httpHost, or use default
	static String fileTransferHost()
	{	return System.getProperty( FT_HOST , FT_HOST_DEFAULT );
	}

	// -----------------------------------------------------------------
	// HTTP
	// -----------------------------------------------------------------
	// Read http.proxyHost, or proxyHost, or null
	static String httpProxyHost()
	{	String host=System.getProperty(NetworkConstants.PROXY_HOST);
		if(host==null)  host=System.getProperty(NetworkConstants.PROXY_HOST_OLD);
		return host;
	}
	// Read http.proxyPort, or proxyPort, or return 8080
	static int httpProxyPort()
	{	String port=System.getProperty(NetworkConstants.PROXY_PORT);
		if(port==null)  port=System.getProperty(NetworkConstants.PROXY_PORT_OLD);
		if(port==null)  port="8080";
		return Integer.parseInt(port);
	}			

	static void initURLConnection(URLConnection uc)
	{	// -----Sets the auth for proxies
		String proxyAuthContent=httpProxyAuth();
		if(proxyAuthContent!=null)
			uc.setRequestProperty("Proxy-Authorization",proxyAuthContent);
	}

	/*public static String getPropString(String key,String def)
	{	return System.getProperty(key,def);
	}

	public static int getPropInt(String key,int def)
	{	String v = System.getProperty(key);
		if(v==null)  return def;
		try { return Integer.parseInt(v); }
			catch(NumberFormatException e) { return def; }
	}

	public static int[] getPropIntA(String key,int[] def)
	{	String v = System.getProperty(key);
		if(v==null)  return def;
		StringTokenizer st = new StringTokenizer(v,",");
		int[] arr = new int[st.countTokens()];
		int cnt=0;
		while(st.hasMoreTokens())
		{	try { arr[cnt++]=Integer.parseInt(st.nextToken()); }
				catch(NumberFormatException e) {}
		}
		return arr;
	}*/


	// -----------------------------------------------------------------
	// For those not familiar with Base64 etc, all this does is treat an
	// array of bytes as a bit stream, sectioning the stream up into six
	// bit slices, which can be represented by the 64 characters in the
	// 'table' provided.  In this fashion raw binary data can be expressed
	// as valid 7 bit printable ASCII - although the size of the data will
	// expand by 25% - three bytes (24 bits) taking up four ASCII characters.
	// Now obviously the bit stream will terminate mid way throught an ASCII
	// character if the input array size isn't evenly divisible by 3.  To
	// flag this, either one or two pad chars are appended to the output.  A
	// single char if we're two over, and two chars if we're only one over.
	// (No chars are appended if the input size evenly divides by 3.)
	// -----------------------------------------------------------------
	static String _base64(String table,char pad,byte[] buffer)
	{	int limit = buffer.length-(buffer.length%3);
		StringBuffer out = new StringBuffer();

		// -----Convert bytes to ints, for convenience
		int[] buff = new int[buffer.length];
		for(int i=0;i<buffer.length;i++)  buff[i]=(int)buffer[i] & 0xff;

		// -----Base 64
		for(int i=0;i<limit;i+=3)
		{	// -----Top 6 bits of first byte
			out.append( table.charAt( buff[i]>>2 ) );
			// -----Bottom 2 bits of first byte append to top 4 bits of second
			out.append( table.charAt( ((buff[i]<<4) & 0x30) | (buff[i+1]>>4) ) );
			// -----Bottom 4 bits of second byte appended to top 2 bits of third
			out.append( table.charAt( ((buff[i+1]<<2) & 0x3c) | (buff[i+2]>>6) ) );
			// -----Bottom six bits of third byte
			out.append( table.charAt( buff[i+2] & 0x3f ) );
		}

		// -----Do we still have a remaining 1 or 2 bytes left?
		int i=limit;
		switch(buff.length-i)
		{	case 1 :
				// -----Top 6 bits of first byte
				out.append( table.charAt( buff[i]>>2 ) );
				// -----Bottom 2 bits of first byte
				out.append( table.charAt( ((buff[i]<<4) & 0x30) ) );
				out.append(pad).append(pad);  break;
			case 2 :
				// -----Top 6 bits of first byte
				out.append( table.charAt( buff[i]>>2 ) );
				// -----Bottom 2 bits of first byte append to top 4 bits of second
				out.append( table.charAt( ((buff[i]<<4) & 0x30) | (buff[i+1]>>4) ) );
				// -----Bottom 4 bits of second byte
				out.append( table.charAt( ((buff[i+1]<<2) & 0x3c) ) );
				out.append(pad);  break;
		}

		return out.toString();
	}

	public static String base64(byte[] buffer)
	{	return _base64
		(	"ABCDEFGHIJKLMNOPQRSTUVWXYZ"+
			"abcdefghijklmnopqrstuvwxyz"+
			"0123456789+/" ,
			'=' ,
			buffer
		);
	}


	// -----------------------------------------------------------------
	// Is Utf-8 text
	// -----------------------------------------------------------------
	public static boolean isUtf8(String s)
	{	for(int i=0;i<s.length();i++)
		{	if(s.charAt(i) > 0x7f)  return true;
		}
		return false;
	}

	// -----------------------------------------------------------------
	// Decode entity encodeded strings
	// -----------------------------------------------------------------
	private final static String[] ENTITIES_STR =
	{	"apos",	"quot",	"amp",	"lt",	"gt",	"nbsp",
		"curren","cent","pound","yen",	"copy"
	};
	private final static char[] ENTITIES_CHR =
	{	'\'',	'\"',	'&',	'<',	'>',	' ',
		164,	162,	163,	165,	169
	};

	static String entityDecode(String s)
	{	StringBuffer result = new StringBuffer();

		int i1=s.indexOf("&") , i2;
		while(i1>=0)
		{	i2=s.indexOf(";");
			if(i2>=0 && i2>i1+1)		// Found the sequence & followed by ;
			{	result.append(s.substring(0,i1));
				String ent=s.substring(i1+1,i2).toLowerCase();
				int j=0;
				for(j=0;j<ENTITIES_STR.length;j++)
				{	// -----Entity matched
					if(ENTITIES_STR[j].equals(ent))
					{	result.append((char)ENTITIES_CHR[j]);  break;
					}
				}
				// -----Entity unmatched
				if(j>=ENTITIES_STR.length)
				{	result.append('&');  result.append(ent);  result.append(';');
				}
				// -----Truncate ip buffer
				s=s.substring(i2+1);
			}
			else						// Found &, but no *following* ;
			{	result.append(s.substring(0,i2+1));
				s=s.substring(i2+1);
			}
			i1=s.indexOf("&");
		}
		result.append(s);
		return result.toString();
	}

	// -----------------------------------------------------------------
	// Mutex lock used simply to prevent different outputs getting entangled
	// -----------------------------------------------------------------
	synchronized static void dump(byte[] array)
	{	String s,c="";
		for(int i=0;i<array.length;i++)
		{	s="0"+Integer.toHexString((int)array[i]);
			System.out.print(s.substring(s.length()-2)+" ");
			if((int)array[i]>=' ' && (int)array[i]<='~')  c=c+(char)array[i];
				else  c=c+".";
			if((i+1)==array.length)
			{	while((i%20)!=19) { System.out.print("   ");  i++; }
			}
			if( (((i+1)%20)==0) || ((i+1)>=array.length) )
			{	System.out.print(" "+c+"\n");  c="";
			}
		}
	}
	static void dump(byte[] array,String s)
	{	System.out.println(s+"\n01-02-03-04-05-06-07-08-09-10-11-12-13-14-15-16-17-18-19-20");
		dump(array);
	}

	// -----------------------------------------------------------------
	// Revert a base64 (yahoo64) encoded string back to its original
	// unsigned byte data.
	// -----------------------------------------------------------------
	static int[] yahoo64Decode(String s)
	{	if(s.length()%4!=0)  throw new IllegalArgumentException("Source string incomplete");

		// -----Figure out the correct length for byte buffer
		int len = s.length()/4;
		if(s.endsWith("--"))  len-=2;
		else if(s.endsWith("-"))  len--;

		int[] buffer = new int[len];
		int[] c = new int[4];
		int bpos=0;

		// -----For data streams which were not exactly divisible by three
		// -----bytes, the below will result in an exception for the padding
		// -----chars on the end of the string.
		try
		{	for(int i=0;i<s.length();i+=4)
			{	for(int j=0;i<c.length;j++)  c[j]=_c2b(s.charAt(i+j));
				buffer[bpos+0] = ( (c[0]<<2)		+ (c[1]>>4)	) & 0xff;
				buffer[bpos+1] = ( (c[1]&0x0f)<<4	+ (c[2]>>2)	) & 0xff;
				buffer[bpos+2] = ( (c[2]&0x03)<<6	+ (c[3])	) & 0xff;
				bpos+=3;
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
		return buffer;
	}
	private static int _c2b(int c)
	{	if(c>='A' && c<='Z')		return c-'A';
		else if(c>='a' && c<='z')	return c-'a'+26;
		else if(c>='0' && c<='9')	return c-'0'+52;
		else if(c=='.')				return 62;
		else if(c=='_')				return 63;
		else						return 0;
	}

	/*public static void main(String[] args)
	{	//int[] b = yahoo64Decode(args[0]);
		//for(int i=0;i<b.length;i++)  System.out.print(i+" ");
		//System.out.print("\n");
		System.out.println(entityDecode("abc&amp;def &u; ; & ; &apos;"));
	}*/

	// -----------------------------------------------------------------
	// Read the file "$HOME/.ymessenger/messages.dat" which holds archived
	// messages (sent while user offline) and not yet deleted!  Returns a
	// Vector of SessionEvent's... null on error
	// -----------------------------------------------------------------
	/*public Vector getArchivedMessages()
	{	// -----This variable is set from the property "ymsg.archive"
		String fn = Util.archiveFilename;
		if(fn==null)  return null;
		// -----Open and read file
		Vector v = new Vector();
		DateFormat df = DateFormat.getDateTimeInstance();
		try
		{	// -----Entries separated by blank lines.  '#' lines are comments
			BufferedReader br = new BufferedReader(new FileReader(fn));
			String in = br.readLine();
			String to=null,from=null,mesg=null,date=null;
			while(in!=null)
			{	in=in.trim();
				if(in.length()==0)					// Empty?  Store!
				{	addArchiveMessage(v,to,from,mesg,date);
					to=null;  from=null;  mesg=null;  date=null;
				}
				else if(in.startsWith("Sender "))	// From
				{	from = in.substring(7+1,in.length()-1);
				}
				else if(in.startsWith("Recipient "))	// To
				{	to = in.substring(10+1,in.length()-1);
				}
				else if(in.startsWith("Date "))		// Date
				{	in = in.substring(5+1,in.length()-1);
					try { date = ""+(df.parse(in).getTime()/1000); }
						catch(java.text.ParseException e) { System.err.println(e.toString()); }
				}
				else if(in.startsWith("Message "))	// Message
				{	mesg = in.substring(8+1,in.length()-1);
				}
				// -----Read next line
				in=br.readLine();
			}
			addArchiveMessage(v,to,from,mesg,date);
			br.close();
			return v;
		}catch(IOException e) { return null; }
	}

	private void addArchiveMessage(Vector v,String to,String from,String mesg,String date)
	{	System.out.println("Adding "+to+" "+from+" "+mesg+" "+date);
		if(to!=null && from!=null && mesg!=null && date!=null)
			v.addElement(new SessionEvent(this,to,from,mesg,date));
	}*/
}
