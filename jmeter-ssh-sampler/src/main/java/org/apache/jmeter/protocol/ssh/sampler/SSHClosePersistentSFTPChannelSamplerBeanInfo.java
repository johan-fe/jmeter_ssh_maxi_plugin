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

import java.beans.PropertyDescriptor;
/**
 * This class is used to realize the GUI to close a persistent SFTP channel that
 * was previously established over a persistent SSH session
 */
public class SSHClosePersistentSFTPChannelSamplerBeanInfo extends AbstractSSHMainSamplerBeanInfo {
	// class must have a public constructor to become visible in the menu
	public SSHClosePersistentSFTPChannelSamplerBeanInfo() {
		// for this inherited class a constructor without parameters is needed
		// which passes the sampler class
		// info to the abstract superclass
		super(SSHClosePersistentSFTPChannelSampler.class);

		createPropertyGroup("connectionManagement", new String[] { "sftpSessionName", // $NON-NLS-1$
				"connectionName" // $NON-NLS-1$
		});

		PropertyDescriptor p = property("sftpSessionName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("connectionName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

	}
}
