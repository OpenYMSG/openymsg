package org.openymsg.contact.status;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class NormalStatusMessageTest {
	@Test
	public void testEquals() {
		EqualsVerifier.forClass(NormalStatusMessage.class).suppress(Warning.STRICT_INHERITANCE).verify();
	}
}
