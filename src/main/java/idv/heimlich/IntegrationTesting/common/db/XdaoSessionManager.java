package idv.heimlich.IntegrationTesting.common.db;

public class XdaoSessionManager extends AbstractXdaoSessionManager {

	public static final String PROP_DEFAULT_CONN_ID = "PCLMSPool"; // "apConn";

	@Override
	protected String getConnId() {
		return PROP_DEFAULT_CONN_ID;
	}

}
