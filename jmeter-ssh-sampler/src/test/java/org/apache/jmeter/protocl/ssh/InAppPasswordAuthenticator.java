package org.apache.jmeter.protocl.ssh;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
 

/**
 * @author Maxim Kalina
 * @version $Id$
 */
public class InAppPasswordAuthenticator implements PasswordAuthenticator {
	//private static final Logger log = LogManager.getLogger(InAppPasswordAuthenticator.class);
	@Override
	public boolean authenticate(String username, String password, ServerSession session) {
		boolean res=username != null && username.equals("johan") && password != null && password.equals("azerty!");
		if (res=true) {
			//log.info("successfully authenticated:"+username);
		} else {
			//log.info("authentication failed for:"+username);
		}
		
		return res;
	}
}
