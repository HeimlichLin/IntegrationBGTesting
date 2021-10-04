package idv.heimlich.IntegrationTesting.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFactory {
	
	private static Logger LOG;

	public static synchronized Logger getInstance() {
		if (LOG == null) {
			LOG = LoggerFactory.getLogger(LogFactory.class);;
		}
		return LOG;
	}

}
