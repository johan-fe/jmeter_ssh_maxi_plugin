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


import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.lang.StringBuilder;

//import com.jcraft.jsch.ChannelExec;
//import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

//create a singleton to store global data
 public enum GlobalDataSsh {
	    INSTANCE;
	    //using concurrent since synchronized may throw exception when iterating over it 
	 	static ConcurrentHashMap<String,SshSession> sessionList = new ConcurrentHashMap<String,SshSession>();
	 	static Long connection_counter=1L;
	 	private static final Logger log = LoggingManager.getLoggerForClass();
	 	public static void addSession(String connName, SshSession ses){
	 		sessionList.put(connName, ses);
	 	}

	 	public static void removeSession(String connName ){
	 		try
	 		{
	 			sessionList.remove(connName );
	 		}
	 		catch(Throwable e)
	 		{
	 			log.error("exception in removeSession:"+e.getMessage());
	 		}
	 	}
	 	public static SshSession GetSessionByName(String connName ){
	 		SshSession ses=null;
	 		try
	 		{
	 			 ses=sessionList.get(connName );
	 		}
	 		catch(NullPointerException e){
	 			log.error("Nullpointerexception in GetSessionByName:"+e.getMessage());
	 			return null;
	 		}
	 		return ses;
	 	}

	    public static String getNewConnectionName() { 
	    	StringBuilder sb=new StringBuilder("CONNECTION_");
	    	Date now = new Date();      
	    	Long longTime = new Long(now.getTime()/1000);
	    	sb.append(Long.toString(longTime)).append("_").append(Long.toString(connection_counter));
	    	connection_counter++;
	    	return sb.toString();
	    }
	    //if blank connection name is given the full list of connections is returned
	    // Retrieval operations (including get) generally do not block, so may overlap with update operations 
	    //(including put and remove). Retrievals reflect the results of the most recently completed 
	    //update operations holding upon their onset. For aggregate operations such as putAll and clear, 
	    //concurrent retrievals may reflect insertion or removal of only some entries. 
	    // Similarly, Iterators and Enumerations return elements reflecting the state of the hash 
	    //table at some point at or since the creation of the iterator/enumeration.
	    //They do not throw ConcurrentModificationException. However, iterators are designed to 
	    // be used by only one thread at a time.
	    //ConcurrentHashMap achieves higher concurrency by slightly relaxing the promises it makes to callers.
	    //A retrieval operation will return the value inserted by the most recent completed insert operation,
	    //and may also return a value added by an insertion operation that is concurrently in progress 
	    //(but in no case will it return a nonsense result). Iterators returned by ConcurrentHashMap.iterator()
	    //will return each element once at most and will not ever throw ConcurrentModificationException, 
	    //but may or may not reflect insertions or removals that occurred since the iterator was 
	    //constructed. No table-wide locking is needed (or even possible) to provide thread-safety 
	    //when iterating the collection. ConcurrentHashMap may be used as a replacement for synchronizedMap 
	    //or Hashtable in any application that does not rely on the ability to lock the entire table to prevent updates
	    //However, iterators are designed to be used by only one thread at a time.
	    
	    static public synchronized String getAllConnectionData(String connName)
	    {
	    //TODO adapt to return also shell data	
	    //start mutex here
	    	
	    	//synchronized (sessionList) {
	    		Iterator<Map.Entry<String, SshSession>> sessionIterator;
	    		StringBuilder sb =new StringBuilder();
	    	
	    		sessionIterator = sessionList.entrySet().iterator();
	    		boolean searchSpecific;
	    		if(connName.equals("")){
	    			searchSpecific=false;
	    		}
	    		else
	    		{
	    			searchSpecific=true;
	    		}
	    		while(sessionIterator.hasNext()) {
		    		 Map.Entry<String, SshSession> entry = sessionIterator.next();
		    		 try
		    		 {
		    		      //String st = Thread.currentThread().getName() + " - [" + entry.getKey() + ", " + entry.getValue() + ']';
		    			 String connNameFromList=entry.getKey();
		    			 SshSession sess=entry.getValue();
		    			 if ( searchSpecific==false ||connName.equals(connNameFromList) ){
		    				 sb.append(connNameFromList);
		    				 String shellInfoStr=sess.GetChannelShellList("");
		    				 String sftpInfoStr=sess.GetChannelSftpList("");
		    				 sb.append("[ShellChannels[").append(shellInfoStr).append("]").append(",");
		    				 sb.append("[SFTPChannels[").append(sftpInfoStr).append("]]");
		    			 	 if(sessionIterator.hasNext()){
		    			 		 sb.append("\n");
		    			 	 }
		    			 }
		    		 } 
		    		 catch (Exception e)
		    		 {
		    		      //e.printStackTrace();
		    		      return "excpetion:"+e.getMessage();
		    		 }
		    		 
		    		 
	    		}
	    		//returning will release the lock no problem
	    		return sb.toString();
	    	//}
	    }
	    
	    static public synchronized String getConnectionList(String connName, boolean dumpChannelInfo)
	    {
	    	
	    //start mutex here
	    	
	    	//synchronized (sessionList) {
	    		Iterator<Map.Entry<String, SshSession>> sessionIterator;
	    		StringBuilder sb =new StringBuilder();
	    	
	    		sessionIterator = sessionList.entrySet().iterator();
	    		boolean searchSpecific;
	    		if(connName.equals("")){
	    			searchSpecific=false;
	    		}
	    		else
	    		{
	    			searchSpecific=true;
	    		}
	    		while(sessionIterator.hasNext()) {
		    		 Map.Entry<String, SshSession> entry = sessionIterator.next();
		    		 try
		    		 {
		    		      //String st = Thread.currentThread().getName() + " - [" + entry.getKey() + ", " + entry.getValue() + ']';
		    			 String connNameFromList=entry.getKey();
		    			 if ( searchSpecific==false ||connName.equals(connNameFromList) ){
		    				 if(dumpChannelInfo==false)
		    				 {
		    					 sb.append(connNameFromList);
		    				 }
		    				 else
		    				 {
		    					 sb.append(GlobalDataSsh.getAllConnectionData(connNameFromList));
		    				 }
		    			 	 if(sessionIterator.hasNext()){
		    			 		 sb.append("\n");
		    			 	 }
		    			 }
		    		 } 
		    		 catch (Exception e)
		    		 {
		    		      //e.printStackTrace();
		    		      return "excpetion:"+e.getMessage();
		    		 }
		    		 
		    		 
	    		}
	    		//returning will release the lock no problem
	    		return sb.toString();
	    	//}
	    }
	    /*
	     * clean up function for junit tests
	    */
	 	public static void removeAllSessions() {
    		Iterator<Map.Entry<String, SshSession>> sessionIterator;
    		sessionIterator = sessionList.entrySet().iterator();
    	
    		while(sessionIterator.hasNext()) {
	    		 Map.Entry<String, SshSession> entry = sessionIterator.next();
	    		 try
	    		 {
	    			
	    			SshSession session=entry.getValue();
	    			try {
	    				session.disconnectAllChannelShells();
	    			}
	    			catch(Exception e) {}
	    			try
	    			{
	    				session.disconnectAllSftpChannels();
	    			}
	    			catch(Exception e) {}
	    			session.disconnect();
	    			GlobalDataSsh.removeSession(entry.getKey());
	    		 } 
	    		 catch (Exception e)
	    		 {
	    		      //e.printStackTrace();
	    		      return;
	    		 }
    		}
	 	}
}