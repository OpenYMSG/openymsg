package org.openymsg.execute;

public enum ExecutorState {
	/**
	 * Not logged in
	 */
    UNSTARTED(false, true, false),
    /**
     * Started to logon
     */
    CONNECTING(false, false, true), 
    /**
     *  Connected to yahoo
     */
    CONNECTED(true, false, true), 
    /**
     * Dead by Error
     */
    FAILED(false, true, false); 

    private boolean connected;
    private boolean startable;
    private boolean loginable;

    ExecutorState(boolean connected, boolean startable, boolean loginable) {
		this.connected = connected;
		this.startable = startable;
		this.loginable = loginable;
    }

	public boolean isConnected() {
		return connected;
	}

	public boolean isStartable() {
		return startable;
	}
	
	public boolean isLoginable() {
		return loginable;
	}
}
