package org.openymsg.legacy.roster;

/**
 * Utility {@link RosterListener} implementation that lets you count how often a request has been dispatched to it.
 * 
 * @author Guus der Kinderen, guus@nimbuzz.com
 * 
 */
public class MockRosterListener implements RosterListener {

    private int eventCount = 0;

    /*
     * (non-Javadoc)
     * 
     * @see org.openymsg.legacy.roster.RosterListener#rosterChanged(org.openymsg.legacy.roster.RosterEvent)
     */
    @Override
	public void rosterChanged(RosterEvent event) {
        eventCount++;
    }

    /**
     * Returns the number of events that have been received by this listener.
     * 
     * @return the eventCount
     */
    public int getEventCount() {
        return eventCount;
    }

    /**
     * Sets the event counter to 0.
     */
    public void resetEventCount() {
        eventCount = 0;
    }
}
