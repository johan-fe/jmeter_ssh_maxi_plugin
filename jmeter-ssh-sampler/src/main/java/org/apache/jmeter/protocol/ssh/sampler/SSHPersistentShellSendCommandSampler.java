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
	private String command="";
 

	private boolean useTty=false;


	public SSHPersistentShellSendCommandSampler() {
		super("SSHPersistentShellSendCommandSampler");
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

		//make a new shell Session
		Session sess=sshSess.getSession();
		//check if session is still open 
		if (sess==null)
		{
			responseMessage = "severe error ssh session is null";
			responseCode = "-5";
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		if (sess.isConnected()==false)
		{
			responseMessage = "ssh connection with name "+this.connectionName+" is not anymore connected";
			responseCode = "-6";
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		
		//ssh session is connected try to connect shell
		SshChannelShell cs = sshSess.getChannelShellByName(this.shellName);
		if (cs==null)
		{
			// ssh connection not found
			responseMessage = "shell with name "+this.shellName+" is null on"+this.connectionName;
			responseCode = "-4";
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		// check if channelshell is still connected
		if (!cs.isConnected())
		{
			responseMessage = "shell with name "+this.shellName+" is not connected on: "+this.connectionName;
			responseCode = "-7";
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		// Channelshell isconnected and all conditions fulfilled 

		//responseMessage = "Shell with name "+this.shellName+" opened on "+this.connectionName;
		// TODO add doCommand code
		cs.getChannelShell().setPty(this.useTty);
		byte [] responseBytes= {};
		try 
		{
			cs.sendCommand(command);
		}
		catch(Exception e)
		{
			//TODO handle thrown exceptions
		}
		try
		{
			responseBytes = cs.readResponse();
		}
		catch(Exception e)
		{
			//TODO handle thrown exceptions
		}
		res.setResponseData(responseBytes);
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		 
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
}
