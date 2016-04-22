package org.openymsg.conference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openymsg.YahooConference;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.YahooConnection;

public class ConferenceSocketServiceTest {
	private String username = "testuser";
	@Mock
	private YahooConnection executor;
	private ConferenceSocketService session;
	@Mock
	private SessionConferenceCallback callback;
	private ConferenceServiceState state;

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
		state = new ConferenceServiceState();
		session = new ConferenceSocketService(callback, state);
	}

	@Test
	public void testReceivedConferenceAccept() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact accepter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Set<YahooContact> members = new HashSet<YahooContact>();
		members.add(accepter);
		session.receivedConferenceAccept(conference, accepter);
		ConferenceMembership membership = state.getMembership(conference.getId());
		assertEquals(members, membership.getMembers());
		Mockito.verify(callback).receivedConferenceAccept(conference, accepter);
	}

	@Test
	public void testReceivedConferneceDeclie() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact decliner = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "Nothankyou.";
		session.receivedConferenceDecline(conference, decliner, message);
		ConferenceMembership membership = state.getMembership(conference.getId());
		assertTrue(membership.getDeclineOrLeft().contains(decliner));
		Mockito.verify(callback).receivedConferenceDecline(conference, decliner, message);
	}

	/**
	 * testuser receives a notice that testbuddy has invited testbuddy2 to a
	 * conference that testuser is already in
	 * 
	 * @throws IOException
	 */
	// TODO this was an announcement
	@Test
	public void testReceivedConferenceExtendSingleExistingSingleInvite() throws IOException {
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact invited = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited);
		session.receivedConferenceExtend(conference, inviter, invitedContacts);
		ConferenceMembership membership = state.getMembership(conference.getId());
		assertTrue(membership.getInvited().containsAll(invitedContacts));
		Mockito.verify(callback).receivedConferenceExtend(conference, inviter, invitedContacts);
	}

	/**
	 * testuser receives a notice that testbuddy has invited testbuddy3,
	 * testbuddy4, testbuddy5 to a conference that testuser is already in
	 * 
	 * @throws IOException
	 */
	// TODO this was an announcement
	@Test
	public void testReceivedConferenceExtendMultiInvite() throws IOException {
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact invited1 = new YahooContact("testbuddy3", YahooProtocol.YAHOO);
		YahooContact invited2 = new YahooContact("testbuddy4", YahooProtocol.YAHOO);
		YahooContact invited3 = new YahooContact("testbuddy5", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited1);
		invitedContacts.add(invited2);
		invitedContacts.add(invited3);
		session.receivedConferenceExtend(conference, inviter, invitedContacts);
		ConferenceMembership membership = state.getMembership(conference.getId());
		assertTrue(membership.getInvited().contains(invited1));
		assertTrue(membership.getInvited().contains(invited2));
		assertTrue(membership.getInvited().contains(invited3));
		Mockito.verify(callback).receivedConferenceExtend(conference, inviter, invitedContacts);
	}

	@Test
	public void testReceivedSingleInviteYahoo() throws IOException {
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "Invitingtestuser";
		Set<YahooContact> invited = new HashSet<YahooContact>();
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		invited.add(me);
		Set<YahooContact> members = new HashSet<YahooContact>();
		members.add(inviter);
		session.receivedConferenceInvite(conference, inviter, invited, members, message);
		ConferenceMembership membership = state.getMembership(conference.getId());
		assertEquals(members, membership.getMembers());
		assertEquals(invited, membership.getInvited());
		Mockito.verify(callback).receivedConferenceInvite(conference, inviter, invited, members, message);
	}

	@Test
	public void testReceivedSingleInviteAckYahoo() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		Set<YahooContact> invited = new HashSet<YahooContact>();
		YahooContact buddy = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		invited.add(buddy);
		Set<YahooContact> members = new HashSet<YahooContact>();
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		members.add(me);
		String message = "Invitingtestuser";
		session.receivedConferenceInviteAck(conference, invited, members, message);
		ConferenceMembership membership = state.getMembership(conference.getId());
		assertEquals(members, membership.getMembers());
		assertEquals(invited, membership.getInvited());
	}

	@Test
	public void testReceivedLogoffMultipleMembersYahoo() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact leaver = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		session.receivedConferenceLeft(conference, leaver);
		ConferenceMembership membership = state.getMembership(conference.getId());
		assertTrue(membership.getDeclineOrLeft().contains(leaver));
		Mockito.verify(callback).receivedConferenceLeft(conference, leaver);
	}

	@Test
	public void testReceivedMessage() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new YahooConference(id);
		YahooContact sender = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "myMessage";
		session.receivedConferenceMessage(conference, sender, message);
		Mockito.verify(callback).receivedConferenceMessage(conference, sender, message);
	}

	@Test
	public void testReceivedConferenceInvite() {
		String conferenceId = "id";
		YahooConference conference = new YahooConference(conferenceId);
		YahooContact inviter = null;
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		Set<YahooContact> invited = new HashSet<YahooContact>();
		invited.add(me);
		Set<YahooContact> members = new HashSet<YahooContact>();
		String message = null;
		session.receivedConferenceInvite(conference, inviter, invited, members, message);
		ConferenceMembership membership = state.getMembership(conference.getId());
		assertEquals(invited, membership.getInvited());
		assertEquals(members, membership.getMembers());
		verify(callback).receivedConferenceInvite(conference, inviter, invited, members, message);
	}

}
