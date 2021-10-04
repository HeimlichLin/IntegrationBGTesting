package idv.heimlich.IntegrationTesting.common.tester.ssh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import idv.heimlich.IntegrationTesting.common.testcase.base.ITestCase;
import idv.heimlich.IntegrationTesting.common.testcase.base.ITestExceute;
import idv.heimlich.IntegrationTesting.common.testcase.base.TestHolder;
import idv.heimlich.IntegrationTesting.common.tester.contant.CheckPros;
import idv.heimlich.IntegrationTesting.common.tester.contant.ITextContextStatus;
import idv.heimlich.IntegrationTesting.common.tester.contant.Status;

/**
 * 檢查SSH 訊息
 */
public class CheckCommnadListener implements ISSHCommandListener {

	private Status status = Status.None;
	private List<CheckPros> checkProsList = new ArrayList<CheckPros>();
	private String message;
	
	public CheckCommnadListener(CheckPros checkPros) {
		this.checkProsList.add(checkPros);
		this.checkProsList.add(CheckPros.COMMON);
	}

	@Override
	public void update(String msg, ISSHCommandContent commandContent) throws InterruptedException {
		this.write(msg);
		for (CheckPros checkPros : checkProsList) {
			for (ITextContextStatus textContextStatus : checkPros.getRules()) {
				if (msg.contains(textContextStatus.getText())) {
					status = textContextStatus.getStatus();
					message = msg;
					if (textContextStatus.getStatus() == Status.Finish) {
						commandContent.doInterrupted();
					}
					if (textContextStatus.getStatus() == Status.Fail) {
						commandContent.doInterrupted();
					}
				}
			}
		}
	}
	
	private void write(String msg) {
		ITestExceute testExceute = TestHolder.getNowCase();
		ITestCase testCase = testExceute.getITestCase();
		try {
			FileUtils.write(new File(testCase.getSCTlog()), msg + "\n", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
	
}
