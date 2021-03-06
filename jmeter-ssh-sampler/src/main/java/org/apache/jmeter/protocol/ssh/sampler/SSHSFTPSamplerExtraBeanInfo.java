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
 * GUI for SSH Sampler that collects single lines of output and returns them as samples.
 *
 */
public class SSHSFTPSamplerExtraBeanInfo extends AbstractSSHSamplerExtraBeanInfo {

	public SSHSFTPSamplerExtraBeanInfo() {

		super(SSHSFTPSamplerExtra.class);

		createPropertyGroup("fileTransfer", new String[] { "action", // $NON-NLS-1$
				"source", // $NON-NLS-1$
				"printFile", // $NON-NLS-1$
				"destination" // $NON-NLS-1$

		});

		PropertyDescriptor p = property("action"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(DEFAULT, "get");
		p.setValue(TAGS,
				new String[] { SSHSFTPSamplerExtra.SFTP_COMMAND_GET, SSHSFTPSamplerExtra.SFTP_COMMAND_PUT,
						SSHSFTPSamplerExtra.SFTP_COMMAND_RM, SSHSFTPSamplerExtra.SFTP_COMMAND_RMDIR,
						SSHSFTPSamplerExtra.SFTP_COMMAND_RENAME,SSHSFTPSamplerExtra.SFTP_COMMAND_LS
						,SSHSFTPSamplerExtra.SFTP_COMMAND_CD });

		p = property("source"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("printFile"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, Boolean.TRUE);

		p = property("destination"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

	}

}
