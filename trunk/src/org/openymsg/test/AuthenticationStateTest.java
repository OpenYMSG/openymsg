package org.openymsg.test;

import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.openymsg.network.AuthenticationState;

public class AuthenticationStateTest {

	/**
	 * Checks if every AuthenticationState long value is unique.
	 */
	@Test
	public void testGetValue() {
		final Collection<Long> checkedValues = new HashSet<Long>();
		final AuthenticationState[] types = AuthenticationState.values();
		for (int i = 0; i < types.length; i++) {
			final Long statusLongValue = Long.valueOf(types[i].getValue());
			assertFalse("Non-unique AuthenticationState value "
					+ types[i].getValue(), checkedValues
					.contains(statusLongValue));
			checkedValues.add(statusLongValue);
		}
	}
}
