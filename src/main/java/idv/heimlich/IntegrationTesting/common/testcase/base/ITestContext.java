package idv.heimlich.IntegrationTesting.common.testcase.base;

import idv.heimlich.IntegrationTesting.common.tester.ITymcommSender;

public interface ITestContext {
	
	/**
	 * 取得Send元件
	 * @return
	 */
	public ITymcommSender getTymcommSFTPSender();

}
