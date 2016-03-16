package org.openymsg.context.auth;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openymsg.config.SessionConfig;
import org.openymsg.network.url.URLStream;
import org.openymsg.network.url.URLStreamBuilder;
import org.openymsg.network.url.URLStreamStatus;
import org.testng.annotations.BeforeMethod;

public class AbstractPasswordTokenTest {
	protected static final String EOL = "\r\n";
	protected SessionAuthenticationImpl sessionAuthorize;
	protected SessionConfig config;
	protected AuthenticationToken token;
	protected URLStreamBuilder builder;
	protected URLStream stream;
	protected URLStreamStatus status;

	public AbstractPasswordTokenTest() {
		super();
	}

	@BeforeMethod
	public void beforeMethod() {
		sessionAuthorize = mock(SessionAuthenticationImpl.class);
		config = mock(SessionConfig.class);
		token = mock(AuthenticationToken.class);
		builder = mock(URLStreamBuilder.class);
		stream = mock(URLStream.class);
		status = mock(URLStreamStatus.class);

		when(config.getURLStreamBuilder()).thenReturn(builder);
		when(builder.build()).thenReturn(stream);
		when(builder.getStatus()).thenReturn(status);

		when(builder.timeout(anyInt())).thenReturn(builder);
		when(builder.url(anyString())).thenReturn(builder);
	}

	protected ByteArrayOutputStream buildResponse(String urlAnswer) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(urlAnswer.getBytes().length);
		out.write(urlAnswer.getBytes());
		return out;
	}

	protected Map<String, List<String>> buildHeader() {
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String cookie = "T=z=OgflPBOm0lPBtylzk8ofGOHNjU2NwY0NzROTjVOMDIyNjcwNj&a=QAE&sk=DAAms2FasHzVrC&ks=EAA3.ap4loNKAH665B4LS_9ug--~E&d=c2wBTVRJeE1BRXpNRE01T1RJNU56VTFNVEEzTVRFNE1RLS0BYQFRQUUBZwEzUVVZT0hDQk9SRzZIWlpCUlpRWERMTkUzWQF6egFPZ2ZsUEJnV0EBdGlwAVl3U3pyQQ--; path=/; domain=.yahoo.com, Y=v=1&n=8463ugu2gggs1&l=d48b88870hj/o&p=m1s09p6012000000&r=ki&lg=en-US&intl=us&np=1; path=/; domain=.yahoo.com, B=atp3hdh7pbu0e&b=3&s=20; expires=Fri, 25-Apr-2014 00:47:10 GMT; path=/; domain=.yahoo.com";
		List<String> cookies = new ArrayList<String>();
		cookies.add(cookie);
		headers.put("Set-Cookie", cookies);
		return headers;
	}

}