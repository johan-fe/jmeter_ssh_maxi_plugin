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
	private static final long serialVersionUID = 1098L;
	private String connectionName = "";
	private String shellName = "";

	public SSHClosePersistentShellSampler() {
		super("SSHClosePersistentShellSampler");
		// TODO Auto-generated constructor stub
	}

	@Override
	public SampleResult sample(Entry arg0) {
		SampleResult res = new SampleResult();
		res.sampleStart();

		String samplerData = "";
		String responseData = "";
		String responseMessage = "";
		String responseCode = "";
		res.setDataType(SampleResult.TEXT);
		res.setContentType("text/plain");
		if (this.connectionName.equals("")) {
			// empty connection name
			responseMessage = "connection name is empty";
			responseCode = "-2";
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}

		SshSession sshSess = GlobalDataSsh.GetSessionByName(this.connectionName);
		if (sshSess == null) {
			// ssh connection not found
			responseMessage = "connection " + this.connectionName + " not found";
			responseCode = "-1";
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		if (this.shellName.equals("")) {
			// ssh connection not found
			responseMessage = "shell name is empty";
			responseCode = "-3";
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		SshChannelShell cs = sshSess.getChannelShellByName(this.shellName);
		if (cs==null)
		{
			// ssh connection not found
			responseMessage = "shell with name "+this.shellName+" not found on "+this.connectionName;
			responseCode = "-4";
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		cs.disconnect();
		
		sshSess.removeChannelShell(this.shellName);
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setResponseData(responseMessage, "UTF-8");
		res.setResponseMessage(responseMessage);
		res.sampleEnd();
		responseMessage = "Shell with name "+this.shellName+" closed on "+this.connectionName;

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
