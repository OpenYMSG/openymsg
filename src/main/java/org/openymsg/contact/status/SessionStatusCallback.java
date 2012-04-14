package org.openymsg.contact.status;

import org.openymsg.Contact;
import org.openymsg.ContactStatus;

public interface SessionStatusCallback {

	void statusUpdate(Contact contact, ContactStatus status);

}
