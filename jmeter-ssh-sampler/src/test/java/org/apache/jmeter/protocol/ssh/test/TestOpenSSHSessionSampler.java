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
import org.apache.jmeter.protocol.ssh.sampler.OpenSSHSessionSampler;
import org.apache.jmeter.protocol.ssh.sampler.SSHCommandSamplerExtra;
import org.apache.jmeter.protocol.ssh.sampler.SendCommandSSHSessionSampler;
import org.apache.jmeter.protocol.ssh.sampler.SshSession;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.Thread;
//import org.apache.jmeter.protocol.ssh.test.SSHTestServer;
//import com.jcraft.jsch.Session;
//import org.hamcrest.core.Is;
//import jline.internal.Log;
//import org.junit.Assert;

public class TestOpenSSHSessionSampler {
	private final static Logger LOG = Logger.getLogger(TestOpenSSHSessionSampler.class.getName());
	private OpenSSHSessionSampler instance = null;
	static private Thread sshts = null;

	public TestOpenSSHSessionSampler() {
		// super(name);
	}
	// Sometimes several tests need to share computationally expensive setup
	// (like logging into a database).

	// If you allocate expensive external resources in a
	// BeforeClass method you need to release them after all the tests in the
	// class have run.
	@AfterClass
	public static void tearDownClass() throws Exception {
		sshts.interrupt();

	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		GlobalDataSsh.removeAllSessions();
		sshts = new Thread(new SSHTestServer());
		sshts.start();
		Thread.sleep(1000);
		LOG.info("ssh test server thread started");
	}

	// When writing tests, it is common to find that several tests need similar
	// objects created before they can run
	@Before
	public void setUp() throws Exception {
		// super.setUp();

		this.instance = new OpenSSHSessionSampler();
		// LOG.log(Level.INFO,"created new instance of SSHCommandSamplerExtra");

	}

	// If you allocate external resources in a Before method you need to release
	// them after the test runs.
	@After
	public void tearDown() throws Exception {
		// super.tearDown();
		this.instance = null;// close connection and cleanup of globaldata in
								// test itself
	}

	// The Test annotation tells JUnit that the public void method to which it
	// is attached can be run as a test case.
	@Test
	public void test_OpenSSHSessionSampler() {
		this.instance = new OpenSSHSessionSampler();
		this.instance.setConnectionName("CONN1");
		this.instance.setConnectionTimeout(3200);
		this.instance.setHostname("127.0.0.1");
		this.instance.setPassword("azerty!");
		this.instance.setPort(5222);
		this.instance.setUsername("johan");
		SampleResult sr = this.instance.sample(null);
		Integer errorCount = sr.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		assertTrue("ErrorCount is 0", errorCount == 0);
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		String responseCode = sr.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode);
		assertTrue(responseCode.equals("0"));
		String responseMessage = sr.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage);
		assertTrue("response message is Connected", responseMessage.equals("Connected"));
		String responseData = sr.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData);
		assertTrue("response data is Connected", responseData.equals("Connected"));
		String responseLabel = sr.getSampleLabel();
		LOG.log(Level.INFO, "response label as string:" + responseLabel);
		assertTrue(" responseLabel is OpenSSHSessionSampler Connect (johan@127.0.0.1)",
				responseLabel.equals("OpenSSHSessionSampler Connect (johan@127.0.0.1)"));
		String responseSamplerData = sr.getSamplerData();
		LOG.log(Level.INFO, "response sampler data as string:" + responseSamplerData);
		assertTrue("responseSamplerData is Open SSH connection (johan@127.0.0.1) with name: CONN1",
				responseSamplerData.equals("Open SSH connection (johan@127.0.0.1) with name: CONN1"));

		SshSession sess = GlobalDataSsh.GetSessionByName("CONN1");
		assertTrue("session is not null", sess != null);

		// clean up before assert
		if (sess != null) {
			try {
				sess.disconnect(); // closes all shells
			} catch (Exception e) {
				LOG.log(Level.INFO, "disconnect failed");
			}
			LOG.log(Level.INFO, "removing ssh session GlobalDataSsh from CONN1");

			GlobalDataSsh.removeSession("CONN1");
		}

	}

	@Test
	public void test_OpenSSHSessionSamplerWrongPasswd() {
		this.instance = new OpenSSHSessionSampler();
		this.instance.setConnectionName("CONN1");
		this.instance.setConnectionTimeout(3200);
		this.instance.setHostname("127.0.0.1");
		this.instance.setPassword("azer");
		this.instance.setPort(5222);
		this.instance.setUsername("johan");
		SampleResult sr = this.instance.sample(null);
		Integer errorCount = sr.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		assertTrue("ErrorCount is 0", errorCount == 1);
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		String responseCode = sr.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode);
		assertTrue(responseCode.equals("-1"));
		String responseMessage = sr.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage);
		assertTrue("response message is Connection Failed:Auth fail",
				responseMessage.equals("Connection Failed:Auth fail"));
		String responseData = sr.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData);
		assertTrue("response data is Connection Failed:Auth fail", responseData.equals("Connection Failed:Auth fail"));
		String responseLabel = sr.getSampleLabel();
		LOG.log(Level.INFO, "response label as string:" + responseLabel);
		assertTrue(" responseLabel is OpenSSHSessionSampler Connect (johan@127.0.0.1) failed",
				responseLabel.equals("OpenSSHSessionSampler Connect (johan@127.0.0.1) failed"));
		String responseSamplerData = sr.getSamplerData();
		LOG.log(Level.INFO, "response sampler data as string:" + responseSamplerData);
		assertTrue("responseSamplerData is Open SSH connection (johan@127.0.0.1) with name: CONN1",
				responseSamplerData.equals("Open SSH connection (johan@127.0.0.1) with name: CONN1"));

		SshSession sess = GlobalDataSsh.GetSessionByName("CONN1");
		assertTrue("session is not null", sess != null);

		// clean up before assert
		if (sess != null) {
			try {
				sess.disconnect(); // closes all shells
			} catch (Exception e) {
				LOG.log(Level.INFO, "disconnect failed");
			}
			LOG.log(Level.INFO, "removing ssh session GlobalDataSsh from CONN1");

			GlobalDataSsh.removeSession("CONN1");
		}

	}
}
