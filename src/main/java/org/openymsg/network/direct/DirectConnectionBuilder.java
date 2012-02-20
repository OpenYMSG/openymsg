package org.openymsg.network.direct;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.SessionConfig;
import org.openymsg.network.CapacityServers;
import org.openymsg.network.ConnectionHandlerStatus;
import org.openymsg.network.NetworkConstants;

public class DirectConnectionBuilder {
	private static final Log log = LogFactory.getLog(DirectConnectionBuilder.class);
	private boolean capacityBuilding;
	private boolean scsBuilding;
	private Socket socket;
	private SessionConfig config;
	private ConnectionHandlerStatus handlerStatus;
	
	public ConnectionHandlerStatus getHandlerStatus() {
		return handlerStatus;
	}

	public DirectConnectionBuilder() {
		
	}

	public DirectConnectionBuilder with(SessionConfig config) {
		this.config = config;
		return this;
	}


	public DirectConnectionBuilder useCapacityServers() {
		this.capacityBuilding = true;
		return this;
	}

	public DirectConnectionBuilder useScsServers() {
		this.scsBuilding = true;
		return this;
	}

	public DirectConnectionHandler build() {
		handlerStatus = new ConnectionHandlerStatus();
		DirectConnectionHandler connection = null;
		boolean connected = false;
		if (capacityBuilding) {
			connected = connectViaCapacityServers(config, handlerStatus);
		}
		if (scsBuilding && !connected) {
			connected = connectViaScsServers(config, handlerStatus);
		}
		if (connected) {
			connection = new DirectConnectionHandler(this.socket);
		}
		this.socket = null;
		this.config = null;
		return connection;
	}

	protected boolean connectViaScsServers(SessionConfig config, ConnectionHandlerStatus handlerStatus) {
		String[] scsHosts = config.getScsHosts();//NetworkConstants.SCS_HOSTS;
		for (String scsHost : scsHosts) {
			try {
				InetAddress[] inetAddresses = InetAddress.getAllByName(scsHost);
				handlerStatus.addScsHostAddresses(scsHost, inetAddresses);
				for (InetAddress inetAddress : inetAddresses) {
					if (openConnection(inetAddress, config)) {
						handlerStatus.setScsIpAddressConnected(this.socket);
						return true;
					}
					else {
						handlerStatus.addScsIpAddressFailure(scsHost, inetAddress);
					}

				}
			}
			catch (UnknownHostException e) {
				log.warn("Failed finding host for: " + scsHost);
				handlerStatus.addScsHostFailure(scsHost);
			}
		}
		return false;
	}

	protected boolean connectViaCapacityServers(SessionConfig config, ConnectionHandlerStatus handlerStatus) {
		CapacityServers capacityServers = new CapacityServers(config);
		Collection<String> ipAddresses = capacityServers.getIpAddresses();
		handlerStatus.setCapacityIpAddresses(ipAddresses);
		for (String ipAddress : ipAddresses) {
			InetAddress inetAddress;
			try {
				inetAddress = InetAddress.getByName(ipAddress);
				if (openConnection(inetAddress, config)) {
					handlerStatus.setCapacityIpAddressConnected(this.socket);
					return true;
				}
			}
			catch (UnknownHostException e) {
				log.error("Failed finding host for: " + ipAddress);
				handlerStatus.addCapacityIpAddressFailure(ipAddress);
			}
		}
		return false;
	}

	protected boolean openConnection(InetAddress ipAddress, SessionConfig config) {
		InetSocketAddress endpoint = new InetSocketAddress(ipAddress, NetworkConstants.DIRECT_PORT);
		socket = new Socket();
		if (config.getLocalSocket() != null) {
			try {
				socket.setReuseAddress(true);
				socket.bind(config.getLocalSocket());
			}
			catch (IOException e) {
				log.warn("Cannot bind to local socket: " + config.getLocalSocket(), e);
			}
		}
		try {
			socket.connect(endpoint, config.getConnectionTimeout());
			// log.info("local" + socket.getLocalSocketAddress());
			Integer socketSize = config.getSocketSize();
			if (socketSize != null && socketSize > 0) {
				int oldSocketSize = socket.getReceiveBufferSize();
				socket.setReceiveBufferSize(socketSize);
				log.debug("Socket before: " + oldSocketSize + ", after: " + socket.getReceiveBufferSize());
			}
			return true;
		}
		catch (SocketTimeoutException e) {
			log.warn("Failed connecting to: " + endpoint + " from: " + config.getLocalSocket());
		}
		catch (IOException e) {
			log.warn("Failed connecting to: " + endpoint + " from: " + config.getLocalSocket(), e);
		}
		return false;
	}

}
