package org.apache.jmeter.protocl.ssh;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

//import org.apache.jmeter.control.LoopController;
//import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.ssh.sampler.SSHCommandSamplerExtra;
//import org.apache.jmeter.protocol.ssh.sampler.SendCommandSSHSessionSamplerBeanInfo;
//import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
		this.instance = null;// tbd close connection
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
		 this.instance.setUsername("id093108");
		 this.instance.setUseTty(false);
 
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
		instance.setUsername("id093108");
		String un = instance.getUsername();
		assertTrue(un.equals("id093108"));
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
