package ymsg.network;

import java.io.*;

class DebugInputStream extends FilterInputStream
{	ByteArrayOutputStream baos;

	public DebugInputStream(InputStream is)
	{	super(is);
		baos = new ByteArrayOutputStream(4096);
	}
	
	public int read() throws IOException
	{	int b = super.read();
		baos.write(b);
		return b;
	}
	
	public int read(byte[] b) throws IOException
	{	int l = super.read(b);
		if(l>0) baos.write(b,0,l);
		return l;
	}
	
	public int read(byte[] b,int off,int len) throws IOException
	{	int l = super.read(b,off,len);
		if(l>0) baos.write(b,off,l);
		return l;
	}
	
	public void debugDump()
	{	Util.dump(baos.toByteArray(),"\n--INPUT--");  baos.reset();
	}

	public void debugReset()
	{	baos.reset();
	}
}
