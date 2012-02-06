package org.openymsg.network.task;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;


public class GetConnectionServerTest extends TestCase{
    
    public void testSingleSuccessCase() {
        List<String> hosts = new ArrayList<String>();
        hosts.add("vcs1.msg.yahoo.com");
        hosts.add("vcs2.msg.yahoo.com");
        hosts.add("httpvcs1.msg.yahoo.com");
        hosts.add("httpvcs2.msg.yahoo.com");
        GetConnectionServer server = new GetConnectionServer();
        for(String host: hosts) {
            String ipAddress = server.getIpAddress(host);
            Assert.assertNotNull("Ip address for: " + host, ipAddress);
            System.out.println("Host: " + host + ", ipAddress: " + ipAddress);
        }
    }

}
