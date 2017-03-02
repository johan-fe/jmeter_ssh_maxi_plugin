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

/**
 * Dump ssh session sampler
 */
public  class DumpSSHSessionSampler extends AbstractSampler implements TestBean  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2954989469756886583L;
	 
	private static final Logger log = LoggingManager.getLoggerForClass();
    private String connectionName= "";
	public DumpSSHSessionSampler() {
		 super();
		//super("SSH Dump Session Sampler");
		setName("SSHDumpSessionSampler");
		//System.out.println("in constructor dump sampler");
	}

	@Override
    public SampleResult sample(Entry e) {

    	SampleResult res=new SampleResult();
    	res.sampleStart();
    	String connList=GlobalDataSsh.GetConnectionList(this.connectionName);
    	if(connList.equals(""))
    	{
    		if(connectionName.equals(""))
    		{res.setResponseCode("No Connections Found");}
    		else
    		{res.setResponseCode("No Matching Connections Found");}
    	}
    	else
    	{
    		if(connectionName.equals(""))
    		{res.setResponseCode("Connection(s) Found");}
    		else
    		{res.setResponseCode("Matching Connection Found");}
    	}
    	String samplerData="";
    	if(connectionName.equals(""))
    	{
    		res.setSampleLabel(getName()+" (show_all_connections)");
    		samplerData="Show all connections avalable";
    	}
    	else
    	{
    		res.setSampleLabel(getName() + " show connections filtered for:("+this.connectionName  + ")");  
    		samplerData="Show if connection"+this.connectionName+" is avalable";
    	}
        // Set up sampler return types
        res.setSamplerData(samplerData);
        res.setDataType(SampleResult.TEXT);
        res.setContentType("text/plain");
        res.setResponseData(connList,"UTF-8");
        res.sampleEnd();
     // source data
       // res.setSamplerData(getRequestData());
        if (connList.equals(""))
        {
        	res.setResponseData("no connection found","UTF-8");
        	res.setResponseMessage("no connection found");
        	res.setSuccessful(false);
        }
        else
        {
        	res.setResponseData(connList,"UTF-8");
        	res.setResponseMessage("OK");
        	res.setSuccessful(true);
        }
    	return res;
    }
       


	public void setConnectionName(String conn){
    	this.connectionName=conn.trim();
    }
    public String getConnectionName()
    {
    	return this.connectionName;
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
