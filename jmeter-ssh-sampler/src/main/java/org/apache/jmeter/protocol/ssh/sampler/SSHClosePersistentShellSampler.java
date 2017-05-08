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

import java.io.IOException;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;


import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelShell;


public class SSHClosePersistentShellSampler extends AbstractSSHMainSampler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 10498L;
	private String connectionName = "";
	private String shellName = "";

	public SSHClosePersistentShellSampler() {
		super("SSHClosePersistentShellSampler");
		
	}

	@Override
	public SampleResult sample(Entry arg0) {
		SampleResult res = new SampleResult();
		res.sampleStart();

		String samplerData = "Close shell "+this.shellName+" on "+this.connectionName;
		String responseData = "";
		String responseMessage = "";
		String responseCode = "";
		res.setDataType(SampleResult.TEXT);
		res.setContentType("text/plain");
		
		if (this.connectionName.equals("")) {
			// empty connection name
			responseMessage = "connection name is empty";
			res.setSampleLabel(getName()+" ("+responseMessage+")");
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
			res.setResponseCode("-1");
			res.setSuccessful(false);
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setSamplerData(samplerData);
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
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		SshChannelShell cs = sshSess.getChannelShellByName(this.shellName);
		if (cs==null)
		{
			// ssh connection not found
			responseMessage = "shell with name "+this.shellName+" not found on "+this.connectionName;
			res.setResponseCode("-4");
			res.setSuccessful(false);
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		cs.disconnect();
		
		sshSess.removeChannelShell(this.shellName);
		responseMessage = "Shell with name "+this.shellName+" closed on "+this.connectionName;

		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setResponseData(responseMessage, "UTF-8");
		res.setResponseMessage(responseMessage);
		res.sampleEnd();
		res.setSampleLabel(getName()+" ("+responseMessage+")");
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
}
