package org.openymsg.network;

import org.openymsg.roster.Roster;
import org.openymsg.v1.network.DirectConnectionHandler;
import org.openymsg.v1.network.HTTPConnectionHandler;
import org.openymsg.v1.network.SOCKSConnectionHandler;
import org.openymsg.v1.network.SessionV1;

public class SessionFactory {
	
	@SuppressWarnings("unchecked")
	public static Session<Roster<YahooUser>> buildSOCKS(String proxy, int port) {
		return (Session<Roster<YahooUser>>)((Session<?>)new SessionV1(new SOCKSConnectionHandler(proxy, port)));
	}
	@SuppressWarnings("unchecked")
	public static Session<Roster<YahooUser>> buildHTTP(String host, int port) {
		return (Session<Roster<YahooUser>>)((Session<?>)new SessionV1(new HTTPConnectionHandler(host, port)));
	}
    // The following line (while ugly) allows us to send japanese users to the right server,
    // while perserving the ability to override servers for rest of the users.
	//    String serverName = (getUserName()!=null && getUserName().endsWith(".jp"))?SERVER_JAPAN:getServerName();
	@SuppressWarnings("unchecked")
	public static Session<Roster<YahooUser>> buildDirect(String host, int port) {
		return (Session<Roster<YahooUser>>)((Session<?>)new SessionV1(new DirectConnectionHandler(host, port)));
	}
	
	@SuppressWarnings("unchecked")
	public static Session<Roster<YahooUser>> build() {
		return (Session<Roster<YahooUser>>)((Session<?>)new SessionV1());
	}

}
