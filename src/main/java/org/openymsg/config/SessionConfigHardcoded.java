package org.openymsg.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.ConnectionBuilder;
import org.openymsg.network.NetworkConstants;
import org.openymsg.network.direct.DirectConnectionBuilder;
import org.openymsg.network.url.URLStreamBuilder;
import org.openymsg.network.url.URLStreamBuilderImpl;

public class SessionConfigHardcoded implements SessionConfig {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionConfigImpl.class);

	@Override
	public String getLoginHost() {
		return NetworkConstants.LOGIN_HOST;
	}

	@Override
	public String getPasswordTokenGetUrl(String username, String password, String seed) {
		String encodedPassword;
		try {
			encodedPassword = URLEncoder.encode(password, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			log.error("Encoding password: " + password, e);
			// TODO handle failure
			return null;
		}
		String encodedSeed;
		try {
			encodedSeed = URLEncoder.encode(seed, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			log.error("Encoding seed: " + seed, e);
			// TODO handle failure
			return null;
		}
		return String.format(NetworkConstants.PASSWORD_TOKEN_GET_URL_FORMAT, username, encodedPassword, encodedSeed);
	}

	@Override
	public String getPasswordTokenLoginUrl(String token) {
		return String.format(NetworkConstants.PASSWORD_TOKEN_LOGIN_URL_FORMAT, token);
	}

	@Override
	public String[] getCapacityHosts() {
		return new String[0];
	}

	@Override
	public int getConnectionTimeout() {
		return 2 * SECOND;
	}

	@Override
	public Integer getSocketSize() {
		return null;
	}

	@Override
	public String[] getScsHosts() {
		String[] ips = { "67.195.187.252", "67.195.187.197" };
		return ips;
	}

	@Override
	public ConnectionBuilder getConnectionBuilder() {
		return new DirectConnectionBuilder().with(this);
	}

	@Override
	public URLStreamBuilder getURLStreamBuilder() {
		return new URLStreamBuilderImpl();
	}
}
