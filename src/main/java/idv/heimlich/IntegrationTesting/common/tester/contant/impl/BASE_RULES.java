package idv.heimlich.IntegrationTesting.common.tester.contant.impl;

import idv.heimlich.IntegrationTesting.common.tester.contant.ITextContextStatus;
import idv.heimlich.IntegrationTesting.common.tester.contant.Status;

/**
 * 共用規則
 */
public enum BASE_RULES implements ITextContextStatus {
	
	FAIL_001("Exception", Status.Fail), //
	FAIL_002("Deq fails", Status.Fail), //

	FINISH_01("sleeping", Status.Finish), //
	;
	
	final String key;
	final Status status;

	private BASE_RULES(String key, Status status) {
		this.key = key;
		this.status = status;
	}
	
	@Override
	public String getText() {
		return this.key;
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

}
