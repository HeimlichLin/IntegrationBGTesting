package idv.heimlich.IntegrationTesting.common.tester.contant;

import idv.heimlich.IntegrationTesting.common.tester.contant.impl.BASE_RULES;
import idv.heimlich.IntegrationTesting.common.tester.contant.impl.TSFT_N1FETCH_001_RULES;

public enum CheckPros {
	
	COMMON(BASE_RULES.values()), //
	// N1
	TSFT_N1FETCH_001(TSFT_N1FETCH_001_RULES.values()) //
	;
	
	final ITextContextStatus[] rules;
	
	private CheckPros(ITextContextStatus[] rules) {
		this.rules = rules;
	}
	
	public ITextContextStatus[] getRules() {
		return rules;
	}

}
