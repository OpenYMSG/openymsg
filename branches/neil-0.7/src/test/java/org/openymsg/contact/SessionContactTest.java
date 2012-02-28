package org.openymsg.contact;

import java.util.HashSet;
import java.util.Set;

import org.openymsg.Contact;
import org.openymsg.ContactGroup;
import org.openymsg.execute.Executor;
import org.openymsg.execute.ExecutorImpl;
import org.openymsg.group.ContactGroupImpl;
import org.openymsg.group.SessionGroupImpl;
import org.openymsg.network.OutgoingPacket;
import org.openymsg.network.TestingConnectionHandler;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionContactTest {
	String username;
	SessionContactImpl sessionContact;
	SessionGroupImpl sessionGroup;
	Contact contact;
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
		sessionContact = new SessionContactImpl(executor, username);
		contact = new Contact("contactA");
		group = new ContactGroupImpl("one");
	}
	
	@AfterMethod
	public void tearDownMethod() {
		executor.shutdown();
	}
	
	@Test
	public void testAddContact() throws InterruptedException {
		sessionContact.addContact(contact, group);
		OutgoingPacket packet = connection.getOutgoingPacket();
		System.err.println("Got a packet: " + packet);
	}

	@Test
	public void testRemoveContact() throws InterruptedException {
		Set<ContactGroup> groups = new HashSet<ContactGroup>();
		group.add(contact);
		groups.add(group);
		sessionGroup.addedGroups(groups);
		sessionContact.removeFromGroup(contact, group);
		OutgoingPacket packet = connection.getOutgoingPacket();
		System.err.println("Got a packet: " + packet);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testRemoveContactNotInGroup() throws InterruptedException {
		Set<ContactGroup> groups = new HashSet<ContactGroup>();
//		group.add(contact);
		groups.add(group);
		sessionGroup.addedGroups(groups);
		sessionContact.removeFromGroup(contact, group);
	}

}
