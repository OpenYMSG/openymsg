package org.openymsg.testing;

import org.junit.Assert;
import org.mockito.Matchers;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.openymsg.connection.write.Message;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageAssert {
	private static final Pattern BODY_PATTERN = Pattern.compile("\\[(.*?)\\]");

	public static final void assertEquals(Message message, String comparisonString, String... excludeFields)
			throws IOException {
		ServiceType serviceType = message.getServiceType();
		MessageStatus messageStatus = message.getMessageStatus();
		PacketBodyBuffer body = message.getBody();
		check(comparisonString, serviceType);
		check(comparisonString, messageStatus);
		check(comparisonString, body, excludeFields);
	}

	public static Message argThatMessage(Message message, String... excludeFields) {
		return (Message) Matchers.argThat(new ReflectionEquals(message, excludeFields));
	}

	public static Request argThatRequest(Request request, String... excludeFields) {
		return (Request) Matchers.argThat(new ReflectionEquals(request, excludeFields));
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

	private static void check(String comparisonString, PacketBodyBuffer body, String... excludeFields) {
		@SuppressWarnings("unchecked")
		List<String> excludedFieldList = excludeFields != null ? Arrays.asList(excludeFields) : Collections.EMPTY_LIST;
		// StringTokenizer tokenizer = new StringTokenizer(comparisonString, " ");
		// Iterator<String> tokenizer = Arrays.asList(comparisonString.split(" ")).iterator();
		// String token = tokenizer.next();
		// while (!token.startsWith("SessionId:"))
		// token = tokenizer.next();
		// if (tokenizer.hasNext()) {
		// token = tokenizer.next(); // skip one
		// }
		Matcher comparisonMatcher = BODY_PATTERN.matcher(comparisonString);
		List<String> comparisonList = new ArrayList<String>();
		while (comparisonMatcher.find()) {
			String token = comparisonMatcher.group(1);
			// System.out.println(token);
			comparisonList.add(token);
		}
		Iterator<String> tokenizer = comparisonList.iterator();
		String bodyString = body.toString();
		String[] values = bodyString.split(" ");
		Iterator<String> bodyTokenizer = Arrays.asList(values).iterator();
		while (tokenizer.hasNext()) {
			String comparisonKey = tokenizer.next();
			String comparisonValue = tokenizer.next();
			if (!excludedFieldList.contains(strip(comparisonKey))) {
				String bodyKey = bodyTokenizer.next();
				if (bodyKey.isEmpty()) {
					bodyKey = bodyTokenizer.next();
				}
				String bodyValue = bodyTokenizer.next();
				// special hack for handling space as a value. this is tokenized out of the body.
				// if (comparisonValue.length() == 1) {
				// // comparisonValue = tokenizer.next();
				// comparisonValue = " ";
				// bodyValue = " ";
				// } else {
				// bodyValue = bodyTokenizer.next();
				// }
				try {
					// System.err.println("comparing: " + comparisonKey + ":" + comparisonValue + "-" + bodyKey + ":"
					// + bodyValue);
					check(comparisonKey, comparisonValue, bodyKey, bodyValue);
				} catch (Exception e) {
					System.err.println("Failed comparing:  " + comparisonKey + ":" + comparisonValue + "-" + bodyKey
							+ ":" + bodyValue);
					e.printStackTrace();
				}
			}
		}
	}

	private static void check(String comparisonKey, String comparisonValue, String bodyKey, String bodyValue) {
		if (!strip(comparisonKey).equals(bodyKey))
			failNotEquals(bodyKey, strip(comparisonKey), "Body key is not the same");
		String stripValue = strip(comparisonValue);
		if (!stripValue.equals(strip(bodyValue))) {
			if (!checkIfCommaSeperated(stripValue, bodyValue)) {
				failNotEquals(bodyValue, strip(comparisonValue), "Body value is not the same for key: " + bodyKey);
			}
		}
	}

	private static boolean checkIfCommaSeperated(String stripValue, String bodyValue) {
		String[] comparisonStrings = stripValue.split(",");
		String[] bodyStrings = bodyValue.split(",");
		if (comparisonStrings.length == bodyStrings.length && comparisonStrings.length > 1) {
			Set<String> comparisonSet = new HashSet<String>(Arrays.asList(comparisonStrings));
			Set<String> bodySet = new HashSet<String>(Arrays.asList(bodyStrings));
			Assert.assertEquals(comparisonSet, bodySet);
			return true;
		}
		return false;
	}

	private static String strip(String string) {
		return string.trim();
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
