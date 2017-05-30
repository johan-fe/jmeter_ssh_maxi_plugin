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


import java.io.InputStream;

import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelShell;
/**
 * SSHOpenPersistentShellSampler Sampler that opens a persistent Shell channel over which shell commands can be sent 
 * to the server. The Shell channel is established over a persistent SSH connection
 */
public class SSHOpenPersistentShellSampler extends AbstractSSHMainSampler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1098L;
	private String connectionName = "";
	private String shellName = "";
	private boolean useTty = false;
	private String resultEncoding = "UTF-8";
	private boolean stripPrompt = false;

	public SSHOpenPersistentShellSampler() {
		super("SSHOpenPersistentShellSampler");
		// TODO Auto-generated constructor stub
	}

	@Override
	public SampleResult sample(Entry arg0) {
		SampleResult res = new SampleResult();
		res.sampleStart();

		String samplerData = "Open shell " + this.shellName + " on " + this.connectionName;
//		String responseData = "";
		String responseMessage = "";
//		String responseCode = "";
		res.setDataType(SampleResult.TEXT);
		res.setContentType("text/plain");
		res.setSamplerData(samplerData);
		if (this.connectionName.equals("")) {
			// empty connection name
			responseMessage = "connection name is empty";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-2");
			res.setSuccessful(false);
			res.setResponseMessage(responseMessage);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}

		SshSession sshSess = GlobalDataSsh.GetSessionByName(this.connectionName);
		if (sshSess == null) {
			// ssh connection not found
			responseMessage = "connection " + this.connectionName + " not found";
			res.setResponseCode("-1");
			res.setSuccessful(false);
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		if (this.shellName.equals("")) {
			// ssh connection not found
			responseMessage = "shell name is empty";
			res.setResponseCode("-3");
			res.setSuccessful(false);
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		SshChannelShell cs = sshSess.getChannelShellByName(this.shellName);
		if (cs != null) {
			// ssh connection not found
			responseMessage = "shell with name " + this.shellName + " already exists, no new shell opened on "
					+ this.connectionName;
			res.setResponseCode("-4");
			res.setSuccessful(false);
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		// make a new shell Session
		Session sess = sshSess.getSession();
		// check if session is still open
		if (sess == null) {
			responseMessage = "severe error ssh session is null";
			res.setResponseCode("-5");
			res.setSuccessful(false);
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		if (sess.isConnected() == false) {
			responseMessage = "ssh connection with name " + this.connectionName + " is not anymore connected";
			res.setResponseCode("-6");
			res.setSuccessful(false);
			res.setResponseData(responseMessage, "UTF-8");
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		// ssh session is connected try to connect shell
		ChannelShell cShell = null;
		InputStream in = null;
		PrintStream ps = null;
		try {

			cShell = (ChannelShell) sess.openChannel("shell");
			cShell.setPty(this.useTty);
			cShell.connect();
			in = cShell.getInputStream();
			OutputStream ops = cShell.getOutputStream();
			ps = new PrintStream(ops, true);
			// TODO read from shell set label

		} catch (JSchException e1) {
			res.setSuccessful(false);
			res.setResponseCode("JSchException");
			res.setResponseMessage(e1.getMessage());
			res.setSampleLabel(getName() + " (Exception)");
			res.setResponseData("", "UTF-8");
			res.sampleEnd();
			return res;
		} /*
			 * catch (IOException e1) { res.setSuccessful(false);
			 * res.setResponseCode("IOException");
			 * res.setResponseMessage(e1.getMessage()); res.setResponseData("",
			 * "UTF-8"); res.sampleEnd(); return res; }
			 */ catch (NullPointerException e1) {
			res.setSuccessful(false);
			res.setResponseCode("Connection Failed");
			res.setSampleLabel(getName() + " (Exception)");
			res.setResponseMessage(e1.getMessage());
			res.setResponseData("", "UTF-8");
			res.sampleEnd();
			return res;
		} catch (Exception e1) {
			res.setSuccessful(false);
			res.setResponseCode("Connection Failed");
			res.setSampleLabel(getName() + " (Exception)");
			res.setResponseMessage(e1.getMessage());
			res.setResponseData("", "UTF-8");
			res.sampleEnd();
			return res;
		}
		// if successfully opened add the shell to shell collection in the ssh
		// session
		responseMessage = "Shell with name " + this.shellName + " opened on " + this.connectionName;
		res.setSampleLabel(getName() + " (" + responseMessage + ")");

		SshChannelShell sshcs = new SshChannelShell();
		sshcs.setChannelShell(cShell);
		sshcs.setInputStream(in);
		sshcs.setpOutputStream(ps);
		sshSess.addChannelShell(this.shellName, sshcs);
		byte[] responseDataBytes = {};
		try {
			responseDataBytes = sshcs.readResponse(false, "", this.stripPrompt, this.resultEncoding);
		} catch (Exception e) {
			byte[] responseDataBytes2 = {};
			res.setResponseCode("-8");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setSampleLabel(getName() + " Exception(" + e.getClass().getSimpleName() + " " + e.getMessage() + ")");
			res.setResponseData(responseDataBytes2);
			res.setResponseMessage("Exception(" + e.getClass().getSimpleName() + " " + e.getMessage() + ")");
			res.sampleEnd();
			return res;
		}
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setResponseData(new String(responseDataBytes), this.resultEncoding);
		res.setResponseMessage(responseMessage);
		res.sampleEnd();
		return res;

	}

	public void setConnectionName(String conn) {
		this.connectionName = conn;
	}

	public String getConnectionName() {
		return this.connectionName;
	}

	public void setShellName(String sh) {
		this.shellName = sh;
	}

	public String getShellName() {
		return this.shellName;
	}

	public String getResultEncoding() {
		return this.resultEncoding;
	}

	public void setResultEncoding(String resultEncoding) {
		this.resultEncoding = resultEncoding;
	}

	public boolean getStripPrompt() {
		return stripPrompt;
	}

	public void setStripPrompt(boolean stripPrompt) {
		this.stripPrompt = stripPrompt;
	}

	public boolean getUseTty() {
		return this.useTty;
	}

	public void setUseTty(boolean useTty) {
		this.useTty = useTty;
	}
}
