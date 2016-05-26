package org.openymsg.context.auth;

import org.openymsg.network.url.URLConnectionFailure;
import org.openymsg.network.url.URLCreationFailure;
import org.openymsg.network.url.URLResponseFailure;
import org.openymsg.network.url.URLSystemFailure;

public class AuthenticationAttemptStatusImpl implements AuthenticationAttemptStatus {
	private final String error;

	public AuthenticationAttemptStatusImpl(String error) {
		this.error = error;
	}

	public AuthenticationAttemptStatusImpl(URLResponseFailure failure) {
		this("Failed handling response of: " + failure.getUrl() + " with exception: " + failure.getException());
	}

	public AuthenticationAttemptStatusImpl(URLCreationFailure failure) {
		this("Failed creating url with: " + failure.getUrl() + " with exception: " + failure.getException());
	}

	public AuthenticationAttemptStatusImpl(URLConnectionFailure failure) {
		this("Failed handling connection response of: " + failure.getUrl() + " with response code: "
				+ failure.getResponseCode() + " with response message: " + failure.getResponseMessage()
				+ " with exception: " + failure.getException());
	}

	public AuthenticationAttemptStatusImpl(URLSystemFailure failure) {
		this("Failed handling system URLConnection with: " + failure.getUrl() + " with type: " + failure.getUcType());
	}

	@Override
	public String getError() {
		return error;
	}
}
