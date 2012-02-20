package org.openymsg.network.url;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class URLStreamBuilderStatus {
	private int responseCode = -1;
	private String responseMessage = null;
	private MalformedURLException malformedURLException = null;
	private IOException urlConnectionException = null;
	private IOException responseException = null;

	public boolean isCorrect() {
		return (responseCode == HttpURLConnection.HTTP_OK) && (malformedURLException == null) && (urlConnectionException == null) && (responseException == null);
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public MalformedURLException getMalformedURLException() {
		return malformedURLException;
	}
	public void setMalformedURLException(MalformedURLException malformedURLException) {
		this.malformedURLException = malformedURLException;
	}
	public IOException getUrlConnectionException() {
		return urlConnectionException;
	}
	public void setUrlConnectionException(IOException urlConnectionException) {
		this.urlConnectionException = urlConnectionException;
	}
	public IOException getResponseException() {
		return responseException;
	}
	public void setResponseException(IOException responseException) {
		this.responseException = responseException;
	}
}
