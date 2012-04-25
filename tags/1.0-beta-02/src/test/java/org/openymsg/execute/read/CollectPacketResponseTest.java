package org.openymsg.execute.read;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CollectPacketResponseTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(CollectPacketResponse.class).verify();
	}

	@Test(enabled = false)
	public void testSimple() {
		Assert.fail("not implemented");
	}

}
