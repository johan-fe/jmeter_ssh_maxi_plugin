package org.apache.jmeter.protocol.ssh.test;
 

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jmeter.protocol.ssh.sampler.GlobalDataSsh;
//import org.apache.jmeter.control.LoopController;
//import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.ssh.sampler.SSHCommandSamplerExtra;
import org.apache.jmeter.protocol.ssh.sampler.SSHOpenPersistentShellSampler;
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
public class TestSSHOpenPresistenShellSampler {
	private final static Logger LOG  =Logger.getLogger(TestSSHOpenPresistenShellSampler.class.getName());
	private SSHCommandSamplerExtra instance = null;
	static private Thread sshts=null;

	public TestSSHOpenPresistenShellSampler() {
		//super(name);
	}
	//Sometimes several tests need to share computationally expensive setup (like logging into a database).	
	@BeforeClass
	public static void setUpClass() throws Exception {
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
	public void testSSHOpenPersistentShellSampler() {
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
		 this.instance.setCloseConnection(false);
 
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
		 
		 SSHOpenPersistentShellSampler pss = new SSHOpenPersistentShellSampler();
		 pss.setConnectionName("CONN1");
		 pss.setShellName("SHELL1");
		 sr= pss.sample(null);
		 errorCount= sr.getErrorCount();
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount));
		 assertTrue("ErrorCount is 0",errorCount==0); 
		 LOG.log(Level.INFO, "content type:"+sr.getContentType());
		 responseCode=sr.getResponseCode();
		 LOG.log(Level.INFO, "response code:"+responseCode);
		 assertTrue(responseCode.equals("0"));
		 responseMessage=sr.getResponseMessage();
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 assertTrue("response Message is OK",responseMessage.equals("Shell with name SHELL1 opened on CONN1") );
		 responseData=sr.getResponseDataAsString();
		 LOG.log(Level.INFO, "response data as string:"+responseData);
		 assertTrue("response data contains Welcome to Application Shell.", responseData.contains("Welcome to Application Shell."));
		 

		 
		 pss = new SSHOpenPersistentShellSampler();
		 pss.setConnectionName("CONN1");
		 pss.setShellName("SHELL2");
		 sr= pss.sample(null);
		 errorCount= sr.getErrorCount();
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount));
		 assertTrue("ErrorCount is 0",errorCount==0); 
		 LOG.log(Level.INFO, "content type:"+sr.getContentType());
		 responseCode=sr.getResponseCode();
		 LOG.log(Level.INFO, "response code:"+responseCode);
		 assertTrue(responseCode.equals("0"));
		 responseMessage=sr.getResponseMessage();
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 assertTrue("response Message is OK",responseMessage.equals("Shell with name SHELL2 opened on CONN1") );
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 responseData=sr.getResponseDataAsString();
		 LOG.log(Level.INFO, "response data as string:"+responseData);
		 assertTrue("response data contains Welcome to Application Shell.", responseData.contains("Welcome to Application Shell."));
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONN1");
		 assertTrue("session CONN1 is not null", sess!=null);
		 
		 String gcd = GlobalDataSsh.getAllConnectionData("");
		 assertTrue("all connection data == CONN1[ChannelShells[SHELL2,SHELL1]]",gcd.equals("CONN1[ChannelShells[SHELL2,SHELL1]]") );
		 LOG.log(Level.INFO,"all connection data:\n"+gcd);
		 // clean up before assert
		 if (sess !=null)
		 {
			 
			 try {
				 sess.disconnect();//disconnecting all sessions closes as well all channelshells
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing ssh session GlobalDataSsh from CONN1" );

			 GlobalDataSsh.removeSession("CONN1");
		 }
		 
	}

}
