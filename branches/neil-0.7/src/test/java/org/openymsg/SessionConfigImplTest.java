package org.openymsg;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import org.openymsg.config.SessionConfig;
import org.openymsg.config.SessionConfigImpl;
import org.testng.annotations.Test;

public class SessionConfigImplTest {

	@Test
	public void getLoginUrl() throws UnsupportedEncodingException {
		SessionConfig config = new SessionConfigImpl();
		String seed = "sfdfsdfdsf";
		String password = "password";
		String username = "username";
		String url = config.getPasswordTokenGetUrl(username, password, seed);
		assert url.contains(username);
		assert url.contains(password);
		assert url.contains(seed);
	}

	@Test
	public void getLocalSocket() {
		SessionConfig config = new SessionConfigImpl();
		InetSocketAddress socket = config.getLocalSocket();
		assert !socket.isUnresolved();
	}
}
