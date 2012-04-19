package org.openymsg;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.testng.Assert;
import org.testng.annotations.Test;

public class YahooContactTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(YahooContact.class).verify();
	}

	@Test
	public void testSimple() {
		String username = "testuser";
		YahooProtocol protocol = YahooProtocol.LOTUS;
		YahooContact contact = new YahooContact(username, protocol);
		Assert.assertEquals(contact.getName(), username);
		Assert.assertEquals(protocol, contact.getProtocol());
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "id cannot be null")
	public void testNullName() {
		String username = null;
		YahooProtocol protocol = YahooProtocol.LOTUS;
		new YahooContact(username, protocol);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "protocol cannot be null")
	public void testNullProtocol() {
		String username = "testuser";
		YahooProtocol protocol = null;
		new YahooContact(username, protocol);
	}

}
