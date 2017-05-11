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

import org.apache.jmeter.samplers.AbstractSampler;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.jcraft.jsch.Session;

/**
 * Close ssh session sampler
 */
public class CloseSSHSessionSampler extends AbstractSSHMainSampler {
	/**
	 * init
	 */
	private static final long serialVersionUID = 2954989469756886583L;

	private static final Logger log = LoggingManager.getLoggerForClass();
	private String connectionName = "";

	public CloseSSHSessionSampler() {
		super("SSHCloseSessionSampler");

	}

	@Override
	public SampleResult sample(Entry e) {

		SampleResult res = new SampleResult();
		res.sampleStart();
		// String connList=GlobalDataSsh.GetConnectionList(this.connectionName);
		String samplerData = "";
		String responseData = "";
		String responseMessage = "";
		String responseCode = "";
		if (connectionName.equals("")) {
			samplerData = "Invalid sampler configuration connection name is required";
			res.setSampleLabel(getName() + " (" + samplerData + ")");
			responseMessage = "connection name not configured in sampler!";
			responseData = responseMessage;
			responseCode = "-1";
			res.setSuccessful(false);
		} else {
			res.setSampleLabel(getName() + " closing connection: (" + this.connectionName + ")");
			samplerData = "close ssh connection " + this.connectionName;
			SshSession sshSess = GlobalDataSsh.GetSessionByName(this.connectionName);

			if (sshSess != null) {
				synchronized (sshSess) {
					sshSess.disconnect();
				}
				GlobalDataSsh.removeSession(this.connectionName);
				responseMessage = "connection " + this.connectionName + " closed";
				responseData = responseMessage;
				responseCode = "0";
				res.setSuccessful(true);
			} else {
				responseMessage = "connection " + this.connectionName + " not found";
				responseCode = "-2";
				responseData = responseMessage;
				res.setSuccessful(false);
			}
		}
		res.sampleEnd();
		// Set up sampler return types
		res.setSamplerData(samplerData);
		res.setDataType(SampleResult.TEXT);
		res.setContentType("text/plain");
		res.setResponseData(responseData, "UTF-8");

		// source data
		// res.setSamplerData(getRequestData());
		res.setResponseMessage(responseMessage);
		res.setResponseCode(responseCode);
		return res;
	}

	public void setConnectionName(String conn) {
		this.connectionName = conn.trim();
	}

	public String getConnectionName() {
		return this.connectionName;
	}

	@Override
	public void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			log.error("SSH dump session error", e);
		} finally {

		}

	}

}
