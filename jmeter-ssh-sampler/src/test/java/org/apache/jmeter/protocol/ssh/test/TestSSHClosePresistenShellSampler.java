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

import org.apache.jmeter.protocol.ssh.sampler.SSHCommandSamplerExtra;
import org.apache.jmeter.protocol.ssh.sampler.SSHOpenPersistentShellSampler;
import org.apache.jmeter.protocol.ssh.sampler.SSHPersistentShellSendCommandSampler;
import org.apache.jmeter.protocol.ssh.sampler.SshSession;
import org.apache.jmeter.protocol.ssh.sampler.GlobalDataSsh;
import org.apache.jmeter.protocol.ssh.sampler.SSHClosePersistentShellSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import jline.internal.Log;



public class TestSSHClosePresistenShellSampler {
	private final static Logger LOG = Logger.getLogger(TestSSHClosePresistenShellSampler.class.getName());
	private SSHCommandSamplerExtra instance = null;
	static private Thread sshts = null;

	public TestSSHClosePresistenShellSampler() {
		// super(name);
	}

	// Sometimes several tests need to share computationally expensive setup
	// (like logging into a database).
	@BeforeClass
	public static void setUpClass() throws Exception {
		GlobalDataSsh.removeAllSessions();

		sshts = new Thread(new SSHTestServer());
		sshts.start();
		Thread.sleep(1000);
		Log.info("ssh test server thread started");
	}

	// If you allocate expensive external resources in a
	// BeforeClass method you need to release them after all the tests in the
	// class have run.
	@AfterClass
	public static void tearDownClass() {
		sshts.interrupt();

	}

	// When writing tests, it is common to find that several tests need similar
	// objects created before they can run
	@Before
	public void setUp() {
		// super.setUp();

		this.instance = new SSHCommandSamplerExtra();
		// LOG.log(Level.INFO,"created new instance of SSHCommandSamplerExtra");

	}

	// If you allocate external resources in a Before method you need to release
	// them after the test runs.
	@After
	public void tearDown()  {
		// super.tearDown();
		this.instance = null;// close connection and cleanup of globaldata in
								// test itself
	}

	// The Test annotation tells JUnit that the public void method to which it
	// is attached can be run as a test case.
	@Test
	public void testSSHPersistentShellSendCommandSampler() {
		// open ssh session
		this.instance = new SSHCommandSamplerExtra();
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
		SampleResult sr = this.instance.sample(null);
		int errorCount = sr.getErrorCount();
		assertTrue("ErrorCount is 0", errorCount == 0);
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		String responseCode = sr.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode);
		assertTrue(responseCode.equals("Connection Successful"));
		String responseMessage = sr.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage);
		assertTrue("response Message is SSH Connection established, no Command Executed",
				responseMessage.equals("SSH Connection established, no Command Executed"));
		String responseData = sr.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData);
		// open persistent shell
		SSHOpenPersistentShellSampler pss = new SSHOpenPersistentShellSampler();
		pss.setConnectionName("CONN1");
		pss.setShellName("SHELL1");
		pss.setResultEncoding("UTF-8");
		pss.setStripPrompt(true);
		sr = pss.sample(null);
		errorCount = sr.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		assertTrue("ErrorCount is 0", errorCount == 0);
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		responseCode = sr.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode);
		assertTrue(responseCode.equals("0"));
		responseMessage = sr.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage);
		assertTrue("response Message is OK", responseMessage.equals("Shell with name SHELL1 opened on CONN1"));
		LOG.log(Level.INFO, "response message:" + responseMessage);
		responseData = sr.getResponseDataAsString();
		// assertTrue("Shell with name SHELL1 opened on CONN1 in response Data",
		// responseData.contains("Shell with name SHELL1 opened on CONN1"));
		LOG.log(Level.INFO, "response data as string:" + responseData);
		// send dir command to persistent shell
		SSHPersistentShellSendCommandSampler pssc = new SSHPersistentShellSendCommandSampler();
		pssc.setCommand("dir");
		pssc.setConnectionName("CONN1");
		pssc.setShellName("SHELL1");
		pssc.setResultEncoding("UTF-8");
		pssc.setStripPrompt(true);
		pssc.setStripCommand(true);
		sr = pssc.sample(null);
		errorCount = sr.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		assertTrue("ErrorCount is 0", errorCount == 0);
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		responseCode = sr.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode);
		assertTrue(responseCode.equals("0"));
		responseMessage = sr.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage);
		assertTrue("response Message is Command dir sent on shell SHELL1 on CONN1",
				responseMessage.equals("Command dir sent on shell SHELL1 on CONN1"));
		responseData = sr.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData);
		assertTrue("======> ", responseData.contains("======> \"dir\""));

		SshSession sess = GlobalDataSsh.GetSessionByName("CONN1");
		assertTrue("session is not null", sess != null);
		String gcd = GlobalDataSsh.getAllConnectionData("");
		LOG.log(Level.INFO, "global connection data:" + gcd);
		assertTrue("all connection data == CONN1[ShellChannels[SHELL1],SFTPChannels[]]",
				gcd.equals("CONN1[ShellChannels[SHELL1],SFTPChannels[]]"));
		// close the persistent shell
		SSHClosePersistentShellSampler cs = new SSHClosePersistentShellSampler();
		cs.setConnectionName("CONN1");
		cs.setShellName("SHELL1");
		sr = cs.sample(null);
		errorCount = sr.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		// assertTrue("ErrorCount is 0",errorCount==0);
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		// responseCode=sr.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode);
		// assertTrue(responseCode.equals("0"));
		responseMessage = sr.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage);
		// assertTrue("response Message is OK",responseMessage.equals("Shell
		// with name SHELL1 opened on CONN1") );
		responseData = sr.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData);
		// assertTrue("Shell with name SHELL1 opened on CONN1 in response Data",
		// responseData.contains("Shell with name SHELL1 opened on CONN1"));

		gcd = GlobalDataSsh.getAllConnectionData("");
		LOG.log(Level.INFO, "All connection data:" + gcd);
		assertTrue("all connection data == CONN1[ShellChannels[],SFTPChannels[]]",
				gcd.equals("CONN1[ShellChannels[],SFTPChannels[]]"));

		// clean up before assert
		if (sess != null) {
			try {
				sess.disconnect(); // closes all shells
			} catch (Exception e) {
				LOG.log(Level.WARNING ,"failed to disconnect");
			}
			LOG.log(Level.INFO, "removing ssh session GlobalDataSsh from CONN1");

			GlobalDataSsh.removeSession("CONN1");
		}

	}

}
