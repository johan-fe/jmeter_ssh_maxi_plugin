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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelShell;

public class SSHPersistentShellSendCommandSampler extends AbstractSSHMainSampler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1098L;
	private String connectionName = "";
	private String shellName = "";
	private String command = "";
	private String resultEncoding = "UTF-8";
	private boolean stripPrompt = true;
	private boolean stripCommand = true;

	private boolean useTty = false;

	public SSHPersistentShellSendCommandSampler() {
		super("SSHPersistentShellSendCommandSampler");
		// TODO Auto-generated constructor stub
	}

	@Override
	public SampleResult sample(Entry arg0) {
		SampleResult res = new SampleResult();
		res.sampleStart();

		String samplerData = "Send Command " + this.command + " to shell " + this.shellName + " on "
				+ this.connectionName;
		String responseData = "";
		String responseMessage = "";
		String responseCode = "";
		res.setDataType(SampleResult.TEXT);
		res.setContentType("text/plain");
		if (this.connectionName.equals("")) {
			// empty connection name
			responseMessage = "connection name is empty";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-2");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}

		SshSession sshSess = GlobalDataSsh.GetSessionByName(this.connectionName);
		if (sshSess == null) {
			// ssh connection not found
			responseMessage = "connection " + this.connectionName + " not found";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-1");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseMessage(responseMessage);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		if (this.shellName.equals("")) {
			// ssh connection not found
			responseMessage = "shell name is empty";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-3");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
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
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-5");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		if (sess.isConnected() == false) {
			responseMessage = "ssh connection with name " + this.connectionName + " is not anymore connected";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-6");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}

		// ssh session is connected try to connect shell
		SshChannelShell cs = sshSess.getChannelShellByName(this.shellName);
		if (cs == null) {
			// ssh connection not found
			responseMessage = "shell with name " + this.shellName + " is null on" + this.connectionName;
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-4");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		// check if channelshell is still connected
		if (!cs.isConnected()) {
			responseMessage = "Shell with name " + this.shellName + " is not connected on: " + this.connectionName;
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-7");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		// Channelshell isconnected and all conditions fulfilled

		responseMessage = "Command " + this.command + " sent on shell " + this.shellName + " on " + this.connectionName;
		// TODO add doCommand code
		cs.getChannelShell().setPty(this.useTty);
		byte[] responseDataBytes = {};
		try {
			cs.sendCommand(command);
		} catch (Exception e) {
			byte[] responseDataBytes2 = {};
			res.setResponseCode("-9");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setSampleLabel(
					getName() + "Send Exception(" + e.getClass().getSimpleName() + " " + e.getMessage() + ")");
			res.setResponseMessage("Send Exception(" + e.getClass().getSimpleName() + " " + e.getMessage() + ")");
			res.setResponseData(responseDataBytes2);
			res.sampleEnd();
			return res;
		}

		try {
			responseDataBytes = cs.readResponse(this.stripCommand, this.command, this.stripPrompt, this.resultEncoding);
		} catch (Exception e) {
			byte[] responseDataBytes2 = {};
			res.setResponseCode("-8");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setSampleLabel(
					getName() + "Receive Exception(" + e.getClass().getSimpleName() + " " + e.getMessage() + ")");
			res.setResponseMessage("Receive Exception(" + e.getClass().getSimpleName() + " " + e.getMessage() + ")");
			res.setResponseData(responseDataBytes2);
			res.sampleEnd();
			return res;
		}
		res.setResponseData(new String(responseDataBytes), this.resultEncoding);
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setSampleLabel(getName() + " (" + responseMessage + ")");

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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public boolean getUseTty() {
		return useTty;
	}

	public void setUseTty(boolean useTty) {
		this.useTty = useTty;
	}

	public boolean getStripPrompt() {
		return stripPrompt;
	}

	public void setStripPrompt(boolean stripPrompt) {
		this.stripPrompt = stripPrompt;
	}

	public boolean getStripCommand() {
		return stripCommand;
	}

	public void setStripCommand(boolean stripCommand) {
		this.stripCommand = stripCommand;
	}

	public String getResultEncoding() {
		return resultEncoding;
	}

	public void setResultEncoding(String resultEncoding) {
		this.resultEncoding = resultEncoding;
	}

}
