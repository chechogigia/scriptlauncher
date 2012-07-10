package common.util;

import strings.Messages;
import ui.Display;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {

	public static ChannelSftp openChannel(Session session){
		ChannelSftp chan;
		try {
			chan = (ChannelSftp) session.openChannel("sftp"); //$NON-NLS-1$
			chan.connect();
			return chan;
		} catch (JSchException e) {
			Display.log(String.format(Messages.getString("SFTPUtil.1"), e.getMessage())); //$NON-NLS-1$
			return null;
		}		
	}
	
	public static void closeChannel(ChannelSftp chan){
		chan.disconnect();
	}
	
	public static boolean renameRemoteFile(ChannelSftp chan, String absoluteRemotePath, String newPath) {
		try {
			chan.rename(absoluteRemotePath, newPath);
		} catch (SftpException e) {
			return false;
		}
		return true;
	}

	public static boolean putLocalFile(ChannelSftp chan, String absoluteLocalPath, String absoluteRemotePath) {
		try {
			chan.put(absoluteLocalPath, absoluteRemotePath);
		} catch (SftpException e) {
			return false;
		}
		return true;
	}

	public static boolean getRemoteFile(ChannelSftp chan, String absoluteRemotePath, String absoluteLocalPath) {
		try {
			chan.get(absoluteRemotePath, absoluteLocalPath);
		} catch (SftpException e) {
			return false;
		}
		return true;
	}

	public static boolean cdRemoto(ChannelSftp chan, String remoteAbsolutePath) {
		try {
			chan.cd(remoteAbsolutePath);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean makeDirRemoto(ChannelSftp chan, String remotePath) {
		try {
			chan.mkdir(remotePath);
		} catch (SftpException e) {
			return false;
		}
		return true;
	}
}
