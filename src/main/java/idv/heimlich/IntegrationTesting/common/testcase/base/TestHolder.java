package idv.heimlich.IntegrationTesting.common.testcase.base;

public class TestHolder {
	
	private static ITestExceute testExceute;
	
	public static void setNowCase(ITestExceute testCase) {
		testExceute = testCase;
	}

	public static ITestExceute getNowCase() {
		return testExceute;
	}
	
}
