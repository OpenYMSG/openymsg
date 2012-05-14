package org.openymsg.contact.roster;

import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.openymsg.Name;
import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooProtocol;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.write.Message;
import org.openymsg.contact.group.ContactGroupImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionRosterImplTest {
	private String username = "testuser";
	private YahooConnection executor;
	private SessionRosterCallback callback;
	private SessionRosterImpl session;

	@BeforeMethod
	public void beforeMethod() {
		executor = Mockito.mock(YahooConnection.class);
		callback = Mockito.mock(SessionRosterCallback.class);
		session = new SessionRosterImpl(executor, username, callback);
	}

	@Test
	public void testAddContact() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContactGroup group = new ContactGroupImpl("group");
		String message = "message";
		session.addContact(contact, group, message);
		Mockito.verify(executor).execute(
				argThat(new ContactAddMessage(this.username, contact, group, message, new Name(null, null))));
	}

	@Test
	public void testAddContactNoMessage() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		YahooContactGroup group = new ContactGroupImpl("group");
		session.addContact(contact, group, null);
		Mockito.verify(executor).execute(
				argThat(new ContactAddMessage(this.username, contact, group, null, new Name(null, null))));
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Argument 'group' cannot be null")
	public void testAddContactNoGroupFail() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "message";
		session.addContact(contact, null, message);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Argument 'contact' cannot be null")
	public void testAddContactNoContactFail() {
		YahooContactGroup group = new ContactGroupImpl("group");
		String message = "message";
		session.addContact(null, group, message);
	}

	@Test
	public void testRemoveContact() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		ContactGroupImpl group = new ContactGroupImpl("group");
		group.add(contact);
		session.removeFromGroup(contact, group);
		// TODO remove from status
		Mockito.verify(executor).execute(argThat(new ContactRemoveMessage(this.username, contact, group)));
		Assert.assertFalse(session.getContacts().contains(contact));
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Argument 'contact' cannot be null.")
	public void testRemoveContactNoContact() {
		ContactGroupImpl group = new ContactGroupImpl("group");
		session.removeFromGroup(null, group);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Argument 'group' cannot be null.")
	public void testRemoveContactNoGroup() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		session.removeFromGroup(contact, null);
	}

	@Test
	public void testAcceptContact() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		session.acceptFriendAuthorization(contact);
		Mockito.verify(executor).execute(argThat(new ContactAddAcceptMessage(this.username, contact)));
	}

	@Test
	public void testDeclineContact() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "message";
		session.rejectFriendAuthorization(contact, message);
		Mockito.verify(executor).execute(argThat(new ContactAddDeclineMessage(this.username, contact, message)));
	}

	@Test
	public void testReceivedAddAccept() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		session.receivedContactAddAccepted(contact);

		// TODO no longer pending, status comes separately
		// TODO in group?
		Assert.assertTrue(session.getContacts().contains(contact));
		Mockito.verify(callback).receivedContactAddAccepted(contact);
	}

	@Test
	public void testReceivedAddDecline() {
		YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
		String message = "message";
		// TODO remove contact, remove pending status
		// TODO remove from group?
		session.receivedContactAddDeclined(contact, message);

		Assert.assertFalse(session.getContacts().contains(contact));
		Mockito.verify(callback).receivedContactAddDeclined(contact, message);
	}

	private Message argThat(Message message, String... excludeFields) {
		return (Message) Mockito.argThat(new ReflectionEquals(message, excludeFields));
	}

}
