/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openymsg.network;

/**
 * Using a circular list, this class allows processes to be scheduled to run,
 * with moderate accuracy. To work as desired, processes must be in
 * chronological order on the list.
 * 
 * This class implements its circular list using a bydirectional chain. The list
 * contains two heads, one for reading and one for (shock horror!) writing. The
 * read head wraps around to the first object once it runs of the end of the
 * list. Note: head == null : List is empty head.next == head.previous : List
 * has only one element head.next != head.previous : List has more than one
 * element
 * 
 * Each process is pulled off in turn. If the process isn't close to being
 * executed (say, within a 10th of a second) then the thread sleeps for the
 * required amount of time. When the time is right, the run() method is called
 * on the node, and a fixed amount of time is added to the next scheduled time.
 * 
 * Because each instance of PeriodicExecuter deals with Runnable's executing at
 * the same fixed rate, we know that nodes cannot swap places on the list.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
class PeriodicExecuter implements Runnable {
	private boolean quitFlag = false; // Exit execute thread

	private long period; // Period between executions

	private Node readHead, writeHead; // Circular list read and write heads

	private final static long CLOSE_ENOUGH = 100;

	PeriodicExecuter(long p) {
		period = p;
		readHead = null;
		writeHead = null;
	}

	/**
	 * Add or remove and object
	 */
	synchronized void add(Runnable o) {
		Node newNode = new Node(o, period);
		if (writeHead == null) {
			// Add to empty list
			writeHead = newNode;
			readHead = newNode; // Both heads set to single node
			newNode.next = newNode; // Node points to self
			newNode.previous = newNode;
		} else {
			// Add to populated list
			Node before = writeHead; // Insert after this 'before' node
			Node after = writeHead.next; // Insert before this 'after' node
			before.next = newNode;
			newNode.previous = before;
			after.previous = newNode;
			newNode.next = after;
			writeHead = newNode; // Head moved to new
		}
		// If the list is empty, the run() method might be waiting for a
		// signal to start pulling Nodes off the list. Strictly speaking
		// we could put this code in the writeHead==null block above, but
		// just to be on the safe side we'll always notify run().
		notifyAll();
	}

	synchronized Runnable remove(Runnable o) {
		// Step zero: empty list?
		if (readHead == null)
			return null;
		// Step one: find
		Node start = readHead, current = start;
		loop1: do {
			if (current.value == o) {
				break loop1;
			}
			current = current.next;
		} while (current != start);
		// Step two: did we find it?
		if (current.value != o)
			return null;
		// Step three: uncouple it
		if (current == current.next) {
			// Only one node in list
			readHead = null;
			writeHead = null;
			return current.value;
		}
		current.next.previous = current.previous;
		current.previous.next = current.next;
		if (readHead == current)
			readHead = current.next;
		if (writeHead == current)
			writeHead = current.next;
		return current.value;
	}

	/**
	 * Read or peek at heads
	 */
	synchronized Node read() {
		Node n = readHead; // Remember current
		if (readHead != null)
			readHead = readHead.next; // Advance to next
		return n; // Return old current
	}

	@Override
	public String toString() {
		if (readHead == null)
			return "[Empty]";
		StringBuffer sb = new StringBuffer();
		Node start = readHead, current = start;
		do {
			if (current == readHead)
				sb.append("[R]");
			if (current == writeHead)
				sb.append("[W]");
			sb.append(current.value).append(", ");
			current = current.next;
		} while (current != start);
		return sb.toString();
	}

	static private class Node {
		Node next, previous; // Links for circular list

		long nextExecutionTime; // Time of preferred execution

		Runnable value; // Runnable to execute

		long period; // Execution gap period

		Node(Runnable o, long p) {
			value = o;
			period = p;
			nextExecutionTime = System.currentTimeMillis() + period;
		}
	}

	/**
	 * Thread code
	 */
	public void run() {
		while (!quitFlag) {
			// Read the next object on the list
			Node n = read();
			if (n != null) {
				// When is it scheduled to run?
				long diff = n.nextExecutionTime - System.currentTimeMillis();
				// If it's more than a short time away, put this thread to sleep
				// for the required length of time. (Note: this condition check
				// will
				// also be false if the execution time has passed.)
				if (diff > CLOSE_ENOUGH) {
					try {
						Thread.sleep(diff);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				// Peform the task, then move the timer on for next execution.
				n.value.run();
				n.nextExecutionTime += period;
			} else {
				// There's nothing on the list. Wait until the add node method
				// notifies us of something to do.
				synchronized (this) {
					if (!quitFlag) {
						try {
							wait();
						} catch (InterruptedException e) {
							// ignore
						}
					}
				}
			}
		}
	}
}
