package org.openymsg.network;

/**
 * Constants for communicating with Yahoo. These should be based on Yahoo Messenger 9 and match libpurple
 * @author neilhart
 */
public interface NetworkConstants {
	int SECOUND = 1000;
	/** Last version 9 */
	String CLIENT_VERSION = "9.0.0.2162";
	/** Last version 9 */
	String CLIENT_VERSION_ID = "4194239";
	/** login host */
	String LOGIN_HOST = "login.yahoo.com";
	/** URL for getting the password token */
	String PASSWORD_TOKEN_GET_URL_FORMAT =
			"https://" + LOGIN_HOST + "/config/pwtoken_get?src=ymsgr&ts=&login=%s&passwd=%s&chal=%s";
	/** URL for logging in with the password token */
	String PASSWORD_TOKEN_LOGIN_URL_FORMAT = "https://" + LOGIN_HOST + "/config/pwtoken_login?src=ymsgr&ts=&token=%s";
	String ADDRESSBOOK_URL = "http://address.yahoo.com/yab/us?v=XM";
	String ADDRESSBOOK_COOKIE_FORMAT = "Y=%s; T=%s";
	/** Internet Protocol Suite */
	byte[] PROTOCOL = "YMSG".getBytes();// { 'Y', 'M', 'S', 'G' };
	/** Used for Version - 16 */
	byte VERSION_PART = 0x10;
	/** Version - 16 */
	byte[] VERSION = {0x00, VERSION_PART, 0x00, 0x00};
	/** hosts for finding connection ips */
	String[] CAPACITY_HOSTS = {"vcs1.msg.yahoo.com", "vcs2.msg.yahoo.com"};
	/** older hosts for finding connection ips */
	String[] SCS_HOSTS = {"scsa.msg.yahoo.com", "scsb.msg.yahoo.com", "scsc.msg.yahoo.com"};
	/** capacity url format */
	String CAPACITY_URL_FORMAT = "http://%s/capacity";
	/** port for direct connections */
	int DIRECT_PORT = 5050;
	/** time out for http request */
	int LOGIN_HTTP_TIMEOUT = 10 * SECOUND;
	String ROOM_LIST_LOCALE_US = "us";
	String ROOM_LIST_LOCALE_JP = "jp";
}
