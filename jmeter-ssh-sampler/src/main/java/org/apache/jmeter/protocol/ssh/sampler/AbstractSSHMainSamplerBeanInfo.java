package org.apache.jmeter.protocol.ssh.sampler;

import org.apache.jmeter.testbeans.BeanInfoSupport;
//a component in the testbean framork is defined by 3 files
//[ComponentName].java (org.apache.jmeter.config.CSVDataSet.java) 
//[ComponentName]BeanInfo.java (org.apache.jmeter.config.CSVDataSetBeanInfo.java) 
//[ComponentName]Resources.properties (org.apache.jmeter.config.CSVDataSetResources.properties) 
//
// [ComponentName]BeanInfo.java should extend org.apache.jmeter.testbeans.BeanInfoSupport 
//create a zero-parameter constructor in which we call super(CSVDataSet.class); 
// Setting up your gui elements in[ComponentName]BeanInfo.java in done in the constructor of the class: 
// You can create groupings for your component's properties. 
//Each grouping you create needs a label and a list of property names to include in that grouping. Ie: 
//createPropertyGroup("csv_data",new String[]{"filename","variableNames"}); 
//Creates a grouping called "csv_data" that will include gui input elements
// for the "filename" and "variableNames" properties of CSVDataSet.
// Then, we need to define what kind of properties we want these to be: 
//p = property("filename");
//p.setValue(NOT_UNDEFINED, Boolean.TRUE);
//p.setValue(DEFAULT, "");
//p.setValue(NOT_EXPRESSION,Boolean.TRUE);
//p = property("variableNames");
//p.setValue(NOT_UNDEFINED, Boolean.TRUE);
//p.setValue(DEFAULT, "");
//p.setValue(NOT_EXPRESSION,Boolean.TRUE);
// see https://wiki.apache.org/jmeter/DeveloperManual/TestBeanTutorial

public abstract class AbstractSSHMainSamplerBeanInfo extends BeanInfoSupport{
	public AbstractSSHMainSamplerBeanInfo(Class<? extends AbstractSSHMainSampler> clazz) {
        super(clazz);
   //     createPropertyGroup("dummy", // $NON-NLS-1$
   //             new String[]{
   //             });
    }
}
