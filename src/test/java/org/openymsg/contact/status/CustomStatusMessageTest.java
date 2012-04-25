package org.openymsg.contact.status;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.testng.annotations.Test;

public class CustomStatusMessageTest {

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(CustomStatusMessage.class).verify();
	}
}
