package org.openymsg.contact.roster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openymsg.testing.MessageAssert.argThatMessage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.YahooConnection;
import org.openymsg.contact.group.ContactGroupImpl;

public class SessionRosterImplTest {
	private String username = "testuser";
	private YahooConnection executor;
	private SessionRosterCallback callback;
	private SessionRosterImpl session;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void beforeMethod() {
		executor = mock(YahooConnection.class);
		callback = mock(SessionRosterCallback.class);
		session = new SessionRosterImpl(executor, username, callback);
	}

	@Test
	public void testAddContact() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContactGroup group = new ContactGroupImpl("group");
		String message = "message";
		session.addContact(contact, group, message);
		verify(executor).execute(argThatMessage(new ContactAddMessage(username, contact, group, message, null)));
	}

	@Test
	public void testAddContactNoMessage() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContactGroup group = new ContactGroupImpl("group");
		session.addContact(contact, group, null);
		verify(executor).execute(argThatMessage(new ContactAddMessage(username, contact, group, null, null)));
	}

	@Test()
	public void testAddContactNoGroupFail() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "message";
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument 'group' cannot be null");
		session.addContact(contact, null, message);
	}

	@Test()
	public void testAddContactNoContactFail() {
		YahooContactGroup group = new ContactGroupImpl("group");
		String message = "message";
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument 'contact' cannot be null");
		session.addContact(null, group, message);
	}

	@Test
	public void testRemoveContact() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		ContactGroupImpl group = new ContactGroupImpl("group");
		group.add(contact);
		session.removeFromGroup(contact, group);
		// TODO remove from status
		verify(executor).execute(argThatMessage(new ContactRemoveMessage(this.username, contact, group)));
		assertFalse(session.getContacts().contains(contact));
	}

	@Test()
	public void testRemoveContactNoContact() {
		ContactGroupImpl group = new ContactGroupImpl("group");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument 'contact' cannot be null");
		session.removeFromGroup(null, group);
	}

	@Test()
	public void testRemoveContactNoGroup() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument 'group' cannot be null.");
		session.removeFromGroup(contact, null);
	}

	@Test
	public void testAcceptContact() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		session.acceptFriendAuthorization(username, contact);
		verify(executor).execute(argThatMessage(new ContactAddAcceptMessage(username, contact)));
	}

	@Test
	public void testDeclineContact() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "message";
		session.rejectFriendAuthorization(contact, message);
		verify(executor).execute(argThatMessage(new ContactAddDeclineMessage(username, contact, message)));
	}

	@Test
	public void testReceivedAddAccept() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		session.receivedContactAddAccepted(contact);
		// TODO no longer pending, status comes separately
		// TODO in group?
		assertTrue(session.getContacts().contains(contact));
		verify(callback).receivedContactAddAccepted(contact);
	}

	@Test
	public void testReceivedAddDecline() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "message";
		// TODO remove contact, remove pending status
		// TODO remove from group?
		session.receivedContactAddDeclined(contact, message);
		assertFalse(session.getContacts().contains(contact));
		verify(callback).receivedContactAddDeclined(contact, message);
	}
}
