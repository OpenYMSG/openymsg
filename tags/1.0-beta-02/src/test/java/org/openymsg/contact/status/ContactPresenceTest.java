package org.openymsg.contact.status;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.testng.annotations.Test;

public class ContactPresenceTest {

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(ContactPresence.class).verify();
	}

}
