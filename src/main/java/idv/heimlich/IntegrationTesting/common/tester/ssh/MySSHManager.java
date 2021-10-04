package idv.heimlich.IntegrationTesting.common.tester.ssh;

import idv.heimlich.IntegrationTesting.common.evn.EVNConfigProducer;
import idv.heimlich.IntegrationTesting.common.evn.IEVNConfig;
import idv.heimlich.IntegrationTesting.common.log.LogFactory;

/**
 * 取得該專案設定檔案之SSH
 */
public class MySSHManager {
	
	public static SSHManager getSSHManager() {
		IEVNConfig config = EVNConfigProducer.getConfig();
		final String connectionIP = config.getConnectionIP();
		final String userName = config.getUserName();
		final String password = config.getPassword();

		LogFactory.getInstance().info("connectionIP:{}", connectionIP);
		LogFactory.getInstance().info("userName:{}", userName);
		LogFactory.getInstance().info("password:{}", password);

		final SSHManager manager = new SSHManager(userName, password, connectionIP);
		return manager;

	}


}
