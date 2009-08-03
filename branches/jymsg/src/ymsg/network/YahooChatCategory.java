package ymsg.network;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

// *********************************************************************
// Categories are like directories.  Each category may contain more categories
// (sub directories), a list of public chatrooms and a list of private
// chatrooms.  Each room is further sub-divided into lobbies which hold a
// limited number of users.
//
// NOTE: this is the second implementation of this class.  The original used
// Yahoo's old method of accessing category/room data.  They have now dropped
// that scheme in favour of an XML based approach.
//
// Categories are modelled by the YahooChatCategory class, rooms by the
// YahooChatRoom class, and lobbies by (shock horror!) the YahooChatLobby class.
//
// The data is delivered from Yahoo via a call the below URL, and in the
// following format :
//
//   http://insider.msg.yahoo.com/ycontent/?chatcat=0
//
// Resulting in (indented for readability)...
//
//   <content time="1061459725">
//     <chatCategories>
//       <category id="1600000002" name="Business &amp; Finance">
//          ** Other categories may be nested here, to any level **
//       </category>
//       ** More categories **
//     </chatCategories>
//   </content>
//
// Rooms inside a category are fetched using the following URL, including the
// room id encoded on the end :
//
//   http://insider.msg.yahoo.com/ycontent/?chatroom_<id>
//
// Resulting in (indented for readability)...
//
//   <content time="1055350260">
//     <chatRooms><room type="yahoo" id="1600326587" name="Computers Lobby" topic="Chat on your phone at http://messenger.yahoo.com/messenger/wireless/">
//         <lobby count="12" users="1" voices="1" webcams="0" />
//         <lobby count="10" users="23" voices="0" webcams="0" />
//         ** Other lobby entries **
//      </room>
//      ** Other public rooms **
//      <room type="user" id="1600004725" name="hassansaeed87&apos;s room" topic="Welcome to My Room">
//        <lobby count="1" users="1" voices="0" webcams="0" />
//      </room>
//      ** Other private rooms **
//   </chatRooms></content>
//
// NOTE: the XML reader used in this code is very simplistic.  As the format
// employed by Yahoo is quite simple, I've choosen to implement my own reader
// rather than rely on the industrial-strength readers which are available for
// later versions of Java.  This keeps the resource footprint of the API small
// and maitains accessiblity to early/embedded versions of Java.  The reader
// is certainly *not* a full (or correct) XML parser, and may break if the
// file format changes radically.
//
// NOTE: this class used to rely on a home-brew HTTP class called
// ymsg.network.HTTPConnection .  This was because Yahoo had a nasty habit of 
// sending back HTTP responses with no blank line between the header and the 
// opening XML line (invalid, in other words!)  As Sept 2006 (v0.7) the 
// problem appears to have been fixed, so this code has been converted to  
// use the regular java.net.* HTTP classes.
// *********************************************************************
public class YahooChatCategory // Cannot be serialised
{	private Session session;				// Session (cookies)
	private String cookieLine;				// Cookie HTTP header
	protected String name;					// Name of cataegory/room
	protected long id;						// Id code
	private int level;						// Sub category level
	protected Vector categories,privateRooms,publicRooms;

	private final static String PUBLIC_TYPE = "yahoo";
	private final static String PRIVATE_TYPE = "user";

	private final static String PREFIX = "http://";
	private final static String TOP_URL = "insider.msg.yahoo.com/ycontent/?chatcat=0";
	private final static String CAT_URL = "insider.msg.yahoo.com/ycontent/?chatroom_"; // +"=0"

	private static String localePrefix="";		// Country code URL prefix, ie: "fr."
	private static Hashtable chatByNetName;		// Chatroom lobbies hashed by network name
	private static /*transient*/ String inputLine;	// Used while reading XML tags
	private static YahooChatCategory rootCategory; // See loadCategories()

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	YahooChatCategory(Session ss,long i,int lv,String nm)
	{	session=ss;  id=i;  level=lv;  name=Util.entityDecode(nm);
		categories = new Vector();
		privateRooms=null;  publicRooms=null;
		// -----Line to send with each HTTP request (Yahoo won't send adult
		// -----group listings without these cookies...!)
		if(session!=null)
		{	String[] cookies = session.getCookies();
			if(cookies!=null)  cookieLine=/* FIXME "Cookie: "+ */
				cookies[NetworkConstants.COOKIE_Y]+"; "+
				cookies[NetworkConstants.COOKIE_T];
			else  cookieLine=null;
		}
	}

	static
	{	chatByNetName = new Hashtable();
	}

	// -----------------------------------------------------------------
	// Add a new sub category
	// -----------------------------------------------------------------
	void add(YahooChatCategory ycl) { categories.addElement(ycl); }

	// -----------------------------------------------------------------
	// Get rooms
	// The loadRooms() method loads both private and public rooms for
	// this category.  If the rooms have already been loaded, the cached
	// copy is returned.  refresh() can be used to reload the category's
	// rooms data.
	// -----------------------------------------------------------------
	public Vector getPublicRooms() throws IOException
	{	if(publicRooms==null)  loadRooms();
		return publicRooms;
	}
	public Vector getPrivateRooms() throws IOException
	{	if(privateRooms==null)  loadRooms();
		return privateRooms;
	}

