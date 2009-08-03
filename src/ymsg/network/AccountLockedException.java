package ymsg.network;

import java.net.URL;

public class AccountLockedException extends LoginRefusedException
{	URL location;

	AccountLockedException(String m,URL u)
	{	super(m,StatusConstants.STATUS_LOCKED);  location=u;
	}

	public URL getWebPage() { return location; }
}
