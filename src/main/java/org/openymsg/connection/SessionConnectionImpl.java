package org.openymsg.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.connection.read.MultiplePacketResponse;
import org.openymsg.connection.read.PacketReaderImpl;
import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.connection.write.Message;
import org.openymsg.connection.write.PacketWriterImpl;
import org.openymsg.execute.Executor;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ConnectionHandlerCallback;
import org.openymsg.network.ServiceType;

public class SessionConnectionImpl implements YahooConnection, ConnectionHandlerCallback {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionConnectionImpl.class);
	private Executor executor;
	private ConnectionState state;
	private ConnectionInfo status;
	private SessionConnectionCallback callback;
	private PacketWriterImpl writer;
	private PacketReaderImpl reader;
	private ConnectionHandler connection;

	public SessionConnectionImpl(Executor executor, SessionConnectionCallback callback) {
		this.executor = executor;
		this.callback = callback;
		this.state = ConnectionState.UNSTARTED;
		this.writer = new PacketWriterImpl(this.executor);
		this.reader = new PacketReaderImpl(this.executor);
	}

	/**
	 * initialize the connection based on the config. Config options should not change
	 * @param sessionConfig configuration
	 */
	public void initialize(SessionConfig sessionConfig) throws IllegalArgumentException {
		if (sessionConfig == null) {
			throw new IllegalArgumentException("sessionConfig must not be null");
		}
		// TODO - maybe needed for restart?
		// ConnectionState state = this.getConnectionState();
		// if (state.isStartable()) {
		this.setState(ConnectionState.CONNECTING);
		this.executor.execute(new ConnectionInitalize(sessionConfig, this));
	}

	@Override
	public ConnectionState getConnectionState() {
		return state;
	}

	@Override
	public ConnectionInfo getConnectionInfo() {
		return status;
	}

	protected void setState(ConnectionState state) {
		this.state = state;
		if (this.state == ConnectionState.CONNECTED) {
			callback.connectionSuccessful();
		} else if (this.state == ConnectionState.FAILED_CONNECTING) {
			callback.connectionFailure();
		} else if (this.state == ConnectionState.FAILED_AFTER_CONNECTED) {
			callback.connectionPrematurelyEnded();
		}
	}

	protected void setState(ConnectionState state, ConnectionInfo status) {
		this.status = status;
		this.setState(state);
	}

	@Override
	public void connectionEnded() {
		if (this.state != ConnectionState.DISCONNECTING) {
			this.setState(ConnectionState.FAILED_AFTER_CONNECTED);
		} else {
			log.warn("got connection ended with state: " + this.state);
		}
	}

	@Override
	public void shutdown() {
		log.info("Shutting down everything");
		this.state = ConnectionState.DISCONNECTING;
		this.reader.shutdown();
		this.writer.shutdown();
		this.connection.shutdown();
		this.executor.shutdown();
	}

	@Override
	public void register(ServiceType type, SinglePacketResponse response) {
		this.reader.register(type, response);
	}

	@Override
	public boolean deregister(ServiceType type, SinglePacketResponse response) {
		return this.reader.deregister(type, response);
	}

	@Override
	public void register(ServiceType type, MultiplePacketResponse response) {
		this.reader.register(type, response);
	}

	@Override
	public boolean deregister(ServiceType type, MultiplePacketResponse response) {
		return this.reader.deregister(type, response);
	}

	public void initializeConnection(ConnectionHandler connection) throws IllegalStateException {
		if (!this.state.isStartable()) {
			// TODO this isn't quite right
			throw new IllegalStateException("Connection was already set state: " + state + ", connection: "
					+ connection);
		}
		this.state = ConnectionState.CONNECTED;
		this.connection = connection;
		this.reader.initializeConnection(connection);
		this.writer.initializeConnection(connection);
	}

	@Override
	public void execute(Message message) {
		this.writer.execute(message);
	}

}
