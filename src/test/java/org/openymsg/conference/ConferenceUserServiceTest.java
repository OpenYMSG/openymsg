package org.openymsg.conference;

import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.conference.message.AcceptConferenceMessage;
import org.openymsg.conference.message.CreateConferenceMessage;
import org.openymsg.conference.message.DeclineConferenceMessage;
import org.openymsg.conference.message.SendConfereneMessage;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.write.Message;

public class ConferenceUserServiceTest {
	private String username = "testuser";
	@Mock
	private YahooConnection executor;
	private ConferenceUserService session;
	@Mock
	private SessionConferenceCallback callback;
	private ConferenceServiceState state;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
		state = new ConferenceServiceState();
		session = new ConferenceUserService(username, executor, state);
	}

	@Test
	public void testSendMessage() {
		String conferenceId = "id";
		YahooConference conference = new YahooConference(conferenceId);
		Set<YahooContact> contacts = new HashSet<YahooContact>();
		String message = "message";
		session.createConference(conferenceId, contacts, null);
		session.sendConferenceMessage(conference, message);
		ConferenceMembership membership = session.getConferenceMembership(conference);
		verify(executor).execute(argThat(new CreateConferenceMessage(username, conference, contacts, null)));
		verify(executor).execute(argThat(new SendConfereneMessage(username, conference, membership, message)));
	}

	@Test
	public void testConferenceDecline() {
		String conferenceId = "id";
		YahooConference conference = new YahooConference(conferenceId);
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		Set<YahooContact> invited = new HashSet<YahooContact>();
		invited.add(me);
		Set<YahooContact> members = new HashSet<YahooContact>();
		state.addMembership(conference.getId(), new ConferenceMembershipImpl());
		ConferenceMembershipImpl membership = state.getMembership(conference.getId());
		membership.addInvited(invited);
		membership.addMember(members);
		session.declineConferenceInvite(conference, null);
		verify(executor).execute(argThat(new DeclineConferenceMessage(username, conference, membership, null)));
	}

	@Test
	public void testConferenceAccept() {
		String conferenceId = "id";
		YahooConference conference = new YahooConference(conferenceId);
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		Set<YahooContact> invited = new HashSet<YahooContact>();
		invited.add(me);
		Set<YahooContact> members = new HashSet<YahooContact>();
		state.addMembership(conference.getId(), new ConferenceMembershipImpl());
		ConferenceMembershipImpl membership = state.getMembership(conference.getId());
		membership.addInvited(invited);
		membership.addMember(members);
		session.acceptConferenceInvite(conference);
		verify(executor).execute(argThat(new AcceptConferenceMessage(username, conference, membership)));
	}

	// TODO copied
	private Message argThat(Message message, String... excludeFields) {
		return (Message) Matchers.argThat(new ReflectionEquals(message, excludeFields));
	}

}
