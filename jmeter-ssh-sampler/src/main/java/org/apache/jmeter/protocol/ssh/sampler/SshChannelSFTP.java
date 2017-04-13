package org.apache.jmeter.protocol.ssh.sampler;

import com.jcraft.jsch.ChannelSftp;
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

public class SshChannelSFTP {
	private static final Logger log = LoggingManager.getLoggerForClass();
	ChannelSftp cSFTP = null;
 

	public void setChannelSftp(ChannelSftp cs) {
		this.cSFTP = cs;
	}

	public ChannelSftp getChannelSftp() {
		return this.cSFTP;
	}

	public void disconnect() {
 

		try {
			if (cSFTP != null) {
				this.cSFTP.disconnect();
			}
		} catch (Exception e) {
			Log.debug("Error when disconnecting Channel SFTP");
		}
	}
 

	public boolean isConnected() {
		return this.cSFTP.isConnected();
	}
 

}
