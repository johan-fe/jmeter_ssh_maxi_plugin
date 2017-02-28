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
	 	static ConcurrentHashMap<String,Session> sessionList = new ConcurrentHashMap<String,Session>();
	 	static Long connection_counter=1L;
	 	private static final Logger log = LoggingManager.getLoggerForClass();
	 	public static void addSession(String connName, Session ses){
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
	 	public static Session GetSessionByName(String connName ){
	 		Session ses=null;
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
	    
	    static public synchronized String GetConnectionList(String connName)
	    {
	    	
	    //start mutex here
	    	
	    	//synchronized (sessionList) {
	    		Iterator<Map.Entry<String, Session>> sessionIterator;
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
		    		 Map.Entry<String, Session> entry = sessionIterator.next();
		    		 try
		    		 {
		    		      //String st = Thread.currentThread().getName() + " - [" + entry.getKey() + ", " + entry.getValue() + ']';
		    			 String connNameFromList=entry.getKey();
		    			 if ( searchSpecific==false ||connName.equals(connNameFromList) ){
		    				 sb.append(connNameFromList);
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
	    
}