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
package org.apache.jmeter.protocol.ssh.test;

 
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jmeter.protocol.ssh.sampler.GlobalDataSsh;
//import org.apache.jmeter.control.LoopController;
//import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.ssh.sampler.SSHCommandSamplerExtra;
import org.apache.jmeter.protocol.ssh.sampler.SshSession;
//import org.apache.jmeter.protocol.ssh.sampler.SendCommandSSHSessionSamplerBeanInfo;
//import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jcraft.jsch.Session;

//import org.junit.runner.RunWith;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import jline.internal.Log;
import org.junit.Assert;
//import org.apache.jmeter.samplers.Entry;
//import org.apache.jmeter.samplers.SampleResult;
//import  kg.apc.emulators.TestJMeterUtils;
//import junit.framework.TestCase;
//import org.apache.jmeter.util.JMeterUtils;
//import org.apache.jorphan.collections.HashTree;
//@RunWith(Suite.class)
//@Suite.SuiteClasses({ TestSSHCommandSamplerExtra.class })
/*public class AllTests {
    // empty
}*/



public class TestSSHCommandSamplerExtra // extends TestCase 
{
	private final static Logger LOG  =Logger.getLogger(TestSSHCommandSamplerExtra.class.getName());
	private SSHCommandSamplerExtra instance = null;
	static private Thread sshts=null;

	public TestSSHCommandSamplerExtra() {
		//super(name);
	}
	/*public TestSSHCommandSamplerExtra(String name) {
		//super(name);
	}*/

	//Sometimes several tests need to share computationally expensive setup (like logging into a database).	
	@BeforeClass
	public static void setUpClass() throws Exception {
		GlobalDataSsh.removeAllSessions();
		sshts = new Thread(new SSHTestServer());
		sshts.start();
		Thread.sleep(1000);
		Log.info("ssh test server thread started");
	}
	
	//If you allocate expensive external resources in a
	//BeforeClass method you need to release them after all the tests in the class have run.
    @AfterClass
    public static void tearDownClass() throws Exception {
    	sshts.interrupt();
    	
    }
    
