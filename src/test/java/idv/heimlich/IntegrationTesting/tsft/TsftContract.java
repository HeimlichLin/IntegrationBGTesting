package idv.heimlich.IntegrationTesting.tsft;

/**
 * 共用變數
 */
public enum TsftContract {
	
	SESEND("sesend")

	;
	final String code;

	private TsftContract(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
