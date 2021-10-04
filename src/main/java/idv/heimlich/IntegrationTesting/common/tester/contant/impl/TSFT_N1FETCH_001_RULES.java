package idv.heimlich.IntegrationTesting.common.tester.contant.impl;

import idv.heimlich.IntegrationTesting.common.tester.contant.ITextContextStatus;
import idv.heimlich.IntegrationTesting.common.tester.contant.Status;

public enum TSFT_N1FETCH_001_RULES implements ITextContextStatus {

	FAIL_NO1("Deq fails", Status.Fail), //
	;
	
	final String key;
	final Status status;
	
	private TSFT_N1FETCH_001_RULES(String key, Status status) {
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
