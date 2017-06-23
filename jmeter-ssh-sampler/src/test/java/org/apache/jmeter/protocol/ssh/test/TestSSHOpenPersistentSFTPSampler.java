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
import org.apache.jmeter.protocol.ssh.sampler.SSHOpenPersistentSFTPSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jline.internal.Log;

public class TestSSHOpenPersistentSFTPSampler {
	private final static Logger LOG = Logger.getLogger(TestSSHOpenPersistentSFTPSampler.class.getName());
	protected OpenSSHSessionSampler instance;
	static private Thread sshts = null;

	public TestSSHOpenPersistentSFTPSampler() {
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
	public void setUp()  {
		// super.setUp();

		this.instance = new OpenSSHSessionSampler();
		// LOG.log(Level.INFO,"created new instance of SSHCommandSamplerExtra");

	}

	// If you allocate external resources in a Before method you need to release
	// them after the test runs.
	@After
	public void tearDown() {
		// super.tearDown();
		this.instance = null;// close connection and cleanup of globaldata in
								// test itself
	}

	// The Test annotation tells JUnit that the public void method to which it
	// is attached can be run as a test case.
	@Test
	public void testSSHOpenPersistentSFTPSamplerWrongSSHSession() {

		SampleResult sr;

		SSHOpenPersistentSFTPSampler sftp = new SSHOpenPersistentSFTPSampler();
		sftp.setConnectionName("CONN1X");
		sftp.setSftpSessionName("SESS1X");
		sr = sftp.sample(null);
		int errorCount = sr.getErrorCount();
		LOG.log(Level.INFO, "errorcount:" + Integer.toString(errorCount));
		assertTrue("ErrorCount is 1", errorCount == 1);
		LOG.log(Level.INFO, "content type:" + sr.getContentType());
		String responseCode = sr.getResponseCode();
		LOG.log(Level.INFO, "response code:" + responseCode);
		assertTrue(responseCode.equals("-1"));
		String responseMessage = sr.getResponseMessage();
		LOG.log(Level.INFO, "response message:" + responseMessage);
		assertTrue("response message is connection CONN1X not found",
				responseMessage.equals("connection CONN1X not found"));
		String responseData = sr.getResponseDataAsString();
		LOG.log(Level.INFO, "response data as string:" + responseData);
		assertTrue("response data is connection CONN1X not found", responseData.equals("connection CONN1X not found"));
		String responseLabel = sr.getSampleLabel();
		LOG.log(Level.INFO, "response label as string:" + responseLabel);
		assertTrue(" responseLabel is SSHOpenPersistentSFTPSampler (connection CONN1X not found)",
				responseLabel.equals("SSHOpenPersistentSFTPSampler (connection CONN1X not found)"));
		String responseSamplerData = sr.getSamplerData();
		LOG.log(Level.INFO, "response sampler data as string:" + responseSamplerData);
		assertTrue("responseSamplerData is Open SFTP SESS1X on CONN1X",
				responseSamplerData.equals("Open SFTP SESS1X on CONN1X"));

	}

}
