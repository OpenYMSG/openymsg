package org.openymsg.unknown;

import org.testng.annotations.Test;

public class Unknown002ResponseTest extends Unknown002Response {

	@Test
	public void test() {
		// TODO use real packet
		Unknown002Response response = new Unknown002Response();
		response.execute(null);
	}
}
