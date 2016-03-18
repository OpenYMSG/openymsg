package org.openymsg.contact.status;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CustomStatusMessageTest {
	@Test
	public void testEquals() {
		EqualsVerifier.forClass(CustomStatusMessage.class).verify();
	}
}
