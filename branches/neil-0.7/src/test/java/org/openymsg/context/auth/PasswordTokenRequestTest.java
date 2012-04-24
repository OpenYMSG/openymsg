package org.openymsg.context.auth;

import java.io.IOException;

import org.mockito.Mockito;
import org.testng.annotations.Test;

public class PasswordTokenRequestTest extends AbstractPasswordTokenTest {

	@Test
	public void testNormal() throws IOException {
		String responseString = "0" + EOL + "ymsgr=ALDJkUlVlvvi8WkgEA6lPnS5jtt5rfJyw_rZBAL1MlhJJL7gcUg-" + EOL
				+ "partnerid=rfJyw_rZBAL1MlhJJL7gcUg-";

		Mockito.when(config.getPasswordTokenGetUrl(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn("authLink");
		Mockito.when(status.isCorrect()).thenReturn(true);
		Mockito.when(stream.getOutputStream()).thenReturn(buildResponse(responseString));

		// String authLink = config.getPasswordTokenGetUrl(username, password, seed);

		PasswordTokenRequest request = new PasswordTokenRequest(sessionAuthorize, config, token);
		request.execute();

		Mockito.verify(sessionAuthorize).receivedPasswordToken();
	}

	@Test
	public void testBadPassword() throws IOException {
		String responseString = "1212";

		Mockito.when(config.getPasswordTokenGetUrl(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn("authLink");
		Mockito.when(status.isCorrect()).thenReturn(true);
		Mockito.when(stream.getOutputStream()).thenReturn(buildResponse(responseString));

		PasswordTokenRequest request = new PasswordTokenRequest(sessionAuthorize, config, token);
		request.execute();

		Mockito.verify(sessionAuthorize).setFailureState(AuthenticationFailure.BAD);
	}

	@Test
	public void testBadUsername() throws IOException {
		String responseString = "1235";

		Mockito.when(config.getPasswordTokenGetUrl(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn("authLink");
		Mockito.when(status.isCorrect()).thenReturn(true);
		Mockito.when(stream.getOutputStream()).thenReturn(buildResponse(responseString));

		PasswordTokenRequest request = new PasswordTokenRequest(sessionAuthorize, config, token);
		request.execute();

		Mockito.verify(sessionAuthorize).setFailureState(AuthenticationFailure.BADUSERNAME);
	}

	@Test
	public void testBadStatus() throws IOException {
		Mockito.when(status.isCorrect()).thenReturn(false);
		PasswordTokenRequest request = new PasswordTokenRequest(sessionAuthorize, config, token);
		request.execute();
		Mockito.verify(sessionAuthorize).setFailureState(AuthenticationFailure.STAGE1);
	}

}
