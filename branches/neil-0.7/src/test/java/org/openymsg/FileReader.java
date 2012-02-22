package org.openymsg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.openymsg.network.YMSG9InputStream;

public class FileReader {
	public FileReader(String filename) {
		
	}
	public static final void main(String[] args) throws UnsupportedEncodingException, IOException {
//		File file = new File("/Users/neilhart/wireshark_export");
//		File file = new File("/Users/neilhart/list_v15");
		File file = new File("/Users/neilhart/change_group");
		FileInputStream stream = new FileInputStream(file);
		YMSG9InputStream ymsg = new YMSG9InputStream(stream);
		System.err.println(ymsg.readPacket());
	}

}
