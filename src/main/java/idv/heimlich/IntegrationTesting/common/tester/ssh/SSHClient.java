package idv.heimlich.IntegrationTesting.common.tester.ssh;

import idv.heimlich.IntegrationTesting.common.evn.EVNConfigProducer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHClient implements ISSHCommand, ISSHCommandContent, ISFTPConnent {
	
	protected JSch jschSSHChannel;
	protected Session sesConnection;
	protected String strUserName;
	protected String strConnectionIP;
	protected int intConnectionPort;
	protected String strPassword;
	protected int intTimeOut;
	private String encode;
	
	public SSHClient() {
		this.encode = EVNConfigProducer.getConfig().getSshEncoding();
	}
	
	private ISSHCommandListener commandListener = new ISSHCommandListener() {
		public void update(String msg, ISSHCommandContent commandContent)
				throws InterruptedException {
		}
	};

	@Override
	public void close() throws Exception {
		this.sesConnection.disconnect();
	}

	@Override
	public ChannelSftp openSFTP() throws JSchException {
		ChannelSftp channelSftp = (ChannelSftp) this.sesConnection
				.openChannel("sftp");
		channelSftp.connect();
		return channelSftp;
	}

	@Override
	public void doInterrupted() throws InterruptedException {
		System.out.println("中斷操作");
		throw new InterruptedException("中斷操作");
	}

	@Override
	public void sendCommand(String command) throws JSchException, IOException {
		Channel channel = this.sesConnection.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		channel.connect();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				channel.getInputStream()));
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.printf("\t[Resp] %s\n", new Object[]{line});
		}
		channel.disconnect();		
	}

	@Override
	public void sendCommand(List<String> commands) throws JSchException, Exception {
		Channel channel = this.sesConnection.openChannel("shell");
		channel.connect();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				channel.getInputStream(), this.encode));
		DataOutputStream cmdOut = new DataOutputStream(
				channel.getOutputStream());
		Iterator<String> arg5 = commands.iterator();
		String line;
		while (arg5.hasNext()) {
			line = (String) arg5.next();
			cmdOut.writeBytes(line + "\r\n");
		}
		cmdOut.writeBytes("exit\r\n");
		cmdOut.flush();
		cmdOut.close();
		line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			this.commandListener.update(line, this);
		}
		br.close();
		channel.disconnect();
	}
	
	public void setCommandListener(ISSHCommandListener commandListener) {
		this.commandListener = commandListener;
	}

	public void connect() {
		try {
			this.sesConnection = this.jschSSHChannel.getSession(
					this.strUserName, this.strConnectionIP,
					this.intConnectionPort);
			this.sesConnection.setPassword(this.strPassword);
			this.sesConnection.setConfig("StrictHostKeyChecking", "no");
			this.sesConnection.connect(this.intTimeOut);
		} catch (JSchException arg1) {
			arg1.printStackTrace();
		}

	}

}
