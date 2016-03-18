package org.openymsg.context.auth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import java.io.IOException;

public class PasswordTokenLoginRequestTest extends AbstractPasswordTokenTest {
	@Test
	public void testNormal() throws IOException {
		String responseString = "0" + EOL + "crumb=yxpjXOHjeV." + EOL
				+ "Y=v=1&n=8463ugu2gggs1&l=d48b88870hj/o&p=m1s09p6012000000&r=ki&lg=en-US&intl=us&np=1; path=/; domain=.yahoo.com"
				+ EOL
				+ "T=z=vRflPBvX0lPBOnsMRSeYXMKNjU2NwY0NzROTjVOMDIyNjcwNj&a=QAE&sk=DAAsrS5s2Iw6kX&ks=EAA9UDCK2ZPU4PFeEKjlWjH.g--~E&d=c2wBTVRJeE1BRXpNRE01T1RJNU56VTFNVEEzTVRFNE1RLS0BYQFRQUUBZwEzUVVZT0hDQk9SRzZIWlpCUlpRWERMTkUzWQF6egF2UmZsUEJnV0EBdGlwAVl3U3pyQQ--; path=/; domain=.yahoo.com"
				+ EOL + "cookievalidfor=86400" + EOL;
		when(stream.getHeaders()).thenReturn(buildHeader());
		when(stream.getOutputStream()).thenReturn(buildResponse(responseString));
		when(status.isCorrect()).thenReturn(true);
		PasswordTokenLoginRequest request = new PasswordTokenLoginRequest(sessionAuthorize, config, token);
		request.execute();
		String cookieY =
				"v=1&n=8463ugu2gggs1&l=d48b88870hj/o&p=m1s09p6012000000&r=ki&lg=en-US&intl=us&np=1; path=/; domain=.yahoo.com";
		String cookieT =
				"z=vRflPBvX0lPBOnsMRSeYXMKNjU2NwY0NzROTjVOMDIyNjcwNj&a=QAE&sk=DAAsrS5s2Iw6kX&ks=EAA9UDCK2ZPU4PFeEKjlWjH.g--~E&d=c2wBTVRJeE1BRXpNRE01T1RJNU56VTFNVEEzTVRFNE1RLS0BYQFRQUUBZwEzUVVZT0hDQk9SRzZIWlpCUlpRWERMTkUzWQF6egF2UmZsUEJnV0EBdGlwAVl3U3pyQQ--; path=/; domain=.yahoo.com";
		String crumb = "yxpjXOHjeV.";
		String cookieB = null;
		verify(token).setCookiesAndCrumb(cookieY, cookieT, crumb, cookieB);
		verify(sessionAuthorize).receivedPasswordTokenLogin();
	}

	@Test
	public void testBadStatus() throws IOException {
		when(status.isCorrect()).thenReturn(false);
		PasswordTokenLoginRequest request = new PasswordTokenLoginRequest(sessionAuthorize, config, token);
		request.execute();
		verify(sessionAuthorize).setFailureState(AuthenticationFailure.STAGE2);
	}

	@Test
	public void testBadResponse() throws IOException {
		when(status.isCorrect()).thenReturn(true);
		when(stream.getHeaders()).thenReturn(buildHeader());
		when(stream.getOutputStream()).thenReturn(buildResponse("1"));
		PasswordTokenLoginRequest request = new PasswordTokenLoginRequest(sessionAuthorize, config, token);
		request.execute();
		verify(sessionAuthorize).setFailureState(AuthenticationFailure.STAGE2);
	}
}
