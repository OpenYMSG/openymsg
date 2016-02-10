package org.openymsg.legacy.network;

import org.junit.Assert;
import org.junit.Test;

public class AuthenticationStateTest {

	@Test
	public void testDuplicateLogin() {
		Assert.assertTrue(AuthenticationState.DUPLICATE_LOGIN1.isDuplicateLogin());
		Assert.assertTrue(AuthenticationState.DUPLICATE_LOGIN2.isDuplicateLogin());
	}
}
