package org.openymsg;

import java.io.IOException;
import java.util.StringTokenizer;

import org.openymsg.execute.write.Message;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

public class MessageAssert {

	public static final void assertEquals(Message message, String comparisonString) throws IOException {
		ServiceType serviceType = message.getServiceType();
		MessageStatus messageStatus = message.getMessageStatus();
		PacketBodyBuffer body = message.getBody();
		check(comparisonString, serviceType);
		check(comparisonString, messageStatus);
		check(comparisonString, body);
	}

	private static void check(String comparisonString, ServiceType serviceType) {
		StringTokenizer tokenizer = new StringTokenizer(comparisonString, " ");
		String token = tokenizer.nextToken();
		while (!token.startsWith("Service:"))
			token = tokenizer.nextToken();
		String stringServiceType = token.substring("Service:".length());
		if (!stringServiceType.equals(serviceType.toString()))
			failNotEquals(serviceType.toString(), stringServiceType, "Service Type is not the same");
	}

	private static void check(String comparisonString, MessageStatus messageStatus) {
		StringTokenizer tokenizer = new StringTokenizer(comparisonString, " ");
		String token = tokenizer.nextToken();
		while (!token.startsWith("Status:"))
			token = tokenizer.nextToken();
		String stringServiceType = token.substring("Status:".length());
		if (!stringServiceType.equals(messageStatus.toString()))
			failNotEquals(messageStatus.toString(), stringServiceType, "Message Status is not the same");
	}

	private static void check(String comparisonString, PacketBodyBuffer body) {
		StringTokenizer tokenizer = new StringTokenizer(comparisonString, " ");
		String token = tokenizer.nextToken();
		while (!token.startsWith("SessionId:"))
			token = tokenizer.nextToken();
		StringTokenizer bodyTokenizer = new StringTokenizer(body.toString(), " ");
		while (tokenizer.hasMoreTokens()) {
			String comparisonKey = tokenizer.nextToken();
			String comparisonValue = tokenizer.nextToken();
			String bodyKey = bodyTokenizer.nextToken();
			String bodyValue = null;
			// special hack for handling space as a value. this is tokenized out of the body.
			if (comparisonValue.length() == 1) {
				comparisonValue = tokenizer.nextToken();
				comparisonValue = "[ ]";
				bodyValue = " ";
			} else {
				bodyValue = bodyTokenizer.nextToken();
			}
			try {
				// System.err.println("comparing:  " + comparisonKey + ":" + comparisonValue + "-" + bodyKey + ":"
				// + bodyValue);
				check(comparisonKey, comparisonValue, bodyKey, bodyValue);
			}
			catch (Exception e) {
				System.err.println("Failed comparing:  " + comparisonKey + ":" + comparisonValue + "-" + bodyKey + ":"
						+ bodyValue);
				e.printStackTrace();
			}
		}

	}

	private static void check(String comparisonKey, String comparisonValue, String bodyKey, String bodyValue) {
		if (!strip(comparisonKey).equals(bodyKey))
			failNotEquals(bodyKey, strip(comparisonKey), "Body key is not the same");
		if (!strip(comparisonValue).equals(bodyValue))
			failNotEquals(bodyValue, strip(comparisonValue), "Body key is not the same");
	}

	private static String strip(String string) {
		return string.substring(1, string.length() - 1);
	}

	static private void failNotEquals(Object actual, Object expected, String message) {
		String formatted = (format(actual, expected, message));
		AssertionError ae = new AssertionError(formatted);
		throw ae;
	}

	static String format(Object actual, Object expected, String message) {
		String formatted = "";
		if (null != message) {
			formatted = message + " ";
		}

		return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
	}

}
