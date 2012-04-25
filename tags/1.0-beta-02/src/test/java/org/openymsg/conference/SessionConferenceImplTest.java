package org.openymsg.conference;

import java.io.IOException;
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
	private SessionConferenceImpl session;
	private SessionConferenceCallback callback;

	@BeforeMethod
	public void beforeMethod() {
		executor = Mockito.mock(Executor.class);
		callback = Mockito.mock(SessionConferenceCallback.class);
		session = new SessionConferenceImpl(username, executor, callback);
	}

	@Test
	public void testSendMessage() {
		String conferenceId = "id";
		YahooConference conference = new ConferenceImpl(conferenceId);
		Set<YahooContact> contacts = new HashSet<YahooContact>();
		String message = "message";

		session.createConference(conferenceId, contacts, null);
		session.sendConferenceMessage(conference, message);

		ConferenceMembership membership = session.getConferenceMembership(conference);
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

		session.receivedConferenceInvite(conference, inviter, invited, members, message);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertEquals(invited, membership.getInvited());
		Assert.assertEquals(members, membership.getMembers());

		session.declineConferenceInvite(conference, null);

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

		session.receivedConferenceInvite(conference, inviter, invited, members, message);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertEquals(invited, membership.getInvited());
		Assert.assertEquals(members, membership.getMembers());

		session.acceptConferenceInvite(conference);

		Mockito.verify(callback).receivedConferenceInvite(conference, inviter, invited, members, message);
		Mockito.verify(executor).execute(argThat(new AcceptConferenceMessage(username, conference, membership)));
	}

	@Test
	public void testReceivedConferenceAccept() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact accepter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		Set<YahooContact> members = new HashSet<YahooContact>();
		members.add(accepter);

		session.receivedConferenceAccept(conference, accepter);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertEquals(members, membership.getMembers());
		Mockito.verify(callback).receivedConferenceAccept(conference, accepter);
	}

	@Test
	public void testReceivedConferneceDeclie() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact decliner = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "Nothankyou.";

		session.receivedConferenceDecline(conference, decliner, message);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertTrue(membership.getDeclineOrLeft().contains(decliner));
		Mockito.verify(callback).receivedConferenceDecline(conference, decliner, message);
	}

	/**
	 * testuser receives a notice that testbuddy has invited testbuddy2 to a conference that testuser is already in
	 * @throws IOException
	 */
	// TODO this was an announcement
	@Test
	public void testReceiveConferenceExtendSingleExistingSingleInvite() throws IOException {
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact invited = new YahooContact("testbuddy2", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited);

		session.receivedConferenceExtend(conference, inviter, invitedContacts);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertTrue(membership.getInvited().containsAll(invitedContacts));
		Mockito.verify(callback).receivedConferenceExtend(conference, inviter, invitedContacts);
	}

	/**
	 * testuser receives a notice that testbuddy has invited testbuddy3, testbuddy4, testbuddy5 to a conference that
	 * testuser is already in
	 * @throws IOException
	 */
	// TODO this was an announcement
	@Test
	public void testReceiveConferenceExtendMultiInvite() throws IOException {
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContact invited1 = new YahooContact("testbuddy3", YahooProtocol.YAHOO);
		YahooContact invited2 = new YahooContact("testbuddy4", YahooProtocol.YAHOO);
		YahooContact invited3 = new YahooContact("testbuddy5", YahooProtocol.YAHOO);
		Set<YahooContact> invitedContacts = new HashSet<YahooContact>();
		invitedContacts.add(invited1);
		invitedContacts.add(invited2);
		invitedContacts.add(invited3);

		session.receivedConferenceExtend(conference, inviter, invitedContacts);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertTrue(membership.getInvited().contains(invited1));
		Assert.assertTrue(membership.getInvited().contains(invited2));
		Assert.assertTrue(membership.getInvited().contains(invited3));
		Mockito.verify(callback).receivedConferenceExtend(conference, inviter, invitedContacts);
	}

	// TODO copied
	private Message argThat(Message message, String... excludeFields) {
		return (Message) Mockito.argThat(new ReflectionEquals(message, excludeFields));
	}

	@Test
	public void testReceiveSingleInviteYahoo() throws IOException {
		String id = "testbuddy-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact inviter = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "Invitingtestuser";
		Set<YahooContact> invited = new HashSet<YahooContact>();
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		invited.add(me);
		Set<YahooContact> members = new HashSet<YahooContact>();
		members.add(inviter);

		session.receivedConferenceInvite(conference, inviter, invited, members, message);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertEquals(members, membership.getMembers());
		Assert.assertEquals(invited, membership.getInvited());
		Mockito.verify(callback).receivedConferenceInvite(conference, inviter, invited, members, message);
	}

	@Test
	public void testReceiveSingleInviteAckYahoo() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		Set<YahooContact> invited = new HashSet<YahooContact>();
		YahooContact buddy = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		invited.add(buddy);
		Set<YahooContact> members = new HashSet<YahooContact>();
		YahooContact me = new YahooContact(username, YahooProtocol.YAHOO);
		members.add(me);
		String message = "Invitingtestuser";

		session.receivedConferenceInviteAck(conference, me, invited, members, message);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertEquals(members, membership.getMembers());
		Assert.assertEquals(invited, membership.getInvited());
	}

	@Test
	public void testReceiveLogoffMultipleMembersYahoo() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact leaver = new YahooContact("testbuddy2", YahooProtocol.YAHOO);

		session.receivedConferenceLeft(conference, leaver);

		ConferenceMembership membership = session.getConferenceMembership(conference);
		Assert.assertTrue(membership.getDeclineOrLeft().contains(leaver));
		Mockito.verify(callback).receivedConferenceLeft(conference, leaver);
	}

	@Test
	public void testReceiveMessage() throws IOException {
		String id = "testuser-8iVmHcCkflGJpBXpjBbzCw--";
		YahooConference conference = new ConferenceImpl(id);
		YahooContact sender = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "myMessage";

		session.receivedConferenceMessage(conference, sender, message);

		Mockito.verify(callback).receivedConferenceMessage(conference, sender, message);
	}

}
