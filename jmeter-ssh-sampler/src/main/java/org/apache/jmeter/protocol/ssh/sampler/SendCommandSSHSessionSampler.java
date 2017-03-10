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
import java.io.InputStreamReader;

import org.apache.jmeter.samplers.AbstractSampler;
 
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
 
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Dump ssh session sampler
 */
public  class SendCommandSSHSessionSampler extends AbstractSSHMainSampler implements TestBean  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2954989469756886583L;
	 
	private static final Logger log = LoggingManager.getLoggerForClass();
    private String connectionName= "";
    private String command = "";
    private boolean useReturnCode = true;
    private boolean useTty = true;
    private boolean printStdErr = true;

	public SendCommandSSHSessionSampler() {
		super("SendCommandSSHSessionSampler");	 
		//setName("SendCommandSSHSessionSampler"); 
	}
	private String doCommand(Session session, String command, SampleResult res) throws JSchException, IOException {
        StringBuilder sb = new StringBuilder();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setPty(useTty);

        BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(channel.getErrStream()));
        channel.setCommand(command);
         
        channel.connect();

        if(printStdErr){
            sb.append("=== stdin ===\n\n");
        }
        
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            sb.append(line);
            sb.append("\n");
        }
        
        if(this.printStdErr){
            sb.append("\n\n=== stderr ===\n\n");
            for (String line = err.readLine(); line != null; line = err.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        }
        channel.disconnect();
        long maxWaitCloseChannelMs=3000L;// wait max 3sec to close
        long until = System.currentTimeMillis() + maxWaitCloseChannelMs;
        while (!channel.isClosed()&&System.currentTimeMillis() < until) {
        	try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
		 
			}
        } // wait until channel is closed otherwise you get -1 in getExitStatus
        if(this.useReturnCode){
            res.setResponseCode(String.valueOf(channel.getExitStatus()));
        }else{
            res.setResponseCodeOK();
        }
        
        //res.sampleEnd();
        
        return sb.toString();
    }
	@Override
    public SampleResult sample(Entry e) {

    	SampleResult res=new SampleResult();
    	res.sampleStart();
    	//String connList=GlobalDataSsh.GetConnectionList(this.connectionName);
    	String samplerData="";
    	String responseData="";
        res.setSamplerData(command);
        res.setDataType(SampleResult.TEXT);
        res.setContentType("text/plain");
    	if(connectionName.equals(""))
    	{
    		res.setResponseMessage(getName() +  " Missing Connection Name in sampler configuration");
    		samplerData="Invalid sampler configuration connection name is required";
    		res.setSampleLabel(getName()+" ("+samplerData+")");
    		responseData="Connection name not configured in sampler!";
    		res.setSuccessful(false);
    	}
    	else
    	{
    		if(this.command.equals(""))
    		{
    			res.setResponseMessage(getName() +  " No Command Configured");
        		samplerData="no command configured in sampler";
        		res.setSampleLabel(getName()+" ("+samplerData+")");
        		responseData="Command not configured in sampler!";
        		res.setSuccessful(false);
        		res.setResponseCode("No Command Configured");
    		}
    		else
    		{
    			SshSession sshSess=GlobalDataSsh.GetSessionByName(this.connectionName);
    			if(sshSess!=null)
    			{
    				res.setSampleLabel(getName() +  "send command "+this.command+" on connection "+this.connectionName);
    				Session ses =sshSess.getSession();
    				synchronized(ses){
    					try{
    	
    						String result=this.doCommand(ses, this.command,res);
    						if (result !=null){
    							res.setResponseMessage("OK");
    							res.setResponseData(result,"UTF-8");
    							res.setSuccessful(true);
    						}
    						else
    						{
    							res.setResponseData("","UTF-8");
    							res.setResponseMessage("result=null");
    							res.setSuccessful(true);
    						}
    					
    					} catch (JSchException e1) {
	    		            res.setSuccessful(false);
	    		            res.setResponseCode("JSchException");
	    		            res.setResponseMessage(e1.getMessage());
	    		            res.setResponseData("","UTF-8");
	    		        } catch (IOException e1) {
	    		            res.setSuccessful(false);
	    		            res.setResponseCode("IOException");
	    		            res.setResponseMessage(e1.getMessage());
	    		            res.setResponseData("","UTF-8");
	    		        } catch (NullPointerException e1) {
	    		            res.setSuccessful(false);
	    		            res.setResponseCode("Connection Failed");
	    		            res.setResponseMessage(e1.getMessage());
	    		            res.setResponseData("","UTF-8");
	    		        }catch (Throwable e1) {
	    		            res.setSuccessful(false);
	    		            res.setResponseCode("Error occurred on command execution");
	    		            res.setResponseMessage(e1.getMessage());
	    		            res.setResponseData("","UTF-8");
	    		        }
    				}
    			}
    			else
    			{
    				// Handle exception session not found
    				res.setResponseMessage(getName()+ " connection "+this.connectionName+" not found");
    				res.setResponseCode("Connection Not Found");
    				res.setSuccessful(false);
    	    		samplerData="coonection " +this.connectionName+ " not found!";
    	    		res.setSampleLabel(getName()+" ("+samplerData+")");
    				res.setResponseData("","UTF-8");
    			}
    		}
    	}
    	res.sampleEnd();
    	return res;
    }
       

 
	public void setConnectionName(String conn){
    	this.connectionName=conn.trim();
    }
    public String getConnectionName()
    {
    	return this.connectionName;
    } 
    
	public void setCommand(String comm){
    	this.command=comm;
    }
    public String getCommand()
    {
    	return this.command;
    }
    
    public boolean getPrintStdErr() {
        return printStdErr;
    }

    public void setPrintStdErr(boolean printStdErr) {
        this.printStdErr = printStdErr;
    }

    public boolean getUseReturnCode() {
        return useReturnCode;
    }

    public void setUseReturnCode(boolean useReturnCode) {
        this.useReturnCode = useReturnCode;
    }

    public boolean getUseTty() {
        return useTty;
    }

    public void setUseTty(boolean useTty) {
        this.useTty = useTty;
    }


    
    @Override
    public void finalize() {
        try {
            super.finalize();
        } 
        catch (Throwable e) {
            log.error("SSH dump session error", e );
        } 
        finally {
            
        }
        
    }
}
