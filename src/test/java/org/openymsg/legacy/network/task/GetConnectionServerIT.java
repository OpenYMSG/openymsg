package org.openymsg.legacy.network.task;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GetConnectionServerIT {
	@Test
	public void testSingleSuccessCase() {
		List<String> hosts = new ArrayList<String>();
		hosts.add("vcs1.msg.yahoo.com");
		hosts.add("vcs2.msg.yahoo.com");
		hosts.add("httpvcs1.msg.yahoo.com");
		hosts.add("httpvcs2.msg.yahoo.com");
		GetConnectionServer server = new GetConnectionServer();
		for (String host : hosts) {
			String ipAddress = server.getIpAddress(host);
			assertNotNull("Ip address for: " + host, ipAddress);
			System.out.println("Host: " + host + ", ipAddress: " + ipAddress);
		}
	}
}
