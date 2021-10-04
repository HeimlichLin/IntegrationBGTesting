package idv.heimlich.IntegrationTesting.common.testcase.base;

import idv.heimlich.IntegrationTesting.common.InterruptedWork;
import idv.heimlich.IntegrationTesting.common.TaskTimeout;
import idv.heimlich.IntegrationTesting.common.tester.ssh.SSHClient;
import idv.heimlich.IntegrationTesting.common.tester.ssh.SSHManager;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;

import com.tradevan.common.db.DoXdaoSession;
import com.tradevan.common.db.utils.sql.ResourceSql;
import com.tradevan.taurus.xdao.XdaoException;

public abstract class BaseBackendTest {
	
	protected final SSHManager sshManager;

	public BaseBackendTest(SSHManager sshManager) {
		this.sshManager = sshManager;
	}
	
	@Before
	public void init() {
		
	}

	public abstract DoXdaoSession getDoXdaoSession();

	public void executeSqlByFile(String file) {
		String sql = ResourceSql.toSql(this.getClass(), file);
		String[] linesSqList = StringUtils.split(sql, ";");
		DoXdaoSession session = this.getDoXdaoSession();
		String[] arg7 = linesSqList;
		int arg6 = linesSqList.length;

		for (int arg5 = 0; arg5 < arg6; ++arg5) {
			String nativeSQL = arg7[arg5];
			if (StringUtils.isNotBlank(nativeSQL)) {
				System.out.println(nativeSQL);

				try {
					session.executeUpdate(nativeSQL, new String[0]);
				} catch (XdaoException arg9) {
					arg9.printStackTrace();
				}
			}
		}

	}

	public void callTimeoutSCT(int timeout, final List<String> cmds)
			throws InterruptedException {
		InterruptedWork interruptedWork = new InterruptedWork(new Runnable() {
			public void run() {
				try {
					BaseBackendTest.this.callSCT(cmds);
				} catch (Throwable arg1) {
					arg1.printStackTrace();
				}

			}
		});
		(new TaskTimeout(timeout, interruptedWork)).execute();
	}

	private void callSCT(List<String> cmds) throws Throwable {
		try {
			SSHClient command = this.sshManager.connect();
			try {
				command.sendCommand(cmds);
			} finally {
				if (command != null) {
					command.close();
				}
			}

		} catch (Throwable arg9) {
			throw arg9;
		}
	}

}
