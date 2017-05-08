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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * SSH Sampler that collects single lines of output and returns
 * them as samples.
 *
 */
public class SendSFTPCommandSSHSessionSampler extends AbstractSSHMainSampler implements TestBean {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9180249991509027397L;
	private static final Logger log = LoggingManager.getLoggerForClass();
    public static final String SFTP_COMMAND_GET = "get";
    public static final String SFTP_COMMAND_PUT = "put";
    public static final String SFTP_COMMAND_RM = "rm";
    public static final String SFTP_COMMAND_RMDIR = "rmdir";
    public static final String SFTP_COMMAND_LS = "ls";
    public static final String SFTP_COMMAND_RENAME = "ls";
    private String source;
    private String destination;
    private String action;
    private String sftpSessionName="";
    private String connectionName="";
    private boolean printFile = true;
	private boolean useTty;
	private String resultEncoding="UTF-8";

    public SendSFTPCommandSSHSessionSampler() {
        super("SendSFTPCommandSSHSessionSampler");
    }

    /**
     * Returns last line of output from the command
     */
    public String getSamplerLabel()
    {
    	StringBuilder sb=new StringBuilder(getName());
    	return sb.toString();
    }
    @Override
	public SampleResult sample(Entry arg0) {
		SampleResult res = new SampleResult();
		res.sampleStart();
		
		String samplerData = "Send Command "+this.action+" to SFTP "+this.sftpSessionName+" on "+this.connectionName;
		String responseData = "";
		String responseMessage = "";
		String responseCode = "";
		res.setSamplerData(samplerData);
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
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseCode("-1");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseMessage(responseMessage);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		if (this.sftpSessionName.equals("")) {
			// ssh connection not found
			responseMessage = "SFTP session name is empty";
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseCode("-3");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}

		//make a new shell Session
		Session sess=sshSess.getSession();
		//check if session is still open 
		if (sess==null)
		{
			responseMessage = "severe error ssh session is null";
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseCode("-5");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		if (sess.isConnected()==false)
		{
			responseMessage = "ssh connection with name "+this.connectionName+" is not anymore connected";
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseCode("-6");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		
		//ssh session is connected try to connect shell
		SshChannelSFTP cSftp = sshSess.getChannelSftpByName(this.sftpSessionName);
		if (cSftp==null)
		{
			// ssh connection not found
			responseMessage = "SFTP session with name "+this.sftpSessionName+" is null on"+this.connectionName;
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseCode("-4");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		// check if channelshell is still connected
		if (!cSftp.isConnected())
		{
			responseMessage = "SFTP session with name "+this.sftpSessionName+" is not connected on: "+this.connectionName;
			res.setSampleLabel(getName()+" ("+responseMessage+")");
			res.setResponseCode("-7");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		// Channelftp isconnected and all conditions fulfilled 

		responseMessage = "Command "+this.action+" sent to SFTP "+this.sftpSessionName+" on "+this.connectionName;
		// TODO add doCommand code
		cSftp.getChannelSftp().setPty(this.useTty);
		
		try {
			// execute the sftp command
			responseData = this.doFileTransfer(cSftp.getChannelSftp(), source, destination, res);
            
		}
		catch(Exception e)
		{
			byte[] responseDataBytes2= {};
			res.setResponseCode("-8");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setSampleLabel(getName()+"Receive Exception("+e.getClass().getSimpleName()+" "+e.getMessage()+ ")");
			res.setResponseMessage("Receive Exception("+e.getClass().getSimpleName()+" "+e.getMessage()+ ")");
			res.setResponseData(responseDataBytes2);
			res.sampleEnd();
			return res;
		}
//		res.setResponseData(new String(responseDataBytes),this.resultEncoding);
		res.setResponseData(responseData.getBytes());
        res.setResponseMessageOK();
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setSampleLabel(getName()+" ("+responseMessage+")");
		res.setResponseMessage(responseMessage);
		res.sampleEnd();
		return res;

	}
    
    
    public SampleResult sampleOld(Entry e) {
        SampleResult res = new SampleResult();
 /*       res.setSampleLabel(this.getSamplerLabel());



        // Set up sampler return types
        res.setSamplerData(action + " " + source);

        res.setDataType(SampleResult.TEXT);
        res.setContentType("text/plain");

        String response;
        if (getSession() == null) {
            connect();
        }

        try {
            if (getSession() == null) {
                log.error("Failed to connect to server with credentials "
                        + getUsername() + "@" + getHostname() + ":" + getPort()
                        + " pw=" + getPassword());
                throw new NullPointerException("Failed to connect to server: " + getFailureReason());
            }

            response = doFileTransfer(getSession(), source, destination, res);
            res.setResponseData(response.getBytes());


            res.setSuccessful(true);

            res.setResponseMessageOK();
        } catch (JSchException e1) {
            res.setSuccessful(false);
            res.setResponseCode("JSchException");
            res.setResponseMessage(e1.getMessage());
        } catch (SftpException e1) {
            res.setSuccessful(false);
            res.setResponseCode("SftpException");
            res.setResponseMessage(e1.getMessage());
        } catch (IOException e1) {
            res.setSuccessful(false);
            res.setResponseCode("IOException");
            res.setResponseMessage(e1.getMessage());
        } catch (NullPointerException e1) {
            res.setSuccessful(false);
            res.setResponseCode("Connection Failed");
            res.setResponseMessage(e1.getMessage());
        } finally {
            // Try a disconnect/sesson = null here instead of in finalize.
            disconnect();
            setSession(null);
        }*/
        return res;
    }

    /**
     * Executes a the given command inside a short-lived channel in the session.
     * 
     * Performance could be likely improved by reusing a single channel, though
     * the gains would be minimal compared to sharing the Session.
     *  
     * @param session Session in which to create the channel
     * @param command Command to send to the server for execution
     * @return All standard output from the command
     * @throws JSchException 
     * @throws SftpException
     * @throws IOException
     */
    private String doFileTransfer(ChannelSftp channel , String src, String dst, SampleResult res) throws JSchException, SftpException, IOException {
        StringBuilder sb = new StringBuilder("");
        //ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
       // channel.connect();

        if (SFTP_COMMAND_GET.equals(action)) {

            if (!printFile) {
                channel.get(src, dst);
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(channel.get(src)));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                    sb.append("\n");
                }
            }

        } else if (SFTP_COMMAND_PUT.equals(action)) {
            channel.put(src, dst);
        } else if (SFTP_COMMAND_LS.equals(action)) {
            List<ChannelSftp.LsEntry> ls = channel.ls(src);
            for (ChannelSftp.LsEntry line : ls) {
                sb.append(line.getLongname());
                sb.append("\n");
            }
        } else if (SFTP_COMMAND_RM.equals(action)) {
            channel.rm(src);
        } else if (SFTP_COMMAND_RMDIR.equals(action)) {
            channel.rmdir(src);
        } else if (SFTP_COMMAND_RENAME.equals(action)) {
            channel.rename(src, dst);
        }

        //res.sampleEnd();


        //channel.disconnect();
        return sb.toString();
    }

    // Accessors
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean getPrintFile() {
        return printFile;
    }

    public void setPrintFile(boolean printFile) {
        this.printFile = printFile;
    }

	public String getSftpSessionName() {
		return this.sftpSessionName;
	}

	public void setSftpSessionName(String sftpSessionName) {
		this.sftpSessionName = sftpSessionName;
	}
 
	public String getConnectionName() {
		return this.connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
}