	public void refresh() throws IOException { loadRooms(); }

	// -----------------------------------------------------------------
	// Other accessors
	// -----------------------------------------------------------------
	/*public int size() { return categories.size(); }
	public YahooChatCategory getCategoryAt(int i)
	{	return (YahooChatCategory)categories.elementAt(i);
	}*/
	public Vector getCategories() { return categories; }
	public String getName() { return name; }
	public long getId() { return id; }

	// -----------------------------------------------------------------
	// The first time a category is inspected, we need to fetch the
	// data from Yahoo to populate it.
	// -----------------------------------------------------------------
	private void loadRooms() throws IOException
	{	publicRooms = new Vector();  privateRooms = new Vector();
		YahooChatRoom ycr=null;

		// -----Open a HTTP connection to a given category
		String addr = PREFIX+localePrefix+CAT_URL;
		URLConnection uConn = new URL(addr+id).openConnection();
		Util.initURLConnection(uConn);
		if(rootCategory.cookieLine!=null)  
			uConn.setRequestProperty("Cookie",rootCategory.cookieLine);
		BufferedReader br = new BufferedReader(
			new InputStreamReader(Util.wrapIn(uConn.getInputStream())) );

		String in = br.readLine();
		
		// -----Prep tag reader - it shouldn't need to read from conn this time
		inputLine=in;  in=nextTag(br);
		// -----Process each tag
		while(in!=null)
		{	if(in.startsWith("<room "))
			{	// -----Create new public room
				Hashtable attrs = getAttrs(in);
				String type = (String)attrs.get("type");
				long id = Long.parseLong((String)attrs.get("id"));
				String name = (String)attrs.get("name");
				String topic = (String)attrs.get("topic");
				ycr = new YahooChatRoom(id,name,topic,type.equals(PUBLIC_TYPE));
			}
			else if(in.startsWith("</room>"))
			{	// -----Add to list
				if(ycr.isPublic())  publicRooms.addElement(ycr);
					else  privateRooms.addElement(ycr);
			}
			else if(in.startsWith("<lobby "))
			{	// -----Add a new lobby
				Hashtable attrs = getAttrs(in);
				int count = Integer.parseInt(_getDef(attrs,"count","0"));
				int users = Integer.parseInt(_getDef(attrs,"users","0"));
				int voices = Integer.parseInt(_getDef(attrs,"voices","0"));
				int webcams = Integer.parseInt(_getDef(attrs,"webcams","0"));
				YahooChatLobby ycl = new YahooChatLobby(count,users,voices,webcams);
				ycr.addLobby(ycl);
				// -----Hash on room:lobby, so we can find it in chat packet code
				chatByNetName.put(ycl.getNetworkName(),ycl);
			}

			in = nextTag(br);
		}
		br.close();
	}
	private String _getDef(Hashtable h,String k,String d)
	{	if(h.containsKey(k))  return (String)h.get(k);
			else  return d;
	}

	// -----------------------------------------------------------------
	// This method fetches the top level categories - if the data has
	// already been loaded, the current copy is returned.  If a *live*
	// Session object is passed, the cookies from that session will also
	// be included (Yahoo will not filter adult categories!)
	// -----------------------------------------------------------------
	public static YahooChatCategory loadCategories() throws IOException
	{	return loadCategories(null);
	}
	public static YahooChatCategory loadCategories(Session ss) throws IOException
	{	// -----Already loaded?
		if(rootCategory!=null)  return rootCategory;

		Stack st = new Stack();

		rootCategory = new YahooChatCategory(ss,0,0,"<root>");
		st.push(rootCategory);

		// -----Open a HTTP connection to the top level
		String addr = PREFIX+localePrefix+TOP_URL;
		URLConnection uConn = new URL(addr).openConnection();
		Util.initURLConnection(uConn);
		if(rootCategory.cookieLine!=null)  
			uConn.setRequestProperty("Cookie",rootCategory.cookieLine);
		BufferedReader br = new BufferedReader(
			new InputStreamReader(Util.wrapIn(uConn.getInputStream())) );

		// -----Header doesn't terminate with blank line (?)
		String in=br.readLine();

		// -----Prep tag reader - it shouldn't need to read from conn this time
		inputLine=in;  in=nextTag(br);
		// -----Process each tag into a tree using a stack
		int level=0;
		while(in!=null)
		{	if(in.startsWith("<content "))
			{	Hashtable attrs=getAttrs(in);
				rootCategory.id=Long.parseLong((String)attrs.get("time"));
			}
			else if(in.startsWith("<category "))
			{	level++;
				try
				{	Hashtable attrs=getAttrs(in);
					long id = Long.parseLong( (String)attrs.get("id") );
					String name = (String)attrs.get("name");
					YahooChatCategory ycc = new YahooChatCategory(ss,id,level,name);
					YahooChatCategory o = (YahooChatCategory)st.peek();
					o.add(ycc);  st.push(ycc);
				}catch(Exception e) { e.printStackTrace(); }
			}
			else if(in.startsWith("</category>"))
			{	st.pop();
			}
			in = nextTag(br);
		}
		br.close();
		return rootCategory;
	}