    //When writing tests, it is common to find that several tests need similar objects created before they can run
    @Before
	public void setUp() throws Exception {
		//super.setUp();
		 
			this.instance = new SSHCommandSamplerExtra();  
			//LOG.log(Level.INFO,"created new instance of SSHCommandSamplerExtra");
		 
	}
    //If you allocate external resources in a Before method you need to release them after the test runs.
    @After
	public void tearDown() throws Exception {
		//super.tearDown();
		this.instance = null;//  close connection and cleanup of globaldata in test itself
	}
    //The Test annotation tells JUnit that the public void method to which it is attached can be run as a test case.
	@Test 
	public void testSample() {
		 this.instance=new SSHCommandSamplerExtra();           
		 this.instance.setCommand("dir");
		 this.instance.setConnectionName("CONN1");
		 this.instance.setConnectionTimeout(3200);
		 this.instance.setHostname("127.0.0.1");
		 this.instance.setPassword("azerty!");
		 this.instance.setPort(5222);
		 this.instance.setPrintStdErr(true);
		 this.instance.setUseReturnCode(false);
		 this.instance.setUsername("johan");
		 this.instance.setUseTty(false);
		 this.instance.setCloseConnection(true);
 
		 SampleResult sr= this.instance.sample(null) ;
		 Integer errorCount= sr.getErrorCount();
		 assertTrue("ErrorCount is 0",errorCount==0);
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount));
		 LOG.log(Level.INFO, "content type:"+sr.getContentType());
		 String responseCode=sr.getResponseCode();
		 assertTrue(responseCode.equals("200"));
		 LOG.log(Level.INFO, "response code:"+responseCode);
		 String responseMessage=sr.getResponseMessage();
		 assertTrue("response Message is OK",responseMessage.equals("OK") );
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 String responseData=sr.getResponseDataAsString();
		 assertTrue("contains stderr in response Data", responseData.contains("=== stderr ==="));
		 assertTrue("contains stderr in response Data", responseData.contains("Welcome to Application Shell"));
		 LOG.log(Level.INFO, "response data as string:"+responseData);
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONN1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONN1, session shoul not be stored in there; setCloseConnection==true!" );

			 GlobalDataSsh.removeSession("CONN1");
		 }
		 assertTrue("session is null", sess==null);
	}
	@Test 
	public void testSampleUnkownDestAddress() {
	     this.instance=new SSHCommandSamplerExtra();          
		 this.instance.setCommand("dir");
		 this.instance.setConnectionName("CONN1");
		 this.instance.setConnectionTimeout(3200);
		 this.instance.setHostname("1.2.3.4");
		 this.instance.setPassword("azerty!");
		 this.instance.setPort(5222);
		 this.instance.setPrintStdErr(true);
		 this.instance.setUseReturnCode(false);
		 this.instance.setUsername("johan");
		 this.instance.setUseTty(false);
		 this.instance.setCloseConnection(true);
		 
		 SampleResult sr= this.instance.sample(null) ;
		 Integer errorCount= sr.getErrorCount();
		 assertTrue("ErrorCount is 1",errorCount==1);
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount));
		 LOG.log(Level.INFO, "content type:"+sr.getContentType());
		 String responseCode=sr.getResponseCode();
		 assertTrue(responseCode.equals("Connection Failed"));
		 LOG.log(Level.INFO, "response code:"+responseCode);
		 String responseMessage=sr.getResponseMessage();
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 assertTrue("responsemessage is correct",responseMessage.equals("Failed to connect to server: timeout: socket is not established")
				 || responseMessage.equals("Failed to connect to server: java.net.NoRouteToHostException: No route to host: connect")
		 || responseMessage.equals("Failed to connect to server: java.net.SocketException: Network is unreachable: connect"));
		 String responseData=sr.getResponseDataAsString();
		 assertTrue("responseData is empty",responseData.equals(""));
		 LOG.log(Level.INFO, "response data as string:"+responseData);
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONN1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONN1, session should not be stored in there; setCloseConnection==true!" );

			 GlobalDataSsh.removeSession("CONN1");
		 }
		 assertTrue("session is null", sess==null);

	}
	@Test 
	public void testSampleEmptyStringCommand() {
	     this.instance=new SSHCommandSamplerExtra();          
		 this.instance.setCommand("");
		 this.instance.setConnectionName("CONN1");
		 this.instance.setConnectionTimeout(3200);
		 this.instance.setHostname("127.0.0.1");
		 this.instance.setPassword("azerty!");
		 this.instance.setPort(5222);
		 this.instance.setPrintStdErr(true);
		 this.instance.setUseReturnCode(false);
		 this.instance.setUsername("johan");
		 this.instance.setUseTty(false);
		 this.instance.setCloseConnection(true);
 
		 SampleResult sr= this.instance.sample(null) ;
		 Integer errorCount= sr.getErrorCount();
		 assertTrue("ErrorCount is 0",errorCount==0);
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount));
		 LOG.log(Level.INFO, "content type:"+sr.getContentType());
		 String responseCode=sr.getResponseCode();
		 assertTrue(responseCode.equals("Connection Successful"));
		 LOG.log(Level.INFO, "response code:"+responseCode);
		 String responseMessage=sr.getResponseMessage();
		 assertTrue("responsemessage is correct",responseMessage.equals("SSH Connection closed, no Command Executed"));
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 String responseData=sr.getResponseDataAsString();
		 assertTrue("responseData is empty",responseData.equals(""));
		 LOG.log(Level.INFO, "response data as string:"+responseData);
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONN1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONN1, session should not be stored in there; setCloseConnection==true!" );

			 GlobalDataSsh.removeSession("CONN1");
		 }
		 assertTrue("session is null", sess==null);
	}
	@Test 
	public void testSampleWrongPassword() {
	     this.instance=new SSHCommandSamplerExtra();          
		 this.instance.setCommand("");
		 this.instance.setConnectionName("CONN1");
		 this.instance.setConnectionTimeout(3200);
		 this.instance.setHostname("127.0.0.1");
		 this.instance.setPassword("xyz!");
		 this.instance.setPort(5222);
		 this.instance.setPrintStdErr(true);
		 this.instance.setUseReturnCode(false);
		 this.instance.setUsername("johan");
		 this.instance.setUseTty(false);
		 this.instance.setCloseConnection(true);
 
		 SampleResult sr= this.instance.sample(null) ;
		 Integer errorCount= sr.getErrorCount();
		 assertTrue("ErrorCount is 1",errorCount==1);
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount));
		 LOG.log(Level.INFO, "content type:"+sr.getContentType());
		 String responseCode=sr.getResponseCode();
		 assertTrue(responseCode.equals("Connection Failed"));
		 LOG.log(Level.INFO, "response code:"+responseCode);
		 String responseMessage=sr.getResponseMessage();
		 assertTrue("responsemessage is correct",responseMessage.equals("Failed to connect to server: Auth fail"));
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 String responseData=sr.getResponseDataAsString();
		 assertTrue("responseData is empty",responseData.equals(""));
		 LOG.log(Level.INFO, "response data as string:"+responseData);
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONN1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONN1, session should not be stored in there; setCloseConnection==true!" );

			 GlobalDataSsh.removeSession("CONN1");
		 }
		 assertTrue("session is null", sess==null);

		 
	}
		
	@Test 
	public void testSampleEmptyStringCommandKeepConnOpen() {
	     this.instance=new SSHCommandSamplerExtra();          
		 this.instance.setCommand("");
		 this.instance.setConnectionName("CONN1");
		 this.instance.setConnectionTimeout(3200);
		 this.instance.setHostname("127.0.0.1");
		 this.instance.setPassword("azerty!");
		 this.instance.setPort(5222);
		 this.instance.setPrintStdErr(true);
		 this.instance.setUseReturnCode(false);
		 this.instance.setUsername("johan");
		 this.instance.setUseTty(false);
		 this.instance.setCloseConnection(false);
 
		 SampleResult sr= this.instance.sample(null) ;
		 Integer errorCount= sr.getErrorCount();
		 assertTrue("ErrorCount is 0",errorCount==0);
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount));
		 LOG.log(Level.INFO, "content type:"+sr.getContentType());
		 String responseCode=sr.getResponseCode();
		 assertTrue(responseCode.equals("Connection Successful"));
		 LOG.log(Level.INFO, "response code:"+responseCode);
		 String responseMessage=sr.getResponseMessage();
		 assertTrue("responsemessage is correct",responseMessage.equals("SSH Connection established, no Command Executed"));
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 String responseData=sr.getResponseDataAsString();
		 assertTrue("responseData is empty",responseData.equals(""));
		 LOG.log(Level.INFO, "response data as string:"+responseData);
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONN1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONN1");
			 GlobalDataSsh.removeSession("CONN1");
		 }
		 assertTrue("session is not null", sess!=null);
	}
	
	@Test
	public void testSetSession() {
		// fail("Not yet implemented");
	}

	@Test
	public void testGetFailureReason() {
		// fail("Not yet implemented");
	}

	@Test
	public void testSetFailureReason() {
		// fail("Not yet implemented");
	}

	@Test
	public void testSSHCommandSamplerExtra() {
		// fail("Not yet implemented");
		assertTrue(true);
	}

 

	@Test
	public void testSetCommand() {
		instance.setCommand("dir");
		String command = instance.getCommand();
		assertTrue(command.equals("dir"));
	}

	@Test
	public void testSetPrintStdErr() {
		instance.setPrintStdErr(true);
		boolean pse = instance.getPrintStdErr();
		assertTrue(pse == true);
	}

	@Test
	public void testSetUseReturnCode() {
		instance.setUseReturnCode(false);
		boolean gurc = instance.getUseReturnCode();
		assertTrue(false == gurc);
	}

	@Test
	public void testSetUseTty() {
		instance.setUseTty(false);
		boolean ustty = instance.getUseTty();
		assertTrue(ustty == false);
	}

	@Test
	public void testConnect() {
		// fail("Not yet implemented");
	}

	@Test
	public void testDisconnect() {
		// fail("Not yet implemented");
	}

	@Test
	public void testSetTunnelsSession() {
		assertTrue(true);
	}

	@Test
	public void testSetSingleTunnel() {
		assertTrue(true);
	}

	@Test
	public void testSetSshkeyfile() {
		instance.setSshkeyfile("hostkey.ser");
		String hkf = instance.getSshkeyfile();
		assertTrue(hkf.equals("hostkey.ser"));
	}

	@Test
	public void testSetPassphrase() {
		instance.setPassphrase("azerty!");
		String pf = instance.getPassphrase();
		assertTrue(pf.equals("azerty!"));
	}

	@Test
	public void testSetHostname() {
		instance.setHostname("127.0.0.1");
		String hn = instance.getHostname();
		assertTrue(hn.equals("127.0.0.1"));
	}

	@Test
	public void testSetPort() {
		instance.setPort(5222);
		assertTrue(true);
		Integer port = instance.getPort();
		assertTrue(port == 5222);
	}

	@Test
	public void testSetUsername() {
		instance.setUsername("johan");
		String un = instance.getUsername();
		assertTrue(un.equals("johan"));
	}

	@Test
	public void testSetPassword() {
		instance.setPassword("azerty!");
		String pw = instance.getPassword();
		assertTrue(pw.equals("azerty!"));
	}

	@Test
	public void testSetConnectionTimeout() {
		instance.setConnectionTimeout(3200);
		Integer cto = instance.getConnectionTimeout();
		assertTrue(cto == 3200);
	}

	@Test
	public void testSetTunnelsString() {
		instance.setTunnels("L5223:127.0.0.1:5222");
		String tuns = instance.getTunnels();
		assertTrue(tuns.equals("L5223:127.0.0.1:5222"));
	}

	@Test
	public void testSetConnectionName() {
		instance.setConnectionName("CONN1");
		String cn = instance.getConnectionName();
		assertTrue(cn.equals("CONN1"));
	}
}
