package org.openymsg.contact.status;

import org.junit.Ignore;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CustomStatusMessageTest {
	@Test
	@Ignore
	public void testEquals() {
		// Need to implement canEquals and some other logic
		EqualsVerifier.forClass(CustomStatusMessage.class).withRedefinedSuperclass().verify();
	}
}
