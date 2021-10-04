package idv.heimlich.IntegrationTesting.common.tester.impl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.tradevan.common.exception.ApBusinessException;
import com.tradevan.common.object.FileLoader;

import idv.heimlich.IntegrationTesting.common.log.LogFactory;
import idv.heimlich.IntegrationTesting.common.testcase.base.BaseTest;
import idv.heimlich.IntegrationTesting.common.testcase.base.CaseTests;
import idv.heimlich.IntegrationTesting.common.testcase.base.ITestCase;
import idv.heimlich.IntegrationTesting.common.tester.ITymcommSender;
import idv.heimlich.IntegrationTesting.common.tester.Tymcomm;
import idv.heimlich.IntegrationTesting.tsft.TsftContract;

/**
 * 公司 TV 傳送至二代平台元件實作
 */
public class TymcommSftpSenderImpl implements ITymcommSender {
	
	final CaseTests caseTests;

	public TymcommSftpSenderImpl(CaseTests caseTests) {
		super();
		this.caseTests = caseTests;
	}
	
	@Override
	public void send() {

		final File tvconfig = this.getResourceForCaseTestFolder("conf/tvconfig.sys");
		final File sftpConfig = this.getResourceForCaseTestFolder("conf/stconfig.sys");
		final File uploadFold = this.getResourceForCaseTestFolder(TsftContract.SESEND.getCode());
		this.checkFile(tvconfig);
		this.checkFile(sftpConfig);
		this.checkFile(uploadFold);
		System.setProperty("Config", tvconfig.getAbsolutePath());
		System.setProperty("sftpConfig", sftpConfig.getAbsolutePath());
		System.setProperty("SSL", "N");
		System.setProperty("UpldPath", String.format("TMP/sesend/%s", this.caseTests));

		try {
			this.move2UploadFolder(uploadFold, this.caseTests);
			Tymcomm.main(new String[] {});
		} catch (final SecurityException e) {
			LogFactory.getInstance().debug("忽略 system exit");
			// Do something if the external code used System.exit()
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			this.deleteTmpFolder(this.caseTests);
		}
		LogFactory.getInstance().debug("sleeping 30s");
		try {
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刪除暫存區檔案
	 * 
	 * @param testCase
	 */
	private void deleteTmpFolder(CaseTests testCase) {
		final File newUploadFolder = new File("/TMP/sesend/" + testCase.getTestNo());
		for (final File uploadFile : newUploadFolder.listFiles()) {
			uploadFile.delete();
		}

	}

	/**
	 * 取得測試個案資源檔案
	 * 
	 * @param folder
	 * @return
	 */
	private File getResourceForCaseTestFolder(String folder) {
		return FileLoader.getResourcesFile(BaseTest.class, this.caseTests.getCaseFolder() + folder);
	}

	/**
	 * 搬移至上傳區 路徑【/tmp/sesend/${測試個案}】
	 * 
	 * @param uploadFold
	 */
	private void move2UploadFolder(File uploadFold, ITestCase testCase) {
		if (!uploadFold.exists()) {
			throw new ApBusinessException("上傳資料夾不存在:" + uploadFold.getAbsoluteFile());
		}
		for (final File uploadFile : uploadFold.listFiles()) {
			if (uploadFile.isDirectory()) {
				throw new ApBusinessException("不支援資料夾上傳");
			}

		}
		final File newUploadFolder = new File("/TMP/sesend/" + testCase.getTestNo());
		if (!newUploadFolder.exists()) {
			LogFactory.getInstance().debug("上傳資料夾不存在，建立資料夾 :{}", newUploadFolder.getAbsoluteFile());
			newUploadFolder.mkdirs();
		}
		for (final File uploadFile : newUploadFolder.listFiles()) {
			uploadFile.delete();
		}
		for (final File orgFile : uploadFold.listFiles()) {
			LogFactory.getInstance().debug("{}檔案移動至{}", orgFile.getName(), newUploadFolder.getAbsoluteFile());
			try {
				FileUtils.copyFile(orgFile, new File(newUploadFolder, orgFile.getName()));
			} catch (final IOException e) {
				e.printStackTrace();
				throw new ApBusinessException("複製檔案失敗，", e);
			}
		}

	}

	private void checkFile(File f) {
		LogFactory.getInstance().debug("check Path:{}", f.getAbsoluteFile());
		if (!f.exists()) {
			throw new ApBusinessException("查無資訊檔案 從" + f.getAbsolutePath());
		}
	}

}
