/**
 * 
 */
package org.openymsg.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openymsg.network.FireEvent;
import org.openymsg.network.ServiceType;
import org.openymsg.network.Session;
import org.openymsg.network.SessionState;
import org.openymsg.network.YahooUser;
import org.openymsg.network.event.WaitListener;
import org.openymsg.roster.Roster;

/**
 * @author Giancarlo Frison - Nimbuzz B.V. <giancarlo@nimbuzz.com>
 * 
 */
public class YahooTestAbstract {
    protected static final String CHATMESSAGE = "CHATMESSAGE";

    protected static final String USERNAME = PropertiesAvailableTest.getUsername("presenceuser1");

    protected static final String PASSWORD = PropertiesAvailableTest.getPassword(USERNAME);

    protected static final String OTHERUSR = PropertiesAvailableTest.getUsername("logintestuser3");

    protected static final String OTHERPWD = PropertiesAvailableTest.getPassword(OTHERUSR);

    static Session sess1;
    static Session sess2;
    static WaitListener listener1;
    static WaitListener listener2;

    /**
     * @throws Throwable
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Throwable {
        try {
            sess1 = new Session();
            sess2 = new Session();
            listener1 = new WaitListener(sess1);
            listener2 = new WaitListener(sess2);
            sess1.login(USERNAME, PASSWORD);
            sess2.login(OTHERUSR, OTHERPWD);
            sess1.addSessionListener(listener1);
            sess2.addSessionListener(listener2);
            listener1.waitForEvent(2, ServiceType.LOGON);
            listener2.waitForEvent(2, ServiceType.LOGON);
            removeAllContacts(sess1, listener1);
            removeAllContacts(sess2, listener2);
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        removeAllContacts(sess1, listener1);
        if (sess1.getSessionStatus() == SessionState.LOGGED_ON) {
            sess1.logout();
        }

        removeAllContacts(sess2, listener2);
        if (sess2.getSessionStatus() == SessionState.LOGGED_ON) {
            sess2.logout();
        }
    }

    /**
     * Removes all contacts from the roster of the user that logged in using the specified session.
     * 
     * @throws IOException
     */
    protected static void removeAllContacts(Session sess, WaitListener listener) {
        drain();

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Roster roster = sess.getRoster();

        List<YahooUser> oldRoster = new ArrayList<YahooUser>(roster);

        // System.err.print("Removing: ");
        for (final YahooUser user : oldRoster) {
            roster.remove(user);
            // System.err.print(user.getId() + ",");
            FireEvent event = listener.waitForEvent(5, ServiceType.FRIENDREMOVE);
        }
    }

    /**
     * Drains the event queue;
     */
    protected static void drain() {
        listener1.clearEvents();
        listener2.clearEvents();
    }

}
