package org.openymsg.test;

import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.openymsg.network.Status;

public class StatusTest {

	/**
	 * Checks if every Status long value is unique.
	 */
	@Test
	public void testGetValue() {
		final Collection<Long> checkedValues = new HashSet<Long>();
		final Status[] types = Status.values();
		for (int i = 0; i < types.length; i++) {
			final Long statusLongValue = Long.valueOf(types[i].getValue());
			assertFalse("Non-unique Status value " + types[i].getValue(),
					checkedValues.contains(statusLongValue));
			checkedValues.add(statusLongValue);
		}
	}
}
