package org.openymsg.context.session;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openymsg.testing.MessageAssert.argThatMessage;

import org.openymsg.YahooStatus;
import org.openymsg.connection.YahooConnection;
import org.openymsg.execute.Executor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SessionSessionImplTest {
	private String username = "testuser";
	private YahooConnection connection;
	private Executor executor;
	private SessionSessionCallback callback;
	private SessionSessionImpl session;
	private Integer timeout = 60 * 1000;

	@BeforeMethod
	public void beforeMethod() {
		connection = mock(YahooConnection.class);
		executor = mock(Executor.class);
		callback = mock(SessionSessionCallback.class);
		session = new SessionSessionImpl(username, executor, connection, timeout, callback);
	}

	@Test
	public void testSetStatus() {
		YahooStatus status = YahooStatus.AVAILABLE;
		session.setStatus(status);
		verify(connection).execute(argThatMessage(new StatusChangeRequest(status)));
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Cannot set custom state without message")
	public void testSetStatusCustomFail() {
		YahooStatus status = YahooStatus.CUSTOM;
		session.setStatus(status);
		verify(connection).execute(argThatMessage(new StatusChangeRequest(status)));
	}

	@Test
	// TODO showBusyIcon isn't tested
	public void testSetCustom() {
		String message = "myMessage";
		boolean showBusyIcon = false;
		session.setCustomStatus(message, showBusyIcon);
		verify(connection).execute(argThatMessage(new StatusChangeRequest(YahooStatus.CUSTOM, message)));
	}

	@Test
	public void testLogout() {
		session.loginComplete();
		session.logout();
		verify(connection).execute(argThatMessage(new LogoutMessage(username)));
	}

	@Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "State is not logging in: LOGGING_IN")
	public void testLogoutFailedLogin() {
		session.logout();
	}

	@Test
	public void testLogoutForced() {
		LogoutReason reason = LogoutReason.DUPLICATE_LOGIN1;
		session.receivedLogout(reason);
		verify(callback).logoffForced(reason);
	}

}
