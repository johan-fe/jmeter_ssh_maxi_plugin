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
 * GUI for send SFTP Command SSH session sampler, This sampler realizes the GUI for sending an SFTP command to
 * an SFTP channel on a persistent SSH session
 */
public class SendSFTPCommandSSHSessionSamplerBeanInfo extends AbstractSSHMainSamplerBeanInfo {

	public SendSFTPCommandSSHSessionSamplerBeanInfo() {

		super(SendSFTPCommandSSHSessionSampler.class);
		createPropertyGroup("connectionManagement", new String[] { "sftpSessionName", // $NON-NLS-1$
				"connectionName" // $NON-NLS-1$

		});
		createPropertyGroup("fileTransfer", new String[] { "action", // $NON-NLS-1$
				"source", // $NON-NLS-1$
				"printFile", // $NON-NLS-1$
				"destination" // $NON-NLS-1$
		});
		createPropertyGroup("fileProperties", new String[] {
				"permissions", // $NON-NLS-1$
				"userId", // $NON-NLS-1$
				"groupId" // $NON-NLS-1$
		});

		PropertyDescriptor p = property("action"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(DEFAULT, "get");
		p.setValue(TAGS,
				new String[] { SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_GET, 
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_PUT,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_RM, 
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_RMDIR,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_LRMDIR,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_MKDIR,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_LMKDIR,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_LS, 
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_LLS,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_LLSL,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_CD,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_LCD,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_PWD,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_LPWD,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_STAT, 
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_LSTAT,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_HRDL,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_VERS,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_CHMOD,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_CHGRP,
						SendSFTPCommandSSHSessionSampler.SFTP_COMMAND_CHOWN});

		p = property("sftpSessionName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("connectionName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("source"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("printFile"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, Boolean.TRUE);

		p = property("destination"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");
		
		p = property("permissions"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "755");
		
		p = property("userId"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "500");
		
		p = property("groupId"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "500");
		

	}

}
