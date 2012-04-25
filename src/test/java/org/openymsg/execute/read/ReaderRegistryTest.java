package org.openymsg.execute.read;

import junit.framework.Assert;

import org.openymsg.contact.status.ListOfStatusesResponse;
import org.openymsg.network.ServiceType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ReaderRegistryTest {
	private ReaderRegistry registry;

	@BeforeMethod
	public void beforeMethod() {
		registry = new ReaderRegistryImpl();
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "response may not be null")
	public void testRegisterSingleNoResponse() {
		ServiceType type = ServiceType.ADDIDENT;
		SinglePacketResponse response = null;
		registry.register(type, response);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "type may not be null")
	public void testRegisterSingleNoServiceType() {
		ServiceType type = null;
		SinglePacketResponse response = new NoOpResponse();
		registry.register(type, response);
	}

	@Test
	public void testRegisterSingleLifeCycle() {
		ServiceType type = ServiceType.ADDIDENT;
		SinglePacketResponse response = new NoOpResponse();
		registry.register(type, response);
		boolean answer = registry.deregister(type, response);
		Assert.assertEquals(true, answer);
	}

	@Test
	public void testRegisterSingleFailDeregister() {
		ServiceType type = ServiceType.ADDIDENT;
		SinglePacketResponse response = new NoOpResponse();
		registry.register(type, response);
		boolean answer = registry.deregister(type, new NoOpResponse());
		Assert.assertEquals(false, answer);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "response may not be null")
	public void testRegisterMultiNoResponse() {
		ServiceType type = ServiceType.ADDIDENT;
		MultiplePacketResponse response = null;
		registry.register(type, response);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "type may not be null")
	public void testRegisterrMultiNoServiceType() {
		ServiceType type = null;
		MultiplePacketResponse response = new ListOfStatusesResponse(null);
		registry.register(type, response);
	}

	@Test
	public void testRegisterrMultiLifeCycle() {
		ServiceType type = ServiceType.ADDIDENT;
		MultiplePacketResponse response = new ListOfStatusesResponse(null);
		registry.register(type, response);
		boolean answer = registry.deregister(type, response);
		Assert.assertEquals(true, answer);
	}

	@Test
	public void testRegisterrMultiFailDeregister() {
		ServiceType type = ServiceType.ADDIDENT;
		MultiplePacketResponse response = new ListOfStatusesResponse(null);
		registry.register(type, response);
		boolean answer = registry.deregister(type, new NoOpResponse());
		Assert.assertEquals(false, answer);
	}

}
