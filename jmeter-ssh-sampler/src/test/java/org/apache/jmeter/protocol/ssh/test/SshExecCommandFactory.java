package org.apache.jmeter.protocol.ssh.test;

import java.io.FilterOutputStream;

/*
* #%L 
* ExpectIt 
* %% 
* Copyright (C) 2014 Alexey Gavrilov and contributors 
* %% 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
*  
*      http://www.apache.org/licenses/LICENSE-2.0 
*  
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License. 
* #L% 
*/

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

 
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.shell.ProcessShellFactory;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;



/**
 * An echo command which pipes the input to the output.
 */
public class SshExecCommandFactory implements CommandFactory, Runnable {
	// private static final Logger log =
	// LogManager.getLogger(SshExecCommandFactory.class);
	/*
	 * private ScheduledExecutorService executor =
	 * Executors.newScheduledThreadPool(1); private OutputStream out; private
	 * InputStream in; private OutputStream err;
	 */

	@Override
	public void run() {
		// log.debug("run SshExecCommandFactory");

		/*
		 * byte[] buffer = new byte[4096]; try { int len = in.read(buffer); if
		 * (len > 0) { out.write(buffer, 0, len); out.flush(); } } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
	}

	// String command="";
	// TODO add linux shell
	@Override
	public Command createCommand(String command) {
		// log.debug("command received:["+command+"]");
		// this.command=command;
		// Command Represents a command, shell or subsystem that can be used to
		// send command.
		// Commands given to this exec function must return data and not request
		// input
		// ProcessShellFactory pf=new ProcessShellFactory(new String[]
		// {"cmd.exe", "/C", command });

		// Command cmdpf=pf.create();

		return new InAppShellExec(command);

	}

	public  static class InAppShellExec implements Command, Runnable {

		//private static final Logger log = LogManager.getLogger(InAppShell.class);

		public static final boolean IS_MAC_OSX = System.getProperty("os.name").startsWith("Mac OS X");

		private static final String SHELL_THREAD_NAME = "InAppShell";
		private static final String SHELL_PROMPT = "app> ";
		private static final String SHELL_CMD_QUIT = "quit";
		private static final String SHELL_CMD_EXIT = "exit";
		private static final String SHELL_CMD_VERSION = "version";
		private static final String SHELL_CMD_HELP = "help";
		private static final String SHELL_CMD_LS = "ls";
		private static final String SHELL_CMD_DATE = "date";
		private static final String SHELL_CMD_DIR = "dir";

		private InputStream in;
		private OutputStream out;
		private OutputStream err;
		private ExitCallback callback;
		private Environment environment;
		private Thread thread;
		private ConsoleReader reader;
		private PrintWriter writer;

		private String commandstring;

		public InAppShellExec(String command) {
			this.commandstring=command;
		}

		public InputStream getIn() {
			return in;
		}

		public OutputStream getOut() {
			return out;
		}

		public OutputStream getErr() {
			return err;
		}

		public Environment getEnvironment() {
			return environment;
		}

		public void setInputStream(InputStream in) {
			this.in = in;
		}

		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		public void setErrorStream(OutputStream err) {
			this.err = err;
		}

		public void setExitCallback(ExitCallback callback) {
			this.callback = callback;
		}

		public void start(Environment env) throws IOException {
			environment = env;
			thread = new Thread(this, SHELL_THREAD_NAME);
			thread.start();
		}

		public void destroy() {
			if (reader != null)
				reader.shutdown();
			thread.interrupt();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			try {

				reader = new ConsoleReader(in, new FilterOutputStream(out) {
					@Override
					public void write(final int i) throws IOException {
						super.write(i);

						// workaround for MacOSX!! reset line after CR..
						if (IS_MAC_OSX && i == ConsoleReader.CR.toCharArray()[0]) {
							super.write(ConsoleReader.RESET_LINE);
						}
					}
				});
				reader.setPrompt(SHELL_PROMPT);
				reader.addCompleter(
						new StringsCompleter(SHELL_CMD_QUIT, SHELL_CMD_EXIT, SHELL_CMD_VERSION, SHELL_CMD_HELP));
				writer = new PrintWriter(reader.getOutput());

				// output welcome banner on ssh session startup
				writer.println("****************************************************");
				writer.println("*        Welcome to Application Shell.             *");
				writer.println("****************************************************");
				writer.flush();

				//String line;
				//while ((line = reader.readLine()) != null) {
					handleUserInput(this.commandstring);
				//}

			} catch (InterruptedIOException e) {
				// Ignore
			} catch (Exception e) {
				//log.error("Error executing InAppShell...", e);
			} finally {
				callback.onExit(0);
			}
		}

		private void handleUserInput(String line) throws InterruptedIOException {

			if (line.equalsIgnoreCase(SHELL_CMD_QUIT) || line.equalsIgnoreCase(SHELL_CMD_EXIT))
				throw new InterruptedIOException();

			String response = null;
			if (line.equalsIgnoreCase(SHELL_CMD_VERSION))
				response = "InApp version 1.0.0";
			else if (line.equalsIgnoreCase(SHELL_CMD_HELP))
				response = "Help is not implemented yet...";
			else if (line.equalsIgnoreCase(SHELL_CMD_LS))
				response = "file1 file2 file3";
			else if (line.equalsIgnoreCase(SHELL_CMD_DIR))
				response = " Volume in drive C is OSDisk\r\n" + 
						" Volume Serial Number is 88C9-85F1\r\n" + 
						"\r\n" + 
						" Directory of C:\\Temp\r\n" + 
						"\r\n" + 
						"23/02/2017  09:18    <DIR>          case wrong recording states\r\n" + 
						"29/11/2016  14:26       213.387.592 HP 850 G3 Video driver latest sp78116.exe\r\n" + 
						"30/01/2017  14:57    <DIR>          LogFiles\r\n" + 
						"20/02/2017  09:49            96.256 RE nPVR Revamp  .msg\r\n" + 
						"20/02/2017  09:50            85.504 RE nPVR Revamp cm.msg\r\n" + 
						"23/02/2017  17:40    <DIR>          urhwal1\r\n" + 
						"               3 File(s)    213.569.352 bytes\r\n" + 
						"               3 Dir(s)  23.825.162.240 bytes free\r\n" + 
						"";
			else if (line.equalsIgnoreCase(SHELL_CMD_DATE)) {
				DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				Date dateobj = new Date();
				response = df.format(dateobj);
			} else
				response = "======> \"" + line + "\"";

			writer.println(response);
			writer.flush();
		}
	}
}