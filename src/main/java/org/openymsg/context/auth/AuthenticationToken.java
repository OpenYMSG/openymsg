package org.openymsg.context.auth;

import org.openymsg.context.auth.challenge.ChalllengeRespondBuilder;

import java.security.NoSuchAlgorithmException;

public class AuthenticationToken {
	private String username;
	private String password;
	private String seed;
	private String cookieY;
	private String cookieT;
	private String cookieB;
	private String crumb;
	private String ymsgr;
	private String challenge;

	public void setUsernameAndPassword(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void setYmsgr(String ymsgr) {
		this.ymsgr = ymsgr;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public void setCookiesAndCrumb(String cookieY, String cookieT, String crumb, String cookieB) {
		this.cookieY = cookieY;
		this.cookieT = cookieT;
		this.crumb = crumb;
		this.cookieB = cookieB;
		this.createChallenge();
	}

	private void createChallenge() {
		challenge = null;
		try {
			challenge = new ChalllengeRespondBuilder().useV16().challenge(crumb + seed).build();
		} catch (NoSuchAlgorithmException e) {
			// TODO handle
			e.printStackTrace();
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSeed() {
		return seed;
	}

	public String getYmsgr() {
		return ymsgr;
	}

	public String getCookieB() {
		return cookieB;
	}

	public String getCookieY() {
		return cookieY;
	}

	public String getCookieT() {
		return cookieT;
	}

	public String getChallenge() {
		return challenge;
	}

	@Override
	public String toString() {
		return "AuthenticationToken [username=" + username + ", password=" + password + ", seed=" + seed + ", cookieY="
				+ cookieY + ", cookieT=" + cookieT + ", cookieB=" + cookieB + ", crumb=" + crumb + ", ymsgr=" + ymsgr
				+ ", challenge=" + challenge + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((challenge == null) ? 0 : challenge.hashCode());
		result = prime * result + ((cookieB == null) ? 0 : cookieB.hashCode());
		result = prime * result + ((cookieT == null) ? 0 : cookieT.hashCode());
		result = prime * result + ((cookieY == null) ? 0 : cookieY.hashCode());
		result = prime * result + ((crumb == null) ? 0 : crumb.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((seed == null) ? 0 : seed.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((ymsgr == null) ? 0 : ymsgr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AuthenticationToken))
			return false;
		AuthenticationToken other = (AuthenticationToken) obj;
		if (challenge == null) {
			if (other.challenge != null)
				return false;
		} else if (!challenge.equals(other.challenge))
			return false;
		if (cookieB == null) {
			if (other.cookieB != null)
				return false;
		} else if (!cookieB.equals(other.cookieB))
			return false;
		if (cookieT == null) {
			if (other.cookieT != null)
				return false;
		} else if (!cookieT.equals(other.cookieT))
			return false;
		if (cookieY == null) {
			if (other.cookieY != null)
				return false;
		} else if (!cookieY.equals(other.cookieY))
			return false;
		if (crumb == null) {
			if (other.crumb != null)
				return false;
		} else if (!crumb.equals(other.crumb))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (seed == null) {
			if (other.seed != null)
				return false;
		} else if (!seed.equals(other.seed))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (ymsgr == null) {
			if (other.ymsgr != null)
				return false;
		} else if (!ymsgr.equals(other.ymsgr))
			return false;
		return true;
	}
}
