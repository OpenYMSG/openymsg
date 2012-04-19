package org.openymsg.conference;

import java.util.HashSet;
import java.util.Set;

import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.execute.Executor;
import org.openymsg.execute.write.Message;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionConferenceImplTest {
	private String username = "testuser";
	private Executor executor;
	private SessionConferenceImpl sessionConference;
	private SessionConferenceCallback callback;

	@BeforeMethod
	public void beforeMethod() {
		executor = Mockito.mock(Executor.class);
		callback = Mockito.mock(SessionConferenceCallback.class);
		sessionConference = new SessionConferenceImpl(username, executor, callback);
	}

	@Test
	public void testSendMessage() {
		String conferenceId = "id";
		YahooConference conference = new ConferenceImpl(conferenceId);
		Set<YahooContact> contacts = new HashSet<YahooContact>();
		String message = "message";
		sessionConference.createConference(conferenceId, contacts, null);
		sessionConference.sendConferenceMessage(conference, message);
		ConferenceMembership membership = sessionConference.getConferenceMembership(conference);
		Mockito.verify(executor).execute(argThat(new CreateConferenceMessage(username, conference, contacts, null)));
		Mockito.verify(executor).execute(argThat(new SendConfereneMessage(username, conference, membership, message)));
	}

	@Test
	public void testConferenceDecline() {
		String conferenceId = "id";
		YahooConference conference = new ConferenceImpl(conferenceId);
		YahooContact inviter = null;
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		Set<YahooContact> invited = new HashSet<YahooContact>();
		invited.add(me);
		Set<YahooContact> members = new HashSet<YahooContact>();
		String message = null;
		sessionConference.receivedConferenceInvite(conference, inviter, invited, members, message);
		ConferenceMembership membership = sessionConference.getConferenceMembership(conference);
		Assert.assertEquals(invited, membership.getInvited());
		Assert.assertEquals(members, membership.getMembers());
		sessionConference.declineConferenceInvite(conference, null);
		Mockito.verify(callback).receivedConferenceInvite(conference, inviter, invited, members, message);
		Mockito.verify(executor).execute(argThat(new DeclineConferenceMessage(username, conference, membership, null)));
	}

	@Test
	public void testConferenceAccept() {
		String conferenceId = "id";
		YahooConference conference = new ConferenceImpl(conferenceId);
		YahooContact inviter = null;
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		Set<YahooContact> invited = new HashSet<YahooContact>();
		invited.add(me);
		Set<YahooContact> members = new HashSet<YahooContact>();
		String message = null;
		sessionConference.receivedConferenceInvite(conference, inviter, invited, members, message);
		ConferenceMembership membership = sessionConference.getConferenceMembership(conference);
		Assert.assertEquals(invited, membership.getInvited());
		Assert.assertEquals(members, membership.getMembers());
		sessionConference.acceptConferenceInvite(conference);
		Mockito.verify(callback).receivedConferenceInvite(conference, inviter, invited, members, message);
		Mockito.verify(executor).execute(argThat(new AcceptConferenceMessage(username, conference, membership)));
	}

	// TODO copied
	private Message argThat(Message message, String... excludeFields) {
		return (Message) Mockito.argThat(new ReflectionEquals(message, excludeFields));
	}

}
