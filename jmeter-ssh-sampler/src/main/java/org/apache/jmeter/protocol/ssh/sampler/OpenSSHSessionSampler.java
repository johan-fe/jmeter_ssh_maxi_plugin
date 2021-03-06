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

import java.io.Serializable;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * Open SSH session sampler, This sampler establishes a persistent named SSH
 * session towards the SSH server
 */
public class OpenSSHSessionSampler extends AbstractSSHMainSampler implements TestBean {
	private static final long serialVersionUID = 2954789087756886583L;
	private static final Logger log = LoggingManager.getLoggerForClass();
	private String hostname = "";
	private int port = 22;
	private String username = "";
	private String password = "";
	private String sshkeyfile = "";
	private String passphrase = "";
	private int connectionTimeout = 5000;

	private String tunnels = "";
	private String connectionName = GlobalDataSsh.getNewConnectionName();

	private int sessionKeepAliveSeconds = 7200;

	private String failureReason = "Unknown";
	private static final JSch jsch = new JSch();
	private Session session = null;
	private SSHSamplerUserInfo userinfo = null;

	public OpenSSHSessionSampler() {
		super("OpenSSHSessionSampler");
		this.userinfo = new SSHSamplerUserInfo(this);

		// following generates a default connection name
		// sometimes fails if the bean info has not initialized the property
		if (this.getPropertyAsString("connectionName", "").equals("")) {
			this.setProperty("connectionName", this.getConnectionName());
		} else {
			// TODO Iterate over these samplers to see if there is no duplicate
			// e.g when dupicating sampler in gui
		}
	}

	// open the configured tunnels on the connection format
	// L1234:123.234.45.67:89098;R456:123.6.7.8:8900
	public void setTunnels(Session s) {
		String trimmedTunnelString = this.tunnels.trim().replaceAll(" ", "");
		if (trimmedTunnelString.equals(""))
			return;
		String[] tunnelArr = trimmedTunnelString.split(";");
		int index = 0;
		while (tunnelArr.length > index) {
			setSingleTunnel(s, tunnelArr[index]);
			index++;
		}

	}

	public void setSingleTunnel(Session s, String tunnel) {
		boolean localForwarding = false;
		boolean remoteForwarding = false;
		String utunnel = tunnel.toUpperCase();
		if (utunnel.startsWith("L"))
			localForwarding = true;
		if (utunnel.startsWith("R"))
			remoteForwarding = true;
		// strip local or remote char
		String sutunnel = utunnel.substring(1);
		String[] splitTunnel = sutunnel.split(":");
		int port1, port2 = 0;
		String tunnelRemoteHost = splitTunnel[1];
		if (splitTunnel.length != 3) {
			log.error("invalid tunnel format on:" + tunnel);
			return;
		}
		try {
			port1 = Integer.parseInt(splitTunnel[0]);
		} catch (NumberFormatException e) {
			log.error("invalid source port " + splitTunnel[0] + " on:" + tunnel);
			return;
		}
		try {
			port2 = Integer.parseInt(splitTunnel[2]);
		} catch (NumberFormatException e) {
			log.error("invalid destination port " + splitTunnel[2] + " on:" + tunnel);
			return;
		}
		try {
			if (localForwarding == true) {
				log.info("setting up local forwarding for port " + Integer.toString(port1) + " " + tunnelRemoteHost
						+ " " + Integer.toString(port2) + " " + tunnel);
				session.setPortForwardingL(port1, tunnelRemoteHost, port2);
			} else if (remoteForwarding == true) {
				log.info("setting up remote forwarding for port " + Integer.toString(port1) + " " + tunnelRemoteHost
						+ " " + Integer.toString(port2) + " " + tunnel);
				session.setPortForwardingR(port1, tunnelRemoteHost, port2);
			}
		} catch (Exception e) {
			log.error("failed to set up tunnel:" + tunnel);
			return;
		}
	}

