package org.openymsg.network.url;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface URLStreamBuilder {

	URLStreamBuilder url(String url);

	URLStreamBuilder timeout(int timeout);

	URLStreamBuilder cookie(String cookie);

	URLStreamBuilder keepHeaders(boolean keepHeaders);

	URLStreamBuilder keepData(boolean keepData);

	URLStreamBuilderStatus build();

	ByteArrayOutputStream getOutputStream();

	InputStream getInputStream();

	Map<String, List<String>> getHeaders();

}