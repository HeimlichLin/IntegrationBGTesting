package idv.heimlich.IntegrationTesting.common.db;

public class XdaoSessionFTZBManager extends AbstractXdaoSessionManager {

	public static final String PROP_DEFAULT_CONN_ID = "PFTZBPool"; // "apConn";

	@Override
	protected String getConnId() {
		return PROP_DEFAULT_CONN_ID;
	}
	
}
