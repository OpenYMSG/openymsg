package org.openymsg.context.session;

public interface SessionSessionCallback {

	void logoffNormalComplete();

	void logoffForced(LogoutReason reason);

}
