package ymsg.network.event;

public class SessionAuthorizationEvent 
        extends SessionEvent
{	
    protected String fName = null, lName = null;
    protected boolean authorizationAccepted = false;
    protected boolean authorizationDenied = false;
    protected boolean authorizationRequest = false;

    public SessionAuthorizationEvent(Object o, String who, String msg)
    {	
        super(o);  
        this.from = who;  
        message = msg;
    }

    public SessionAuthorizationEvent(Object o, 
            String id, String who, String fname, String lname, String msg)
    {	
        this(o, who, msg);  
        this.fName = fname;
        this.lName = lname;
        this.to = id;
    }


    // -----------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------
    
    public void setStatus(int s) 
    {
        super.setStatus(s);
        
        switch(s)
        {
            case 1: authorizationAccepted = true;   /* Authorization Accepted */ break; 
            case 2: authorizationDenied = true;     /* Authorization Denied */ break; 
            default: authorizationRequest = true;   /* Authorization Request? */ break; 
        }
        status=s; 
    }
    
    public boolean isAuthorizationAccepted()
    {
        return authorizationAccepted;
    }
    
    public boolean isAuthorizationDenied()
    {
        return authorizationDenied;
    }
    
    public boolean isAuthorizationRequest()
    {
        return authorizationRequest;
    }

    public String toString()
    {	
        return "to:"+to+" from:"+from+" message:"+message;
    }
}
