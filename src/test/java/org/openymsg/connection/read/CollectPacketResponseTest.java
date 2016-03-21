package org.openymsg.connection.read;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CollectPacketResponseTest {
	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(CollectPacketResponse.class).verify();
	}
}
