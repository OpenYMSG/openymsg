package org.openymsg.session;

public interface SessionSessionCallback {

	void logoffNormalComplete();

	void logoffForced(LogoutReason state);

}
