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
package org.apache.jmeter.protocol.ssh.test;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Maxim Kalina
 * @version $Id$
 */
public class InAppShellFactory implements Factory<Command> {
	// private static final Logger log =
	// LogManager.getLogger(InAppShellFactory.class);

	@Override
	public Command create() {
		return new InAppShell();
	}

	private static class InAppShell implements Command, Runnable {

		// private static final Logger log =
		// LogManager.getLogger(InAppShell.class);

		public static final boolean IS_MAC_OSX = System.getProperty("os.name").startsWith("Mac OS X");

		private static final String SHELL_THREAD_NAME = "InAppShell";
		private static final String SHELL_PROMPT = "app> ";
		private static final String SHELL_CMD_QUIT = "quit";
		private static final String SHELL_CMD_EXIT = "exit";
		private static final String SHELL_CMD_VERSION = "version";
		private static final String SHELL_CMD_HELP = "help";
		private static final String SHELL_CMD_LS = "ls";
		private static final String SHELL_CMD_DATE = "date";

		private InputStream in;
		private OutputStream out;
		private OutputStream err;
		private ExitCallback callback;
		private Environment environment;
		private Thread thread;
		private ConsoleReader reader;
		private PrintWriter writer;

		@SuppressWarnings("unused")
		public InputStream getIn() {
			return in;
		}

		@SuppressWarnings("unused")
		public OutputStream getOut() {
			return out;
		}

		@SuppressWarnings("unused")
		public OutputStream getErr() {
			return err;
		}

		@SuppressWarnings("unused")
		public Environment getEnvironment() {
			return environment;
		}

		@Override
		public void setInputStream(InputStream in) {
			this.in = in;
		}

		@Override
		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		@Override
		public void setErrorStream(OutputStream err) {
			this.err = err;
		}

		@Override
		public void setExitCallback(ExitCallback callback) {
			this.callback = callback;
		}

		@Override
		public void start(Environment env) throws IOException {
			environment = env;
			thread = new Thread(this, SHELL_THREAD_NAME);
			thread.start();
		}

		@Override
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

				String line;
				while ((line = reader.readLine()) != null) {
					handleUserInput(line.trim());
				}

			} catch (InterruptedIOException e) {
				// Ignore
			} catch (Exception e) {
				// log.error("Error executing InAppShell...", e);
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
				response = "file1 file2 file2";
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