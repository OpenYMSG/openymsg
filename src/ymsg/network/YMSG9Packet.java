package ymsg.network;

import java.util.Map.*;
import java.util.*;

// *********************************************************************
// This class is nothing more than a convenient data structure to hold
// the information extracted from a single YMSG packet (message).  The
// body array holds the list of strings, in sequence, as they appeared
// in the body section of the packet.
//
// This class is returned by YMSG9InputStreamReader.readPacket();
// See YMSG9InputStream.java for more details on the protocol.
//
// Note: the term 'packet' here is strictly speaking incorrect, as a
// YMSG message could in theory take up more than one TCP packet - but
// it helps to distinguish these lower-level network messages from
// the higher-level dialogue 'message's in the protocol.
// *********************************************************************
class YMSG9Packet
{   String magic;
    int version,length,service;
    long status,sessionId;
    String[] body;

    // Returns the *key index* (not value index) of n'th key of type k
    private int getNthLocation(String k,int n)
    {   for(int i=0;i<body.length;i+=2)
        {   if(body[i].equals(k))  n--;
            if(n<0)  return i;
        }
        return -1;
    }

    String getNthValue(String k,int n)
    {   int l = getNthLocation(k,n);
        if(l<0)  return null;  else  return body[l+1];
    }
    String getValue(String k) { return getNthValue(k,0); }

    String[] getValues(String k)
    {   int cnt=0,j=0;
        for(int i=0;i<body.length;i+=2)
            if(body[i].equals(k))  cnt++;
        String[] sa = new String[cnt];
        for(int i=0;i<body.length;i+=2)
            if(body[i].equals(k))  sa[j++]=body[i+1];
        return sa;
    }

    String getValueFromNthSet(String set,String k,int n)
    {   int i=getNthLocation(set,n);
        if(i<0)  return null;
        i+=2;
        while(i<body.length)
        {   if(body[i].equals(k))  return body[i+1];    // Found it
            else if(body[i].equals(set))  return null;  // Start of next set
            else  i+=2;
        }
        return null;
    }

    public Collection<String[]> entries()
    {
        Vector<String[]> result = new Vector<String[]>();

        for (int i = 0; i < body.length; i+=2)
        {
            result.add(new String[]{body[i], body[i + 1]});
        }

        return result;
    }

    boolean exists(String k) { return (getValue(k)!=null); }

    // FIX: Not thread safe (reading array while copies are taking place)
    void append(YMSG9Packet pkt)
    {   String[] arr = new String[body.length + pkt.body.length];
        System.arraycopy(body,0 , arr,0 , body.length);
        System.arraycopy(pkt.body,0 , arr,body.length , pkt.body.length);
        body=arr;
    }

    // Merge the supplied packet into this one, with special regard to
    // certain fields which need to be concatenated.  If the field key is
    // in the array provided, it is appended onto the end of the current value
    // for that key (if it doesn't exist it is created).  Other keys are
    // appended to the end of this packet.
    // FIX: Not thread safe (reading array while copies are taking place)
    void merge(YMSG9Packet pkt,String[] concatFields)
    {   Vector appendBuffer = new Vector();

        for(int i=0;i<pkt.body.length;i+=2)
        {   // -----Get next key/value
            String k=pkt.body[i] , v=pkt.body[i+1];
            // -----Look for key in list of merging fields
            boolean b=false;
            for(int j=0;j<concatFields.length;j++)
                if(concatFields[j].equals(k)) { b=true;  break; }
            // -----Its on the list, so attempt to merge to current
            if(b)
            {   int idx=getNthLocation(k,0);
                // -----If may be on the list, but do we have a field like this already?
                if(idx<0)
                {   // -----No!  Forget about the merge, just append to list
                    // FIX: what happens if two such fields appear in the same
                    // packet - one will overwrite the other?  (Can this happen?)
                    appendBuffer.addElement(k);
                    appendBuffer.addElement(pkt.body[i+1]);
                }
                else
                {   // -----Yes!  Merge!
                    body[idx+1]=body[idx+1]+v;
                }
            }
            else
            {   // -----Append new body field to current body field
                appendBuffer.addElement(k);
                appendBuffer.addElement(pkt.body[i+1]);
            }
        }
        if(appendBuffer.size()>0)
        {   String[] arr = new String[body.length + appendBuffer.size()];
            System.arraycopy(body,0 , arr,0 , body.length);
            for(int i=0;i<appendBuffer.size();i++)
                arr[body.length+i] = (String)appendBuffer.elementAt(i);
            body=arr;
        }
    }

    public String toString()
    {   String s=   "Magic:"+magic+" Version:"+version+" Length:"+length+
                    " Service:"+service+" Status:"+status+" SessionId:0x"+
                    Long.toHexString(sessionId)+"\n ";
        for(int i=0;i<body.length;i++)  s=s+" ["+body[i]+"]";
        return s;
    }

    public static void main(String[] args)
    {   YMSG9Packet p1 = new YMSG9Packet();
        YMSG9Packet p2 = new YMSG9Packet();

        String[] ta1={ "1","abc" , "2","123" };
        String[] ta2={ "1","def" , "2","123" , "3","x" , "4","zyx" };
        String[] ta3={ "4","xyz" };
        String[] ma1={ "1","4" };
        p1.body=ta1;
//      p2.body=ta2;
//      p1.merge(p2,ma1);
        System.out.println(p1);
//      p2.body=ta3;
//      p1.merge(p2,ma1);  System.out.println(p1);
    }
}
