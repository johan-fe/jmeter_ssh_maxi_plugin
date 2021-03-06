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
import org.apache.jmeter.testbeans.gui.FileEditor;

//import org.apache.jmeter.testbeans.gui.FileEditor;
//public abstract class BeanInfoSupport
//extends SimpleBeanInfo

//Support class for test bean beanInfo objects. 
//It will help using the introspector to get most of the information, to then modify it at will. 
//To use, subclass it, create a subclass with a parameter-less constructor that: 
//Calls super(beanClass) 
//Modifies the property descriptors, bean descriptor, etc. at will. 
//
//Even before any such modifications, a resource bundle named xxxResources
//(where xxx is the fully qualified bean class name) will be obtained if available and used to localize the following: 
//Bean's display name -- from property displayName. 
//-Properties' display names -- from properties propertyName.displayName. 
//-Properties' short descriptions -- from properties propertyName.shortDescription. 
//The resource bundle will be stored as the bean descriptor's "resourceBundle"
//attribute, so that it can be used for further localization. 
//TestBeanGUI, for example, uses it to obtain the group's display names from properties groupName.displayName.
/**
 * GUI for Open SSH session sampler, This sampler establishes a persistent named SSH session towards the SSH server
 * 
 */
public class OpenSSHSessionSamplerBeanInfo extends AbstractSSHMainSamplerBeanInfo {
	// class must have a public constructor to become visible in the menu
	// public SendCommandSSHSessionSamplerBeanInfo(Class<? extends
	// SendCommandSSHSessionSampler> clazz) {
	// super(clazz);
	// }
	public OpenSSHSessionSamplerBeanInfo() {

		super(OpenSSHSessionSampler.class);

		createPropertyGroup("server", // $NON-NLS-1$
				new String[] { "hostname", // $NON-NLS-1$
						"port", // $NON-NLS-1$
						"connectionTimeout" });

		createPropertyGroup("user", // $NON-NLS-1$
				new String[] { "username", // $NON-NLS-1$
						"password" // $NON-NLS-1$
				});

		createPropertyGroup("keyFile", // $NON-NLS-1$
				new String[] { "sshkeyfile", // $NON-NLS-1$
						"passphrase" // $NON-NLS-1$
				});
		createPropertyGroup("connectionManagement", // $NON-NLS-1$
				new String[] { "tunnels", // $NON-NLS-1$
						"connectionName", // $NON-NLS-1$
						"sessionKeepAliveSeconds", // $NON-NLS-1$
				});

		PropertyDescriptor p;
		p = property("username"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("password"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("sshkeyfile");// $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");
		p.setPropertyEditorClass(FileEditor.class);

		p = property("passphrase");// $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("hostname"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("port"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, new Integer(22));

		p = property("connectionTimeout");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, new Integer(50000));

		p = property("tunnels"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("connectionName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("sessionKeepAliveSeconds");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, new Integer(7200));

		p = property("connectionName"); // $NON-NLS-1$
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

	}
}
