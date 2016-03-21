package org.openymsg;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nl.jqno.equalsverifier.EqualsVerifier;

public class YahooContactTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(YahooContact.class).verify();
	}

	@Test
	public void testSimple() {
		String username = "testuser";
		YahooProtocol protocol = YahooProtocol.LOTUS;
		YahooContact contact = new YahooContact(username, protocol);
		assertEquals(contact.getName(), username);
		assertEquals(protocol, contact.getProtocol());
	}

	@Test()
	public void testNullName() {
		String username = null;
		YahooProtocol protocol = YahooProtocol.LOTUS;
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("id cannot be null");
		new YahooContact(username, protocol);
	}

	@Test()
	public void testNullProtocol() {
		String username = "testuser";
		YahooProtocol protocol = null;
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("protocol cannot be null");
		new YahooContact(username, protocol);
	}
}
