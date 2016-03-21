/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol. Copyright (C) 2007 G. der Kinderen,
 * Nimbuzz.com This program is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openymsg.legacy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class PropertiesAvailableTest {
	private static final Properties props = new Properties();
	static {
		final Class<?> me;
		final InputStream is;
		final InputStream ip;
		try {
			me = Class.forName("org.openymsg.legacy.test.PropertiesAvailableTest");
			is = me.getClassLoader().getResourceAsStream("legacy/yahooAuthenticationForJUnitTests.properties");
			ip = me.getClassLoader().getResourceAsStream("log4j.xml");
			props.load(is);
			final Properties log4j = new Properties();
			log4j.load(ip);
			// PropertyConfigurator.configure(log4j);
			is.close();
			ip.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetTestUser() throws Exception {
		InputStream is = getClass().getResourceAsStream("/legacy/yahooAuthenticationForJUnitTests.properties");
		props.load(is);
		final String[] user = getAccount("testuser");
		assertNotNull(user);
		assertEquals("DummyYahooUsername", user[0]);
		assertEquals("YahooPassword", user[1]);
	}

	public static String getUsername(String accountname) {
		final String value = props.getProperty(accountname);
		assertNotNull("The property 'testuser' should be set in the resource file, but isn't.", value);
		return value;
	}

	public static String getPassword(String username) {
		assertNotNull(username);
		final String value = props.getProperty(username);
		assertNotNull("There's no property that specifies the password for this username set in the resource file.",
				value);
		return value;
	}

	public static String[] getAccount(String accountName) {
		final String username = getUsername(accountName);
		final String password = getPassword(username);
		return new String[] {username, password};
	}
}
