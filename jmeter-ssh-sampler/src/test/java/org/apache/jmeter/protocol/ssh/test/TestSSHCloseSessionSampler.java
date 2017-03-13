package org.apache.jmeter.protocol.ssh.test ;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jmeter.protocol.ssh.sampler.CloseSSHSessionSampler;
import org.apache.jmeter.protocol.ssh.sampler.DumpSSHSessionSampler;
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

public class TestSSHCloseSessionSampler {
	private final static Logger LOG = Logger.getLogger(TestSendCommandSSHSessionSampler.class.getName());
	private SSHCommandSamplerExtra instance = null;
	static private Thread sshts = null;

	// Sometimes several tests need to share computationally expensive setup
	// (like logging into a database).
	@BeforeClass
	public static void setUpClass() throws Exception {
		sshts = new Thread(new SSHTestServer());
		sshts.start();
		Thread.sleep(1000);
		Log.info("ssh test server thread started");
	}

	// If you allocate expensive external resources in a
	// BeforeClass method you need to release them after all the tests in the
	// class have run.
	@AfterClass
	public static void tearDownClass() throws Exception {
		sshts.interrupt();

	}

	@Before
	public void setUp() throws Exception {
		// this.instance = new SSHCommandSamplerExtra(); // for some reason this
		// must be there or else it fails
	}

	@After
	public void tearDown() throws Exception {
		// super.tearDown();
		this.instance = null;// close connection and cleanup of globaldata in
								// test itself
	}

