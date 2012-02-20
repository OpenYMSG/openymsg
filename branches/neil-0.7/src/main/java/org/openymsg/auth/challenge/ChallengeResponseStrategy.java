package org.openymsg.auth.challenge;

import java.security.NoSuchAlgorithmException;

public interface ChallengeResponseStrategy {
	String getStrings(String challenge) throws NoSuchAlgorithmException;
}
