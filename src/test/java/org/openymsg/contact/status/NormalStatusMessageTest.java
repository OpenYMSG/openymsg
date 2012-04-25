package org.openymsg.contact.status;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.testng.annotations.Test;

public class NormalStatusMessageTest {

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(NormalStatusMessage.class).verify();
	}

}
