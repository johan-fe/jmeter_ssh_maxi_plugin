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
 * GUI for the SSHOpenPersistentShellSampler Sampler that opens a persistent Shell channel over which shell commands can be sent 
 * to the server. The Shell channel is established over a persistent SSH connection
 */
public class SSHOpenPersistentShellSamplerBeanInfo extends AbstractSSHMainSamplerBeanInfo {
	// class must have a public constructor to become visible in the menu
	public SSHOpenPersistentShellSamplerBeanInfo() {
		// for this inherited class a constructor without parameters is needed
		// which passes the sampler class
		// info to the abstract superclass

		super(SSHOpenPersistentShellSampler.class);
		createPropertyGroup("connectionManagement", new String[] { "shellName", // $NON-NLS-1$
				"connectionName", // $NON-NLS-1$
				"useTty" // $NON-NLS-1$
		});
		createPropertyGroup("resultProcessing", new String[] { "resultEncoding", // $NON-NLS-1$
				"stripPrompt" // $NON-NLS-1$

		});
		PropertyDescriptor p = property("shellName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("connectionName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("resultEncoding"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "UTF-8");

		p = property("stripPrompt"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, Boolean.FALSE);

		p = property("useTty"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, Boolean.FALSE);
	}
}
