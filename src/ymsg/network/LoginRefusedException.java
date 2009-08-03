package ymsg.network;

public class LoginRefusedException extends YahooException
{	private long status=-1;

	LoginRefusedException(String m) { super(m); }
	LoginRefusedException(String m,long st) { this(m);  status=st; }

	public long getStatus() { return status; }
}
