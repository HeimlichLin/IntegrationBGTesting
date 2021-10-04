package idv.heimlich.IntegrationTesting.common.testcase.base;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.tradevan.common.db.DoXdaoSession;
import com.tradevan.common.object.FileLoader;

import idv.heimlich.IntegrationTesting.common.db.XdaoSessionFactory;
import idv.heimlich.IntegrationTesting.common.db.impl.XdaoSessionFactoryImpl;
import idv.heimlich.IntegrationTesting.common.log.LogFactory;
import idv.heimlich.IntegrationTesting.common.tester.ITymcommSender;
import idv.heimlich.IntegrationTesting.common.tester.impl.TymcommSenderFactory;
import idv.heimlich.IntegrationTesting.common.tester.ssh.MySSHManager;
import idv.heimlich.IntegrationTesting.common.tester.ssh.SSHClient;
import idv.heimlich.IntegrationTesting.common.tester.ssh.SSHManager;
import idv.heimlich.IntegrationTesting.common.tester.utils.ChannelSftpUtils;
import idv.heimlich.IntegrationTesting.common.tester.utils.CheckDataLookupHelper;
import idv.heimlich.IntegrationTesting.common.tester.utils.ExcelMegeSCTLog;
import idv.heimlich.IntegrationTesting.tsft.TsftContract;

public class BaseTest extends BaseBackendTest implements ITestContext, ITestExceute {

	protected String initSQLFile;// 初始化資料
	protected String checlExcelFile;// 資料檢查檔案
	protected String outFile;// 資料檢查檔案
	final List<String> commands;
	protected final CaseTests caseTest;
	final protected static String INITDATA = "initData";
	final protected static String CHECKRESULT = "checkResult";
	private String sctLogLocal;
	private String connId;
	private XdaoSessionFactory xdaoSessionFactory = new XdaoSessionFactoryImpl();

	public BaseTest(final CaseTests caseTest) {
		super(MySSHManager.getSSHManager());
		this.caseTest = caseTest;
		this.initSQLFile = caseTest.initSQLFile;
		this.checlExcelFile = caseTest.getCheclExcelFile();
		this.outFile = caseTest.outFile;
		this.commands = caseTest.commands;
		this.sctLogLocal = caseTest.getOutfolder() + "sct-op.log";
		new File(caseTest.getOutfolder()).mkdirs();
	}
	
	public BaseTest(final CaseTests caseTest, String connId) {
		this(caseTest);
		this.connId = connId;

	}
	
	@Before
	public void init() {
		TestHolder.setNowCase(this);
	}

	@After
	public void testEnd() {
		TestHolder.setNowCase(null);
	}

	@Override
	public ITestCase getITestCase() {
		return caseTest;
	}

	@Override
	public ITymcommSender getTymcommSFTPSender() {
		return TymcommSenderFactory.get(TymcommSenderFactory.TYPE.SFTP, caseTest);
	}

	@Override
	public DoXdaoSession getDoXdaoSession() {
		return xdaoSessionFactory.getXdaoSession(connId);
	}
	
	public String getSctLogLocal() {
		return sctLogLocal;
	}

	public void setSctLogLocal(String sctLogLocal) {
		this.sctLogLocal = sctLogLocal;
	}
	
	/**
	 * get 初始化資料
	 */
	public void initData() {
		try {
//			File file = FileLoader.getResourcesFile(BaseTest.class, this.initSQLFile);
			this.executeSqlByFile(this.initSQLFile);
		} catch (Exception e) {
			LogFactory.getInstance().warn("匯入資料不存在，忽略執行");
		}

	}

	/**
	 * 呼叫SCT
	 * 
	 * @param timeout
	 * @throws InterruptedException
	 */
	public void callTimeoutSCT(int timeout) {
		try {
			callTimeoutSCT(timeout, commands);
		} catch (InterruptedException e) {
			LogFactory.getInstance().warn("程式中斷");
			e.printStackTrace();
		}
	}

	/**
	 * 檢查結果
	 */
	public void checkResult() {
		CheckDataLookupHelper.check(this.checlExcelFile, this.outFile, getDoXdaoSession());
		try {
			new ExcelMegeSCTLog().merge(new File(outFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SSHManager getSshManager() {
		return sshManager;
	}

	/**
	 * 該案件存放資料區
	 * 
	 * @return
	 */
	public String getCaseFolder() {
		return caseTest.getCaseFolder();
	}
	
	/**
	 * 取得測試個案資源檔案
	 * 
	 * @param folder
	 * @return
	 */
	protected File getResourceForCaseTestFolder(String folder) {
		return FileLoader.getResourcesFile(BaseTest.class, caseTest.getCaseFolder() + folder);
	}
	
	/**
	 * 刪除【個案】中相關檔sesend底下檔案
	 * 
	 * @param delFolder
	 *            路徑
	 * @param isLike
	 *            相似名稱
	 * @throws Exception
	 */
	protected void deleteInitFile(String[] delFolder, boolean isLike) throws Exception {
		try (SSHClient client = this.sshManager.connect()) {
			final ChannelSftp channelSftp = client.openSFTP();
			for (final String remove : delFolder) {
				final File folder = FileLoader.getResourcesFile(getClass(),//
						this.getCaseFolder() + TsftContract.SESEND.getCode());
				for (final File file : folder.listFiles()) {
					final String remvoeFile = remove + file.getName();
					try {
						if (isLike) {
							channelSftp.rm(remvoeFile + "*");
						} else {
							channelSftp.rm(remvoeFile);
						}

						LogFactory.getInstance().debug("移除檔案 " + file.getName() + " 成功");
					} catch (final SftpException e) {
						LogFactory.getInstance().error("檔案不存在 不執行刪除:" + remvoeFile);
					}

				}
			}
		}
	}
	
	/**
	 * 刪除指定檔位位置
	 * 
	 * @param path
	 * @throws Exception
	 */
	protected void deletePath(String path) throws Exception {
		try (SSHClient client = this.sshManager.connect()) {
			final ChannelSftp channelSftp = client.openSFTP();
			final String remvoeFile = path;
			try {
				channelSftp.rm(path);
				LogFactory.getInstance().debug("移除檔案 " + " 成功");
			} catch (final SftpException e) {
				LogFactory.getInstance().error("檔案不存在 不執行刪除:" + remvoeFile);
			}

		}
	}
	
	/**
	 * 刪除【個案】中相關檔sesend底下檔案
	 * 
	 * @param delFolder
	 * @throws Exception
	 */
	protected void deleteInitFile(String[] delFolder) throws Exception {
		this.deleteInitFile(delFolder, false);
	}

	public boolean checkRemoteVSLocalFile(String remoteFile, String remoteBase, File localFile) throws Exception {
		boolean isContentSame = false;
		try (SSHClient client = this.sshManager.connect()) {
			final ChannelSftp channelSftp = client.openSFTP();
			@SuppressWarnings("unchecked")
			final Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remoteBase + remoteFile);

			final String contentLocal = FileUtils.readFileToString(localFile, "utf-8");
			for (final ChannelSftp.LsEntry f : files) {
				final String contentRemote = ChannelSftpUtils.readFileContent(channelSftp, remoteBase + f.getFilename());
				if (StringUtils.equals(contentLocal, contentRemote)) {

					LogFactory.getInstance().debug("產生檔案之檔案與比對內容相同");
					isContentSame = true;
					break;
				}
				LogFactory.getInstance().debug("contentLocal:" + contentLocal);
				LogFactory.getInstance().debug("contentRemote:" + contentRemote);
			}

		}
		return isContentSame;

	}

}
