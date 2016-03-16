package org.openymsg.connection;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Information on the connection.  There are a few different ways that a connection can be established.  This
 * contains the information on what was tried and what failed.
 * @author neilhart
 *
 */
public class ConnectionInfo {
	/** Information on the remote connection */
	private SocketAddress yahooAddress;
	/** Information on our local connection */
	private SocketAddress localAddress;
	/** Ip addresses returned from the capacity servers */
	private Collection<String> capacityIpAddresses;
	/** whether a connection using the capacity was successful */
	private boolean connectedViaCapacity;
	/** attempted and failed ip addresses returned by the capacity servers*/
	private Set<String> failedCapacityIpAddresses = new HashSet<String>();
	/** whether a connection using the scs hosts was successful */
	private boolean connectedViaScs;
	/** fail scs hosts */
	private Set<String> failedScsHosts = new HashSet<String>();
	/** ip addresses returned from the scs hosts */
	private Map<String, Set<String>> scsHostAddresses = new HashMap<String,  Set<String>>();
	/** attempted and failed ip addresses returned from the scs hosts */
	private Map<String, Set<String>> failedScsIpAddresses = new HashMap<String,  Set<String>>();
	
	public void setCapacityIpAddresses(Collection<String> capacityIpAddresses) {
		this.capacityIpAddresses = capacityIpAddresses; 
	}

	public void setCapacityIpAddressConnected(Socket socket) {
		this.connectedViaCapacity = true;
		this.yahooAddress = socket.getRemoteSocketAddress();
		this.localAddress = socket.getLocalSocketAddress();
	}

	public void addCapacityIpAddressFailure(String ipAddress) {
		this.failedCapacityIpAddresses.add(ipAddress);
	}

	public void addScsHostAddresses(String scsHost, InetAddress[] inetAddresses) {
		Set<String> ipAddresses = new HashSet<String>();
		for (InetAddress inetAddress : inetAddresses) {
			ipAddresses.add(inetAddress.getHostAddress());
		}
		this.scsHostAddresses.put(scsHost, ipAddresses);
	}

	public void setScsIpAddressConnected(Socket socket) {
		this.connectedViaScs = true;
		this.yahooAddress = socket.getRemoteSocketAddress();
		this.localAddress = socket.getLocalSocketAddress();
	}

	public void addScsIpAddressFailure(String scsHost, InetAddress inetAddress) {
		Set<String> inetAddresses = this.failedScsIpAddresses.get(scsHost);
		if (inetAddresses == null) {
			inetAddresses = new HashSet<String>();
			this.failedScsIpAddresses.put(scsHost, inetAddresses);
		}
		inetAddresses.add(inetAddress.getHostAddress());
	}

	public void addScsHostFailure(String scsHost) {
		this.failedScsHosts.add(scsHost);
	}
	
	public boolean isConnected() {
		return this.connectedViaCapacity || this.connectedViaScs;
	}

	@Override
	public String toString() {
		return "ConnectionHandlerStatus [capacityIpAddresses=" + capacityIpAddresses + ", yahooAddress=" + yahooAddress
				+ ", localAddress=" + localAddress + ", connectedViaCapacity=" + connectedViaCapacity
				+ ", failedCapacityIpAddresses=" + failedCapacityIpAddresses + ", failedScsHosts=" + failedScsHosts
				+ ", connectedViaScs=" + connectedViaScs + ", scsHostAddresses=" + scsHostAddresses
				+ ", failedScsIpAddresses=" + failedScsIpAddresses + "]";
	}

}
