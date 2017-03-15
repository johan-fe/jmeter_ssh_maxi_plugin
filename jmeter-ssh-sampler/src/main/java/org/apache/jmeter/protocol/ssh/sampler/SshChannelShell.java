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

	protected byte[] appendData(byte[] firstObject, byte[] secondObject, int lengthSecond) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			if (firstObject != null && firstObject.length != 0)
				outputStream.write(firstObject);
			if (secondObject != null && lengthSecond != 0)
				outputStream.write(secondObject,0,lengthSecond );
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputStream.toByteArray();
	}
	public byte[] readResponse(boolean stripCommand, String command, boolean stripPrompt, String encoding ) throws Exception
	{
		String result=new String(readResponseBytes(), encoding);
		if (stripCommand==true)
		{
			//remove command if at first position and immediately followed by CR/LF LF/Cr CR LF
			int indexLF=result.indexOf("\n");
			int indexCR=result.indexOf("\r");
			int maxindex=Math.max(indexLF,indexCR);
			log.info("indexLF:"+Integer.toString(indexLF));
			log.info("indexCR:"+Integer.toString(indexLF));
			log.info("command length:"+command.length());
			int commandIndex=result.indexOf(command);
			int commandLength=command.length();
			int stripLength=0;
			if (commandIndex==0)
			{
				if(indexLF==commandLength+1)
				{
					stripLength=commandLength+2;
					if(indexCR==commandLength+2) {
						stripLength++;
					}
				}
				else if (indexCR==commandLength+1)
				{
					stripLength=commandLength+2;
					if(indexLF==commandLength+2) {
						stripLength++;
					}
				}
				result=result.substring(stripLength);
			}
		}
		//todo handle special case command with eempty response just prompt returned
		if(stripPrompt)
		{
			int lastIndexCR=result.lastIndexOf("\r");
			int lastIndexLF=result.lastIndexOf("\n");
			int maxLastIndex=Math.max(lastIndexCR, lastIndexLF);
			int stripIndex=result.length();
			if (maxLastIndex > 0)
			{
				if (lastIndexCR > lastIndexLF)
				{
					if(lastIndexLF==lastIndexCR-1)
						stripIndex=lastIndexLF;
					else
						stripIndex=lastIndexCR;
				}
				else
				{
					if(lastIndexCR==lastIndexLF-1)
						stripIndex=lastIndexCR;
					else
						stripIndex=lastIndexLF;
				}
				result=result.substring(0, stripIndex);
				//promptregex toevoegen
			}

		}
		return result.getBytes();
	}
	private byte[] readResponseBytes() {
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
					result2 = appendData(result, tmp, i);
					result=result2;
					//log.debug("while: "+Integer.toString(i)+"==="+new String(tmp, "UTF-8")+"\n=====");
				}
			} catch (Exception e) {
				//log.info("(while)Exception in SSHChannelShell:"+e.getMessage());
				return result;// TODO throw exception
			}
			if (cShell.isClosed()) {
				try {
					while (in.available() > 0) {
						int i = in.read(tmp, 0, 1024);
						result2 = appendData(result, tmp,i);
						result=result2;
						log.debug("shell closed while: "+new String(result,  "UTF-8"));
					}
				} catch (Exception e) {
					//log.info("(if closed)Exception in SSHChannelShell:"+e.getMessage());
					return result;// TODO throw exception
				}
				count = 5;
			}
			try {
				Thread.sleep(200);
				count = count - 1;
			} catch (Exception e) {
				//log.info("(thread sleep)Exception in SSHChannelShell:"+e.getMessage());
				return result;//TODO throw exception 
			}
		}
		return result;
	}
}
