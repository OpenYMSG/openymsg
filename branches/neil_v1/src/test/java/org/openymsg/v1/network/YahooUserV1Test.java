package org.openymsg.v1.network;

import org.openymsg.network.YahooUser;
import org.openymsg.network.YahooUserTest;

public class YahooUserV1Test extends YahooUserTest {

	@Override
	protected YahooUser createTestInstance(String name) {
		return new YahooUserV1(name);
	}

}
