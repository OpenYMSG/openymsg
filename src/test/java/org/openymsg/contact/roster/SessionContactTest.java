package org.openymsg.contact.roster;

import java.util.HashSet;
import java.util.Set;

import org.openymsg.YahooContact;
import org.openymsg.YahooContactGroup;
import org.openymsg.YahooProtocol;
import org.openymsg.contact.group.ContactGroupImpl;
import org.openymsg.contact.group.SessionGroupImpl;
import org.openymsg.execute.Executor;
import org.openymsg.execute.ExecutorImpl;
import org.openymsg.network.OutgoingPacket;
import org.openymsg.network.TestingConnectionHandler;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionContactTest {
	String username;
	SessionRosterImpl sessionContact;
	SessionGroupImpl sessionGroup;
	YahooContact contact;
	ContactGroupImpl group;
	TestingConnectionHandler connection;
	Executor executor;

	@BeforeClass
	public void setUp() {
		username = "testuser";
	}

	@BeforeMethod
	public void setUpMethod() {
		executor = new ExecutorImpl(username);
		connection = new TestingConnectionHandler();
		executor.initializeConnection(connection);
		SessionRosterCallback callback = null;
		sessionContact = new SessionRosterImpl(executor, username, callback);
		contact = new YahooContact("contactA", YahooProtocol.YAHOO);
		group = new ContactGroupImpl("one");
		sessionGroup = new SessionGroupImpl(executor, username);
	}

	@AfterMethod
	public void tearDownMethod() {
		executor.shutdown();
	}

	@Test
	public void testAddContact() throws InterruptedException {
		sessionContact.addContact(contact, group);
		OutgoingPacket packet = connection.getOutgoingPacket();
		// System.err.println("Got a packet: " + packet);
	}

	@Test
	public void testRemoveContact() throws InterruptedException {
		Set<YahooContactGroup> groups = new HashSet<YahooContactGroup>();
		group.add(contact);
		groups.add(group);
		sessionGroup.addedGroups(groups);
		sessionContact.removeFromGroup(contact, group);
		OutgoingPacket packet = connection.getOutgoingPacket();
		// System.err.println("Got a packet: " + packet);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testRemoveContactNotInGroup() throws InterruptedException {
		Set<YahooContactGroup> groups = new HashSet<YahooContactGroup>();
		// group.add(contact);
		groups.add(group);
		sessionGroup.addedGroups(groups);
		sessionContact.removeFromGroup(contact, group);
	}

}
