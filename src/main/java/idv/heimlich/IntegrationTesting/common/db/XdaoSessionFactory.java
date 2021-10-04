package idv.heimlich.IntegrationTesting.common.db;

import com.tradevan.common.db.DoXdaoSession;

public interface XdaoSessionFactory {
	
	DoXdaoSession getXdaoSession(String conn);

}
