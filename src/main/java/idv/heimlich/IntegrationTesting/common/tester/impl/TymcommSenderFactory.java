package idv.heimlich.IntegrationTesting.common.tester.impl;

import com.tradevan.common.exception.ApBusinessException;

import idv.heimlich.IntegrationTesting.common.testcase.base.CaseTests;
import idv.heimlich.IntegrationTesting.common.tester.ITymcommSender;

public class TymcommSenderFactory {
	
	public enum TYPE {
		SSL, //
		NO_SSL, //
		SFTP, //
	}
	
	public static ITymcommSender get(TYPE type, CaseTests caseTests) {
		switch (type) {
		case SFTP:
			return new TymcommSftpSenderImpl(caseTests);

		default:
			throw new ApBusinessException("newo only support sftp");
		}
	}

}
