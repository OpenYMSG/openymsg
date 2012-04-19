package org.openymsg.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.ConnectionBuilder;
import org.openymsg.network.NetworkConstants;
import org.openymsg.network.direct.DirectConnectionBuilder;

/**
 * Default SessionConfig
 * @author neilhart
 */
public class SessionConfigImpl implements SessionConfig {
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
		return NetworkConstants.CAPACITY_HOSTS;
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
		return NetworkConstants.SCS_HOSTS;
	}

	@Override
	public ConnectionBuilder getBuilder() {
		return new DirectConnectionBuilder().with(this);
	}
}