	// -----------------------------------------------------------------
	// Set the locale prefix for reading chatroom data.  For example "fr",
	// which would result in "fr.insider.yahoo.com"
	// -----------------------------------------------------------------
	public static void setLocalePrefix(String l)
	{	localePrefix = (l==null) ? "" : l+".";
	}
	
	// -----------------------------------------------------------------
	// Get the local string.
	// -----------------------------------------------------------------
	public static String getLocale()
	{	return (localePrefix.length()<=0) ? "us" : localePrefix.substring(0,2);
	}

	// -----------------------------------------------------------------
	// Low level XML reading methods
	// -----------------------------------------------------------------
	private static String nextTag(BufferedReader br) throws IOException
	{	// -----Note: inputLine is a private transient class member
		if(inputLine==null)  return null;
		while(inputLine.trim().length()<=0 || inputLine.indexOf("<")<0)
		{	inputLine=br.readLine();
			if(inputLine==null)  return null;
		}
		// -----Remove everything before opening '<'
		inputLine=inputLine.substring(inputLine.indexOf("<"));
		
		// -----Sanitise (deal with \n or <> inside quoted strings
		// -----First look for \n inside quotes
		boolean inside;
		do
		{	inside=false;
			for(int i=0;i<inputLine.length();i++)
				if(inputLine.charAt(i)=='\"')  inside=!inside;
			if(inside)
			{	String s = br.readLine();
				if(s==null)  s="\"";
				inputLine=inputLine+s;
			}
		}while(inside);
		// -----Now look for <> inside strings
		StringBuffer sb = new StringBuffer();
		inside=false;
		for(int i=0;i<inputLine.length();i++)
		{	switch(inputLine.charAt(i))
			{	case '<' : sb.append(inside?"&lt;":"<");  break;
				case '>' : sb.append(inside?"&gt;":">");  break;
				case '\"' : inside=!inside;  // ...
				default : sb.append(inputLine.charAt(i));  break;
			}
		}
		inputLine=sb.toString();
		
		// -----Extract everything up to next '>'
		int idx=inputLine.indexOf(">");
		String r=null;
		if(idx>=0) { r=inputLine.substring(0,idx+1);  inputLine=inputLine.substring(idx+1); }
			else { r=inputLine;  inputLine=""; }
		return r;
	}

	private static Hashtable getAttrs(String s)
	{	Hashtable h = new Hashtable();
		int idx = s.indexOf("=\"");
		while(idx>=0)
		{	int len=idx+2;

			String a1 = s.substring(0,idx);			// Before separator
			String a2 = s.substring(idx+2);			// ...and after

			idx=a1.indexOf(" ");					// Attr name starts at first
			if(idx>=0)  a1=a1.substring(idx+1);		// whitespace char

			idx=a2.indexOf("\"");					// Value ends at closing
			if(idx>=0)  a2=a2.substring(0,idx);		// quote char

			h.put(a1,a2);

			len+=a2.length()+1;  s=s.substring(len);
			idx = s.indexOf("=\"");
		}
		return h;
	}

	// -----------------------------------------------------------------
	// Package level methods: get lobby object based upon network name
	// -----------------------------------------------------------------
	static YahooChatLobby getLobby(String nn) { return (YahooChatLobby)chatByNetName.get(nn); }


	// -----------------------------------------------------------------
	// Object as text string
	// -----------------------------------------------------------------
	public String toString()
	{	return new StringBuffer("name=").append(name)
			.append(" id=").append(id).toString();
	}


	// -----------------------------------------------------------------
	// DEBUG
	// -----------------------------------------------------------------
	/*void printGraph(String tb)
	{	if(categories.size()>0)
		{	System.out.println(tb+"<"+name+">");
			for(int i=0;i<categories.size();i++)
				((YahooChatCategory)categories.elementAt(i)).printGraph(tb+"  ");
			System.out.println(tb+"</"+name+">");
		}
		else
		{	System.out.println(tb+name);
		}
	}

	private static void dump(Vector v)
	{	for(int i=0;i<v.size();i++)
		{	YahooChatRoom ycr = (YahooChatRoom)v.elementAt(i);
			System.out.println(ycr.toString());
			Vector v2 = ycr.getLobbies();
			for(int j=0;j<v2.size();j++)
				System.out.println("  "+((YahooChatLobby)v2.elementAt(j)).toString());
		}
	}

	public static void main(String[] st)
	{	try
		{	YahooChatCategory ycc = loadCategories();
			ycc.printGraph("");

			ycc = (YahooChatCategory)(ycc.getCategories().elementAt(0));
			dump(ycc.getPublicRooms());
			dump(ycc.getPrivateRooms());
		}catch(Exception e) { e.printStackTrace(); }
	}*/
}
