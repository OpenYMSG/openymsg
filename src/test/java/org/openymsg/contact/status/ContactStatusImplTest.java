package org.openymsg.contact.status;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ContactStatusImplTest {
	@Test
	public void testEquals() {
		EqualsVerifier.forClass(ContactStatusImpl.class).verify();
	}
}
