package org.openymsg.context.session;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openymsg.testing.MessageAssert.argThatMessage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openymsg.YahooStatus;
import org.openymsg.connection.YahooConnection;
import org.openymsg.connection.read.ReaderRegistry;
import org.openymsg.connection.write.PacketWriter;
import org.openymsg.execute.Executor;

public class SessionSessionImplTest {
	private String username = "testuser";
	@Mock
	private YahooConnection connection;
	@Mock
	private Executor executor;
	@Mock
	private SessionSessionCallback callback;
	@Mock
	private PacketWriter writer;
	@Mock
	private ReaderRegistry registry;
	private SessionSessionImpl session;
	private Integer timeout = 60 * 1000;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
		when(connection.getPacketWriter()).thenReturn(writer);
		when(connection.getReaderRegistry()).thenReturn(registry);
		session = new SessionSessionImpl(username, executor, connection, timeout, callback);
	}

	@Test
	public void testSetStatus() {
		YahooStatus status = YahooStatus.AVAILABLE;
		session.setStatus(status);
		verify(writer).execute(argThatMessage(new StatusChangeRequest(status)));
	}

	@Test()
	public void testSetStatusCustomFail() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Cannot set custom state without message");
		YahooStatus status = YahooStatus.CUSTOM;
		session.setStatus(status);
		verify(writer).execute(argThatMessage(new StatusChangeRequest(status)));
	}

	@Test
	// TODO showBusyIcon isn't tested
	public void testSetCustom() {
		String message = "myMessage";
		boolean showBusyIcon = false;
		session.setCustomStatus(message, showBusyIcon);
		verify(writer).execute(argThatMessage(new StatusChangeRequest(YahooStatus.CUSTOM, message, false)));
	}

	@Test
	public void testLogout() {
		session.loginComplete();
		session.logout();
		verify(writer).execute(argThatMessage(new LogoutMessage(username)));
	}

	@Test()
	public void testLogoutFailedLogin() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("State is not logging in: LOGGING_IN");
		session.logout();
	}

	@Test
	public void testLogoutForced() {
		LogoutReason reason = LogoutReason.DUPLICATE_LOGIN1;
		session.receivedLogout(reason);
		verify(callback).logoffForced(reason);
	}
}
