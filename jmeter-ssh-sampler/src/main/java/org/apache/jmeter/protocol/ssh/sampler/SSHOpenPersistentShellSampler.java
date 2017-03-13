package org.apache.jmeter.protocol.ssh.sampler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

public class SSHOpenPersistentShellSampler extends AbstractSSHMainSampler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1098L;
	private String connectionName = "";
	private String shellName = "";

	public SSHOpenPersistentShellSampler() {
		super("SSHOpenPersistentShellSampler");
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
		if (cs!=null)
		{
			// ssh connection not found
			responseMessage = "shell with name "+this.shellName+" already exists, no new shell opened on "+this.connectionName;
			responseCode = "-4";
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
		ChannelShell cShell=null;
		InputStream in=null;
		PrintStream ps=null;
		try {
		
			cShell=(ChannelShell) sess.openChannel("shell");
			//TODO add usepty option
			cShell.setPty(false);

			//ext input stream not used for now should be used before connect sends 
			//InputStream extStr=cShell.getExtInputStream();
	        //BufferedReader ext = new BufferedReader(new InputStreamReader(errStr));
			cShell.connect();
			in=cShell.getInputStream();
	        OutputStream ops = cShell.getOutputStream();
            ps = new PrintStream(ops, true);
            
           // byte[] bt=new byte[1024];
		}
		catch (JSchException e1) {
            res.setSuccessful(false);
            res.setResponseCode("JSchException");
            res.setResponseMessage(e1.getMessage());
            res.setResponseData("", "UTF-8");
            res.sampleEnd();
            return res;
        }/* catch (IOException e1) {
            res.setSuccessful(false);
            res.setResponseCode("IOException");
            res.setResponseMessage(e1.getMessage());
            res.setResponseData("", "UTF-8");
            res.sampleEnd();
            return res;
        }*/ catch (NullPointerException e1) {
            res.setSuccessful(false);
            res.setResponseCode("Connection Failed");
            res.setResponseMessage(e1.getMessage());
            res.setResponseData("", "UTF-8");
            res.sampleEnd();
            return res;
        }catch (Exception e1) {
            res.setSuccessful(false);
            res.setResponseCode("Connection Failed");
            res.setResponseMessage(e1.getMessage());
            res.setResponseData("", "UTF-8");
            res.sampleEnd();
            return res;
        }
		//if successfully opened add the shell to shell collection in the ssh session
		responseMessage = "Shell with name "+this.shellName+" opened on "+this.connectionName;
		SshChannelShell sshcs = new SshChannelShell();
		sshcs.setChannelShell(cShell);
		sshcs.setInputStream(in);
		sshcs.setpOutputStream(ps);
		sshSess.addChannelShell(this.shellName, sshcs);
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setResponseData(responseMessage, "UTF-8");
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
}
