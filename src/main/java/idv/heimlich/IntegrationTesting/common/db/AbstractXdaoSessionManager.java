package idv.heimlich.IntegrationTesting.common.db;

import com.tradevan.common.db.DoXdaoSession;
import com.tradevan.taurus.xdao.XdaoFactory;
import com.tradevan.taurus.xdao.XdaoSession;

public abstract class AbstractXdaoSessionManager {
	
	protected static XdaoFactory xdaoFactory = XdaoFactory.getInstance();
	private static ThreadLocal<XdaoSession> sessions = new ThreadLocal<XdaoSession>();
	protected static PoDaoMapper myMapper = new PoDaoMapper();

	public XdaoSession getXdaoSession() {
		XdaoSession session = (XdaoSession) sessions.get();
		if (xdaoFactory == null) {
			init();
		}
		XdaoSession xdaoSession = xdaoFactory.getXdaoSession(getConnId());
		session = new DoXdaoSession(xdaoSession, myMapper);
		sessions.set(session);
		return session;

	}

	public DoXdaoSession getDoXdaoSession() {
		return (DoXdaoSession) getXdaoSession();
	}

	protected abstract String getConnId();

	private static void init() {
		try {
			xdaoFactory = null;
			xdaoFactory = XdaoFactory.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
