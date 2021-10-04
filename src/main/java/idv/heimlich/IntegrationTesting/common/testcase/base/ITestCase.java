package idv.heimlich.IntegrationTesting.common.testcase.base;

/**
 * 測試個案
 */
public interface ITestCase {
	
	/**
	 * 測試編號
	 * 
	 * @return
	 */
	public String getTestNo();

	/**
	 * 測試說明
	 * 
	 * @return
	 */
	public String getMemo();

	public String getOutfolder();

	public String getSCTlog();

}
