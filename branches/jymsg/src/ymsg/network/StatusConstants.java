package ymsg.network;

public interface StatusConstants
{	public final static int UNSTARTED = 0;			// Not logged in
	public final static int AUTH = 1;				// Logging in
	public final static int MESSAGING = 2;			// Logged in
	public final static int FAILED = 3;				// Dead
	public final static int CONNECT = 100;			// (Chat) Introduction
	public final static int LOGON = AUTH;			// (Chat) Alias for auth

	public static final long STATUS_AVAILABLE		= 0;
	public static final long STATUS_BRB				= 1;
	public static final long STATUS_BUSY			= 2;
	public static final long STATUS_NOTATHOME		= 3;
	public static final long STATUS_NOTATDESK		= 4;
	public static final long STATUS_NOTINOFFICE		= 5;
	public static final long STATUS_ONPHONE			= 6;
	public static final long STATUS_ONVACATION		= 7;
	public static final long STATUS_OUTTOLUNCH		= 8;
	public static final long STATUS_STEPPEDOUT		= 9;
	public static final long STATUS_INVISIBLE		= 12;
	public static final long STATUS_BAD				= 13; // Bad login?
	public static final long STATUS_LOCKED			= 14; // You've been naughty
	public static final long STATUS_CUSTOM			= 99;
	public static final long STATUS_IDLE			= 999;
	public static final long STATUS_OFFLINE			= 0x5a55aa56;
    public static final long STATUS_WEBLOGIN        = 0x5a55aa55;
	public static final long STATUS_TYPING			= 0x16;

    public static final long STATUS_DISCONNECTED    = 0xffffffff;

	public static final long STATUS_BADUSERNAME		= 3; // Account unknown?
	public static final long STATUS_INCOMPLETE		= 5; // Chat login etc.
	public static final long STATUS_COMPLETE		= 1; // Chat login etc.

	public static final String STATUS_AVAILABLE_STR		= "Available";
	public static final String STATUS_BRB_STR			= "Be right back";
	public static final String STATUS_BUSY_STR			= "Busy";
	public static final String STATUS_NOTATHOME_STR		= "Not at home";
	public static final String STATUS_NOTATDESK_STR		= "Not at desk";
	public static final String STATUS_NOTINOFFICE_STR	= "Not in office";
	public static final String STATUS_ONPHONE_STR		= "On the phone";
	public static final String STATUS_ONVACATION_STR	= "On vacation";
	public static final String STATUS_OUTTOLUNCH_STR	= "Out to lunch";
	public static final String STATUS_STEPPEDOUT_STR	= "Stepped out";
	public static final String STATUS_INVISIBLE_STR		= "Invisible";
	public static final String STATUS_CUSTOM_STR		= "<custom>";
	public static final String STATUS_IDLE_STR			= "Zzz";

	public static final String NOTIFY_TYPING 		= "TYPING";
	public static final String NOTIFY_GAME 			= "GAME";
}
