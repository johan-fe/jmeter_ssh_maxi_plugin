package org.apache.jmeter.protocol.ssh.sampler;

import java.beans.PropertyDescriptor;

public class SSHOpenPersistentSFTPSamplerBeanInfo extends AbstractSSHMainSamplerBeanInfo {
	// class must have a public constructor to become visible in the menu
	public SSHOpenPersistentSFTPSamplerBeanInfo() {
	// for this inherited class a constructor without parameters is needed which passes the sampler class 
		//info to the abstract superclass 

		super(SSHOpenPersistentSFTPSampler.class);
		createPropertyGroup("connectionManagement", new String[] { 
				"sftpSessionName", // $NON-NLS-1$
				"connectionName",// $NON-NLS-1$
				"useTty" // $NON-NLS-1$
		});
        createPropertyGroup("resultProcessing", new String[]{ 
                "resultEncoding", // $NON-NLS-1$
                "stripPrompt"  // $NON-NLS-1$
 
            });
		PropertyDescriptor p = property("sftpSessionName"); // $NON-NLS-1$
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
	}
}
