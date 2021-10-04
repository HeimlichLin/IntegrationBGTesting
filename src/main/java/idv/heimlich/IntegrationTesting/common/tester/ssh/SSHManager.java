package idv.heimlich.IntegrationTesting.common.tester.ssh;

import com.jcraft.jsch.JSch;

public class SSHManager {
	
	private JSch jschSSHChannel;
	private String strUserName;
	private String strConnectionIP;
	private final int intConnectionPort;
	private String strPassword;
	private final int intTimeOut;
//	private static final int INT_TIMEOUT = 60000;
	
	private ISSHCommandListener commandListener = new ISSHCommandListener() {
		public void update(String msg, ISSHCommandContent commandContent)
				throws InterruptedException {
		}
	};
	
	public SSHManager(String userName, String password, String connectionIP) {
		this.doCommonConstructorActions(userName, password, connectionIP);
		this.intConnectionPort = 22;
		this.intTimeOut = '';
	}
	
	public SSHManager(String userName, String password, String connectionIP,
			int connectionPort) {
		this.doCommonConstructorActions(userName, password, connectionIP);
		this.intConnectionPort = connectionPort;
		this.intTimeOut = '';
	}
	
	public SSHManager(String userName, String password, String connectionIP,
			int connectionPort, int timeOutMilliseconds) {
		this.doCommonConstructorActions(userName, password, connectionIP);
		this.intConnectionPort = connectionPort;
		this.intTimeOut = timeOutMilliseconds;
	}

	public void setCommandListener(ISSHCommandListener commandListener) {
		this.commandListener = commandListener;
	}

	public SSHClient connect() {
		SSHClient sshclient = new SSHClient();
		sshclient.jschSSHChannel = this.jschSSHChannel;
		sshclient.strUserName = this.strUserName;
		sshclient.strConnectionIP = this.strConnectionIP;
		sshclient.intConnectionPort = this.intConnectionPort;
		sshclient.strPassword = this.strPassword;
		sshclient.intTimeOut = this.intTimeOut;
		sshclient.setCommandListener(this.commandListener);
		sshclient.connect();
		return sshclient;
	}

	private void doCommonConstructorActions(String userName, String password,
			String connectionIP) {
		this.jschSSHChannel = new JSch();
		this.strUserName = userName;
		this.strPassword = password;
		this.strConnectionIP = connectionIP;
	}
	
}
