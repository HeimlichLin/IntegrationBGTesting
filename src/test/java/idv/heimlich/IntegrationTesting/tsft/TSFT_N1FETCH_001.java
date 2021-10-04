package idv.heimlich.IntegrationTesting.tsft;

import idv.heimlich.IntegrationTesting.common.testcase.base.BaseTest;
import idv.heimlich.IntegrationTesting.common.testcase.base.CaseTests;
import idv.heimlich.IntegrationTesting.common.tester.contant.CheckPros;
import idv.heimlich.IntegrationTesting.common.tester.contant.Status;
import idv.heimlich.IntegrationTesting.common.tester.runner.PostTest;
import idv.heimlich.IntegrationTesting.common.tester.runner.PrePostRunner;
import idv.heimlich.IntegrationTesting.common.tester.runner.PreTest;
import idv.heimlich.IntegrationTesting.common.tester.ssh.CheckCommnadListener;
import idv.heimlich.IntegrationTesting.common.tester.ssh.SSHClient;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jcraft.jsch.ChannelSftp;
import com.tradevan.common.object.FileLoader;


/**
 * 保證金繳納
 */
@RunWith(value = PrePostRunner.class)
public class TSFT_N1FETCH_001 extends BaseTest {

	private static final String REVFIL_PATH = "/PCLMS/TMP/REVFIL/N1/";
	
	public TSFT_N1FETCH_001() {
		super(CaseTests.TSFT_N1FETCH_001);
	}
	
	@PreTest(value = { BaseTest.INITDATA // 初始資料
	})
	
	@PostTest(value = { BaseTest.CHECKRESULT // 檢查結果
	})
	
	@Test
	public void test() throws Exception {
		try (SSHClient client = sshManager.connect()) {
			ChannelSftp channelSftp = client.openSFTP();
			final File toN1Folder = FileLoader.getResourcesFile(getClass(), this.getCaseFolder() + TsftContract.SESEND.getCode());
			for (File file : toN1Folder.listFiles()) {
				channelSftp.put(new FileInputStream(file), REVFIL_PATH + file.getName());
			}
		}
		
		// 呼叫SCT
		final CheckCommnadListener commandListener = new CheckCommnadListener(CheckPros.TSFT_N1FETCH_001);
		this.getSshManager().setCommandListener(commandListener);
		this.callTimeoutSCT(1000 * 30);
		Assert.assertTrue(commandListener.getMessage(), commandListener.getStatus() != Status.Fail);
	}

}
