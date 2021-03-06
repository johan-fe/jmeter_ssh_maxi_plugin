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

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
// a component in the testbean framork is defined by 3 files
//[ComponentName].java (org.apache.jmeter.config.CSVDataSet.java) 
//[ComponentName]BeanInfo.java (org.apache.jmeter.config.CSVDataSetBeanInfo.java) 
//[ComponentName]Resources.properties (org.apache.jmeter.config.CSVDataSetResources.properties) 
//[ComponentName].java:
//this class extends to a  ConfigTestElement which will make it a component in the test plan 
// The type of config element to which we extend will say what kind element your component will be 
// ie AbstractSampler, AbstractVisualizer, GenericController, etc - though you can also 
// make different types of elements just by instantiating the right interfaces, the abstract classes can make your life easier
// TestBean is a marker interface, so there are no methods to implement. 
//When implementing a TestBean, pay careful attention to your properties.
//These properties will become the basis of a gui form by which users will configure the CSVDataSet element. 
//Your element will be cloned by JMeter when the test starts. 
//Each thread will get it's own instance. However, you will have a 
//chance to control how the cloning is done, if you need it. 
// Properties need to be defined in [ComponentName].java with public getters and setters. 

public abstract class AbstractSSHMainSampler extends AbstractSampler implements TestBean {
	/**
	 * Parent class of some Samplers in the SSH package
	 */

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggingManager.getLoggerForClass();

	public AbstractSSHMainSampler(String name) {
		super();
		setName(name);
		log.info("initializing class:" + name);
	}

	@Override
	public void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			log.error("SSH session finalize error", e);
		} finally {
//
		}

	}
}