	@Test
	public void testCloseSSHSessionSampler() {
		this.instance = new SSHCommandSamplerExtra();
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

		SampleResult sr = this.instance.sample(null);
		Integer errorCount = sr.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		String responseCode = sr.getResponseCode();
		assertTrue(responseCode.equals("200"));
		LOG.log(Level.INFO, "response code:" + responseCode);
		String responseMessage = sr.getResponseMessage();
		assertTrue("response Message is OK", responseMessage.equals("OK"));
		LOG.log(Level.INFO, "response message:" + responseMessage);
		String responseData = sr.getResponseDataAsString();
		assertTrue("contains stderr in response Data", responseData.contains("=== stderr ==="));
		assertTrue("contains stderr in response Data", responseData.contains("Welcome to Application Shell"));
		LOG.log(Level.INFO, "response data as string:" + responseData);
		// sr.connectEnd();
		// sr.cleanAfterSample();

		SendCommandSSHSessionSampler sender = new SendCommandSSHSessionSampler();
		sender.setCommand("ls");
		sender.setConnectionName("CONNECT1");
		sender.setPrintStdErr(true);
		sender.setUseReturnCode(true);
		LOG.log(Level.INFO, "calling command sender");
		SampleResult sr2 = sender.sample(null);
		Integer errorCount2 = sr2.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount2 == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount2));
		LOG.log(Level.INFO, "content type:" + sr2.getContentType());
		String responseCode2 = sr2.getResponseCode();
		assertTrue(responseCode2.equals("0"));
		LOG.log(Level.INFO, "response code:" + responseCode2);
		String responseMessage2 = sr2.getResponseMessage();
		assertTrue("response Message is OK", responseMessage2.equals("OK"));
		LOG.log(Level.INFO, "response message:" + responseMessage2);
		String responseData2 = sr2.getResponseDataAsString();
		assertTrue("contains stderr in response Data", responseData2.contains("=== stderr ==="));
		assertTrue("contains Welcome to Application Shell in response data",
				responseData2.contains("Welcome to Application Shell"));
		assertTrue("contains file1 file2 in response Data", responseData2.contains("file1 file2"));
		LOG.log(Level.INFO, "response data as string:" + responseData2);

		
		CloseSSHSessionSampler css = new CloseSSHSessionSampler();
		css.setConnectionName("CONNECT1");
		SampleResult sr5 =css.sample(null);
		Integer errorCount5 = sr5.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount5 == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount5));
		LOG.log(Level.INFO, "content type:" + sr5.getContentType());
		String responseCode5 = sr5.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode5);
		assertTrue(responseCode5.equals("0"));
		String responseMessage5 = sr5.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage5);
		assertTrue("response Message is connection CONNECT1 closed", responseMessage5.equals("connection CONNECT1 closed"));
		String responseData5 = sr5.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData5);
		assertTrue("resonse data empty", responseData5.equals(""));
		String samplerData5 =sr5.getSamplerData();
		LOG.log(Level.INFO, "sampler data:" + samplerData5);
		assertTrue("sampler data is close ssh connection CONNECT1", samplerData5.equals("close ssh connection CONNECT1"));
		
		SshSession sess = GlobalDataSsh.GetSessionByName("CONNECT1");
		// clean up before assert
		if (sess != null) {
			try {
				sess.disconnect();
			} catch (Exception e) {
			}
			LOG.log(Level.INFO, "removing session GlobalDataSsh from CONNECT1,session not correctly closed");

			GlobalDataSsh.removeSession("CONNECT1");
		}
		assertTrue("connection CONNECT1 sucessfully closed",sess==null);
	}

	@Test
	public void testCloseSSHSessionSamplerMultipleconnections() {
		this.instance = new SSHCommandSamplerExtra();
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

		SampleResult sr = this.instance.sample(null);
		Integer errorCount = sr.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		String responseCode = sr.getResponseCode();
		assertTrue(responseCode.equals("200"));
		LOG.log(Level.INFO, "response code:" + responseCode);
		String responseMessage = sr.getResponseMessage();
		assertTrue("response Message is OK", responseMessage.equals("OK"));
		LOG.log(Level.INFO, "response message:" + responseMessage);
		String responseData = sr.getResponseDataAsString();
		assertTrue("contains stderr in response Data", responseData.contains("=== stderr ==="));
		assertTrue("contains stderr in response Data", responseData.contains("Welcome to Application Shell"));
		LOG.log(Level.INFO, "response data as string:" + responseData);
		// sr.connectEnd();
		// sr.cleanAfterSample();
		
		SSHCommandSamplerExtra instance2 = new SSHCommandSamplerExtra();
		instance2.setCommand("dir");
		instance2.setConnectionName("CONNECT2");
		instance2.setConnectionTimeout(32000);
		instance2.setSessionKeepAliveSeconds(23000);
		instance2.setHostname("127.0.0.1");
		instance2.setPassword("azerty!");
		instance2.setPort(5222);
		instance2.setPrintStdErr(true);
		instance2.setUseReturnCode(false);
		instance2.setUsername("johan");
		instance2.setUseTty(false);
		instance2.setCloseConnection(false);
		SampleResult sr4 = instance2.sample(null);
		Integer errorCount4 = sr4.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount4 == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount4));
		LOG.log(Level.INFO, "content type:" + sr4.getContentType());
		String responseCode4 = sr4.getResponseCode();
		assertTrue(responseCode4.equals("200"));
		LOG.log(Level.INFO, "response code:" + responseCode4);
		String responseMessage4 = sr4.getResponseMessage();
		assertTrue("response Message is OK", responseMessage4.equals("OK"));
		LOG.log(Level.INFO, "response message:" + responseMessage4);
		String responseData4 = sr4.getResponseDataAsString();
		assertTrue("contains stderr in response Data", responseData4.contains("=== stderr ==="));
		assertTrue("contains stderr in response Data", responseData4.contains("Welcome to Application Shell"));

		SendCommandSSHSessionSampler sender = new SendCommandSSHSessionSampler();
		sender.setCommand("ls");
		sender.setConnectionName("CONNECT1");
		sender.setPrintStdErr(true);
		sender.setUseReturnCode(true);
		LOG.log(Level.INFO, "calling command sender");
		SampleResult sr2 = sender.sample(null);
		Integer errorCount2 = sr2.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount2 == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount2));
		LOG.log(Level.INFO, "content type:" + sr2.getContentType());
		String responseCode2 = sr2.getResponseCode();
		assertTrue(responseCode2.equals("0"));
		LOG.log(Level.INFO, "response code:" + responseCode2);
		String responseMessage2 = sr2.getResponseMessage();
		assertTrue("response Message is OK", responseMessage2.equals("OK"));
		LOG.log(Level.INFO, "response message:" + responseMessage2);
		String responseData2 = sr2.getResponseDataAsString();
		assertTrue("contains stderr in response Data", responseData2.contains("=== stderr ==="));
		assertTrue("contains Welcome to Application Shell in response data",
				responseData2.contains("Welcome to Application Shell"));
		assertTrue("contains file1 file2 in response Data", responseData2.contains("file1 file2"));
		LOG.log(Level.INFO, "response data as string:" + responseData2);

		DumpSSHSessionSampler dumper = new DumpSSHSessionSampler();
		dumper.setConnectionName("");

		LOG.log(Level.INFO, "calling session dump sampler");
		SampleResult sr3 = dumper.sample(null);
		Integer errorCount3 = sr3.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount3 == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount3));
		LOG.log(Level.INFO, "content type:" + sr3.getContentType());
		String responseCode3 = sr3.getResponseCode();
		assertTrue(responseCode3.equals("Connection(s) Found"));
		LOG.log(Level.INFO, "response code:" + responseCode3);
		String responseMessage3 = sr3.getResponseMessage();
		assertTrue("response Message is OK", responseMessage3.equals("OK"));
		LOG.log(Level.INFO, "response message:" + responseMessage3);
		String responseData3 = sr3.getResponseDataAsString();
		assertTrue("contains CONNECT1 in response Data", responseData3.contains("CONNECT1"));
		assertTrue("contains CONNECT2 in response Data", responseData3.contains("CONNECT2"));
		LOG.log(Level.INFO, "response data as string:" + responseData3);
		
		CloseSSHSessionSampler css = new CloseSSHSessionSampler();
		css.setConnectionName("CONNECT1");
		SampleResult sr5 =css.sample(null);
		Integer errorCount5 = sr5.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount5));
		assertTrue("ErrorCount is 0", errorCount5 == 0);
		LOG.log(Level.INFO, "content type:" + sr5.getContentType());
		String responseCode5 = sr5.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode5);
		assertTrue(responseCode5.equals("0"));
		String responseMessage5 = sr5.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage5);
		assertTrue("response Message is connection CONNECT1 closed", responseMessage5.equals("connection CONNECT1 closed"));
		String responseData5 = sr5.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData5);
		assertTrue("resonse data empty", responseData5.equals(""));
		String samplerData5 =sr5.getSamplerData();
		LOG.log(Level.INFO, "sampler data:" + samplerData5);
		assertTrue("sampler data is close ssh connection CONNECT1", samplerData5.equals("close ssh connection CONNECT1"));
		
		
		CloseSSHSessionSampler css2 = new CloseSSHSessionSampler();
		css2.setConnectionName("CONNECT2");
		SampleResult sr6 =css2.sample(null);
		Integer errorCount6 = sr6.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount6));
		assertTrue("ErrorCount is 0", errorCount6 == 0);
		LOG.log(Level.INFO, "content type:" + sr6.getContentType());
		String responseCode6 = sr6.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode6);
		assertTrue("responsecode is 0", responseCode6.equals("0"));
		String responseMessage6 = sr6.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage6);
		assertTrue("response Message is connection CONNECT2 closed", responseMessage6.equals("connection CONNECT2 closed"));
		String responseData6 = sr6.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData6);
		assertTrue("resonse data empty", responseData6.equals(""));
		String samplerData6 =sr6.getSamplerData();
		LOG.log(Level.INFO, "sampler data:" + samplerData6);
		assertTrue("sampler data is close ssh connection CONNECT2", samplerData6.equals("close ssh connection CONNECT2"));

		//check globalSSHData is empty
		SshSession sess = GlobalDataSsh.GetSessionByName("CONNECT1");
		// clean up before assert
		if (sess != null) {
			try {
				sess.disconnect();
			} catch (Exception e) {
			}
			LOG.log(Level.INFO, "removing session GlobalDataSsh from CONNECT1,session not correctly closed");

			GlobalDataSsh.removeSession("CONNECT1");
		}
		assertTrue("connection CONNECT1 sucessfully closed",sess==null);

	 
		// clean up before assert
		sess = GlobalDataSsh.GetSessionByName("CONNECT2");
		if (sess != null) {
			try {
				sess.disconnect();
			} catch (Exception e) {
			}
			LOG.log(Level.INFO, "removing session GlobalDataSsh from CONNECT2, session not closed correctly");

			GlobalDataSsh.removeSession("CONNECT2");
		}
		assertTrue("connection CONNECT2 sucessfully closed",sess==null);

	}
	@Test
	public void testCloseSSHSessionSamplerEmptyConnection() {
	
		CloseSSHSessionSampler css = new CloseSSHSessionSampler();
		css.setConnectionName("");
		SampleResult sr5 =css.sample(null);
		Integer errorCount5 = sr5.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount5));
		assertTrue("ErrorCount is 1", errorCount5 == 1);		
		LOG.log(Level.INFO, "content type:" + sr5.getContentType());
		String responseCode5 = sr5.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode5);
		assertTrue(responseCode5.equals("-1"));
		String responseMessage5 = sr5.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage5);
		assertTrue("response Message is connection name not configured in sampler!", responseMessage5.equals("connection name not configured in sampler!"));
		String responseData5 = sr5.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData5);
		assertTrue("resonse data empty", responseData5.equals(""));
		String samplerData5 =sr5.getSamplerData();
		LOG.log(Level.INFO, "sampler data:" + samplerData5);
		assertTrue("sampler data is Invalid sampler configuration connection name is required", samplerData5.equals("Invalid sampler configuration connection name is required"));
		
	}
	@Test
	public void testCloseSSHSessionSamplerNonExistingConnection() {
	
		CloseSSHSessionSampler css = new CloseSSHSessionSampler();
		css.setConnectionName("CCCC");
		SampleResult sr5 =css.sample(null);
		Integer errorCount5 = sr5.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount5));
		assertTrue("ErrorCount is 1", errorCount5 == 1);		
		LOG.log(Level.INFO, "content type:" + sr5.getContentType());
		String responseCode5 = sr5.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode5);
		assertTrue(responseCode5.equals("-2"));
		String responseMessage5 = sr5.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage5);
		assertTrue("response Message is connection CCCC not found", responseMessage5.equals("connection CCCC not found"));
		String responseData5 = sr5.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData5);
		assertTrue("resonse data empty", responseData5.equals(""));
		String samplerData5 =sr5.getSamplerData();
		LOG.log(Level.INFO, "sampler data:" + samplerData5);
		assertTrue("sampler data is close ssh connection CCCC", samplerData5.equals("close ssh connection CCCC"));
		
	}
	@Test
	public void testCloseClosedSSHSessionSampler() {
		this.instance = new SSHCommandSamplerExtra();
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

		SampleResult sr = this.instance.sample(null);
		Integer errorCount = sr.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		String responseCode = sr.getResponseCode();
		assertTrue(responseCode.equals("200"));
		LOG.log(Level.INFO, "response code:" + responseCode);
		String responseMessage = sr.getResponseMessage();
		assertTrue("response Message is OK", responseMessage.equals("OK"));
		LOG.log(Level.INFO, "response message:" + responseMessage);
		String responseData = sr.getResponseDataAsString();
		assertTrue("contains stderr in response Data", responseData.contains("=== stderr ==="));
		assertTrue("contains stderr in response Data", responseData.contains("Welcome to Application Shell"));
		LOG.log(Level.INFO, "response data as string:" + responseData);
		// sr.connectEnd();
		// sr.cleanAfterSample();
		
		SshSession sess = GlobalDataSsh.GetSessionByName("CONNECT1");
		// clean up before assert
		if (sess != null) {
			try {
				sess.disconnect();
			} catch (Exception e) {
			}
			LOG.log(Level.INFO, "GlobalDataSsh from CONNECT1,session disconnected");

			
		}
	
		
		CloseSSHSessionSampler css = new CloseSSHSessionSampler();
		css.setConnectionName("CONNECT1");
		SampleResult sr5 =css.sample(null);
		Integer errorCount5 = sr5.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount5));
		assertTrue("ErrorCount is 0", errorCount5 == 0);
		LOG.log(Level.INFO, "content type:" + sr5.getContentType());
		String responseCode5 = sr5.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode5);
		assertTrue(responseCode5.equals("0"));
		String responseMessage5 = sr5.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage5);
		assertTrue("response Message is connection CONNECT1 closed", responseMessage5.equals("connection CONNECT1 closed"));
		String responseData5 = sr5.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData5);
		assertTrue("resonse data empty", responseData5.equals(""));
		String samplerData5 =sr5.getSamplerData();
		LOG.log(Level.INFO, "sampler data:" + samplerData5);
		assertTrue("sampler data is close ssh connection CONNECT1", samplerData5.equals("close ssh connection CONNECT1"));
		
		sess = GlobalDataSsh.GetSessionByName("CONNECT1");
		// clean up before assert
		if (sess != null) {
			try {
				sess.disconnect();
			} catch (Exception e) {
			}
			LOG.log(Level.INFO, "GlobalDataSsh from CONNECT1,session disconnected");
			GlobalDataSsh.removeSession("CONNECT1");
			
		}
		assertTrue("session correctly removed",sess==null);
	}
}
