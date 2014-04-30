package org.openymsg.network;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTesting {
	private static final byte[] MAGIC = { 'Y', 'M', 'S', 'G' };
	static ServerSocket serverSocket;
	static Socket socket;

	public static final void main(String[] args) {
		new SocketTesting();
	}

	public SocketTesting() {
		byte[] value = new byte[1000000]; // blocks on write
		for (int i = 0; i < value.length; i++) {
			value[i] = 'T';
		}
		try {
			serverSocket = new ServerSocket(20000);
			socket = new Socket("localhost", 20000);
			DataOutputStream ops = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			Checker checker = new Checker();
			Thread thread = new Thread(checker);
			thread.start();

			System.out.println("1");
			ops.write(value);
			System.out.println("2");
			ops.flush();
			System.out.println("3");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class Checker implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("closing");
				socket.close();
				System.out.println("closed");
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
