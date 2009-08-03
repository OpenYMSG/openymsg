package ymsg.network;

import java.io.*;

class DebugOutputStream extends FilterOutputStream
{	ByteArrayOutputStream baos;

	public DebugOutputStream(OutputStream os)
	{	super(os);
		baos = new ByteArrayOutputStream(1024);
	}
	
	public void write(int b) throws IOException
	{	super.write(b);  baos.write(b);
	}
	
	public void write(byte[] b) throws IOException
	{	super.write(b);  // baos.write(b);  Calls write(byte[],int,int)
	}
	
	public void write(byte[] b,int off,int len) throws IOException
	{	super.write(b,off,len);  //baos.write(b,off,len);  Calls write(int)
	}
	
	public void flush() throws IOException
	{	super.flush();  Util.dump(baos.toByteArray(),"\n--OUTPUT-- " + System.currentTimeMillis());  baos.reset();
	}
	
	public void close() throws IOException
	{	super.close();  baos.close();
	}
}
