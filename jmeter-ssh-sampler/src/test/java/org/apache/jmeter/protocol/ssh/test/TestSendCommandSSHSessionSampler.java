package org.apache.jmeter.protocol.ssh.test;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jmeter.protocol.ssh.sampler.GlobalDataSsh;
import org.apache.jmeter.protocol.ssh.sampler.SSHCommandSamplerExtra;
import org.apache.jmeter.protocol.ssh.sampler.SendCommandSSHSessionSampler;
import org.apache.jmeter.protocol.ssh.sampler.SshSession;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jcraft.jsch.Session;

import jline.internal.Log;

public class TestSendCommandSSHSessionSampler {
	private final static Logger LOG  =Logger.getLogger(TestSendCommandSSHSessionSampler.class.getName());
	private SSHCommandSamplerExtra instance = null;
	static private Thread sshts=null;

	
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
	@Before
	public void setUp() throws Exception {
	 	this.instance = new SSHCommandSamplerExtra();  //  for some reason this must be there or else it fails
	}

	@After
	public void tearDown() throws Exception {
		//super.tearDown();
		this.instance = null;//  close connection and cleanup of globaldata in test itself
	}

	@Test
	public void testSampleSendCommandSession() {
		
		 this.instance=new SSHCommandSamplerExtra();           
		 this.instance.setCommand("dir");
		 this.instance.setConnectionName("CONNECT1");
		 this.instance.setConnectionTimeout(32000);
		 this.instance.setSessionKeepAliveSeconds(23000);
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
		 //sr.connectEnd();
		 //sr.cleanAfterSample();
		 
		 SendCommandSSHSessionSampler sender = new SendCommandSSHSessionSampler();
		 sender.setCommand("ls");
		 sender.setConnectionName("CONNECT1");
		 sender.setPrintStdErr(true);
		 sender.setUseReturnCode(true);
		 LOG.log(Level.INFO, "calling command sender");
		 SampleResult sr2=sender.sample(null);
		 Integer errorCount2= sr2.getErrorCount();
		 assertTrue("ErrorCount is 0",errorCount2==0);
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount2));
		 LOG.log(Level.INFO, "content type:"+sr2.getContentType());
		 String responseCode2=sr2.getResponseCode();
		 assertTrue(responseCode2.equals("0"));
		 LOG.log(Level.INFO, "response code:"+responseCode2);
		 String responseMessage2=sr2.getResponseMessage();
		 assertTrue("response Message is OK",responseMessage2.equals("OK") );
		 LOG.log(Level.INFO, "response message:"+responseMessage2);
		 String responseData2=sr2.getResponseDataAsString();
		 assertTrue("contains stderr in response Data", responseData2.contains("=== stderr ==="));
		 assertTrue("contains Welcome to Application Shell in response data", responseData2.contains("Welcome to Application Shell"));
		 assertTrue("contains file1 file2 in response Data", responseData2.contains("file1 file2"));
		 LOG.log(Level.INFO, "response data as string:"+responseData2);
	
		 
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONNECT1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONNECT1, test send command completed" );

			 GlobalDataSsh.removeSession("CONNECT1");
		 }
	}	 
	@Test
	public void testSampleSendCommandSessionUnknownSession() {
		

		 SshSession sess=GlobalDataSsh.GetSessionByName("CONNECT1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONNECT1, test send command ready to start" );

			 GlobalDataSsh.removeSession("CONNECT1");
		 } 
		 SendCommandSSHSessionSampler sender = new SendCommandSSHSessionSampler();
		 sender.setCommand("ls");
		 sender.setConnectionName("CONNECT1");
		 sender.setPrintStdErr(true);
		 sender.setUseReturnCode(true);
		 LOG.log(Level.INFO, "calling command sender");
		 SampleResult sr2=sender.sample(null);
		 Integer errorCount2= sr2.getErrorCount();
		 assertTrue("ErrorCount is 1",errorCount2==1);
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount2));
		 LOG.log(Level.INFO, "content type:"+sr2.getContentType());
		 String responseCode2=sr2.getResponseCode();
		 assertTrue(responseCode2.equals("Connection Not Found"));
		 LOG.log(Level.INFO, "response code:"+responseCode2);
		 String responseMessage2=sr2.getResponseMessage();
		 assertTrue("response Message is SendCommandSSHSessionSampler connection CONNECT1 not found",responseMessage2.equals("SendCommandSSHSessionSampler connection CONNECT1 not found") );
		 LOG.log(Level.INFO, "response message:"+responseMessage2);
		 String responseData2=sr2.getResponseDataAsString();
		 assertTrue("response data is empty string", responseData2.equals(""));
		 LOG.log(Level.INFO, "response data as string:"+responseData2);
	
		 
	}
	@Test
	public void testSampleSendCommandSessionEmptyCommand() {
		
		 this.instance=new SSHCommandSamplerExtra();           
		 this.instance.setCommand("dir");
		 this.instance.setConnectionName("CONNECT1");
		 this.instance.setConnectionTimeout(32000);
		 this.instance.setSessionKeepAliveSeconds(23000);
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


		 SendCommandSSHSessionSampler sender = new SendCommandSSHSessionSampler();
		 sender.setCommand("");
		 sender.setConnectionName("CONNECT1");
		 sender.setPrintStdErr(true);
		 sender.setUseReturnCode(true);
		 LOG.log(Level.INFO, "calling command sender");
		 SampleResult sr2=sender.sample(null);
		 Integer errorCount2= sr2.getErrorCount();
		 assertTrue("ErrorCount is 1",errorCount2==1);
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount2));
		 LOG.log(Level.INFO, "content type:"+sr2.getContentType());
		 String responseCode2=sr2.getResponseCode();
		 assertTrue(responseCode2.equals("No Command Configured"));
		 LOG.log(Level.INFO, "response code:"+responseCode2);
		 String responseMessage2=sr2.getResponseMessage();
		 assertTrue("response Message is SSendCommandSSHSessionSampler No Command Configured",responseMessage2.equals("SendCommandSSHSessionSampler No Command Configured") );
		 LOG.log(Level.INFO, "response message:"+responseMessage2);
		 String responseData2=sr2.getResponseDataAsString();
		 //assertTrue("response data is empty string", responseData2.equals(""));
		 LOG.log(Level.INFO, "response data as string:"+responseData2);	 
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONNECT1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONNECT1, test send empty command completed" );

			 GlobalDataSsh.removeSession("CONNECT1");
		 } 
	}	
	@Test
	public void testSampleSendCommandSessionDoNotShowStdErr() {
		
		 this.instance=new SSHCommandSamplerExtra();           
		 this.instance.setCommand("dir");
		 this.instance.setConnectionName("CONNECT1");
		 this.instance.setConnectionTimeout(320000);
		 this.instance.setSessionKeepAliveSeconds(230000);
		 this.instance.setHostname("127.0.0.1");
		 this.instance.setPassword("azerty!");
		 this.instance.setPort(5222);
		 this.instance.setPrintStdErr(true);
		 this.instance.setUseReturnCode(true);
		 this.instance.setUsername("johan");
		 this.instance.setUseTty(false);
		 this.instance.setCloseConnection(false);
 
		 SampleResult sr= this.instance.sample(null) ;
		 Integer errorCount= sr.getErrorCount();
		 LOG.log(Level.INFO, "errorcount:"+ Integer.toString(errorCount));
		 assertTrue("ErrorCount is 0",errorCount==0);
		
		 LOG.log(Level.INFO, "content type:"+sr.getContentType());
		 String responseCode=sr.getResponseCode();
		 LOG.log(Level.INFO, "response code:"+responseCode);
		 assertTrue(responseCode.equals("0"));
		 
		 String responseMessage=sr.getResponseMessage();
		 LOG.log(Level.INFO, "response message:"+responseMessage);
		 assertTrue("response Message is OK",responseMessage.equals("OK") );
		 String responseData=sr.getResponseDataAsString();
		 assertTrue("contains stderr in response Data", responseData.contains("=== stderr ==="));
		 assertTrue("contains stderr in response Data", responseData.contains("Welcome to Application Shell"));
		 LOG.log(Level.INFO, "response data as string:"+responseData);
		 
		 SendCommandSSHSessionSampler sender = new SendCommandSSHSessionSampler();
		 sender.setCommand("ls");
		 sender.setConnectionName("CONNECT1");
		 sender.setPrintStdErr(false);
		 sender.setUseReturnCode(true);
		 sender.setUseTty(false);
		 
		 LOG.log(Level.INFO, "calling command sender");
		 SampleResult sr2=sender.sample(null);
		 Integer errorCount2= sr2.getErrorCount();
		 LOG.log(Level.INFO, "errorcount2:"+ Integer.toString(errorCount2));
		 assertTrue("ErrorCount is 0",errorCount2==0);
		 
		 LOG.log(Level.INFO, "content type:"+sr2.getContentType());
		 String responseCode2=sr2.getResponseCode();
		 LOG.log(Level.INFO, "response code2:"+responseCode2);
		 assertTrue("responsecode2 ",responseCode2.equals("0")||responseCode2.equals("-1"));		 
		 String responseMessage2=sr2.getResponseMessage();
		 LOG.log(Level.INFO, "response message2:"+responseMessage2);
		 assertTrue("response Message is OK",responseMessage2.equals("OK") );		 
		 String responseData2=sr2.getResponseDataAsString();
		 assertTrue("contains no stderr in response Data", !responseData2.contains("=== stderr ==="));
		 assertTrue("contains Welcome to Application Shell in response data", responseData2.contains("Welcome to Application Shell"));
		 assertTrue("contains file1 file2 in response Data", responseData2.contains("file1 file2"));
		 LOG.log(Level.INFO, "response data as string:"+responseData2);
	
		 
		 SshSession sess=GlobalDataSsh.GetSessionByName("CONNECT1");
		 // clean up before assert
		 if (sess !=null)
		 {
			 try {
				 sess.disconnect();
			 }
			 catch(Exception e) {}
			 LOG.log(Level.INFO, "removing session GlobalDataSsh from CONNECT1, test send command completed" );

			 GlobalDataSsh.removeSession("CONNECT1");
		 }
	}	
	
}
