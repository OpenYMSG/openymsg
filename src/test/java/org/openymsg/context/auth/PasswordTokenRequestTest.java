package org.openymsg.context.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import java.io.IOException;

public class PasswordTokenRequestTest extends AbstractPasswordTokenTest {
	@Test
	public void testNormal() throws IOException {
		String responseString = "0" + EOL + "ymsgr=ALDJkUlVlvvi8WkgEA6lPnS5jtt5rfJyw_rZBAL1MlhJJL7gcUg-" + EOL
				+ "partnerid=rfJyw_rZBAL1MlhJJL7gcUg-";
		when(config.getPasswordTokenGetUrl(anyString(), anyString(), anyString())).thenReturn("authLink");
		when(stream.getOutputStream()).thenReturn(buildResponse(responseString));
		// String authLink = config.getPasswordTokenGetUrl(username, password, seed);
		PasswordTokenRequest request = new PasswordTokenRequest(sessionAuthorize, config, token);
		request.execute();
		verify(sessionAuthorize).receivedPasswordToken();
	}

	@Test
	public void testBadPassword() throws IOException {
		String responseString = "1212";
		when(config.getPasswordTokenGetUrl(anyString(), anyString(), anyString())).thenReturn("authLink");
		when(stream.getOutputStream()).thenReturn(buildResponse(responseString));
		PasswordTokenRequest request = new PasswordTokenRequest(sessionAuthorize, config, token);
		request.execute();
		verify(sessionAuthorize).setFailureState(AuthenticationFailure.BAD);
	}

	@Test
	public void testBadUsername() throws IOException {
		String responseString = "1235";
		when(config.getPasswordTokenGetUrl(anyString(), anyString(), anyString())).thenReturn("authLink");
		when(stream.getOutputStream()).thenReturn(buildResponse(responseString));
		PasswordTokenRequest request = new PasswordTokenRequest(sessionAuthorize, config, token);
		request.execute();
		verify(sessionAuthorize).setFailureState(AuthenticationFailure.BADUSERNAME);
	}

	@Test
	public void testBadStatus() throws IOException {
		status.setFailedHandlingResponse(null, null, null);
		PasswordTokenRequest request = new PasswordTokenRequest(sessionAuthorize, config, token);
		request.execute();
		verify(sessionAuthorize).setConnectionFailureStatus(eq(AuthenticationStep.PasswordTokenRequest),
				any(AuthenticationAttemptStatus.class));
	}
}
