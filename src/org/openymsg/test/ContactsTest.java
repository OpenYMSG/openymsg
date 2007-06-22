/**
 * 
 */
package org.openymsg.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openymsg.network.AccountLockedException;
import org.openymsg.network.FireEvent;
import org.openymsg.network.LoginRefusedException;
import org.openymsg.network.ServiceType;
import org.openymsg.network.Status;
import org.openymsg.network.YahooGroup;
import org.openymsg.network.YahooUser;

/**
 * @author Giancarlo Frison - Nimbuzz B.V. <giancarlo@nimbuzz.com>
 *
 */
public class ContactsTest extends YahooTest{

	private static Logger log = Logger.getLogger(ContactsTest.class);



	@Test
	public void testAddContact() throws IllegalStateException, IOException {
		removeAll(sess1);
		removeAll(sess2);
		addfriend();

	}
	
	@Test
	public void testReLoginFriendAndChangeStatusBuddy() throws IllegalStateException, IOException, AccountLockedException, LoginRefusedException, InterruptedException {
		sess2.logout();
		Thread.sleep(500);
		YahooUser buddy = null;
		for (String contact: sess1.getUsers().keySet()) 
			if(contact.equals(OTHERUSR))
				buddy = sess1.getUser(contact);
		assertNotNull(buddy);
		assertEquals(Status.OFFLINE, buddy.getStatus());
		sess2.login(OTHERUSR, OTHERPWD);
		Thread.sleep(500);
		buddy = sess1.getUser(OTHERUSR);
		assertNotNull(buddy);
		assertEquals(Status.AVAILABLE, buddy.getStatus());
		
	}

	/**
	 * @throws IOException
	 */
	private void addfriend() throws IOException {
		drain();
//		sess1.addFriend(OTHERUSR, sess1.getGroups().iterator().next().getName());
		sess1.addFriend(OTHERUSR, "group");
		FireEvent event = listener2.waitForEvent(5,ServiceType.CONTACTNEW);
		assertNotNull(event);
		assertEquals(event.getType(), ServiceType.CONTACTNEW);
		assertEquals(event.getEvent().getFrom(), USERNAME);
		event = listener1.waitForEvent(5,ServiceType.FRIENDADD);
		assertEquals(event.getType(), ServiceType.FRIENDADD);
		boolean existinList = false;
		for (String contact: sess1.getUsers().keySet()) 
			if(contact.equals(OTHERUSR))
				existinList=true;
		assertTrue(existinList);
	}

	@Test
	public void testRejectContact() throws IOException, InterruptedException {
		removeAll(sess1);
		sess1.addFriend(OTHERUSR, "group");
		assertNotNull(listener1.waitForEvent(5, ServiceType.FRIENDADD));
		Thread.sleep(500);
		FireEvent event = listener2.waitForEvent(5,ServiceType.CONTACTNEW);
		sess2.rejectContact(event.getEvent(), "i don't want you");
		assertNotNull(listener1.waitForEvent(5,ServiceType.CONTACTREJECT));
		boolean existinList = false;
		for (String contact: sess1.getUsers().keySet()) {
			log.info("still friend:"+contact);
			if(contact.equals(OTHERUSR))
				existinList=true;
		}
		assertFalse("the user may be removed",existinList);
	}



	@Test
	public void testRenameGroup() throws IllegalStateException, IOException {
		boolean existinList = false;
		for(YahooGroup group : sess1.getGroups())
			if(group.getName().equals("group"))
				existinList = true;
		if(!existinList) {
			sess1.addFriend(OTHERUSR, "group");
			listener1.waitForEvent(5, ServiceType.FRIENDADD);
		}
		sess1.renameGroup("group", "group-renamed" );
		FireEvent event = listener1.waitForEvent(5, ServiceType.GROUPRENAME);
		assertNotNull(event);
		existinList = false;
		for(YahooGroup group : sess1.getGroups())
			if(group.getName().equals("group-renamed"))
				existinList = true;
		assertTrue("the group may be exist",existinList);
	}

	@Test
	public void testRemoveContact() throws IOException {
		if(sess1.getUsers().size()==0)
			addfriend();
		for(YahooGroup group: sess1.getGroups())
			for(YahooUser user: group.getUsers()) {
				sess1.removeFriend(user.getId(), group.getName());
				FireEvent event =  listener1.waitForEvent(5,ServiceType.FRIENDREMOVE);
				assertEquals(event.getType(), ServiceType.FRIENDREMOVE);
				boolean existinList = false;
				for(String user2 : sess1.getUsers().keySet())
					if(user2.equals(user))
						existinList = true;
				assertFalse("the user may be removed",existinList);
			}
	}
	@Test
	public void testRemoveUnknowContact() throws IOException {
		sess1.removeFriend("ewrgergerg", CHATMESSAGE);
		FireEvent event =  listener1.waitForEvent(5);
		assertNull(event);
	}

	@Test
	public void testGroupsManagmenet() throws IllegalStateException, IOException, AccountLockedException, LoginRefusedException, InterruptedException {
		removeAll(sess1);
		sess1.addFriend(OTHERUSR, "grupponuovissimo");
		assertNotNull(listener1.waitForEvent(5,ServiceType.FRIENDADD));
		boolean exist =false;
		for(YahooGroup g: sess1.getGroups())
			if(g.getName().equals("grupponuovissimo"))
				exist=true;
		assertTrue("we expect the group exist",exist);
		exist =false;
		for(YahooUser user:sess1.getUsers().values())
			if(user.getId().equals(OTHERUSR)) exist=true;
		assertTrue("we expect the user exist",exist);

		sess1.logout();
		Thread.sleep(200);
		
		sess1.login(USERNAME, PASSWORD);
		assertNotNull(listener1.waitForEvent(5,ServiceType.LOGON));
		exist =false;
		for(YahooGroup g: sess1.getGroups())
			if(g.getName().equals("grupponuovissimo"))
				exist=true;
		assertTrue("we expect the group exist",exist);
		exist =false;
		for(YahooUser user:sess1.getUsers().values())
			if(user.getId().equals(OTHERUSR)) exist=true;
		assertTrue("we expect the user exist",exist);

		

		sess1.removeFriend(OTHERUSR, "grupponuovissimo");
		assertNotNull(listener1.waitForEvent(5,ServiceType.FRIENDREMOVE));
		exist =false;
		for(YahooGroup g: sess1.getGroups())
			if(g.getName().equals("grupponuovissimo"))
				exist=true;
		assertFalse("we expect the group doesn't exist",exist);
		exist =false;
		for(YahooUser user:sess1.getUsers().values())
			if(user.getId().equals(OTHERUSR)) exist=true;
		assertFalse("we expect the user doesn't exist",exist);


		sess1.logout(); 
		Thread.sleep(200);
		sess1.login(USERNAME, PASSWORD);
		assertNotNull(listener1.waitForEvent(5,ServiceType.LOGON));
		exist =false;
		for(YahooGroup g: sess1.getGroups())
			if(g.getName().equals("grupponuovissimo"))
				exist=true;
		assertFalse("we expect the group doesn't exist",exist);
		exist =false;
		for(YahooUser user:sess1.getUsers().values())
			if(user.getId().equals(OTHERUSR)) exist=true;
		assertFalse("we expect the user doesn't exist",exist);
	}




}
