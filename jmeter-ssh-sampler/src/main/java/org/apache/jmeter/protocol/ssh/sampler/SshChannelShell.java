package org.apache.jmeter.protocol.ssh.sampler;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class SshChannelShell {
	private static final Logger log = LoggingManager.getLoggerForClass();
	ChannelShell cShell = null;
	public void setChannelShell(ChannelShell cs) 
	{
		this.cShell=cs;
	}
	public ChannelShell getChannelShell() {
		return this.cShell;
	}
	public void disconnect() {
		try {
			this.cShell.disconnect();
		}
		catch(Exception e) {}
	}
}
