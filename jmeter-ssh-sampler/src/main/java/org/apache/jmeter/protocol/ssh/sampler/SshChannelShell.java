package org.apache.jmeter.protocol.ssh.sampler;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;

import jline.internal.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class SshChannelShell {
	private static final Logger log = LoggingManager.getLoggerForClass();
	ChannelShell cShell = null;
	InputStream in = null;
	PrintStream pOut = null;

	public void setChannelShell(ChannelShell cs) {
		this.cShell = cs;
	}

	public ChannelShell getChannelShell() {
		return this.cShell;
	}

	public void disconnect() {
		try {
			if(this.in !=null)
			{
				this.in.close();
			}
		}
		catch(Exception e) {
			//
		}
		try {
			if(this.pOut!=null)
			{
				this.pOut.close();
			}
		}
		catch(Exception e) {
			//
		}
		try {
			if (cShell != null) {
				this.cShell.disconnect();
			}
		} catch (Exception e) {
			Log.debug("Error when disconnecting Channel Shell");
		}
	}

	public void setInputStream(InputStream br) {
		this.in = br;
	}

	public InputStream getInputStream() {
		return this.in;
	}

	public PrintStream getpOutputStream() {
		return pOut;
	}

	public void setpOutputStream(PrintStream pOut) {
		this.pOut = pOut;
	}

	public boolean isConnected() {
		return this.cShell.isConnected();
	}

	public void sendCommand(String command) {
		this.pOut.print(command + "\n");
	}

	protected byte[] appendData(byte[] firstObject, byte[] secondObject) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			if (firstObject != null && firstObject.length != 0)
				outputStream.write(firstObject);
			if (secondObject != null && secondObject.length != 0)
				outputStream.write(secondObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputStream.toByteArray();
	}

	public byte[] readResponse() {
		int count = 5;
		byte[] tmp = new byte[1024];
		byte[] result = {};
		byte[] result2= {};
		while (count > 0) {
			try {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) {
						count = 5;
						break;
					}
					result2 = appendData(result, tmp);
					result=result2;
					log.debug("while: "+result.toString());
				}
			} catch (Exception e) {
				log.info("(while)Exception in SSHChannelShell:"+e.getMessage());
				return result;// TODO throw exception
			}
			if (cShell.isClosed()) {
				try {
					while (in.available() > 0) {
						int i = in.read(tmp, 0, 1024);
						result2 = appendData(result, tmp);
						result=result2;
						log.debug("shell closed while: "+result.toString());
					}
				} catch (Exception e) {
					log.info("(if closed)Exception in SSHChannelShell:"+e.getMessage());
					return result;// TODO throw exception
				}
				count = 5;
			}
			try {
				Thread.sleep(200);
				count = count - 1;
			} catch (Exception e) {
				log.info("(thread sleep)Exception in SSHChannelShell:"+e.getMessage());
				return result;//TODO throw exception 
			}
		}
		return result;
	}
}
