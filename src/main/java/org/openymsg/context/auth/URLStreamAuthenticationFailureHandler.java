package org.openymsg.context.auth;

import org.openymsg.network.url.URLConnectionFailure;
import org.openymsg.network.url.URLCreationFailure;
import org.openymsg.network.url.URLResponseFailure;
import org.openymsg.network.url.URLSystemFailure;
import org.openymsg.network.url.UrlStreamStatusCallback;

public class URLStreamAuthenticationFailureHandler implements UrlStreamStatusCallback {
	private final SessionAuthenticationAttemptCallback attemptCallback;
	private final AuthenticationStep step;

	public URLStreamAuthenticationFailureHandler(SessionAuthenticationAttemptCallback attemptCallback,
			AuthenticationStep step) {
		this.attemptCallback = attemptCallback;
		this.step = step;
	}

	@Override
	public void failed(URLResponseFailure failure) {
		AuthenticationAttemptStatus status = new AuthenticationAttemptStatusImpl(failure);
		attemptCallback.setConnectionFailureStatus(step, status);
	}

	@Override
	public void failed(URLCreationFailure failure) {
		AuthenticationAttemptStatus status = new AuthenticationAttemptStatusImpl(failure);
		attemptCallback.setConnectionFailureStatus(step, status);
	}

	@Override
	public void failed(URLConnectionFailure failure) {
		AuthenticationAttemptStatus status = new AuthenticationAttemptStatusImpl(failure);
		attemptCallback.setConnectionFailureStatus(step, status);
	}

	@Override
	public void failed(URLSystemFailure failure) {
		AuthenticationAttemptStatus status = new AuthenticationAttemptStatusImpl(failure);
		attemptCallback.setConnectionFailureStatus(step, status);
	}
}
