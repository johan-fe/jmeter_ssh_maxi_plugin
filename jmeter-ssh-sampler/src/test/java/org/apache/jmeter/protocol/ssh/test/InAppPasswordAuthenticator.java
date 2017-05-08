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

import java.util.logging.Logger;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
 

/**
 * @author Maxim Kalina
 * @version $Id$
 */
public class InAppPasswordAuthenticator implements PasswordAuthenticator {
	 private static final Logger log =Logger.getLogger(SSHTestServer.class.getName());
	//private static final Logger log = LogManager.getLogger(InAppPasswordAuthenticator.class);
	@Override
	public boolean authenticate(String username, String password, ServerSession session) {
		boolean res=username != null && username.equals("johan") && password != null && password.equals("azerty!");
		if (res==true) {
			log.info("successfully authenticated:"+username);
		} else {
			log.info("authentication failed for:"+username);
		}
		
		return res;
	}
}
