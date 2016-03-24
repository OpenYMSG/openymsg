package org.openymsg.legacy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openymsg.SlowTest;
import org.openymsg.legacy.network.FireEvent;
import org.openymsg.legacy.network.ServiceType;
import org.openymsg.legacy.network.Session;
import org.openymsg.legacy.network.Status;
import org.openymsg.legacy.network.YahooProtocol;
import org.openymsg.legacy.network.YahooUser;
import org.openymsg.legacy.network.event.SessionFriendEvent;
import org.openymsg.legacy.network.event.WaitListener;
import org.openymsg.legacy.roster.Roster;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class StatusIT extends YahooTestAbstract {
	/**
	 * Checks if every Status long value is unique.
	 */
	@Test
	public void testGetValue() {
		final Collection<Long> checkedValues = new HashSet<Long>();
		final Status[] types = Status.values();
		for (int i = 0; i < types.length; i++) {
			final Long statusLongValue = Long.valueOf(types[i].getValue());
			assertFalse("Non-unique Status value " + types[i].getValue(), checkedValues.contains(statusLongValue));
			checkedValues.add(statusLongValue);
		}
	}

	// @Test
	public void testChangeStatus() throws IllegalArgumentException, IOException {
		final Roster roster = sess1.getRoster();
		final boolean existinList = roster.containsUser(OTHERUSR);
		if (!existinList) {
			roster.add(new YahooUser(OTHERUSR, "group", YahooProtocol.YAHOO));
			listener1.waitForEvent(5, ServiceType.FRIENDADD);
		}
		changeStatus(Status.BRB);
		changeStatus(Status.BUSY);
		changeStatus(Status.NOTATHOME);
		changeStatus(Status.NOTATDESK);
		changeStatus(Status.NOTINOFFICE);
		changeStatus(Status.ONPHONE);
		changeStatus(Status.ONVACATION);
		changeStatus(Status.OUTTOLUNCH);
		changeStatus(Status.STEPPEDOUT);
		changeStatus(Status.INVISIBLE);
		changeStatus(Status.OFFLINE);
		changeStatus(Status.AVAILABLE);
	}

	// @Test
	// TODO: this test fails because the two user are not subscribed to each
	// other.
	public void testReceiveLogoutStatus() throws Exception {
		sess2.logout();
		FireEvent event = listener1.waitForEvent(5, ServiceType.Y6_STATUS_UPDATE);
		assertNotNull(event);
		System.out.println(event);
		SessionFriendEvent sfe = (SessionFriendEvent) event.getEvent();
		assertEquals(sfe.getUser().getId(), OTHERUSR);
		assertEquals(sfe.getUser().getStatus(), Status.OFFLINE);
		sess2.login(OTHERUSR, OTHERPWD);
	}

	private void changeStatus(Status status) throws IllegalArgumentException, IOException {
		listener1.clearEvents();
		sess2.setStatus(status);
		FireEvent event = listener1.waitForEvent(5, ServiceType.Y6_STATUS_UPDATE);
		assertNotNull(event);
		SessionFriendEvent sfe = (SessionFriendEvent) event.getEvent();
		assertEquals(sfe.getUser().getId(), OTHERUSR);
		assertEquals(sfe.getUser().getStatus(), status);
	}

	@Test
	@Category(SlowTest.class)
	public void testServerBanYouout4AnotherLoginWithSameUser() throws Exception {
		Session sessKiller = new Session();
		listener2.clearEvents();
		Thread.sleep(500);
		WaitListener sl = new WaitListener(sessKiller);
		sessKiller.login(OTHERUSR, OTHERPWD);
		sl.waitForEvent(5, ServiceType.LOGON);
		FireEvent event = listener2.waitForEvent(5, ServiceType.LOGOFF);
		assertNotNull(event);
		sess2.login(OTHERUSR, OTHERPWD);
		listener2.waitForEvent(5, ServiceType.LOGON);
	}
}
