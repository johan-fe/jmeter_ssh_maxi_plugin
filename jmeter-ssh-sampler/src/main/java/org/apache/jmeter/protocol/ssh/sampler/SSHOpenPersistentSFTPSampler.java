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
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelShell;

public class SSHOpenPersistentSFTPSampler extends AbstractSSHMainSampler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1098L;
	private String connectionName = "";
	private String sftpSessionName="";
	private boolean usePty=false;
	private String resultEncoding="UTF-8";
//	private boolean stripPrompt=false;
	


	public SSHOpenPersistentSFTPSampler() {
		super("SSHOpenPersistentSFTPSampler");
	}

	@Override
	public SampleResult sample(Entry arg0) {
		SampleResult res = new SampleResult();
		res.sampleStart();

		String samplerData = "Open SFTP "+this.sftpSessionName+" on "+this.connectionName;
		String responseData = "";
		String responseMessage = "";
		String responseCode = "";
		res.setDataType(SampleResult.TEXT);
		res.setContentType("text/plain");
		res.setSamplerData(samplerData);
		if (this.connectionName.equals("")) {
			// empty connection name
			responseMessage = "connection name is empty";
			res.setSampleLabel(getName()+" ("+responseMessage+")");
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
			res.setSampleLabel(getName()+" ("+responseMessage+")");
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
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		SshChannelShell cs = sshSess.getChannelShellByName(this.sftpSessionName);
		if (cs!=null)
		{
			// ssh connection not found
			responseMessage = "SFTP session with name "+this.sftpSessionName+" already exists, no new SFTP session opened on "+this.connectionName;
			res.setResponseCode("-4");
			res.setSuccessful(false);
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		//make a new SFTP Session
		Session sess=sshSess.getSession();
		//check if session is still open 
		if (sess==null)
		{
			responseMessage = "severe error ssh session is null";
			res.setResponseCode("-5");
			res.setSuccessful(false);
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		if (sess.isConnected()==false)
		{
			responseMessage = "ssh connection with name "+this.connectionName+" is not anymore connected";
			res.setResponseCode("-6");
			res.setSuccessful(false);
			res.setResponseData(responseMessage, "UTF-8");
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		//ssh session is connected try to connect shell
		ChannelSftp cSftp=null;
		InputStream in=null;
		PrintStream ps=null;
		try {
		
			cSftp=(ChannelSftp) sess.openChannel("sftp");
			cSftp.setPty(this.usePty);
			cSftp.connect();
			in=cSftp.getInputStream();
	        OutputStream ops = cSftp.getOutputStream();
            ps = new PrintStream(ops, true);
            //TODO read from shell set label
 
		}
		catch (JSchException e1) {
            res.setSuccessful(false);
            res.setResponseCode("JSchException");
            res.setResponseMessage(e1.getMessage());
			res.setSampleLabel(getName()+" (Exception)");
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
			res.setSampleLabel(getName()+" (Exception)");
            res.setResponseMessage(e1.getMessage());
            res.setResponseData("", "UTF-8");
            res.sampleEnd();
            return res;
        }catch (Exception e1) {
            res.setSuccessful(false);
            res.setResponseCode("Connection Failed");
			res.setSampleLabel(getName()+" (Exception)");
            res.setResponseMessage(e1.getMessage());
            res.setResponseData("", "UTF-8");
            res.sampleEnd();
            return res;
        }
		//if successfully opened add the shell to shell collection in the ssh session
		responseMessage = "SFTP with name "+this.sftpSessionName+" opened on "+this.connectionName;
		res.setSampleLabel(getName()+" ("+responseMessage+")");

		SshChannelSFTP sshcsftp = new SshChannelSFTP();
		sshcsftp.setChannelSftp(cSftp);
 
		sshSess.addChannelSftp(this.sftpSessionName, sshcsftp);
		/*byte[] responseDataBytes= {}; // no need here to read greeting data like oin interactive shell
		try {
			responseDataBytes=sshcs.readResponse(false,"",this.stripPrompt,this.resultEncoding );
		}
		catch(Exception e)
		{
			byte[] responseDataBytes2= {};
			res.setResponseCode("-8");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setSampleLabel(getName()+" Exception("+e.getClass().getSimpleName()+" "+e.getMessage()+ ")");
			res.setResponseData(responseDataBytes2);
			res.setResponseMessage("Exception("+e.getClass().getSimpleName()+" "+e.getMessage()+ ")");
			res.sampleEnd();
			return res;
		}*/
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setResponseData(responseMessage,this.resultEncoding);//new String(responseDataBytes),this.resultEncoding);
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



	public String getResultEncoding() {
		return resultEncoding;
	}

	public void setResultEncoding(String resultEncoding) {
		this.resultEncoding = resultEncoding;
	}
/*
	public boolean getStripPrompt() {
		return stripPrompt;
	}

	public void setStripPrompt(boolean stripPrompt) {
		this.stripPrompt = stripPrompt;
	}
*/
	public String getSftpSessionName() {
		return this.sftpSessionName;
	}

	public void setSftpSessionName(String sftpSessionName) {
		this.sftpSessionName = sftpSessionName;
	}
	public boolean getUsePty() {
		return this.usePty;
	}
	public void setUsePty(boolean uPty) {
		this.usePty=uPty;
	}
}
