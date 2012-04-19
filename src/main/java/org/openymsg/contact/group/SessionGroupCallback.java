package org.openymsg.contact.group;

import java.util.Set;

import org.openymsg.YahooContactGroup;

public interface SessionGroupCallback {
	void addedGroups(Set<YahooContactGroup> contactGroups);

}
