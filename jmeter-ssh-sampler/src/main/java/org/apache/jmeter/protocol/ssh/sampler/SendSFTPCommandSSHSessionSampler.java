
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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
//import org.apache.jorphan.logging.LoggingManager;
//import org.apache.log.Logger;

/**
 * Send SFTP Command SSH session sampler, This sampler sends an SFTP command to
 * an SFTP channel on a persistent SSH session
 */
public class SendSFTPCommandSSHSessionSampler extends AbstractSSHMainSampler implements TestBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9180249991509027397L;
	// private static final Logger log = LoggingManager.getLoggerForClass();
	public static final String SFTP_COMMAND_GET = "get";
	public static final String SFTP_COMMAND_PUT = "put";
	public static final String SFTP_COMMAND_RM = "rm";
	public static final String SFTP_COMMAND_RMDIR = "rmdir";
	public static final String SFTP_COMMAND_LRMDIR = "local rmdir";
	public static final String SFTP_COMMAND_LS = "ls";
	public static final String SFTP_COMMAND_RENAME = "rename";
	public static final String SFTP_COMMAND_LLSL = "local ls -l";
	public static final String SFTP_COMMAND_CD = "cd";
	public static final String SFTP_COMMAND_LCD = "local cd";
	public static final String SFTP_COMMAND_PWD = "pwd";
	public static final String SFTP_COMMAND_LPWD = "local pwd";
	public static final String SFTP_COMMAND_MKDIR = "mkdir";
	public static final String SFTP_COMMAND_STAT = "stat";
	public static final String SFTP_COMMAND_LSTAT = "lstat";
	public static final String SFTP_COMMAND_LLS = "local ls";
	public static final String SFTP_COMMAND_LMKDIR = "local mkdir";
	public static final String SFTP_COMMAND_HRDL = "hard link";
	public static final String SFTP_COMMAND_VERS = "get server and client version";
	public static final String SFTP_COMMAND_CHMOD = "chmod";
	public static final String SFTP_COMMAND_CHGRP = "chgrp";
	public static final String SFTP_COMMAND_CHOWN = "chown";
	
	private String source;
	private String destination;
	private String action;
	private String sftpSessionName = "";
	private String connectionName = "";
	private int groupId=0;
	private int userId=0;
	private int permissions=755;
	private boolean printFile = true;
	private boolean useTty;
	// private String resultEncoding = "UTF-8";

	public SendSFTPCommandSSHSessionSampler() {
		super("SendSFTPCommandSSHSessionSampler");
	}

	/**
	 * function to get last line as sampler label
	 * 
	 * @return last line of output from the command
	 */
	public String getSamplerLabel() {
		StringBuilder sb = new StringBuilder(getName());
		return sb.toString();
	}

	@Override
	public SampleResult sample(Entry arg0) {
		SampleResult res = new SampleResult();
		res.sampleStart();

		String samplerData = "Send Command " + this.action + " to SFTP " + this.sftpSessionName + " on "
				+ this.connectionName;
		String responseData = "";
		String responseMessage = "";
		// String responseCode = "";
		res.setSamplerData(samplerData);
		res.setDataType(SampleResult.TEXT);
		res.setContentType("text/plain");
		if (this.connectionName.equals("")) {
			// empty connection name
			responseMessage = "connection name is empty";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-2");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}

		SshSession sshSess = GlobalDataSsh.GetSessionByName(this.connectionName);
		if (sshSess == null) {
			// ssh connection not found
			responseMessage = "connection " + this.connectionName + " not found";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-1");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseMessage(responseMessage);
			res.setResponseData(responseMessage, "UTF-8");
			res.sampleEnd();
			return res;
		}
		if (this.sftpSessionName.equals("")) {
			// ssh connection not found
			responseMessage = "SFTP session name is empty";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-3");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}

		// make a new shell Session
		Session sess = sshSess.getSession();
		// check if session is still open
		if (sess == null) {
			responseMessage = "severe error ssh session is null";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-5");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		if (sess.isConnected() == false) {
			responseMessage = "ssh connection with name " + this.connectionName + " is not anymore connected";
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-6");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}

		// ssh session is connected try to connect shell
		SshChannelSFTP cSftp = sshSess.getChannelSftpByName(this.sftpSessionName);
		if (cSftp == null) {
			// ssh connection not found
			responseMessage = "SFTP session with name " + this.sftpSessionName + " is null on " + this.connectionName;
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-4");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		// check if channelshell is still connected
		if (!cSftp.isConnected()) {
			responseMessage = "SFTP session with name " + this.sftpSessionName + " is not connected on: "
					+ this.connectionName;
			res.setSampleLabel(getName() + " (" + responseMessage + ")");
			res.setResponseCode("-7");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setResponseData(responseMessage, "UTF-8");
			res.setResponseMessage(responseMessage);
			res.sampleEnd();
			return res;
		}
		// Channelftp isconnected and all conditions fulfilled

		responseMessage = "Command " + this.action + " sent to SFTP " + this.sftpSessionName + " on "
				+ this.connectionName;
		// TODO add doCommand code
		cSftp.getChannelSftp().setPty(this.useTty);

		try {
			// execute the sftp command
			responseData = this.doSSHAction(cSftp.getChannelSftp(), source, destination,userId,groupId,permissions, res);

		} catch (Exception e) {
			byte[] responseDataBytes2 = {};
			res.setResponseCode("-8");
			res.setSuccessful(false);
			res.setSamplerData(samplerData);
			res.setSampleLabel(
					getName() + "Receive Exception(" + e.getClass().getSimpleName() + " " + e.getMessage() + ")");
			res.setResponseMessage("Receive Exception(" + e.getClass().getSimpleName() + " " + e.getMessage() + ")");
			res.setResponseData(responseDataBytes2);
			res.sampleEnd();
			return res;
		}
		// res.setResponseData(new
		// String(responseDataBytes),this.resultEncoding);
		res.setResponseData(responseData.getBytes());
		res.setResponseMessageOK();
		res.setResponseCode("0");
		res.setSuccessful(true);
		res.setSamplerData(samplerData);
		res.setSampleLabel(getName() + " (" + responseMessage + ")");
		res.setResponseMessage(responseMessage);
		res.sampleEnd();
		return res;

	}

	public SampleResult sampleOld(Entry e) {
		SampleResult res = new SampleResult();
		/*
		 * res.setSampleLabel(this.getSamplerLabel());
		 * 
		 * 
		 * 
		 * // Set up sampler return types res.setSamplerData(action + " " +
		 * source);
		 * 
		 * res.setDataType(SampleResult.TEXT); res.setContentType("text/plain");
		 * 
		 * String response; if (getSession() == null) { connect(); }
		 * 
		 * try { if (getSession() == null) {
		 * log.error("Failed to connect to server with credentials " +
		 * getUsername() + "@" + getHostname() + ":" + getPort() + " pw=" +
		 * getPassword()); throw new
		 * NullPointerException("Failed to connect to server: " +
		 * getFailureReason()); }
		 * 
		 * response = doFileTransfer(getSession(), source, destination, res);
		 * res.setResponseData(response.getBytes());
		 * 
		 * 
		 * res.setSuccessful(true);
		 * 
		 * res.setResponseMessageOK(); } catch (JSchException e1) {
		 * res.setSuccessful(false); res.setResponseCode("JSchException");
		 * res.setResponseMessage(e1.getMessage()); } catch (SftpException e1) {
		 * res.setSuccessful(false); res.setResponseCode("SftpException");
		 * res.setResponseMessage(e1.getMessage()); } catch (IOException e1) {
		 * res.setSuccessful(false); res.setResponseCode("IOException");
		 * res.setResponseMessage(e1.getMessage()); } catch
		 * (NullPointerException e1) { res.setSuccessful(false);
		 * res.setResponseCode("Connection Failed");
		 * res.setResponseMessage(e1.getMessage()); } finally { // Try a
		 * disconnect/sesson = null here instead of in finalize. disconnect();
		 * setSession(null); }
		 */
		return res;
	}

	/**
	 * Executes a the given command inside a short-lived channel in the session.
	 * 
	 * Performance could be likely improved by reusing a single channel, though
	 * the gains would be minimal compared to sharing the Session.
	 * 
	 * @param channel
	 *            channel on which the sftp command is sent
	 * @param scr
	 * 	   source file on which the command is executed 
	 * @param dst
	 * 	   destination file on which the command is executed 
	 * @param uid 
	 * 	   numeric user id for the chown command 
	 * @param gid
	 *     numeric group id for the  chgrp command 
	 * @param permiss 
	 * 	   numeric permissions for the chmod command
	 * @param res
	 *     contains the result of the operation 
	 * @return All standard output from the command
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	private String doSSHAction(ChannelSftp channel, String src, String dst,int uid, int gid , int permiss ,SampleResult res)
			throws SftpException, IOException, Exception {
		StringBuilder sb = new StringBuilder("");
		// ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		// channel.connect();

		if (SFTP_COMMAND_GET.equals(action)) {

			if (!printFile) {
				channel.get(src, dst);
				sb.append("retrieved file '").append(src).append("' to '").append(dst).append("'.");
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(channel.get(src)));
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					sb.append(line);
					sb.append("\n");
				}
			}

		} else if (SFTP_COMMAND_PUT.equals(action)) {
			channel.put(src, dst);
		} else if (SFTP_COMMAND_LS.equals(action)) {
			@SuppressWarnings("unchecked")
			List<ChannelSftp.LsEntry> ls = channel.ls(src);
			for (ChannelSftp.LsEntry line : ls) {
				sb.append(line.getLongname());
				sb.append("\n");
			}
		} else if (SFTP_COMMAND_RM.equals(action)) {
			channel.rm(src);
			sb.append("Successfully removed remote file '").append(src).append("'");
		} else if (SFTP_COMMAND_RMDIR.equals(action)) {
			channel.rmdir(src);
			sb.append("Successfully removed remote directory '").append(src).append("'");
		} else if (SFTP_COMMAND_RENAME.equals(action)) {
			channel.rename(src, dst);
			sb.append("Successfully remote file '").append(src).append("' to '").append(dst).append("'");
		} else if (SFTP_COMMAND_CD.equals(action)) {
			// try {
			channel.cd(src);
			sb.append("change to remote directory '" + src + "' succeeded");
		} else if (SFTP_COMMAND_PWD.equals(action)) {
			String workingDirectory = channel.pwd();
			sb.append("Current remote working directory is '").append(workingDirectory).append("'");
		} else if (SFTP_COMMAND_LPWD.equals(action)) {
			String workingDirectory = channel.lpwd();
			sb.append("Current local working directory is '").append(workingDirectory).append("'");
		} else if (SFTP_COMMAND_LCD.equals(action)) {
			channel.lcd(src);
			sb.append("Changed local directory to '").append(src).append("'");
		} else if (SFTP_COMMAND_MKDIR.equals(action)) {
			channel.mkdir(src);
			sb.append("Created remote directory '" + src).append("'");
		} else if (SFTP_COMMAND_STAT.equals(action)) {
			SftpATTRS sftpAttrs = channel.stat(src);
			int permissionsAttr = sftpAttrs.getPermissions();
			String aTimeString = sftpAttrs.getAtimeString();
			String extendedPermissions[] = sftpAttrs.getExtended();
			int flags = sftpAttrs.getFlags();
			int gidAttr = sftpAttrs.getGId();
			String mTimeString = sftpAttrs.getMtimeString();
			String permissionString = sftpAttrs.getPermissionsString();
			long size = sftpAttrs.getSize();
			int uidAttr = sftpAttrs.getUId();
			int hashcode = sftpAttrs.hashCode();
			boolean isBlk = sftpAttrs.isBlk();
			boolean isChr = sftpAttrs.isChr();
			boolean isDir = sftpAttrs.isDir();
			boolean isFifo = sftpAttrs.isFifo();
			boolean isLink = sftpAttrs.isLink();
			boolean isReg = sftpAttrs.isReg();
			boolean isSock = sftpAttrs.isSock();
			sb.append(sftpAttrs.toString()).append("\n\n");
			sb.append("permissions: ").append(permissionString).append(" (").append(permissionsAttr).append(")")
					.append("\n");
			sb.append("atime: ").append(aTimeString).append("\n");
			sb.append("mtime: ").append(mTimeString).append("\n");
			sb.append("size: ").append(size).append("\n");
			sb.append("gid: ").append(gidAttr).append("\n");
			sb.append("uid: ").append(uidAttr).append("\n");
			sb.append("flags: ").append(flags).append("\n");
			sb.append("is block device: ").append(isBlk).append("\n");
			sb.append("is character device: ").append(isChr).append("\n");
			sb.append("is directory: ").append(isDir).append("\n");
			sb.append("is symbolic link: ").append(isLink).append("\n");
			sb.append("is regular file: ").append(isReg).append("\n");
			sb.append("is FIFO: ").append(isFifo).append("\n");
			sb.append("is socket: ").append(isSock).append("\n");
			sb.append("hashcode: ").append(hashcode).append("\n");
			if (extendedPermissions != null && extendedPermissions.length > 0) {
				int index = 1;
				for (String extendedPermission : extendedPermissions) {
					sb.append("extended permission[").append(index).append("]: ").append(extendedPermission);
					index++;
				}
			} else {
				sb.append("no extended permissions");
			}
			/*
			 * import org.apache.hadoop.fs.permission.FsPermission; FsPermission
			 * permission = new FsPermission((short)
			 * sftpAttrs.getPermissions());
			 * 
			 * channelExec1 = fsHelper.getExecChannel("id " +
			 * sftpAttrs.getUId()); String userName =
			 * IOUtils.toString(channelExec1.getInputStream());
			 * 
			 * 
			 * channelExec2 = fsHelper.getExecChannel("id " +
			 * sftpAttrs.getGId()); String groupName =
			 * IOUtils.toString(channelExec2.getInputStream());
			 * 
			 * FileStatus fs = new FileStatus(sftpAttrs.getSize(),
			 * sftpAttrs.isDir(), 1, 0l, (long) sftpAttrs.getMTime(), (long)
			 * sftpAttrs.getATime(), permission,
			 * StringUtils.trimToEmpty(userName),
			 * StringUtils.trimToEmpty(groupName), path);
			 */
		} else if (SFTP_COMMAND_LSTAT.equals(action)) {

			SftpATTRS sftpAttrs = channel.lstat(src);
			int permissionsAttr = sftpAttrs.getPermissions();
			String aTimeString = sftpAttrs.getAtimeString();
			String extendedPermissions[] = sftpAttrs.getExtended();
			int flags = sftpAttrs.getFlags();
			int gidAttr = sftpAttrs.getGId();
			String mTimeString = sftpAttrs.getMtimeString();
			String permissionString = sftpAttrs.getPermissionsString();
			long size = sftpAttrs.getSize();
			int uidAttr = sftpAttrs.getUId();
			int hashcode = sftpAttrs.hashCode();
			boolean isBlk = sftpAttrs.isBlk();
			boolean isChr = sftpAttrs.isChr();
			boolean isDir = sftpAttrs.isDir();
			boolean isFifo = sftpAttrs.isFifo();
			boolean isLink = sftpAttrs.isLink();
			boolean isReg = sftpAttrs.isReg();
			boolean isSock = sftpAttrs.isSock();
			sb.append(sftpAttrs.toString()).append("\n\n");
			sb.append("permissions: ").append(permissionString).append(" (").append(permissionsAttr).append(")")
					.append("\n");
			sb.append("atime: ").append(aTimeString).append("\n");
			sb.append("mtime: ").append(mTimeString).append("\n");
			sb.append("size: ").append(size).append("\n");
			sb.append("gid: ").append(gidAttr).append("\n");
			sb.append("uid: ").append(uidAttr).append("\n");
			sb.append("flags: ").append(flags).append("\n");
			sb.append("is block device: ").append(isBlk).append("\n");
			sb.append("is character device: ").append(isChr).append("\n");
			sb.append("is directory: ").append(isDir).append("\n");
			sb.append("is symbolic link: ").append(isLink).append("\n");
			sb.append("is regular file: ").append(isReg).append("\n");
			sb.append("is FIFO: ").append(isFifo).append("\n");
			sb.append("is socket: ").append(isSock).append("\n");
			sb.append("hashcode: ").append(hashcode).append("\n");
			if (extendedPermissions != null && extendedPermissions.length > 0) {
				int index = 1;
				for (String extendedPermission : extendedPermissions) {
					sb.append("extended permission[").append(index).append("]: ").append(extendedPermission);
					index++;
				}
			} else {
				sb.append("no extended permissions");
			}
		} else if (SFTP_COMMAND_LLS.equals(action)) {

			String localPath = channel.lpwd();
			Path dir = Paths.get(localPath);

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*")) {
				for (Path file : stream) {
					sb.append(file).append("\n");
				}
			}

		} else if (SFTP_COMMAND_LMKDIR.equals(action)) {
			// returns pathnames for files and directory
			String localPath = channel.lpwd();
			String directoryName = localPath + File.separator + src;
			File f = new File(directoryName);

			// create
			boolean result = f.mkdir();

			if (result == true) {
				sb.append("Created directory '").append(directoryName).append("'");
			} else {
				sb.append("Failed to create directory '").append(directoryName).append("'");
				if (f.exists()) {
					sb.append(", directory already exists");
				}
				Exception e = new Exception(sb.toString());
				throw e;
			}
		} else if (SFTP_COMMAND_LRMDIR.equals(action)) {
			String localPath = channel.lpwd();
			String directoryName = localPath + File.separator + src;
			File f = new File(directoryName);

			// create
			boolean result = false;
			if (f.isDirectory()) {
				result = f.delete();
			} else {
				Exception e = new Exception(directoryName + " is not a directory");
				throw e;
			}

			if (result == true) {
				sb.append("Deleted directory '").append(directoryName).append("'");
			} else {
				sb.append("Failed to delete directory '").append(directoryName).append("'");
				Exception e = new Exception(sb.toString());
				throw e;
			}
		} else if (SFTP_COMMAND_LLSL.equals(action)) {
			String localPath = channel.lpwd();
			Path dir = Paths.get(localPath);

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*")) {
				for (Path file : stream) {
					BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
					// BasicFileAttributeView
					// bav=Files.getFileAttributeView(file,
					// BasicFileAttributeView.class);
					// BasicFileAttributeView bav =new BasicFileAttributeView();
					// FileTime creationTime=attr.creationTime();
					// FileTime lastAccessTime=attr.lastAccessTime();
					FileTime lastModifiedTime = attr.lastModifiedTime();
					// boolean isDirectory =attr.isDirectory();
					String lastModTimeStr = "";
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(System.currentTimeMillis());// set
																	// current
																	// date to
																	// get
																	// current
																	// year
																	// below
					int curYear = cal.get(Calendar.YEAR);
					cal.setTimeInMillis(lastModifiedTime.toMillis());
					int fileYear = cal.get(Calendar.YEAR);
					String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());

					String sM = String.format("%-3s", month);
					String sD = String.format("%-2s", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
					if (curYear == fileYear) {
						String sHou = String.format("%-2s", Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
						String sMin = String.format("%-2s", Integer.toString(cal.get(Calendar.MINUTE)));
						lastModTimeStr = sM + ' ' + sD + ' ' + sHou + ':' + sMin;
					} else {
						String sY = String.format("%-5s", Integer.toString(fileYear));
						lastModTimeStr = sM + ' ' + sD + ' ' + sY;
					}
					// try to make DOS atttribute strings
					String dattrStr = "";
					try {
						DosFileAttributeView daView = Files.getFileAttributeView(file, DosFileAttributeView.class);
						DosFileAttributes dattr = daView.readAttributes();
						dattrStr = (attr.isDirectory() ? "d" : "-") + (dattr.isHidden() ? 'h' : '-')
								+ (dattr.isArchive() ? 'a' : '-') + (dattr.isReadOnly() ? 'r' : '-')
								+ (dattr.isSystem() ? 's' : '-');
					} catch (Exception e) {
						dattrStr = "";
					}
					String attrStr = "";
					try {
						PosixFileAttributeView paView = Files.getFileAttributeView(file, PosixFileAttributeView.class);
						String group = paView.readAttributes().group().getName();
						String owner = paView.readAttributes().owner().getName();
						final Set<PosixFilePermission> perms = Files.getPosixFilePermissions(file);
						attrStr = (attr.isDirectory() ? "d" : "-")
								+ (perms.contains(PosixFilePermission.OWNER_READ) ? 'r' : '-')
								+ (perms.contains(PosixFilePermission.OWNER_WRITE) ? 'w' : '-')
								+ (perms.contains(PosixFilePermission.OWNER_EXECUTE) ? 'x' : '-')
								+ (perms.contains(PosixFilePermission.GROUP_READ) ? 'r' : '-')
								+ (perms.contains(PosixFilePermission.GROUP_WRITE) ? 'w' : '-')
								+ (perms.contains(PosixFilePermission.GROUP_EXECUTE) ? 'x' : '-')
								+ (perms.contains(PosixFilePermission.OTHERS_READ) ? 'r' : '-')
								+ (perms.contains(PosixFilePermission.OTHERS_WRITE) ? 'w' : '-')
								+ (perms.contains(PosixFilePermission.OTHERS_EXECUTE) ? 'x' : '-') + ' ' + owner + ' '
								+ group;
					} catch (Exception e) {
						attrStr = dattrStr;
					}

					long bytes = attr.size();
					String sizeStr = "";
					final String units = "BKMG";
					int unit = 0;
					int fraction = 0;
					while (bytes > 1000 && (unit + 1) < units.length()) {
						bytes /= 100;
						fraction = (int) (bytes % 10);
						bytes /= 10;
						unit++;
					}
					if (bytes < 10) {
						sizeStr = bytes + "." + fraction + units.charAt(unit);
					} else {
						sizeStr = (bytes < 100 ? " " : "") + bytes + units.charAt(unit);
					}

					// String attrString=bav.toString();
					sb.append(attrStr).append(' ').append(sizeStr).append(' ').append(lastModTimeStr).append(' ')
							.append(file).append("\n");
				}
			} 

		}else if (SFTP_COMMAND_VERS.equals(action)) {
		 	int serverVersion = channel.getServerVersion();
		 	String version= channel.version();
		 	sb.append("Server Version:").append(serverVersion).append(" Client Version:").append(version);
		}else if (SFTP_COMMAND_HRDL.equals(action)) {
			channel.hardlink(src, dst);
			sb.append("'").append(src).append("' hard linked to '").append(dst).append("'");
		}else if (SFTP_COMMAND_CHMOD.equals(action)) {
			channel.chmod(permiss, src);
			sb.append("changed permssions of file '").append(src).append("' to: ").append(permiss);
		}else if (SFTP_COMMAND_CHGRP.equals(action)) {
			channel.chgrp(gid, src);
			sb.append("changed group of file '").append(src).append("' to: ").append(gid);
		}else if (SFTP_COMMAND_CHOWN.equals(action)) {
			channel.chown(uid, src);
			sb.append("Changed owner of file '").append(src).append("' to: ").append(uid);
	
		}

		// res.sampleEnd();

		// channel.disconnect();
		return sb.toString();
	}

	// Accessors
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean getPrintFile() {
		return printFile;
	}

	public void setPrintFile(boolean printFile) {
		this.printFile = printFile;
	}

	public String getSftpSessionName() {
		return this.sftpSessionName;
	}

	public void setSftpSessionName(String sftpSessionName) {
		this.sftpSessionName = sftpSessionName;
	}

	public String getConnectionName() {
		return this.connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getGroupId() {
		return Integer.toString(groupId);
	}

	public void setGroupId(String groupId) {
		this.groupId = Integer.parseInt(groupId);
	}

	public String getUserId() {
		return Integer.toString(userId);
	}

	public void setUserId(String userId) {
		this.userId = Integer.parseInt(userId);
	}

	public String getPermissions() {
		return Integer.toString(permissions);
	}

	public void setPermissions(String permissions) {
		this.permissions = Integer.parseInt(permissions);
	}


}
