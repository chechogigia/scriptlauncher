package common;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import common.properties.DefaultProperties;
import common.properties.PropertiesManager;
import common.util.SFTPUtil;
import common.util.SessionUtil;


public class Connection {

	private String environment;

	private String host;
	private String user;
	private String pass;
	private int port;
	
	private String proxyhost;
	private int proxyport;

	private Session session;
	private ChannelSftp sftpChannel;

	public Connection(String environment) {
		this.environment = environment;
		host = PropertiesManager.getProperty(environment, "Host");
		user = PropertiesManager.getProperty(environment, "User");
		pass = PropertiesManager.getProperty(environment, "Pass");
		port = PropertiesManager.getIntProperty(environment, "Port", 22);
		proxyhost = PropertiesManager.getLocalProperty("ProxyHost");
		proxyport = PropertiesManager.getIntProperty(DefaultProperties.Local, "ProxyPort", 1080);
		initSession();
	}

	public boolean initSession() {
		if (session == null)
			session = SessionUtil.createSession(host, user, pass, port, proxyhost, proxyport);
		return session != null;
	}

	public void closeSession() {
		if (session != null)
			SessionUtil.closeSession(session);
	}
	
	public void initSftpChannel(){
		if (session != null && sftpChannel == null)
			sftpChannel = SFTPUtil.openChannel(getSession());
	}
	
	public void closeSftpChannel() {
		if (sftpChannel != null)
			SFTPUtil.closeChannel(sftpChannel);
	}

	public String getHost() {
		return host;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public int getPort() {
		return port;
	}
	
	public String getProxyHost() {
		return proxyhost;
	}
	
	public int getProxyPort() {
		return proxyport;
	}

	public Session getSession() {
		return session;
	}

	public String getEnvironment() {
		return environment;
	}
	
	public ChannelSftp getSftpChannel(){
		return sftpChannel;
	}
}
