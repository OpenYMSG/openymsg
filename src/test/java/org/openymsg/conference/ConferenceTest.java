package org.openymsg.conference;

import java.io.IOException;

import org.junit.Test;
import org.openymsg.network.IllegalIdentityException;
import org.openymsg.network.NoSuchConferenceException;
import org.openymsg.network.YahooConference;
import org.openymsg.network.event.SessionAdapter;
import org.openymsg.network.event.SessionConferenceDeclineInviteEvent;
import org.openymsg.network.event.SessionConferenceInviteEvent;
import org.openymsg.network.event.SessionConferenceLogoffEvent;
import org.openymsg.network.event.SessionConferenceLogonEvent;
import org.openymsg.network.event.SessionConferenceMessageEvent;
import org.openymsg.network.event.SessionListener;
import org.openymsg.test.YahooTestAbstract;

public class ConferenceTest extends YahooTestAbstract {
    
    @Test
    public void testSendingInvite() throws IllegalStateException, IllegalIdentityException, IOException, InterruptedException {
        String msg = "test sending invite";
        String[] users = new String[1];
        users[0] = sess2.getLoginID().getId();
        sess2.addSessionListener(createSess2Listener());
        sess1.addSessionListener(createSess1Listener());
        YahooConference conference1 = sess1.createConference(users, msg);
        Thread.sleep(3000);
        YahooConference conference2 = sess2.getConference(conference1.getName());
        sess1.sendConferenceMessage(conference1, "here is the first message");
        Thread.sleep(3000);
        sess2.sendConferenceMessage(conference2, "here is the second message");
        Thread.sleep(3000);
        sess2.leaveConference(conference2);
        Thread.sleep(3000);
        sess1.extendConference(conference1, sess2.getLoginID().getId(), "Another try");
        Thread.sleep(3000);
        sess1.leaveConference(conference1);
        Thread.sleep(3000);
        sess2.leaveConference(conference2);
        Thread.sleep(3000);
        System.out.println ("Conferences");
        for (YahooConference conference : sess1.getConferences()) {
            System.out.println("conference: " + conference);
        }
    }

    @Test
    public void testCreateConferenceWithoutInvitingAnyone() throws IllegalStateException, IllegalIdentityException, IOException, InterruptedException {
        String msg = "test sending invite";
        String[] users = new String[0];
        sess1.addSessionListener(createSess1Listener());
        YahooConference conference1 = sess1.createConference("conferencename2323", users, msg);
        Thread.sleep(3000);
        sess1.sendConferenceMessage(conference1, "here is the first message");
        Thread.sleep(3000);
        sess1.extendConference(conference1, sess2.getLoginID().getId(), "Another try");
        Thread.sleep(3000);
        sess1.leaveConference(conference1);
        Thread.sleep(3000);
        System.out.println ("Conferences");
        for (YahooConference conference : sess1.getConferences()) {
            System.out.println("conference: " + conference);
        }
    }
    
    private SessionListener createSess1Listener() {
        return new SessionAdapter() {

            @Override
            public void conferenceInviteDeclinedReceived(SessionConferenceDeclineInviteEvent event) {
                System.out.println ("conferenceInviteDeclinedReceived: " + event);
            }

            @Override
            public void conferenceInviteReceived(SessionConferenceInviteEvent event) {
                System.out.println ("conferenceInviteReceived: " + event);
            }

            @Override
            public void conferenceLogoffReceived(SessionConferenceLogoffEvent event) {
                System.out.println ("conferenceLogoffReceived: " + event);
            }

            @Override
            public void conferenceLogonReceived(SessionConferenceLogonEvent event) {
                System.out.println ("conferenceLogonReceived: " + event);
            }

            @Override
            public void conferenceMessageReceived(SessionConferenceMessageEvent event) {
                System.out.println ("conferenceMessageReceived: " + event);
            }
            
        };
    }
    private SessionListener createSess2Listener() {
        return new SessionAdapter() {

            @Override
            public void conferenceInviteDeclinedReceived(SessionConferenceDeclineInviteEvent event) {
                System.out.println ("conferenceInviteDeclinedReceived: " + event);
            }

            @Override
            public void conferenceInviteReceived(SessionConferenceInviteEvent event) {
                System.out.println ("conferenceInviteReceived: " + event);
                try {
                    sess2.acceptConferenceInvite(event.getRoom());
                }
                catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (NoSuchConferenceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void conferenceLogoffReceived(SessionConferenceLogoffEvent event) {
                System.out.println ("conferenceLogoffReceived: " + event);
            }

            @Override
            public void conferenceLogonReceived(SessionConferenceLogonEvent event) {
                System.out.println ("conferenceLogonReceived: " + event);
            }

            @Override
            public void conferenceMessageReceived(SessionConferenceMessageEvent event) {
                System.out.println ("conferenceMessageReceived: " + event);
            }
            
        };
    }
    
}
