package idv.heimlich.IntegrationTesting.common.tester.ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;

public interface ISFTPConnent {
	
	ChannelSftp openSFTP() throws JSchException;

}
