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

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.TestBean;

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



public class CloseSSHSessionSamplerBeanInfo extends  BeanInfoSupport {
	//class must have a public constructor to become visible in the menu
	
	public CloseSSHSessionSamplerBeanInfo() {
       
		super( CloseSSHSessionSampler.class); 

        createPropertyGroup("connectionManagement", 
        		new String[]{ 
                "connectionName", // $NON-NLS-1$

            });
        PropertyDescriptor p;
     
        p = property("connectionName"); // $NON-NLS-1$
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT,"" );

	}
}
