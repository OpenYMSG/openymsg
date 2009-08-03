package ymsg.network;

import java.io.IOException;

public abstract class ConnectionHandler implements NetworkConstants
{	abstract void install(Session ss,ThreadGroup tg);
	abstract void open() throws IOException;
	abstract void close() throws IOException;
	abstract void sendPacket(PacketBodyBuffer body,int service,long status,long sessionID) throws IOException;
	abstract YMSG9Packet receivePacket() throws IOException;

	// Creates an string array from cookies in packet
	protected static String[] extractCookies(YMSG9Packet pkt)
	{	String[] cookies = new String[3];
		for(int i=0;i<cookies.length;i++)
		{	String s = pkt.getNthValue("59",i);
			if(s==null)  break;
			if(s.indexOf(";")>=0)  s=s.substring(0,s.indexOf(";"));
			switch(s.charAt(0))
			{	case 'Y' : cookies[COOKIE_Y]="Y="+s.substring(2);  break;
				case 'T' : cookies[COOKIE_T]="T="+s.substring(2);  break;
				case 'C' : cookies[COOKIE_C]="C="+s.substring(2);  break;
				default : System.err.println("Unknown cookie: "+s);
			}
		}
		return cookies;
	}
}
