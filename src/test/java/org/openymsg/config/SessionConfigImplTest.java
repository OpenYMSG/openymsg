package org.openymsg.config;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class SessionConfigImplTest {
	@Test
	public void getLoginUrl() throws UnsupportedEncodingException {
		SessionConfig config = new SessionConfigSimple();
		String seed = "sfdfsdfdsf";
		String password = "password";
		String username = "username";
		String url = config.getPasswordTokenGetUrl(username, password, seed);
		assert url.contains(username);
		assert url.contains(password);
		assert url.contains(seed);
	}
}
