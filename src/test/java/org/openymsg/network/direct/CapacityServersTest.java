package org.openymsg.network.direct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.openymsg.config.SessionConfig;
import org.openymsg.config.SessionConfigSimple;
import org.testng.annotations.Test;

public class CapacityServersTest {

	@Test
	// TODO needs internet
	public void getIpAddress() {
		SessionConfig config = new SessionConfigSimple();
		CapacityServers servers = new CapacityServers(config);
		for (String ipAddress : servers.getIpAddresses()) {
			System.out.println("ipAddress: " + ipAddress);
		}
		;
	}

	@Test
	public void readIpAddress() throws IOException {
		SessionConfig config = new SessionConfigSimple();
		CapacityServers servers = new CapacityServers(config);
		String host = "host";
		String url = "url";
		ByteArrayOutputStream out = new ByteArrayOutputStream(100);
		String response = "COLO_CAPACITY=1\r\n" + "CS_IP_ADDRESS=10.10.10.10\r\n";
		// System.out.println(response);
		out.write(response.getBytes());
		String ipAddress = servers.readIpAddress(host, url, out);
		assert ipAddress.equals("10.10.10.10");
	}

	@Test
	public void read2IpAddress() throws IOException {
		SessionConfig config = new SessionConfigSimple();
		CapacityServers servers = new CapacityServers(config);
		String host = "host";
		String url = "url";
		ByteArrayOutputStream out = new ByteArrayOutputStream(100);
		String response = "COLO_CAPACITY=2\r\n" + "CS_IP_ADDRESS=10.10.10.10\r\n" + "CS_IP_ADDRESS=10.10.10.11\r\n";
		// System.out.println(response);
		out.write(response.getBytes());
		String ipAddress = servers.readIpAddress(host, url, out);
		assert ipAddress.equals("10.10.10.10");
	}

}
