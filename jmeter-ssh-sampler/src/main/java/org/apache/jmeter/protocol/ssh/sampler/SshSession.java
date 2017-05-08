/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.jmeter.protocol.ssh.sampler;


import com.jcraft.jsch.Session;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;

public class SshSession {
	private static final Logger log = LoggingManager.getLoggerForClass();
	Session session = null;
	ConcurrentHashMap<String, SshChannelShell> shellChannels = new ConcurrentHashMap<String, SshChannelShell>();
	ConcurrentHashMap<String, SshChannelSFTP> sftpChannels = new ConcurrentHashMap<String, SshChannelSFTP>();
	public SshSession(Session sess) {
		this.session = sess;
	}
	public Session getSession() {
		return session;
	}
	public void disconnectAllChannelShells() {
		synchronized (shellChannels) {
			Iterator<Map.Entry<String, SshChannelShell>> shellChannelIterator;

			shellChannelIterator = this.shellChannels.entrySet().iterator();

			while (shellChannelIterator.hasNext()) {
				Map.Entry<String, SshChannelShell> entry = shellChannelIterator.next();
				try {
					// String st = Thread.currentThread().getName() + " - [" +
					// entry.getKey() + ", " + entry.getValue() + ']';
					String shellChannelNameFromList = entry.getKey();
					SshChannelShell cShell = entry.getValue();
					cShell.disconnect();
					cShell = null;
					// this thread should not suffer from it be other threads may still have references 
					// so their operations may fail if the shell is closed here
					shellChannels.remove(shellChannelNameFromList);
					log.debug("Session "+shellChannelNameFromList+" successfully closed");
				} catch (Exception e) {
					// e.printStackTrace();
					log.debug("excpetion in closeAllChannelShells:" + e.getMessage());
				}
			}
		}
	}
	public void disconnectAllSftpChannels() {
		synchronized (sftpChannels) {
			Iterator<Map.Entry<String, SshChannelSFTP>> sftpChannelIterator;

			sftpChannelIterator = this.sftpChannels.entrySet().iterator();

			while (sftpChannelIterator.hasNext()) {
				Map.Entry<String, SshChannelSFTP> entry = sftpChannelIterator.next();
				try {
					// String st = Thread.currentThread().getName() + " - [" +
					// entry.getKey() + ", " + entry.getValue() + ']';
					String sftpChannelNameFromList = entry.getKey();
					SshChannelSFTP cSftp = entry.getValue();
					cSftp.disconnect();
					cSftp = null;
					// this thread should not suffer from it be other threads may still have references 
					// so their operations may fail if the shell is closed here
					shellChannels.remove(sftpChannelNameFromList);
					log.debug("Session "+sftpChannelNameFromList+" successfully closed");
				} catch (Exception e) {
					// e.printStackTrace();
					log.debug("excpetion in closeAllChannelShells:" + e.getMessage());
				}
			}
		}
	}
	/* closes the ssh session and closes and removes all channels */
	public synchronized void disconnect() {
		this.disconnectAllChannelShells();
		this.disconnectAllSftpChannels();
		if (session !=null ) {
			try {
				session.disconnect();
			}
			catch(Exception e) {
				log.debug("excpetion in ssh session disconnect:" + e.getMessage());
			}
			session=null;
		}
	}

	public void addChannelShell(String cName, SshChannelShell cs) {
		this.shellChannels.put(cName, cs);
	}
	public void removeChannelShell(String cName)
	{
		try
		{
			this.shellChannels.remove(cName);
		}
		catch(Exception e) {
			log.debug("exception when removing Shell Channel "+cName);
		}
	}
	public SshChannelShell getChannelShellByName(String cName) {
		SshChannelShell ses = null;
		try {
			ses = shellChannels.get(cName);
		} catch (NullPointerException e) {
			log.error("Nullpointerexception in GetSessionByName:" + e.getMessage());
			return null;
		}
		return ses;

	}
	
	public void addChannelSftp(String cName, SshChannelSFTP csftp) {
		this.sftpChannels.put(cName, csftp);
	}
	public void removeChannelSftp(String cName)
	{
		try
		{
			this.sftpChannels.remove(cName);
		}
		catch(Exception e) {
			log.debug("exception when removing Sftp Channel "+cName);
		}
	}
	public SshChannelSFTP getChannelSftpByName(String csftpName) {
		SshChannelSFTP ses = null;
		try {
			ses = sftpChannels.get(csftpName);
		} catch (NullPointerException e) {
			log.error("Nullpointerexception in GetSessionByName:" + e.getMessage());
			return null;
		}
		return ses;

	}

	public synchronized String GetChannelShellList(String csName) {
		// start mutex here
		// synchronized (sessionList) {
		Iterator<Map.Entry<String, SshChannelShell>> shellChannelIterator;
		StringBuilder sb = new StringBuilder();

		shellChannelIterator = this.shellChannels.entrySet().iterator();
		boolean searchSpecific;
		if (csName.equals("")) {
			searchSpecific = false;
		} else {
			searchSpecific = true;
		}
		while (shellChannelIterator.hasNext()) {
			Map.Entry<String, SshChannelShell> entry = shellChannelIterator.next();
			try {
				// String st = Thread.currentThread().getName() + " - [" +
				// entry.getKey() + ", " + entry.getValue() + ']';
				String shellChannelNameFromList = entry.getKey();
				if (searchSpecific == false || csName.equals(shellChannelNameFromList)) {
					sb.append(shellChannelNameFromList);
					if (shellChannelIterator.hasNext()) {
						sb.append(",");
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
				return "excpetion in GetChannelShellList:" + e.getMessage();
			}

		}
		// returning will release the lock no problem
		return sb.toString();
		// }
	}
	public synchronized String GetChannelSftpList(String csftpName) {
		// start mutex here
		// synchronized (sessionList) {
		Iterator<Map.Entry<String, SshChannelSFTP>> sftpChannelIterator;
		StringBuilder sb = new StringBuilder();

		sftpChannelIterator = this.sftpChannels.entrySet().iterator();
		boolean searchSpecific;
		if (csftpName.equals("")) {
			searchSpecific = false;
		} else {
			searchSpecific = true;
		}
		while (sftpChannelIterator.hasNext()) {
			Map.Entry<String, SshChannelSFTP> entry = sftpChannelIterator.next();
			try {
				// String st = Thread.currentThread().getName() + " - [" +
				// entry.getKey() + ", " + entry.getValue() + ']';
				String sftpChannelNameFromList = entry.getKey();
				if (searchSpecific == false || csftpName.equals(sftpChannelNameFromList)) {
					sb.append(sftpChannelNameFromList);
					if (sftpChannelIterator.hasNext()) {
						sb.append(",");
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
				return "excpetion in GetChannelSftpList:" + e.getMessage();
			}

		}
		// returning will release the lock no problem
		return sb.toString();
		// }
	}
}
