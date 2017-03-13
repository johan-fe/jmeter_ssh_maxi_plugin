package org.apache.jmeter.protocol.ssh.sampler;

import com.jcraft.jsch.Session;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.jcraft.jsch.ChannelShell;

public class SshSession {
	private static final Logger log = LoggingManager.getLoggerForClass();
	Session session = null;
	ConcurrentHashMap<String, SshChannelShell> shellChannels = new ConcurrentHashMap<String, SshChannelShell>();

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
		if (session !=null ) {
			try {
				session.disconnect();
			}
			catch(Exception e) {}
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
				return "excpetion:" + e.getMessage();
			}

		}
		// returning will release the lock no problem
		return sb.toString();
		// }
	}
}
