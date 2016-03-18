package org.openymsg.connection.read;

import static org.junit.Assert.fail;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CollectPacketResponseTest {
	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(CollectPacketResponse.class).verify();
	}

	@Test()
	public void testSimple() {
		fail("not implemented");
	}
}
