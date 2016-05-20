package org.openymsg.execute;

import java.util.HashSet;
import java.util.Set;

import org.openymsg.execute.dispatch.Request;
import org.openymsg.execute.dispatch.RequestWrapper;

public class ThisThreadExecutor implements Executor {
	Set<Request> scheduleRequests = new HashSet<Request>();
	Set<Request> scheduledOnceRequests = new HashSet<Request>();

	@Override
	public void execute(Request request) throws IllegalStateException {
		new RequestWrapper(request).run();
	}

	@Override
	public void schedule(Request request, long repeatInterval) throws IllegalStateException {
		scheduleRequests.add(request);
	}

	@Override
	public void scheduleOnce(Request request, long delay) throws IllegalStateException {
		scheduledOnceRequests.add(request);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<Request> getScheduleRequests() {
		return scheduleRequests;
	}

	public Set<Request> getScheduledOnceRequests() {
		return scheduledOnceRequests;
	}

}
