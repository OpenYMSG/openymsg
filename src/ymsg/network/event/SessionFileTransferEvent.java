package ymsg.network.event;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.List;

// *********************************************************************
//							To		From	Message	Timestamp	Location
// fileTransferReceived		y		y		y		y			y
// *********************************************************************
public class SessionFileTransferEvent extends SessionEvent
{
    protected URL location=null;

    public static final int UNKNOWN = -1;
    public static final int ACCEPT = 0;
    public static final int CANCEL = 1;
    public static final int RECEIVED = 2;
    public static final int SENT = 3;
//    public static final int PREPARING = 4;
    public static final int IN_PROGRESS = 4;
    public static final int FAILED = 5;
    public static final int REFUSED = 6;

    private int state = UNKNOWN;

    private String id = null;
    private List<String> fileNames = null;
    private List<String> fileSizes = null;
    private long progress = 0;

	// -----------------------------------------------------------------
	// CONSTRUCTORS
	// -----------------------------------------------------------------
	public SessionFileTransferEvent(Object o,String t,String f,String m,String dt,String l)
	{	super(o,t,f,m,dt);
		try { location = new URL(l); }
			catch(MalformedURLException e) { location=null; }
	}

    public SessionFileTransferEvent(Object o,String to,String from, String id)
    {
        super(o, to, from);
        this.id = id;
    }

    public SessionFileTransferEvent(Object o,String to,String from, String id, int state)
    {
        this(o, to, from, id);
        this.state = state;
    }

    public SessionFileTransferEvent(Object o,String to,String from, String id, int state, long progress)
    {
        this(o, to, from, id);
        this.state = state;
        this.progress = progress;
    }

    public SessionFileTransferEvent(Object o,String to,String from, 
        List<String> fileNames, List<String> fileSizes, String id, int state)
    {
        this(o, to, from, id);
        this.state = state;
        this.fileNames = fileNames;
        this.fileSizes = fileSizes;
    }

	// -----------------------------------------------------------------
	// Accessors
	// -----------------------------------------------------------------
	public URL getLocation() { return location; }

	public String toString()
	{	return super.toString()+" location:"+location;
	}

	// -----------------------------------------------------------------
	// Unqualified name of file sent
	// -----------------------------------------------------------------
	public String getFilenameFromLocation()
	{	try
		{	String s = location.getFile();
			if(s.lastIndexOf("/")>0)  s=s.substring(s.lastIndexOf("/")+1);
			if(s.indexOf("?")>=0)  s=s.substring(0,s.indexOf("?"));
			return s;
		}catch(Exception e) { return "ymsg_default.out"; }
	}

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return the state
     */
    public int getState()
    {
        return state;
    }

    /**
     * @return the fileName
     */
    public List<String> getFileNames()
    {
        return fileNames;
    }

    /**
     * @return the fileSize
     */
    public List<String> getFileSizes()
    {
        return fileSizes;
    }

    /**
     * @return the progress
     */
    public long getProgress()
    {
        return progress;
    }

    /**
     * @param progress the progress to set
     */
    public void setProgress(long progress)
    {
        this.progress = progress;
    }
}
