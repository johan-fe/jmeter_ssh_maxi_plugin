package org.apache.jmeter.protocol.ssh.sampler;

import java.beans.PropertyDescriptor;

public class SSHClosePersistentShellSamplerBeanInfo extends AbstractSSHMainSamplerBeanInfo {
	// class must have a public constructor to become visible in the menu
	public SSHClosePersistentShellSamplerBeanInfo() {
	// for this inherited class a constructor without parameters is needed which passes the sampler class 
		//info to the abstract superclass 
		super(SSHClosePersistentShellSampler.class);
		
		createPropertyGroup("connectionManagement", new String[] { 
				"shellName", // $NON-NLS-1$
				"connectionName" // $NON-NLS-1$
		});

		PropertyDescriptor p = property("shellName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("connectionName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");
 
	}
}
