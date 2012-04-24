package org.openymsg.contact.status;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.testng.annotations.Test;

public class ContactStatusImplTest {

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(ContactStatusImpl.class).verify();
	}

}
