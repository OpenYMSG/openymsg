package org.openymsg.conference;

import junit.framework.Assert;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.openymsg.YahooConference;
import org.testng.annotations.Test;

public class YahooConferenceTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(ConferenceImpl.class).verify();
	}

	@Test
	public void testSimple() {
		String id = "id";
		YahooConference conference = new ConferenceImpl(id);
		Assert.assertEquals(id, conference.getId());
	}

}
