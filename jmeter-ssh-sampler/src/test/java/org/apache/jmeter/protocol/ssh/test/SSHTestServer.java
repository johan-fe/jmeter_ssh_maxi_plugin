package org.apache.jmeter.protocol.ssh.test;

//import java.security.Security;
import java.lang.System;
import java.lang.Thread;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import java.net.URL;
import java.net.URLClassLoader;
//import java.security.Provider;
//import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;

//import java.util.logging.Logger;
import java.util.List;

//import org.apache.log.Logger;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.KeyExchange;
import org.apache.sshd.common.NamedFactory;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.kex.DHG1;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SSHTestServer extends Thread {
	 private static final Logger log =Logger.getLogger(SSHTestServer.class.getName());
	// LogManager.getLogger(SSHTestServer.class);
	// static final String pathLoggerConfig =
	// "C:\\Users\\id093108\\Documents\\tools\\eclipse_oxygen\\johanworkspace_oxygen_java\\sshd-daemon-demo\\target\\log4j.properties";
	static BouncyCastleProvider bcProvider = null;
	SSHTestServer(){
		super();
	}
	/*public void GenSignCastle() {
		if (bcProvider == null) {
			bcProvider = new BouncyCastleProvider();
			Provider[] providers = Security.getProviders();

			String name = bcProvider.getName();
			Security.removeProvider(name); // remove old instance

			Security.addProvider(bcProvider);
		}
	}*/

	 void prinClassPath() {
		ClassLoader cl = ClassLoader.getSystemClassLoader();

		URL[] urls = ((URLClassLoader) cl).getURLs();
		System.out.println("CLASSPATH");
		for (URL url : urls) {
			System.out.println(url.getFile());
		}
		System.out.println("");
	}
	//public static void main(String args[]) {
	      //  (new Thread(new SSHTestServer())).start();
	//}
	public void startdaemon() {
		// PropertyConfigurator.configure(pathLoggerConfig);
		// prinClassPath();
		// GenSignCastle();
		SshServer sshd = SshServer.setUpDefaultServer();
		int port = 5222;
		sshd.setPort(port);
		// log.debug("using port:"+Integer.toString(port));
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshd.setPasswordAuthenticator(new InAppPasswordAuthenticator());
		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
		userAuthFactories.add(new UserAuthPassword.Factory());
		// List<NamedFactory<UserAuth>> userAuthFactories = new
		// ArrayList<NamedFactory<UserAuth>>();
		// userAuthFactories.add(new UserAuthPassword.Factory());
		sshd.setUserAuthFactories(userAuthFactories);
		sshd.setKeyExchangeFactories(Arrays.<NamedFactory<KeyExchange>>asList(new DHG1.Factory()));

		sshd.setShellFactory(new InAppShellFactory());
		
		/*
		 * if (System.getProperty("os.name").toLowerCase().indexOf("windows") <
		 * 0) { sshd.setShellFactory(new ProcessShellFactory(new String[] {
		 * "/bin/sh", "-i", "-l" },
		 * EnumSet.of(ProcessShellFactory.TtyOptions.ONlCr))); } else {
		 * sshd.setShellFactory( new ProcessShellFactory(new String[] {
		 * "cmd.exe " }, EnumSet.of(ProcessShellFactory.TtyOptions.Echo,
		 * ProcessShellFactory.TtyOptions.ICrNl,
		 * ProcessShellFactory.TtyOptions.ONlCr))); }
		 */

		// sshd.setFileSystemFactory(new NativeFileSystemFactory() );
		sshd.setFileSystemFactory(new VirtualFileSystemFactory("C:/Temp"));

		List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
		namedFactoryList.add(new SftpSubsystem.Factory());
		sshd.setSubsystemFactories(namedFactoryList);

		// sshd.setSubsystemFactories(Collections.<NamedFactory<Command>>singletonList(new
		// SftpSubsystem.Factory()));

		
		// sshd.setCommandFactory(new ScpCommandFactory());
		sshd.setCommandFactory(new SshExecCommandFactory());
		// log.info("registering SshExecCommandFactory");
		sshd.setCommandFactory(new ScpCommandFactory(new SshExecCommandFactory()));
		// You can also use the ScpCommandFactory on top of your own
		// CommandFactory:
		log.log(Level.INFO, "sshd start calling");
		try {
			sshd.start();// is a non blocking call so prevent to leave by
							// sleeping
							// afterwards

			// sshd.setCommandFactory(new ScpCommandFactory(myCommandFactory));

			Thread.sleep(Long.MAX_VALUE);
			while (!Thread.currentThread().isInterrupted()) {
				try {
		              Thread.sleep(1000L);
		          } catch (InterruptedException e) {
		              // good practice
		              Thread.currentThread().interrupt();
		              log.log(Level.INFO, "SSHTestServer stopped");
		              return;
		          }
			}
		} catch (Exception e) {
			Thread.currentThread().interrupt();
			log.log(Level.INFO, "SSHTestServer stopped");
            return;
		}
		// java.lang.System.out.println( "leaving daemon ssh");
	}
	/*void stop() {
		Thread.currentThread().interrupt();
	}*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		startdaemon();
		log.log(Level.INFO, "SSHTestServer leaving run function");
		
	}

}
