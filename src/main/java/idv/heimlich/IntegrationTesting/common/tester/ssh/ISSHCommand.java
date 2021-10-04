package idv.heimlich.IntegrationTesting.common.tester.ssh;

import java.io.IOException;
import java.util.List;

import com.jcraft.jsch.JSchException;

public interface ISSHCommand extends AutoCloseable {
	
	void sendCommand(String arg0) throws JSchException, IOException;

	void sendCommand(List<String> arg0) throws JSchException, Exception;

}
