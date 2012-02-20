package org.openymsg;

import java.util.Set;

public interface Conference {

	String getId();

	Set<String> getMemberIds();
	
	// TODO - message is transient
	// String getMessage();

}