	@Override
	public SampleResult sample(Entry en) {
		SampleResult res = new SampleResult();

		String samplerData = "Open SSH connection (" + this.getUsername() + "@" + this.getHostname() + ") with name: "
				+ this.connectionName;
		// String responseData = "";
		String responseMessage = "";
		// String responseCode = "";
		res.setDataType(SampleResult.TEXT);
		res.setContentType("text/plain");
		res.sampleStart();
		try {
			failureReason = "Unknown";
			session = jsch.getSession(getUsername(), getHostname(), getPort());
			// session.setPassword(getPassword()); // Use a userinfo instead
			if (userinfo.hasConnectionName() && session != null) {
				SshSession sshS = new SshSession(session);
				GlobalDataSsh.addSession(connectionName, sshS);
			}
			session.setUserInfo(userinfo);
			if (userinfo.useKeyFile()) {
				jsch.addIdentity(getSshkeyfile());
			}

			session.setConfig("StrictHostKeyChecking", "no");

			session.setServerAliveInterval(this.sessionKeepAliveSeconds * 1000); // must
																					// be
																					// called
																					// before
																					// connect
			session.connect(connectionTimeout);
			this.setTunnels(session);
		} catch (JSchException e) {
			failureReason = e.getMessage();
			session.disconnect();
			session = null;
			log.error("SSH connection error", e);
			responseMessage = "Connection Failed:" + failureReason;
			res.setSampleLabel(getName() + " Connect (" + this.getUsername() + "@" + this.getHostname() + ") failed");
			res.setResponseCode("-1");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		responseMessage = "Connected";
		res.setSampleLabel(getName() + " Connect (" + this.getUsername() + "@" + this.getHostname() + ")");
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setResponseData(responseMessage, "UTF-8");
		res.setResponseMessage(responseMessage);
		res.sampleEnd();
		return res;
	}

	// Accessors
	public void setSshkeyfile(String sshKeyFile) {
		this.sshkeyfile = sshKeyFile;
	}

	public String getSshkeyfile() {
		return sshkeyfile;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String server) {
		this.hostname = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	protected Session getSession() {
		return session;
	}

	protected void setSession(Session session) {
		this.session = session;
	}

	protected String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public String getTunnels() {
		return this.tunnels;
	}

	public void setTunnels(String tun) {
		this.tunnels = tun;
	}

	public void setConnectionName(String conn) {
		this.connectionName = conn;
	}

	public String getConnectionName() {
		return this.connectionName;
	}

	public void setSessionKeepAliveSeconds(int ska) {
		this.sessionKeepAliveSeconds = ska;
	}

	public int getSessionKeepAliveSeconds() {
		return this.sessionKeepAliveSeconds;
	}

	@Override
	public void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			log.error("SSH finalize error", e);
		} finally {
			if (session != null) {
				// TODO if not in sessionslist disconnect
				session.disconnect();
				session = null;
			}
		}
	}

	/**
	 * A private implementation of com.jcraft.jsch.UserInfo. This takes a
	 * AbstractSSHSampler when constructed and looks over its data when queried
	 * for information. This should only be visible to the SSH Sampler class.
	 */
	private class SSHSamplerUserInfo implements UserInfo, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5142236074798800100L;

		private OpenSSHSessionSampler owner;

		public SSHSamplerUserInfo(OpenSSHSessionSampler owner) {
			this.owner = owner;
		}

		@Override
		public String getPassphrase() {
			String retval = owner.getPassphrase();
			if ((retval.length() == 0) && !useKeyFile()) {
				retval = null;
			}
			return retval;
		}

		@Override
		public String getPassword() {
			String retval = owner.getPassword();
			if (retval.length() == 0) {
				retval = null;
			}
			return retval;
		}

		/* Prompts/show should be taken care of by Jmeter */
		@Override
		public boolean promptPassword(String message) {
			return true;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return true;
		}

		@Override
		public boolean promptYesNo(String message) {
			return true;
		}

		@Override
		public void showMessage(String message) {
			return;
		}

		/*
		 * These are not part of the UserInfo interface, but since this object
		 * can inspect its owner's data it seems cleaner to just ask it to
		 * figure out what the user wants to do, rather than cluttering the
		 * sampler code with this.
		 */
		/**
		 * useKeyFile returns true if owner.sshkeyfile is not empty
		 */
		public boolean useKeyFile() {
			return owner.getSshkeyfile().length() > 0;
		}

		public boolean hasConnectionName() {
			if (owner.getConnectionName() != null) {
				return owner.getConnectionName().length() > 0;
			}
			return false;

		}
	} /* Class SSHSamplerUserInfo */

}
