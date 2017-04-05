package org.apache.jmeter.protocol.ssh.sampler;

import java.io.IOException;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;


import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelShell;


public class SSHClosePersistentSFTPChannelSampler extends AbstractSSHMainSampler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1098L;
	private String connectionName = "";
	private String sftpSessionName = "";

	public SSHClosePersistentSFTPChannelSampler() {
		super("SSHClosePersistentSFTPChannelSampler");
		// TODO Auto-generated constructor stub
	}

	@Override
	public SampleResult sample(Entry arg0) {
		SampleResult res = new SampleResult();
		res.sampleStart();

		String samplerData = "Close SFTP "+this.sftpSessionName+" on "+this.connectionName;
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
		if (this.sftpSessionName.equals("")) {
			// ssh connection not found
			responseMessage = "SFTP session name is empty";
			res.setResponseCode("-3");
			res.setSuccessful(false);
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		SshChannelSFTP csftp = sshSess.getChannelSftpByName(this.sftpSessionName);
		if (csftp==null)
		{
			// ssh connection not found
			responseMessage = "SFTP session with name "+this.sftpSessionName+" not found on "+this.connectionName;
			res.setResponseCode("-4");
			res.setSuccessful(false);
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		csftp.disconnect();
		
		sshSess.removeChannelShell(this.sftpSessionName);
		responseMessage = "SFTP with name "+this.sftpSessionName+" closed on "+this.connectionName;

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

	public void setSftpSessionName(String sh) {
		this.sftpSessionName = sh;
	}

	public String getSftpSessionName() {
		return this.sftpSessionName;
	}
}
