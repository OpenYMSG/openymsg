package org.openymsg.conference;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openymsg.YahooConference;

import nl.jqno.equalsverifier.EqualsVerifier;

public class YahooConferenceTest {
	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(YahooConference.class).verify();
	}

	@Test
	public void testSimple() {
		String id = "id";
		YahooConference conference = new YahooConference(id);
		assertEquals(id, conference.getId());
	}
}
