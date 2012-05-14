package org.openymsg.connection.read;

import org.testng.annotations.Test;

public class NoOpResponseTest {

	@Test
	public void test() {
		NoOpResponse response = new NoOpResponse();
		response.execute(null);
	}

}
