package org.openymsg.network;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionHandlerStatus {
	private Collection<String> capacityIpAddresses;
	private SocketAddress yahooAddress;
	private SocketAddress localAddress;
	private boolean connectedViaCapacity;
	private Set<String> failedCapacityIpAddresses = new HashSet<String>();
	private Set<String> failedScsHosts = new HashSet<String>();
	private boolean connectedViaScs;
	private Map<String, Set<String>> scsHostAddresses = new HashMap<String,  Set<String>>();
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
