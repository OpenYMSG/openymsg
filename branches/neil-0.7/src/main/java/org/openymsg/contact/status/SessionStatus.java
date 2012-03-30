package org.openymsg.contact.status;

import org.openymsg.Contact;
import org.openymsg.ContactStatus;

public interface SessionStatus {

	ContactStatus getStatus(Contact contact);
}
