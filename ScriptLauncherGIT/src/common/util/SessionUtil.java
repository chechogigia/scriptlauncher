package common.util;
import java.io.InputStream;
import java.util.Properties;

import strings.Messages;
import ui.Display;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.ProxySOCKS5;
import com.jcraft.jsch.Session;

public class SessionUtil {

	
	public static Session createSession(final String host, final String user, final String pass, final int port, final String proxyhost, final int proxyport) {

		JSch jsch = new JSch();

		try {
			Display.log(String.format(Messages.getString("SessionUtil.0"), user,host,port)); //$NON-NLS-1$

			Session session = jsch.getSession(user, host, port);
			session.setPassword(pass);
			
			if (proxyhost != null && !proxyhost.isEmpty())
				session.setProxy(new ProxySOCKS5(proxyhost,proxyport));

			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			prop.put("PreferredAuthentications", "keyboard-interactive,gssapi-with-mic,publickey,password"); //$NON-NLS-1$ //$NON-NLS-2$
			session.setConfig(prop);
			
			session.connect();

			Display.log(Messages.getString("SessionUtil.5")); //$NON-NLS-1$

			return session;

		} catch (Exception e) {
			Display.log(String.format(Messages.getString("SessionUtil.6"), e.getMessage())); //$NON-NLS-1$
			e.printStackTrace();
		}

		return null;
	}

	public static void closeSession(Session session) {
		session.disconnect();
		Display.log(Messages.getString("SessionUtil.7")); //$NON-NLS-1$
	}

	public static void execRemoteCommand(Session session, String command) {
		Display.log(String.format(Messages.getString("SessionUtil.8"), command)); //$NON-NLS-1$
		try {
			ChannelExec chan = (ChannelExec) session.openChannel("exec"); //$NON-NLS-1$
			chan.setCommand(command);
			chan.setInputStream(null);
			chan.setErrStream(System.err);
			
			InputStream in = chan.getInputStream();
			chan.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					Display.rawLog(new String(tmp, 0, i));
				}
				if (chan.isClosed()) {
					Display.log(Messages.getString("SessionUtil.9") + chan.getExitStatus()); //$NON-NLS-1$
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			chan.disconnect();
		} catch (Exception e) {
			Display.log(String.format(Messages.getString("SessionUtil.6"), e.getMessage())); //$NON-NLS-1$
			e.printStackTrace();
		}
	}
	
	public static void execRemoteCommandShowErr(Session session, String command) {
		Display.log(String.format(Messages.getString("SessionUtil.8"), command)); //$NON-NLS-1$
		try {
			ChannelExec chan = (ChannelExec) session.openChannel("exec"); //$NON-NLS-1$
			chan.setCommand(command);
			chan.setOutputStream(System.out);
			
			InputStream in = chan.getErrStream();
			chan.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					Display.rawLog(new String(tmp, 0, i));
				}
				if (chan.isClosed()) {
					Display.log(Messages.getString("SessionUtil.9") + chan.getExitStatus()); //$NON-NLS-1$
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			
			chan.disconnect();
		} catch (Exception e) {
			Display.log(String.format(Messages.getString("SessionUtil.6"), e.getMessage())); //$NON-NLS-1$
			e.printStackTrace();
		}
	}
}
