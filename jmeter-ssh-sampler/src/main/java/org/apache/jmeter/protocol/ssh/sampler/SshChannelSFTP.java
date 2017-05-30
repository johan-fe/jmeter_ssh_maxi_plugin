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

import com.jcraft.jsch.ChannelSftp;

import jline.internal.Log;


//import org.apache.jorphan.logging.LoggingManager;
//import org.apache.log.Logger;
/**
 * This class is used to store the information about a persistent SFTP channel that
 * is established over a persistent SSH session
 */
public class SshChannelSFTP {
//	private static final Logger log = LoggingManager.getLoggerForClass();
	ChannelSftp cSFTP = null;

	public void setChannelSftp(ChannelSftp cs) {
		this.cSFTP = cs;
	}

	public ChannelSftp getChannelSftp() {
		return this.cSFTP;
	}

	public void disconnect() {

		try {
			if (cSFTP != null) {
				this.cSFTP.disconnect();
			}
		} catch (Exception e) {
			Log.debug("Error when disconnecting Channel SFTP");
		}
	}

	public boolean isConnected() {
		return this.cSFTP.isConnected();
	}

}
