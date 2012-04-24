package org.openymsg.contact.status;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.YahooStatus;
import org.openymsg.execute.read.SinglePacketResponse;
import org.openymsg.network.ServiceType;
import org.openymsg.network.YMSG9Packet;

/**
 * LOGON packets can contain multiple friend status sections, ISAWAY and ISBACK packets contain only one. Update the
 * YahooUser details and fire event. status == 0 is a single status
 */
public class SingleStatusResponse implements SinglePacketResponse {
	private static final Log log = LogFactory.getLog(SingleStatusResponse.class);
	private SessionStatusImpl sessionStatus;

	public SingleStatusResponse(SessionStatusImpl sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	/**
	 * [300] [315] [7] [buddy] [10] [0] [13] [1] [97] [1] [192] [2140265804] [198] [0] [213] [0] [244] [33554367] [302]
	 * [316] [300] [316] [135] [2.0.42] [258] [16762d37-0eb1-40d0-5863-5fa5ade02675] [310] [en-us] [301] [316] [300]
	 * [316] [135] [2.0.4] [258] [4eb73995-f313-4f4a-49a5-1bc4d7c3ee68] [310] [en-us] [301] [316] [300] [316] [135]
	 * [0.2.11] [258] [af6d7f82-d94d-4ce0-7707-a527d5a01f3d] [310] [en-us] [301] [316] [303] [316] [300] [444] [440] [0]
	 * [301] [444] [550] [OK3X2E2PORYZ3DKCIUGF6S5CUI] [301] [315]
	 * @param pkt
	 */
	@Override
	public void execute(YMSG9Packet packet) {
		// If LOGOFF packet, the packet's user status is wrong (available)
		// TODO - handle log off
		final boolean logoff = (packet.service == ServiceType.LOGOFF);
		// Process online friends data
		// Process each friend
		Iterator<String[]> iter = packet.entries().iterator();
		long longStatus = 0;
		Boolean onChat = null;
		Boolean onPager = null;
		String visibility = null;
		String clearIdleTime = null;
		String idleTime = null;
		String customMessage = null;
		String customStatus = null;
		YahooProtocol protocol = YahooProtocol.YAHOO;
		String userId = null;
		String clientVersion = null;
		while (iter.hasNext()) {
			String[] s = iter.next();

			int key = Integer.valueOf(s[0]);
			String value = s[1];
			// log.info("Key: " + key + ", value: " + value);
			switch (key) {
			case 300:
				// initial row, most of the time
				break;
			case 7:
				// check and see if we have one
				if (userId != null) {
					updateFriendStatus(logoff, userId, onChat, onPager, visibility, clearIdleTime, idleTime,
							customMessage, customStatus, longStatus, protocol, clientVersion);
					longStatus = 0;
					onChat = null;
					onPager = null;
					visibility = null;
					clearIdleTime = null;
					idleTime = null;
					customMessage = null;
					customStatus = null;
					userId = null;
					clientVersion = null;
					protocol = YahooProtocol.YAHOO;

				}
				userId = value;
				break;
			case 10:
				try {
					longStatus = Long.parseLong(value);
				}
				catch (NumberFormatException e) {
					customMessage = value;
				}
				break;
			case 17:
				onChat = value.equals("1");
				break;
			case 13: // one of these matters
				onPager = value.equals("1");
				visibility = value;
				break;
			case 19:
				customMessage = value;
				break;
			case 47:
				customStatus = value;
				break;
			case 97:
				// TODO - unicodeStatus
				boolean unicodeStatusMessage = value.equals("1");
				break;
			case 138:
				clearIdleTime = value;
				break;
			case 241:
				protocol = YahooProtocol.getProtocolOrDefault(value, userId);
				break;
			case 244:
				// TODO - track version
				clientVersion = value;
				// 6 - at least MSN - Windows Live Messenger 2011 (Build 15.4.3538.513)
				break;
			case 137:
				idleTime = value;
				break;
			case 301:
				// ending row, most of the time
				break;
			}
		}
		if (userId != null) {
			updateFriendStatus(logoff, userId, onChat, onPager, visibility, clearIdleTime, idleTime, customMessage,
					customStatus, longStatus, protocol, clientVersion);
		}

	}

	private void updateFriendStatus(boolean logoff, String userId, Boolean onChat, Boolean onPager, String visibility,
			String clearIdleTime, String idleTime, String customMessage, String customStatus, long longStatus,
			YahooProtocol protocol, String clientVersion) {
		log.trace("UpdateFriendStatus arguments: logoff: " + logoff + ", user: " + userId + ", onChat: " + onChat
				+ ", onPager: " + onPager + ", visibility: " + visibility + ", clearIdleTime: " + clearIdleTime
				+ ", idleTime: " + idleTime + ", customMessage: " + customMessage + ", customStatus: " + customStatus
				+ ", longStatus: " + longStatus + ", protocol: " + protocol);
		YahooStatus newStatus = YahooStatus.AVAILABLE;
		YahooContact contact = new YahooContact(userId, protocol);
		// ContactStatusImpl status = sessionStatus.getStatus(contact);
		// TODO - handle this
		// When we add a friend, we get a status update before
		// getting a confirmation ADD_BUDDY packet (crazy!)
		// if (status == null) {
		// }

		// TODO - do this check
		// if (user.getProtocol() == null || !user.getProtocol().equals(protocol)) {
		// log.warn("In updateFriendStatus, Protocols do not match for user: " + user.getId() + " "
		// + user.getProtocol() + "/" + protocol);
		// }

		if (longStatus == -1 && (onPager == null || !onPager)) {
			// Offline -- usually federated or msn live
			logoff = true;
		}
		try {
			newStatus = logoff ? YahooStatus.OFFLINE : YahooStatus.getStatus(longStatus);
		}
		catch (IllegalArgumentException e) {
			log.error("missing status: " + longStatus);
			newStatus = YahooStatus.OFFLINE;
		}

		ContactPresence presence = null;
		StatusMessage status = null;

		if (onChat != null) {
			presence = new ContactPresence(onChat, onPager);
			status = new NormalStatusMessage(newStatus);
			// log.info("update: " + newStatus + "/" + onChat + "/" + onPager);
			// status.update(newStatus, onChat, onPager);
		} else if (onPager != null) {
			// log.info("update: " + newStatus + "/" + visibility);
			presence = getPresenceByVisibility(visibility);
			status = new NormalStatusMessage(newStatus);
			// status.update(newStatus, visibility);
		} else if (logoff) {
			presence = new ContactPresence(false, false);
			status = new NormalStatusMessage(newStatus);
			// logoff message doesn't have chat or pager info, but we reset those in this case.
			// log.info("update: " + newStatus + " and false/false");
			// status.update(newStatus, false, false);
		} else {
			// status update with no chat, nor pager information, so leave those values alone.
			// log.info("update: " + newStatus);
			status = new NormalStatusMessage(newStatus);
			// status.update(newStatus);
		}
		if (customMessage != null) {
			status = new CustomStatusMessage(customStatus, customMessage);

			// log.info("update custom: " + customMessage + "/" + customStatus);
			// status.setCustom(customMessage, customStatus);
		}

		Long statusIdleTime = getIdleTime(clearIdleTime, idleTime);
		// Hack for MSN users
		if (contact.getProtocol().isMsn() && status.getStatus() == YahooStatus.STEPPEDOUT) {
			status = new NormalStatusMessage(YahooStatus.AWAY);
		}
		ContactStatusImpl contactStatus = new ContactStatusImpl(status, presence, statusIdleTime);
		this.sessionStatus.statusUpdate(contact, contactStatus);
	}

	/**
	 * Updates the YahooUser with the new values.
	 * @param newStatus replacement for current Status
	 * @param newVisibility replacement for current onChat and onPager values
	 */
	public ContactPresence getPresenceByVisibility(String visibility) {
		// This is the new version, where 13=combined pager/chat
		final int iVisibility = (visibility == null) ? 0 : Integer.parseInt(visibility);
		return new ContactPresence((iVisibility & 2) > 0, (iVisibility & 1) > 0);
	}

	private Long getIdleTime(String clearIdleTime, String idleTime) {
		if (clearIdleTime != null) {
			return -1L;
		}
		if (idleTime != null) {
			return Long.parseLong(idleTime);
		}
		return -1L;
	}
}
