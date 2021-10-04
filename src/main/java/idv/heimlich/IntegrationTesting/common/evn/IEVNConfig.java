package idv.heimlich.IntegrationTesting.common.evn;

/**
 * 環境設定檔案介面
 */
public interface IEVNConfig {
	
	/**
	 * 對應IP
	 * 
	 * @return
	 */
	public String getConnectionIP();

	/**
	 * 使用者帳號
	 * 
	 * @return
	 */
	public String getUserName();

	/**
	 * 密碼
	 * 
	 * @return
	 */
	public String getPassword();

	/**
	 * 產出BASE_DIR路徑
	 * 
	 * @return
	 */
	public String getBaseDir();
	
	public String getSshEncoding();

}
