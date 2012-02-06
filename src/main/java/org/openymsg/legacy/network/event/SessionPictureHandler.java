package org.openymsg.legacy.network.event;

/**
 * Handler definition to work with pictures (avatars).
 * 
 * @author Damian Minkov (damencho)
 */
public interface SessionPictureHandler {

    /**
     * Returns a checksum of the picture in question. An example implementation is this:
     * 
     * <pre>
     * File pic = new File(&quot;/home/damencho/personal/damencho-small.jpg&quot;);
     * in = new FileInputStream(pic);
     * byte[] data = new byte[in.available()];
     * in.read(data);
     * in.close();
     * return String.valueOf(new String(data).hashCode());
     * </pre>
     * 
     * @return The checksum of the picure.
     */
    public String getPictureChecksum();

    /**
     * Receives an incoming picture event
     * 
     * @param event
     *            The event that holds the picture information.
     */
    public void pictureReceived(SessionPictureEvent event);
    // TODO: unsure what to make of this - it was in Damian's code, but unused
}
