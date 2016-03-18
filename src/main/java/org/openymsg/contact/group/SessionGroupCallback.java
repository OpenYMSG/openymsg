package org.openymsg.contact.group;

import org.openymsg.YahooContactGroup;

import java.util.Set;

public interface SessionGroupCallback {
	void addedGroups(Set<YahooContactGroup> contactGroups);
}
