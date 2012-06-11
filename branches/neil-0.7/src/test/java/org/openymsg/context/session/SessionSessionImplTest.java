package org.openymsg.context.session;

import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.openymsg.YahooStatus;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.write.Message;
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
		connection = Mockito.mock(YahooConnection.class);
		executor = Mockito.mock(Executor.class);
		callback = Mockito.mock(SessionSessionCallback.class);
		session = new SessionSessionImpl(username, executor, connection, timeout, callback);
	}

	@Test
	public void testSetStatus() {
		YahooStatus status = YahooStatus.AVAILABLE;
		session.setStatus(status);
		Mockito.verify(connection).execute(argThat(new StatusChangeRequest(status)));
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Cannot set custom state without message")
	public void testSetStatusCustomFail() {
		YahooStatus status = YahooStatus.CUSTOM;
		session.setStatus(status);
		Mockito.verify(connection).execute(argThat(new StatusChangeRequest(status)));
	}

	@Test
	// TODO showBusyIcon isn't tested
	public void testSetCustom() {
		String message = "myMessage";
		boolean showBusyIcon = false;
		session.setCustomStatus(message, showBusyIcon);
		Mockito.verify(connection).execute(argThat(new StatusChangeRequest(YahooStatus.CUSTOM, message)));
	}

	@Test
	public void testLogout() {
		session.loginComplete();
		session.logout();
		Mockito.verify(connection).execute(argThat(new LogoutMessage(username)));
	}

	@Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "State is not logging in: LOGGING_IN")
	public void testLogoutFailedLogin() {
		session.logout();
	}

	@Test
	public void testLogoutForced() {
		LogoutReason reason = LogoutReason.DUPLICATE_LOGIN1;
		session.receivedLogout(reason);
		Mockito.verify(callback).logoffForced(reason);
	}

	// TODO copied
	private Message argThat(Message message, String... excludeFields) {
		return (Message) Mockito.argThat(new ReflectionEquals(message, excludeFields));
	}

}
