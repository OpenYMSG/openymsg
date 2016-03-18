package org.openymsg.contact.status;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class NormalStatusMessageTest {
	@Test
	public void testEquals() {
		EqualsVerifier.forClass(NormalStatusMessage.class).verify();
	}
}
