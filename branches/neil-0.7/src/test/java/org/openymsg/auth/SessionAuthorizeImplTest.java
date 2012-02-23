package org.openymsg.auth;

import org.openymsg.Contact;
import org.openymsg.Status;
import org.openymsg.conference.SessionConference;
import org.openymsg.conference.SessionConferenceImpl;
import org.openymsg.config.SessionConfig;
import org.openymsg.config.SessionConfigHardcoded;
import org.openymsg.execute.dispatch.ExecutorImpl;
import org.openymsg.keepalive.SessionKeepAlive;
import org.openymsg.keepalive.SessionKeepAliveImpl;
import org.openymsg.mail.SessionMail;
import org.openymsg.mail.SessionMailImpl;
import org.openymsg.message.SessionMessage;
import org.openymsg.message.SessionMessageCallback;
import org.openymsg.message.SessionMessageImpl;
import org.openymsg.session.SessionSession;
import org.openymsg.session.SessionSessionImpl;
import org.openymsg.status.SessionStatus;
import org.openymsg.status.SessionStatusImpl;
import org.openymsg.unknown.SessionUnknown;
import org.testng.annotations.Test;

public class SessionAuthorizeImplTest {

	public static final void main(String[] args) {
//		authorize.login("openymsgtest1", "Qwerty12");
//		authorize.login("MarketSec_Kyte", "B4rCg3c3");
//		authorize.login("Fred", "Wilma");
//		String username = "openymsgtest1";
//		String password = "Qwerty12";
		String username = "neilvhart";
		String password = "h8.nit12";
		ExecutorImpl executor = new ExecutorImpl(username);
//		SessionConfig config = new SessionConfigImpl();
		SessionConfig config = new SessionConfigHardcoded();
//		executor.initialize(config);
		SessionAuthentication authorize = new SessionAuthenticationImpl(config, executor);
		SessionSession session = new SessionSessionImpl(username, executor);
		SessionStatus status = new SessionStatusImpl(executor);
		SessionMessageCallback messageCallback = new SessionMessageCallback() {

			@Override
			public void receivedMessage(String contactId, String message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void receivedOfflineMessage(String contactId, String message, long timestampl) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void receivedTypingNotification(String contactId, boolean isTyping) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void receivedBuzz(String contactId) {
				// TODO Auto-generated method stub
				
			}};
		SessionMessage message = new SessionMessageImpl(executor, username, messageCallback);
		SessionConference conference = new SessionConferenceImpl(username, executor);
		SessionMail mail = new SessionMailImpl(executor);
		SessionUnknown unknown = new SessionUnknown(executor);
		SessionKeepAlive keepAlive = new SessionKeepAliveImpl(executor, username);
		authorize.login(username, password);

		try {
			Thread.sleep(7 * 1000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		status.setStatus(Status.BUSY);
		
		try {
			Thread.sleep(1 * 1000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Contact messageContact = new Contact("neiliihart");
		message.sendMessage(messageContact , "testing");

		try {
			Thread.sleep(5 * 1000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		session.logout();
	}
	
	
	@Test
	public void initialize() {
		String username = "neilvhart";
//		Dispatcher dispatcher = new DispatcherImpl(username);
//		SessionConfig config = new SessionConfigImpl();
//		SessionAuthorize authorize = new SessionAuthorizeImpl(dispatcher );
//		authorize.initialize(config);
	}

	@Test
	public void login() {
	}
}
