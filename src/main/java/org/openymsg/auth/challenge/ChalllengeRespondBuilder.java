package org.openymsg.auth.challenge;

import java.security.NoSuchAlgorithmException;

public class ChalllengeRespondBuilder {
	private ChallengeResponseStrategy strategy = null;
	private String challenge;

	public ChalllengeRespondBuilder() {
	}

	public ChalllengeRespondBuilder challenge(String challenge) {
		this.challenge = challenge;
		return this;
	}

	public ChalllengeRespondBuilder useV16() {
		this.strategy = new ChallengeResponseV16();
		return this;
	}

	public String build() throws NoSuchAlgorithmException {
		return this.strategy.getStrings(challenge);
	}

}
