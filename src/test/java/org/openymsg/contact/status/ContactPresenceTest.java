package org.openymsg.contact.status;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ContactPresenceTest {
	@Test
	public void testEquals() {
		EqualsVerifier.forClass(ContactPresence.class).verify();
	}
}
