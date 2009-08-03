package ymsg.network;

// *********************************************************************
// Various constant settings for network code.
// *********************************************************************
public interface NetworkConstants
{	// -----Header constants
	// -----Note: between versions 10 and 12 the version field appears to have 
	// -----swapped endian-ness, from 0x0a00 to 0x000c
	final static byte PROTOCOL = 0x10;	// Protocol version 10 (YMSG 10)
	final static byte[] MAGIC = { 'Y','M','S','G' };
	final static byte[] VERSION = { 0x00,PROTOCOL,0x00,0x00 };
	final static byte[] VERSION_HTTP = { PROTOCOL,0x00,(byte)0xc8,0x00 };
	final static int YMSG9_HEADER_SIZE = 20;
	final static String CLIENT_VERSION = "9.0.0.2152";
    final static String CLIENT_VERSION_ID = "4194239";

	// -----File transfer
	/* Now the property ymsg.network.httpFileTransferHost, accessed via Util.class
	public final static String FILE_TF_HOST = "filetransfer.msg.yahoo.com";
	public final static String FILE_TF_URL = "http://"+FILE_TF_HOST+":80/notifyft";
	*/
	final static String FILE_TF_PORTPATH = ":80/notifyft";
	final static String FILE_TF_USER = "FILE_TRANSFER_SYSTEM";

	// -----HTTP
	final static String USER_AGENT = "Mozilla/4.5 [en] (X11; U; FreeBSD 2.2.8-STABLE i386)";
	final static String END = "\n";	// Line terminator

	// -----HTTP proxy property names
	final static String PROXY_HOST_OLD = "proxyHost";
	final static String PROXY_PORT_OLD = "proxyPort";
	final static String PROXY_HOST = "http.proxyHost";
	final static String PROXY_PORT = "http.proxyPort";
	final static String PROXY_SET = "proxySet";
	final static String PROXY_NON = "http.nonProxyHosts";

	// -----SOCKS proxy property names
	final static String SOCKS_HOST = "socksProxyHost";
	final static String SOCKS_PORT = "socksProxyPort";
	final static String SOCKS_SET = "socksProxySet";

	// -----Cookies in array (see Session.getCookies())
	final static int COOKIE_Y = 0;
	final static int COOKIE_T = 1;
	final static int COOKIE_C = 2;

	// -----Default timouts (seconds)
	final static int LOGIN_TIMEOUT = 60;

	// -----Ping timeout
	final static int PING_TIMEOUT = 1000*60*20;	// 20 minutes


	// -----Chat server

	// -----Buzz string
	final static String BUZZ = "<ding>";
}
