package org.openymsg.connection.read;

import org.junit.Test;

public class NoOpResponseTest {
	@Test
	public void test() {
		NoOpResponse response = new NoOpResponse();
		response.execute(null);
	}
}
