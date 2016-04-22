package org.openymsg.connection.read;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openymsg.contact.status.response.ListOfStatusesResponse;
import org.openymsg.network.ServiceType;

public class ReaderRegistryTest {
	private ReaderRegistry registry;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void beforeMethod() {
		registry = new ReaderRegistryImpl();
	}

	@Test()
	public void testRegisterSingleNoResponse() {
		ServiceType type = ServiceType.ADDIDENT;
		SinglePacketResponse response = null;
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("response may not be null");
		registry.register(type, response);
	}

	@Test()
	public void testRegisterSingleNoServiceType() {
		ServiceType type = null;
		SinglePacketResponse response = new NoOpResponse();
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("type may not be null");
		registry.register(type, response);
	}

	@Test
	public void testRegisterSingleLifeCycle() {
		ServiceType type = ServiceType.ADDIDENT;
		SinglePacketResponse response = new NoOpResponse();
		registry.register(type, response);
		boolean answer = registry.deregister(type, response);
		assertEquals(true, answer);
	}

	@Test
	public void testRegisterSingleFailDeregister() {
		ServiceType type = ServiceType.ADDIDENT;
		SinglePacketResponse response = new NoOpResponse();
		registry.register(type, response);
		boolean answer = registry.deregister(type, new NoOpResponse());
		assertEquals(false, answer);
	}

	@Test()
	public void testRegisterMultiNoResponse() {
		ServiceType type = ServiceType.ADDIDENT;
		MultiplePacketResponse response = null;
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("response may not be null");
		registry.register(type, response);
	}

	@Test()
	public void testRegisterrMultiNoServiceType() {
		ServiceType type = null;
		MultiplePacketResponse response = new ListOfStatusesResponse(null);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("type may not be null");
		registry.register(type, response);
	}

	@Test
	public void testRegisterrMultiLifeCycle() {
		ServiceType type = ServiceType.ADDIDENT;
		MultiplePacketResponse response = new ListOfStatusesResponse(null);
		registry.register(type, response);
		boolean answer = registry.deregister(type, response);
		assertEquals(true, answer);
	}

	@Test
	public void testRegisterrMultiFailDeregister() {
		ServiceType type = ServiceType.ADDIDENT;
		MultiplePacketResponse response = new ListOfStatusesResponse(null);
		registry.register(type, response);
		boolean answer = registry.deregister(type, new NoOpResponse());
		assertEquals(false, answer);
	}
}
