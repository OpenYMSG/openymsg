package org.openymsg.conference;

import java.util.HashSet;
import java.util.Set;

import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConferenceMembershipImplTest {
	private YahooContact contact1 = new YahooContact("1", YahooProtocol.YAHOO);
	private YahooContact contact2 = new YahooContact("2", YahooProtocol.YAHOO);
	private YahooContact contact3 = new YahooContact("3", YahooProtocol.YAHOO);
	private YahooContact contact4 = new YahooContact("4", YahooProtocol.YAHOO);
	private YahooContact contact5 = new YahooContact("5", YahooProtocol.YAHOO);
	private YahooContact contact6 = new YahooContact("6", YahooProtocol.YAHOO);
	private YahooContact contact7 = new YahooContact("7", YahooProtocol.YAHOO);
	private ConferenceMembershipImpl membership;

	@BeforeMethod
	public void beforeMethod() {
		membership = new ConferenceMembershipImpl();
	}

	@Test
	public void testSimpleLifecycle() {
		Set<YahooContact> contacts = new HashSet<YahooContact>();
		contacts.add(contact1);
		contacts.add(contact2);
		contacts.add(contact3);
		contacts.add(contact4);
		contacts.add(contact5);
		contacts.add(contact6);
		contacts.add(contact7);
		boolean answer = membership.addInvited(contacts);
		Assert.assertTrue(answer);
		answer = membership.addMember(contacts);
		Assert.assertTrue(answer);
		for (YahooContact yahooContact : contacts) {
			answer = membership.addLeft(yahooContact);
			Assert.assertTrue(answer);
		}
	}

	@Test
	public void testDecline() {
		boolean answer = membership.addInvited(contact1);
		Assert.assertTrue(answer);
		answer = membership.addDecline(contact1);
		Assert.assertTrue(answer);
	}

	@Test
	public void testDeclineFail() {
		boolean answer = membership.addMember(contact1);
		Assert.assertTrue(answer);
		answer = membership.addDecline(contact1);
		Assert.assertFalse(answer);
	}

	@Test
	public void testLeftFail() {
		boolean answer = membership.addInvited(contact1);
		Assert.assertTrue(answer);
		answer = membership.addLeft(contact1);
		Assert.assertFalse(answer);
	}

}
