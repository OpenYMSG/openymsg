package org.openymsg.v1.network;

import org.openymsg.network.YahooGroup;
import org.openymsg.network.YahooGroupTest;

public class YahooGroupV1Test extends YahooGroupTest {

	@Override
	protected YahooGroup createTestInstance(String name) {
		return new YahooGroupV1(name);
	}

}
